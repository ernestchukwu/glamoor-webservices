package uk.co.glamoor.bookings.service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import uk.co.glamoor.bookings.config.AppConfig;
import uk.co.glamoor.bookings.dto.request.TimeSlot;
import uk.co.glamoor.bookings.dto.response.TimeSlotResponse;
import uk.co.glamoor.bookings.exception.NoAvailableDateException;
import uk.co.glamoor.bookings.mapper.GlamoorJsonMapper;
import uk.co.glamoor.bookings.model.Availability;
import uk.co.glamoor.bookings.dto.request.DailyAvailabilityRequest;
import uk.co.glamoor.bookings.dto.request.MonthlyAvailabilityRequest;
import uk.co.glamoor.bookings.dto.response.MonthlyAvailabilityResponse;
import uk.co.glamoor.bookings.repository.AvailabilityRepository;

@Service
public class AvailabilityService {
	
	private final AvailabilityRepository availabilityRepository;
	private final AvailabilityCreator availabilityCreator;
	private final MessagingService messagingService;
	private final AppConfig appConfig;
		
	public AvailabilityService(AvailabilityRepository availabilityRepository, AvailabilityCreator availabilityCreator,
                               @Lazy MessagingService messagingService,
                               AppConfig appConfig) {
		
		this.availabilityRepository = availabilityRepository;
        this.availabilityCreator = availabilityCreator;
        this.messagingService = messagingService;
        this.appConfig = appConfig;
	}

	public List<TimeSlotResponse> getServiceProvidersDailyAvailabilities(
			String stylistId,
			String serviceProviderId,
			LocalDate localDate,
			int duration,
			String timeZone) {

		ZoneId zone = ZoneId.of(timeZone);
		Instant startOfDayUtc = localDate.atStartOfDay(zone).minusSeconds(1).toInstant();
		Instant endOfDayUtc = localDate.atTime(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository
				.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
						stylistId,
						serviceProviderId,
						startOfDayUtc,
						endOfDayUtc
				);

		if (availabilities.isEmpty()) {
			throw new NoAvailableDateException("No availability found for the selected date");
		}

		List<TimeSlotResponse> allSlots = new ArrayList<>();

		for (Availability availability : availabilities) {
			List<Availability.TimeSlot> availableSlots = availability.getAvailableSlots(duration);

			for (Availability.TimeSlot slot : availableSlots) {

				Instant slotInstant = availability.getDateUtc().atZone(ZoneOffset.UTC)
						.withHour(slot.getStartTime().getHour()).withMinute(slot.getStartTime().getMinute()).toInstant();

				if (slotInstant.isBefore(startOfDayUtc) || slotInstant.isAfter(endOfDayUtc))
					continue;

				LocalDateTime startLocalDateTime = LocalDateTime.of(LocalDate.now(), slot.getStartTime());
				LocalDateTime endLocalDateTime = LocalDateTime.of(LocalDate.now(), slot.getEndTime());

				LocalTime startLocalTime = startLocalDateTime.atZone(zone).toLocalTime();
				LocalTime endLocalTime = endLocalDateTime.atZone(zone).toLocalTime();

				allSlots.add(new TimeSlotResponse(
						startLocalTime,
						endLocalTime));
			}
		}

		return allSlots;
	}

	public LocalDate findNextAvailableDate(String stylistId, String serviceProviderId,
										   LocalDate startDate, int duration, String timeZone) {

		LocalDate current = startDate;

		while (current.isBefore(startDate.plusDays(appConfig.getFutureBookingDaysExtent()))) {
			LocalDate startOfMonth = current.equals(startDate) ? startDate : current.withDayOfMonth(1);
			LocalDate endOfMonth = current.withDayOfMonth(current.lengthOfMonth());

			List<Availability> availabilities = availabilityRepository
					.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
							stylistId, serviceProviderId,
							startOfMonth.atStartOfDay(ZoneOffset.UTC).toInstant(),
							endOfMonth.atStartOfDay(ZoneOffset.UTC).toInstant());

			for (Availability availability : availabilities) {
				List<Availability.TimeSlot> availableSlots = availability.getAvailableSlots(duration);
				if (!availableSlots.isEmpty()) {
					return availability.getDateUtc().atZone(ZoneId.of(timeZone)).toLocalDate();
				}
			}

			current = current.plusMonths(1).withDayOfMonth(1);
		}

