package uk.co.glamoor.stylists.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.glamoor.stylists.model.GlamoorService;
import uk.co.glamoor.stylists.service.GlamoorServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/stylists/services")
@Validated
public class GlamoorServiceController {

    private final GlamoorServiceService glamoorServiceService;

    public GlamoorServiceController(GlamoorServiceService glamoorServiceService) {
    	this.glamoorServiceService = glamoorServiceService;
    }

    @PostMapping
    public ResponseEntity<GlamoorService> addService(@RequestBody @Valid  GlamoorService glamoorService) {
        return ResponseEntity.ok(glamoorServiceService.addGlamoorService(glamoorService));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlamoorService> getService(@PathVariable String id) {
        return ResponseEntity.ok(glamoorServiceService.getGlamoorService(id));
    }

    @GetMapping
    public ResponseEntity<List<GlamoorService>> getServices(
    		@RequestParam(required = false, defaultValue = "0") int offset) {
        return ResponseEntity.ok(glamoorServiceService.getServices(offset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlamoorService> updateService(@PathVariable String id, @RequestBody @Valid  GlamoorService glamoorService) {
        return ResponseEntity.ok(glamoorServiceService.updateService(id, glamoorService));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable String id) {
        glamoorServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<GlamoorService>> getServicesByCategory(@PathVariable String categoryId,
    		@RequestParam(required = false, defaultValue = "0") int offset) {
        List<GlamoorService> services = glamoorServiceService.getServicesByCategory(categoryId, offset);
        return ResponseEntity.ok(services);
    }
}

