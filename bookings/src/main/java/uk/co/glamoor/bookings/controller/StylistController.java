package uk.co.glamoor.bookings.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.glamoor.bookings.exception.EntityNotFoundException;
import uk.co.glamoor.bookings.exception.EntityType;
import uk.co.glamoor.bookings.model.ServiceProvider;
import uk.co.glamoor.bookings.model.Stylist;
import uk.co.glamoor.bookings.model.StylistServiceSpecification;
import uk.co.glamoor.bookings.repository.StylistRepository;

import java.util.List;

@RestController
@RequestMapping("/api/bookings/stylists")
@Validated
public class StylistController {

    private final StylistRepository stylistRepository;

    public StylistController(StylistRepository stylistRepository) {
        this.stylistRepository = stylistRepository;
    }

    @GetMapping("/{stylistId}/serviceProviders")
    public ResponseEntity<List<ServiceProvider>> getServiceProviders(
            @PathVariable @NotBlank(message = "Stylist ID must not be blank.") String stylistId) {

        Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
                () -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
        
        return ResponseEntity.ok(stylist.getServiceProviders());
    }

    @GetMapping("/{stylistId}/serviceSpecifications")
    public ResponseEntity<List<StylistServiceSpecification>> getServiceSpecifications(
            @PathVariable @NotBlank(message = "Stylist ID must not be blank.") String stylistId) {

        Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
                () -> new EntityNotFoundException(stylistId, EntityType.STYLIST));

        return ResponseEntity.ok(stylist.getServiceSpecifications());
    }
}
