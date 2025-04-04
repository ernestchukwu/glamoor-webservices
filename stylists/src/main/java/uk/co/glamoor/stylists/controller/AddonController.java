package uk.co.glamoor.stylists.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.glamoor.stylists.model.Addon;
import uk.co.glamoor.stylists.service.AddonService;

import java.util.List;

@RestController
@RequestMapping("/api/stylists/addons")
@Validated
public class AddonController {

    private final AddonService addonService;
    
    public AddonController(AddonService addonService) {
    	this.addonService = addonService;
    }

    @PostMapping
    public ResponseEntity<Addon> addAddon(@RequestBody @Valid Addon addon) {
        return ResponseEntity.ok(addonService.addAddon(addon));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Addon> getAddon(@PathVariable String id) {
        return ResponseEntity.ok(addonService.getAddon(id));
    }

    @GetMapping
    public ResponseEntity<List<Addon>> getAddons(
    		@RequestParam(required = false, defaultValue = "0") int offset) {
        return ResponseEntity.ok(addonService.getAddons(offset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Addon> updateAddon(@PathVariable String id, @RequestBody @Valid Addon addon) {
        return ResponseEntity.ok(addonService.updateAddon(id, addon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddon(@PathVariable String id) {
        addonService.deleteAddon(id);
        return ResponseEntity.noContent().build();
    }
}