		throw new NoAvailableDateException("No available dates found for selected service provider starting from "
				+ startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + ".");
	}
	
	public List<MonthlyAvailabilityResponse> getServiceProvidersMonthlyAvailabilities(String stylistId,
			String serviceProviderId, LocalDate date, int duration, String timeZone) {

		ZoneId zone = ZoneId.of(timeZone);
		Instant startOfMonthUtc = date.withDayOfMonth(1).atStartOfDay(zone).minusSeconds(1).toInstant();
		Instant endOfMonthUtc = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
				stylistId, serviceProviderId, startOfMonthUtc, endOfMonthUtc);


		return availabilities.stream()
				.map(availability -> new MonthlyAvailabilityResponse(
						availability.getDateUtc().atZone(ZoneId.of(timeZone)).getDayOfMonth(),
								!availability.getAvailableSlots(duration).isEmpty()
						)).toList();

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
	
	public void createDailyAvailabilities(DailyAvailabilityRequest request) {

		availabilityCreator.createDailyAvailabilities(request);

	}

	public void createMonthlyAvailabilities(MonthlyAvailabilityRequest request) {
		availabilityCreator.createMonthlyAvailabilities(request);
	}

	public void deleteDailyAvailabilities(String stylistId, String serviceProviderId, LocalDate date) {
		
		availabilityRepository.deleteByStylistIdAndServiceProviderIdAndDateUtc(
				stylistId, serviceProviderId, date);
		
		messagingService.sendMessage(MessagingService.STYLIST_EXCHANGE,
				MessagingService.STYLISTS_AVAILABILITIES_UNBOOK_ROUTING_KEY, 
				GlamoorJsonMapper.toJson(date),
				Map.of("stylistId", stylistId, "serviceProviderId", serviceProviderId));
	}
	public void deleteMonthlyAvailabilities(String stylistId, String serviceProviderId, LocalDate date) {
		LocalDate startDate = date.withDayOfMonth(1);
		LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
		
		availabilityRepository
        .deleteByStylistIdAndServiceProviderIdAndDateUtcBetween(stylistId, serviceProviderId, startDate, endDate);
		
		messagingService.sendMessage(MessagingService.STYLIST_EXCHANGE,
				MessagingService.STYLISTS_AVAILABILITIES_UNBOOK_ROUTING_KEY, 
				GlamoorJsonMapper.toJson(date),
				Map.of("stylistId", stylistId, "serviceProviderId", serviceProviderId));
	}
	
	public void bookEscrowedSlot(String stylistId, String serviceProviderId, LocalDateTime dateTime,
								 int duration, String timeZone) {
		ZoneId zone = ZoneId.of(timeZone);
		Instant dateTimeUtc = dateTime.atZone(zone).toInstant();
		Instant startOfDayUtc = dateTime.with(LocalTime.MIN).atZone(zone).minusSeconds(1).toInstant();
		Instant endOfDayUtc = dateTime.with(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository
				.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
						stylistId,
						serviceProviderId,
						startOfDayUtc,
						endOfDayUtc
				);

		LocalTime slotTime = dateTimeUtc.atZone(ZoneOffset.UTC).toLocalTime();

		for (Availability availability : availabilities) {

			if (!availability.getUnavailableSlots().stream()
					.filter(slot -> slot.getStatus() == Availability.Status.ESCROW && slot.getStartTime()
							.equals(slotTime) && slot.getEndTime()
							.equals(slotTime.plusMinutes(duration))).toList().isEmpty()) {
				availability.bookEscrowedSlot(slotTime, duration);
				break;
			}
		}

		availabilityRepository.saveAll(availabilities);
		
	}
	
	public void unbookSlot(String stylistId, String serviceProviderId, LocalDateTime dateTime,
						   int duration, String timeZone) {

		ZoneId zone = ZoneId.of(timeZone);
		Instant dateTimeUtc = dateTime.atZone(zone).toInstant();
		Instant startOfDayUtc = dateTime.with(LocalTime.MIN).atZone(zone).minusSeconds(1).toInstant();
		Instant endOfDayUtc = dateTime.with(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository
				.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
						stylistId,
						serviceProviderId,
						startOfDayUtc,
						endOfDayUtc
				);

		LocalTime slotTime = dateTimeUtc.atZone(ZoneOffset.UTC).toLocalTime();

		for (Availability availability : availabilities) {

			if (!availability.getUnavailableSlots().stream()
					.filter(slot -> slot.getStatus() == Availability.Status.BOOKED && slot.getStartTime()
							.equals(slotTime) && slot.getEndTime()
							.equals(slotTime.plusMinutes(duration))).toList().isEmpty()) {
				availability.unbookSlot(dateTimeUtc.atZone(ZoneOffset.UTC).toLocalTime(), duration);
				break;
			}
		}

		availabilityRepository.saveAll(availabilities);

		messagingService.sendMessage(MessagingService.STYLIST_EXCHANGE,
				MessagingService.STYLISTS_AVAILABILITIES_UNBOOK_ROUTING_KEY,
				GlamoorJsonMapper.toJson(new TimeSlot(slotTime,
						slotTime.plusMinutes(duration))),
				Map.of("stylistId", stylistId,
						"date", dateTime.toLocalDate(), "serviceProviderId", serviceProviderId));
	}
	
	public void escrowSlot(String stylistId, String serviceProviderId, LocalDateTime dateTime, int duration,
						   String timeZone) {

		ZoneId zone = ZoneId.of(timeZone);
		Instant dateTimeUtc = dateTime.atZone(zone).toInstant();
		Instant startOfDayUtc = dateTime.with(LocalTime.MIN).atZone(zone).minusSeconds(1).toInstant();
		Instant endOfDayUtc = dateTime.with(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository
				.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
						stylistId,
						serviceProviderId,
						startOfDayUtc,
						endOfDayUtc
				);

		LocalTime slotTime = dateTimeUtc.atZone(ZoneOffset.UTC).toLocalTime();

		for (Availability availability : availabilities) {

			if (!availability.getAvailableSlots(duration).stream()
					.filter(slot -> slot.getStartTime()
							.equals(slotTime) && slot.getEndTime()
							.equals(slotTime.plusMinutes(duration))).toList().isEmpty()) {
				availability.escrowSlot(slotTime, duration, appConfig.getTimeSlotEscrowDurationSeconds());
				break;
			}
		}

		availabilityRepository.saveAll(availabilities);
		
	}

	public void sendPulseToSlot(String stylistId, String serviceProviderId, LocalDateTime dateTime, int duration,
								String timeZone) {
		ZoneId zone = ZoneId.of(timeZone);
		Instant dateTimeUtc = dateTime.atZone(zone).toInstant();
		Instant startOfDayUtc = dateTime.with(LocalTime.MIN).atZone(zone).minusSeconds(1).toInstant();
		Instant endOfDayUtc = dateTime.with(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository
				.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
						stylistId,
						serviceProviderId,
						startOfDayUtc,
						endOfDayUtc
				);

		LocalTime slotTime = dateTimeUtc.atZone(ZoneOffset.UTC).toLocalTime();

		for (Availability availability : availabilities) {

			if (!availability.getUnavailableSlots().stream()
					.filter(slot -> slot.getStatus() == Availability.Status.ESCROW && slot.getStartTime()
							.equals(slotTime) && slot.getEndTime()
							.equals(slotTime.plusMinutes(duration))).toList().isEmpty()) {
				availability.pulseEscrowedSlot(slotTime, duration, appConfig.getTimeSlotEscrowDurationSeconds());
				break;
			}
		}

		availabilityRepository.saveAll(availabilities);

	}
	
	public void unescrowSlot(String stylistId, String serviceProviderId, LocalDateTime dateTime, int duration,
							 String timeZone) {

		ZoneId zone = ZoneId.of(timeZone);
		Instant dateTimeUtc = dateTime.atZone(zone).toInstant();
		Instant startOfDayUtc = dateTime.with(LocalTime.MIN).atZone(zone).minusSeconds(1).toInstant();
		Instant endOfDayUtc = dateTime.with(LocalTime.MAX).atZone(zone).plusSeconds(1).toInstant();

		List<Availability> availabilities = availabilityRepository
				.findByStylistIdAndServiceProviderIdAndDateUtcBetween(
						stylistId,
						serviceProviderId,
						startOfDayUtc,
						endOfDayUtc
				);

		LocalTime slotTime = dateTimeUtc.atZone(ZoneOffset.UTC).toLocalTime();

		for (Availability availability : availabilities) {

			if (!availability.getUnavailableSlots().stream()
					.filter(slot -> slot.getStatus() == Availability.Status.ESCROW && slot.getStartTime()
							.equals(slotTime) && slot.getEndTime()
							.equals(slotTime.plusMinutes(duration))).toList().isEmpty()) {
				availability.unescrowSlot(slotTime, duration);
				break;
			}
		}

		availabilityRepository.saveAll(availabilities);
	}
	
	
}
