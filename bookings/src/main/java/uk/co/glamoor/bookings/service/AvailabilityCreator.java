package uk.co.glamoor.bookings.service;

import org.springframework.stereotype.Service;
import uk.co.glamoor.bookings.dto.request.DailyAvailabilityRequest;
import uk.co.glamoor.bookings.dto.request.MonthlyAvailabilityRequest;
import uk.co.glamoor.bookings.dto.request.TimeSlot;
import uk.co.glamoor.bookings.mapper.GlamoorJsonMapper;
import uk.co.glamoor.bookings.model.Availability;

import org.springframework.beans.factory.annotation.Autowired;
import uk.co.glamoor.bookings.repository.AvailabilityRepository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilityCreator {
    private final AvailabilityRepository availabilityRepository;
    private final MessagingService messagingService;

    @Autowired
    public AvailabilityCreator(AvailabilityRepository availabilityRepository,
                               MessagingService messagingService) {
        this.availabilityRepository = availabilityRepository;
        this.messagingService = messagingService;
    }

    public void createMonthlyAvailabilities(MonthlyAvailabilityRequest request) {
        request.validateDates();
        List<Availability> availabilities = createAvailabilities(request);
        availabilityRepository.saveAll(availabilities);
        notifyAvailabilityUpdate(request);
    }

    public void createDailyAvailabilities(DailyAvailabilityRequest request) {
        Availability availability = createAvailability(request);
        availabilityRepository.save(availability);
        notifyAvailabilityUpdate(request);
    }

    private Availability createAvailability(DailyAvailabilityRequest request) {
        String timezone = request.getTimeZone();
        Instant startInstant = calculateStartInstant(request.getDate(), timezone);
        Instant endInstant = calculateEndInstant(request.getDate(), timezone);
        long timeDifferenceMinutes = calculateTimeDifference(request.getDate(), startInstant);
        Set<Instant> existingDates = loadExistingAvailabilities(
                request.getStylistId(),
                request.getServiceProviderId(),
                startInstant,
                endInstant
        );

        List<Availability> availabilities = new ArrayList<>();
        createBaseAvailability(request, timezone, existingDates, availabilities);
        processTimeSlots(request, timezone, timeDifferenceMinutes, existingDates, availabilities);
        mergeAllTimeSlots(availabilities);

        return availabilities.get(0);
    }


    private List<Availability> createAvailabilities(MonthlyAvailabilityRequest request) {
        request.validateDates();
        String timezone = request.getTimeZone();
        Instant startInstant = calculateStartInstant(request.getStartDate(), timezone);
        Instant endInstant = calculateEndInstant(request.getEndDate(), timezone);
        long timeDifferenceMinutes = calculateTimeDifference(request.getStartDate(), startInstant);
        Set<Instant> existingDates = loadExistingAvailabilities(
                request.getStylistId(),
                request.getServiceProviderId(),
                startInstant,
                endInstant
        );

        List<Availability> availabilities = new ArrayList<>();
        createBaseAvailabilities(request, timezone, existingDates, availabilities);
        processTimeSlots(request, timezone, timeDifferenceMinutes, existingDates, availabilities);
        mergeAllTimeSlots(availabilities);

        return availabilities;
    }

    private Instant calculateStartInstant(LocalDate date, String timezone) {
        return date.atStartOfDay(ZoneId.of(timezone)).toInstant();
    }

    private Instant calculateEndInstant(LocalDate date, String timezone) {
        return date.atStartOfDay(ZoneId.of(timezone)).plusDays(1).toInstant();
    }

    private long calculateTimeDifference(LocalDate date, Instant instant) {
        Instant utcStart = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        return Duration.between(utcStart, instant).toMinutes();
    }

    private Set<Instant> loadExistingAvailabilities(String stylistId, String serviceProviderId,
                                                    Instant start, Instant end) {
        List<Availability> existing = availabilityRepository
                .findByStylistIdAndServiceProviderIdAndDateUtcBetween(stylistId, serviceProviderId, start, end);
        return existing.stream().map(Availability::getDateUtc).collect(Collectors.toSet());
    }

    private void createBaseAvailability(DailyAvailabilityRequest request, String timezone,
                                          Set<Instant> existingDates, List<Availability> availabilities) {
        Instant startInstantInSlot = calculateSlotStartInstant(
                request.getDate(), request.getTimeSlots().get(0), timezone);
        Instant endInstantInSlot = calculateSlotEndInstant(
                request.getDate(), request.getTimeSlots().get(request.getTimeSlots().size()-1), timezone);

        if (existingDates.contains(startInstantInSlot)) {
            return;
        }

        createAvailability(request, startInstantInSlot, timezone, availabilities);

        if (spansMultipleUtcDays(startInstantInSlot, endInstantInSlot)) {
            createAvailability(request, endInstantInSlot, timezone, availabilities);
        }
    }

    private void createBaseAvailabilities(MonthlyAvailabilityRequest request, String timezone,
                                          Set<Instant> existingDates, List<Availability> availabilities) {
        for (LocalDate date = request.getStartDate(); !date.isAfter(request.getEndDate()); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            List<TimeSlot> dailySlots = request.getWeeklySchedule().getOrDefault(dayOfWeek, new ArrayList<>());

            if (dailySlots.isEmpty()) {
                continue;
            }

            Instant startInstantInSlot = calculateSlotStartInstant(date, dailySlots.get(0), timezone);
            Instant endInstantInSlot = calculateSlotEndInstant(date, dailySlots.get(dailySlots.size()-1), timezone);

            if (existingDates.contains(startInstantInSlot)) {
                continue;
            }

            createAvailability(request, startInstantInSlot, timezone, availabilities);

            if (spansMultipleUtcDays(startInstantInSlot, endInstantInSlot)) {
                createAvailability(request, endInstantInSlot, timezone, availabilities);
            }
        }
    }

    private Instant calculateSlotStartInstant(LocalDate date, TimeSlot slot, String timezone) {
        return date.atTime(slot.start()).atZone(ZoneId.of(timezone)).toInstant();
    }

    private Instant calculateSlotEndInstant(LocalDate date, TimeSlot slot, String timezone) {
        return date.atTime(slot.end()).atZone(ZoneId.of(timezone)).toInstant();
    }

    private boolean spansMultipleUtcDays(Instant start, Instant end) {
        return !start.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay()
                .equals(end.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay());
    }

    private void createAvailability(DailyAvailabilityRequest request, Instant dateUtc,
                                    String timezone, List<Availability> availabilities) {
        Availability availability = new Availability();
        availability.setStylistId(request.getStylistId());
        availability.setServiceProviderId(request.getServiceProviderId());
        availability.setDateUtc(dateUtc);
        availability.setOriginalTimeZone(timezone);
        availabilities.add(availability);
    }

    private void createAvailability(MonthlyAvailabilityRequest request, Instant dateUtc,
                                    String timezone, List<Availability> availabilities) {
        Availability availability = new Availability();
        availability.setStylistId(request.getStylistId());
        availability.setServiceProviderId(request.getServiceProviderId());
        availability.setDateUtc(dateUtc);
        availability.setOriginalTimeZone(timezone);
        availabilities.add(availability);
    }

    private void processTimeSlots(DailyAvailabilityRequest request, String timezone,
                                  long timeDifferenceMinutes, Set<Instant> existingDates,
                                  List<Availability> availabilities) {
        if (existingDates.contains(request.getDate().atStartOfDay(ZoneId.of(timezone)).toInstant())) {
            return;
        }

        processSlotsForDate(request.getDate(), request.getTimeSlots(), timezone, timeDifferenceMinutes, availabilities);
    }

    private void processTimeSlots(MonthlyAvailabilityRequest request, String timezone,
                                  long timeDifferenceMinutes, Set<Instant> existingDates,
                                  List<Availability> availabilities) {
        for (LocalDate date = request.getStartDate(); !date.isAfter(request.getEndDate()); date = date.plusDays(1)) {
            if (existingDates.contains(date.atStartOfDay(ZoneId.of(timezone)).toInstant())) {
                continue;
            }

            List<TimeSlot> dailySlots = getEffectiveTimeSlots(request, date);
            if (dailySlots.isEmpty()) {
                continue;
            }

            processSlotsForDate(date, dailySlots, timezone, timeDifferenceMinutes, availabilities);
        }
    }

    private List<TimeSlot> getEffectiveTimeSlots(MonthlyAvailabilityRequest request, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<TimeSlot> dailySlots = request.getWeeklySchedule().getOrDefault(dayOfWeek, new ArrayList<>());

        for (MonthlyAvailabilityRequest.Exemption exemption : request.getExemptions()) {
            if (exemption.getDate().equals(date)) {
                return exemption.isExclude() ? Collections.emptyList() : exemption.getOverrideSlots();
            }
        }
        return dailySlots;
    }

    private void processSlotsForDate(LocalDate date, List<TimeSlot> dailySlots, String timezone,
                                     long timeDifferenceMinutes, List<Availability> availabilities) {
        for (TimeSlot slot : dailySlots) {
            List<Availability.TimeSlot> utcSlots = convertToUtcWithSplit(slot, date, ZoneId.of(timezone));

            if (utcSlots.size() == 1) {
                addSingleSlotToAvailabilities(date, utcSlots.get(0), timezone, availabilities);
            } else {
                addSplitSlotsToAvailabilities(date, utcSlots, timeDifferenceMinutes, availabilities, timezone);
            }
        }
    }

    private void addSingleSlotToAvailabilities(LocalDate date, Availability.TimeSlot slot,
                                               String timezone, List<Availability> availabilities) {
        availabilities.stream()
                .filter(avail -> matchesDate(avail, date, timezone))
                .forEach(avail -> avail.getTimeSlots().add(slot));
    }

    private void addSplitSlotsToAvailabilities(LocalDate date, List<Availability.TimeSlot> slots,
                                               long timeDifferenceMinutes,
                                               List<Availability> availabilities, String timezone) {
        for (int i = 0; i < availabilities.size(); i++) {
            if (matchesDate(availabilities.get(i), date, timezone)) {
                if (timeDifferenceMinutes < 0) {
                    availabilities.get(i-1).getTimeSlots().add(slots.get(0));
                    availabilities.get(i).getTimeSlots().add(slots.get(1));
                } else {
                    availabilities.get(i).getTimeSlots().add(slots.get(0));
                    availabilities.get(i+1).getTimeSlots().add(slots.get(1));
                }
            }
        }
    }

    private boolean matchesDate(Availability availability, LocalDate date, String timezone) {
        return availability.getDateUtc().atZone(ZoneOffset.UTC).toLocalDate()
                .equals(date.atStartOfDay(ZoneId.of(timezone)).toLocalDate());
    }

    private void mergeAllTimeSlots(List<Availability> availabilities) {
        availabilities.forEach(avail ->
                avail.setTimeSlots(mergeTimeSlots(Collections.emptyList(), avail.getTimeSlots()))
        );
    }

    private List<Availability.TimeSlot> convertToUtcWithSplit(TimeSlot slot, LocalDate date, ZoneId zoneId) {
        Instant startInstant = date.atTime(slot.start()).atZone(zoneId).toInstant();
        Instant endInstant = date.atTime(slot.end()).atZone(zoneId).toInstant();

        LocalDate startUtcDate = startInstant.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endUtcDate = endInstant.atZone(ZoneOffset.UTC).toLocalDate();

        if (startUtcDate.equals(endUtcDate)) {
            return List.of(createTimeSlot(startInstant, endInstant));
        } else {
            return splitAcrossMidnight(startInstant, endInstant);
        }
    }

    private Availability.TimeSlot createTimeSlot(Instant start, Instant end) {
        return new Availability.TimeSlot(
                LocalTime.from(start.atZone(ZoneOffset.UTC)),
                LocalTime.from(end.atZone(ZoneOffset.UTC))
        );
    }

    private List<Availability.TimeSlot> splitAcrossMidnight(Instant start, Instant end) {
        return List.of(
                createTimeSlot(start, LocalDateTime.of(
                        start.atZone(ZoneOffset.UTC).toLocalDate().plusDays(1),
                        LocalTime.MIDNIGHT
                ).toInstant(ZoneOffset.UTC)),
                createTimeSlot(
                        LocalDateTime.of(
                                end.atZone(ZoneOffset.UTC).toLocalDate(),
                                LocalTime.MIDNIGHT
                        ).toInstant(ZoneOffset.UTC),
                        end
                )
        );
    }

    private void notifyAvailabilityUpdate(DailyAvailabilityRequest request) {
        messagingService.sendMessage(
                MessagingService.STYLIST_EXCHANGE,
                MessagingService.STYLISTS_AVAILABILITIES_DAILY_ADD_ROUTING_KEY,
                GlamoorJsonMapper.toJson(request)
        );
    }

    private void notifyAvailabilityUpdate(MonthlyAvailabilityRequest request) {
        messagingService.sendMessage(
                MessagingService.STYLIST_EXCHANGE,
                MessagingService.STYLISTS_AVAILABILITIES_MONTHLY_ADD_ROUTING_KEY,
                GlamoorJsonMapper.toJson(request)
        );
    }

    private List<Availability.TimeSlot> mergeTimeSlots(
            List<Availability.TimeSlot> existingSlots,
            List<Availability.TimeSlot> newSlots) {
        List<Availability.TimeSlot> allSlots = new ArrayList<>(existingSlots);
        allSlots.addAll(newSlots);

        allSlots.sort(Comparator.comparing(Availability.TimeSlot::getStartTime));

        List<Availability.TimeSlot> mergedSlots = new ArrayList<>();
        for (Availability.TimeSlot slot : allSlots) {
            if (mergedSlots.isEmpty()) {
                mergedSlots.add(slot);
            } else {
                Availability.TimeSlot lastSlot = mergedSlots.get(mergedSlots.size() - 1);
                if (slot.getStartTime().isBefore(lastSlot.getEndTime()) || slot.getStartTime().equals(lastSlot.getEndTime())) {
                    lastSlot.setEndTime(slot.getEndTime().isAfter(lastSlot.getEndTime()) ? slot.getEndTime() : lastSlot.getEndTime());
                } else {
                    mergedSlots.add(slot);
                }
            }
        }
        return mergedSlots;
    }
}
