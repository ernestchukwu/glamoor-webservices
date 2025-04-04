package uk.co.glamoor.bookings.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.co.glamoor.bookings.dto.response.TimeSlotResponse;
import uk.co.glamoor.bookings.dto.request.DailyAvailabilityRequest;
import uk.co.glamoor.bookings.dto.request.MonthlyAvailabilityRequest;
import uk.co.glamoor.bookings.dto.response.NextAvailabilityResponse;
import uk.co.glamoor.bookings.enums.AvailabilityType;
import uk.co.glamoor.bookings.service.AvailabilityService;
import uk.co.glamoor.bookings.service.StylistService;

@RestController
@RequestMapping("/api/bookings/availabilities")
@Validated
public class AvailabilityController {
	
	private final AvailabilityService availabilityService;
	private final StylistService stylistService;
	
	public AvailabilityController(AvailabilityService availabilityService,
                                  StylistService stylistService) {
		this.availabilityService = availabilityService;
		this.stylistService = stylistService;
    }
	
	@PostMapping("/monthly")
	public ResponseEntity<?> createMonthlyAvailabilities(
			@RequestBody @Valid MonthlyAvailabilityRequest request) {
        
//		stylistService.validateStylistIncludesServiceProvider(
//				request.getStylistId(), request.getServiceProviderId());
		availabilityService.createMonthlyAvailabilities(request);
        return ResponseEntity.ok("Monthly availabilities created successfully.");
        
	}
	
	@PostMapping("/daily")
	public ResponseEntity<?> createDailyAvailabilities(
			@RequestBody @Valid DailyAvailabilityRequest request) {
        
//		stylistService.validateStylistIncludesServiceProvider(
//				request.getStylistId(), request.getServiceProviderId());

		availabilityService.createDailyAvailabilities(request);
        return ResponseEntity.ok("Daily availabilities created successfully.");
        
	}
	
	@DeleteMapping("/{stylistId}/{serviceProviderId}/monthly")
	public ResponseEntity<?> deleteMonthlyAvailability(
			@PathVariable @NotBlank @NotNull String stylistId,
			@PathVariable @NotBlank @NotNull String serviceProviderId,
			@RequestParam @FutureOrPresent(message = "Date must not be in the past.") LocalDate date) {
        
		stylistService.validateStylistIncludesServiceProvider(
				stylistId, serviceProviderId);
		availabilityService.deleteMonthlyAvailabilities(stylistId, serviceProviderId, date);
        return ResponseEntity.ok("Monthly availabilities created successfully.");
        
	}
	
	@DeleteMapping("/{stylistId}/{serviceProviderId}/daily")
	public ResponseEntity<?> deleteDailyAvailability(
			@PathVariable @NotBlank @NotNull String stylistId,
			@PathVariable @NotBlank @NotNull String serviceProviderId,
			@RequestParam @FutureOrPresent(message = "Date must not be in the past.") LocalDate date) {
        
		stylistService.validateStylistIncludesServiceProvider(
				stylistId, serviceProviderId);
		availabilityService.deleteDailyAvailabilities(stylistId, serviceProviderId, date);
        return ResponseEntity.ok("Daily availabilities created successfully.");
        
	}

	@GetMapping("/{serviceProviderId}")
	public ResponseEntity<?> getServiceProviderAvailabilities(
			@PathVariable @NotBlank(message = "Service Provider ID must not be blank.") String serviceProviderId,
			@RequestParam @NotBlank(message = "Stylist ID must not be blank.") String stylistId,
		    @RequestParam @NotNull(message = "Date must not be null.") LocalDate date,
			@RequestParam @NotNull(message = "TimeZone is required.") String timeZone,
		    @RequestParam @Min(value = 1) int duration,
		    @RequestParam @NotNull AvailabilityType availabilityType) {

		switch (availabilityType) {
			case DAILY:
				List<TimeSlotResponse> availableTimeSlots = availabilityService
						.getServiceProvidersDailyAvailabilities(stylistId, serviceProviderId, date, duration, timeZone);

				return ResponseEntity.ok(availableTimeSlots);
			case MONTHLY:
				return ResponseEntity.ok(availabilityService.getServiceProvidersMonthlyAvailabilities(
						stylistId, serviceProviderId, date, duration, timeZone));
			default:
				throw new IllegalArgumentException("Incorrect type.");
		}
	    
	}
	//findNextAvailableDate

	@GetMapping("/{serviceProviderId}/next-available-date")
	public ResponseEntity<NextAvailabilityResponse> getServiceProviderNextAvailability(
			@PathVariable @NotBlank(message = "Service Provider ID must not be blank.") String serviceProviderId,
			@RequestParam @NotBlank(message = "Stylist ID must not be blank.") String stylistId,
			@RequestParam @NotNull(message = "Date must not be null.") LocalDate startDate,
			@RequestParam @NotNull(message = "TimeZone is required.") String timeZone,
			@RequestParam @Min(value = 1) int duration) {

		LocalDate date = availabilityService.findNextAvailableDate(
				stylistId, serviceProviderId, startDate, duration, timeZone);

		NextAvailabilityResponse nextAvailabilityResponse = new NextAvailabilityResponse();
		nextAvailabilityResponse.setDate(date);
		nextAvailabilityResponse.setMonthlyAvailabilities(availabilityService
				.getServiceProvidersMonthlyAvailabilities(stylistId, serviceProviderId, date, duration, timeZone));


		return ResponseEntity.ok(nextAvailabilityResponse);

	}
	
	@PatchMapping("/escrow")
	public ResponseEntity<?> escrowTimeSlot(
			@RequestParam @NotBlank(message = "Service Provider ID must not be blank.") String serviceProviderId,
			@RequestParam @NotBlank(message = "Stylist ID must not be blank.") String stylistId,
		    @RequestParam @NotNull(message = "Date/time must not be null.") LocalDateTime dateTime,
			@RequestParam @NotBlank(message = "TimeZone must not be null.") String timeZone,
		    @RequestParam @Min(value = 1) int duration) {

		availabilityService.escrowSlot(stylistId, serviceProviderId, dateTime, duration, timeZone);
		
	    return ResponseEntity.noContent().build();
	}

	@PatchMapping("/pulse")
	public ResponseEntity<?> pulseTimeSlot(
			@RequestParam @NotBlank(message = "Service Provider ID must not be blank.") String serviceProviderId,
			@RequestParam @NotBlank(message = "Stylist ID must not be blank.") String stylistId,
			@RequestParam @NotNull(message = "Date/time must not be null.") LocalDateTime dateTime,
			@RequestParam @NotBlank(message = "TimeZone must not be null.") String timeZone,
			@RequestParam @Min(value = 1) int duration) {

		availabilityService.sendPulseToSlot(stylistId, serviceProviderId, dateTime, duration, timeZone);

		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/unescrow")
	public ResponseEntity<?> unescrowAvailabilities(
			@RequestParam @NotBlank(message = "Service Provider ID must not be blank.") String serviceProviderId,
			@RequestParam @NotBlank(message = "Stylist ID must not be blank.") String stylistId,
			@RequestParam @NotNull(message = "Date/time must not be null.") LocalDateTime dateTime,
			@RequestParam @NotBlank(message = "TimeZone must not be null.") String timeZone,
		    @RequestParam @Min(value = 1) int duration) {

		availabilityService.unescrowSlot(stylistId, serviceProviderId, dateTime, duration, timeZone);
		
	    return ResponseEntity.noContent().build();
	}
}
