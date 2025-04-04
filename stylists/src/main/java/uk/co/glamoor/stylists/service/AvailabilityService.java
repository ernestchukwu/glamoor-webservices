package uk.co.glamoor.stylists.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import uk.co.glamoor.stylists.model.Availability;
import uk.co.glamoor.stylists.model.availability_request.DailyAvailabilityRequest;
import uk.co.glamoor.stylists.model.availability_request.MonthlyAvailabilityRequest;
import uk.co.glamoor.stylists.model.availability_request.TimeSlot;
import uk.co.glamoor.stylists.repository.AvailabilityRepository;

@Service
public class AvailabilityService {
	
	private final AvailabilityRepository availabilityRepository;
	
	public AvailabilityService(AvailabilityRepository availabilityRepository) {
		this.availabilityRepository = availabilityRepository;
	}

	private List<Availability.TimeSlot> mergeTimeSlots(
	        List<Availability.TimeSlot> existingSlots,
	        List<Availability.TimeSlot> newSlots) {
	    List<Availability.TimeSlot> allSlots = new ArrayList<>(existingSlots);
	    allSlots.addAll(newSlots);

	    allSlots.sort(Comparator.comparing(Availability.TimeSlot::getStart));

	    List<Availability.TimeSlot> mergedSlots = new ArrayList<>();
	    for (Availability.TimeSlot slot : allSlots) {
	        if (mergedSlots.isEmpty()) {
	            mergedSlots.add(slot);
	        } else {
	            Availability.TimeSlot lastSlot = mergedSlots.get(mergedSlots.size() - 1);
	            if (slot.getStart().isBefore(lastSlot.getEnd()) || slot.getStart().equals(lastSlot.getEnd())) {
	                lastSlot.setEnd(slot.getEnd().isAfter(lastSlot.getEnd()) ? slot.getEnd() : lastSlot.getEnd());
	            } else {
	                mergedSlots.add(slot);
	            }
	        }
	    }
	    return mergedSlots;
	}
	
	public void createDailyAvailabilities(DailyAvailabilityRequest request) {
	    Optional<Availability> existingAvailability = availabilityRepository
	            .findByStylistIdAndServiceProviderIdAndDate(request.getStylistId(),
	                    request.getServiceProviderId(), request.getDate());

	    List<Availability.TimeSlot> newTimeSlots = request.getTimeSlots().stream()
	            .map(slot -> new Availability.TimeSlot(slot.getStart(), slot.getEnd()))
	            .toList();

	    if (existingAvailability.isPresent()) {
	        Availability availability = existingAvailability.get();
	        List<Availability.TimeSlot> mergedTimeSlots = mergeTimeSlots(availability.getTimeSlots(), newTimeSlots);
	        availability.setTimeSlots(mergedTimeSlots);
	        availabilityRepository.save(availability);
	    } else {
	        Availability newAvailability = new Availability();
	        newAvailability.setStylistId(request.getStylistId());
	        newAvailability.setServiceProviderId(request.getServiceProviderId());
	        newAvailability.setDate(request.getDate());
	        newAvailability.setTimeSlots(mergeTimeSlots(Collections.emptyList(), newTimeSlots));
	        availabilityRepository.save(newAvailability);
	    }
	}
	
	public void createMonthlyAvailabilities(MonthlyAvailabilityRequest request) {

	    request.validateDates();

	    LocalDate startDate = request.getStartDate();
	    LocalDate endDate = request.getEndDate();
	    
	    List<Availability> existingAvailabilities = availabilityRepository
                .findByStylistIdAndServiceProviderIdAndDateBetween(request.getStylistId(), 
                		request.getServiceProviderId(), startDate, endDate);


	    Set<LocalDate> existingDates = existingAvailabilities.stream()
	            .map(Availability::getDate)
	            .collect(Collectors.toSet());

	    List<Availability> availabilities = new ArrayList<>();

	    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
	        
	    	if (existingDates.contains(date)) {
	            continue;
	        }
	    	
	        DayOfWeek dayOfWeek = date.getDayOfWeek();

	        List<TimeSlot> dailySlots = request.getWeeklySchedule().getOrDefault(dayOfWeek, new ArrayList<>());

	        for (MonthlyAvailabilityRequest.Exemption exemption : request.getExemptions()) {
	            if (exemption.getDate().equals(date)) {
	                if (exemption.isExclude()) {
	                    dailySlots.clear();
	                } else if (exemption.isOverride()) {
	                    dailySlots = exemption.getOverrideSlots();
	                }
	                break;
	            }
	        }

	        if (dailySlots.isEmpty()) {
	            continue;
	        }

	        List<Availability.TimeSlot> timeSlots = dailySlots.stream()
	                .map(slot -> new Availability.TimeSlot(slot.getStart(), slot.getEnd()))
	                .toList();

	        Availability availability = new Availability();
            availability.setStylistId(request.getStylistId());
            availability.setServiceProviderId(request.getServiceProviderId());
            availability.setDate(date);
            availability.setTimeSlots(mergeTimeSlots(Collections.emptyList(), timeSlots));
            availabilities.add(availability);
	    }

	    availabilityRepository.saveAll(availabilities);
	}
	
	public void bookEscrowedSlot(String stylistId, String serviceProviderId, LocalDate date, LocalTime time, int duration) {
		
		Availability availability = availabilityRepository.findByStylistIdAndServiceProviderIdAndDate(
				stylistId, serviceProviderId, date).orElseThrow();
		
		availability.bookEscrowedSlot(time, duration);
		
		availabilityRepository.save(availability);
		
	}
	
	public void deleteDailyAvailabilities(String stylistId, String serviceProviderId, LocalDate date) {
		availabilityRepository.deleteByStylistIdAndServiceProviderIdAndDate(
				stylistId, serviceProviderId, date);
	}
	public void deleteMonthlyAvailabilities(String stylistId, String serviceProviderId, LocalDate date) {
		LocalDate startDate = date.withDayOfMonth(1);
		LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
		
		availabilityRepository
        .deleteByStylistIdAndServiceProviderIdAndDateBetween(stylistId, serviceProviderId, startDate, endDate);
	}
	
	public void bookSlot(String stylistId, String serviceProviderId, LocalDate date, TimeSlot slot) {
		LocalTime time = slot.getStart();
		int duration = (int) slot.getDuration();
		
		Availability availability = availabilityRepository.findByStylistIdAndServiceProviderIdAndDate(
				stylistId, serviceProviderId, date).orElseThrow();
		
		availability.escrowSlot(time, duration);
		
		availabilityRepository.save(availability);
		
	}
	
	public void unbookSlot(String stylistId, String serviceProviderId, LocalDate date, TimeSlot slot) {
		LocalTime time = slot.getStart();
		int duration = (int) slot.getDuration();
		
		Availability availability = availabilityRepository.findByStylistIdAndServiceProviderIdAndDate(
				stylistId, serviceProviderId, date).orElseThrow();
		
		availability.unbookSlot(time, duration);
		
		availabilityRepository.save(availability);
	}
}
