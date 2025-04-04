package uk.co.glamoor.stylists.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.glamoor.stylists.model.ServiceCategory;
import uk.co.glamoor.stylists.service.ServiceCategoryService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/stylists/service-categories")
@Validated
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;
    
    public ServiceCategoryController(ServiceCategoryService serviceCategoryService) {
    	this.serviceCategoryService = serviceCategoryService;
    }

    @Value("${static.images.path:/app/images/}")
    private String imagesPath;

    @GetMapping("/images/{category}/{fileName:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String category,
            @PathVariable String fileName) {

        try {
            Path filePath = Paths.get(imagesPath, category, fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ServiceCategory> addCategory(@RequestBody @Valid ServiceCategory category) {
        return ResponseEntity.ok(serviceCategoryService.addCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceCategory> getCategory(@PathVariable String id) {
        return ResponseEntity.ok(serviceCategoryService.getCategory(id));
    }

    @GetMapping
    public ResponseEntity<List<ServiceCategory>> getCategories(
    		@RequestParam(required = false, defaultValue = "0") int offset) {
        return ResponseEntity.ok(serviceCategoryService.getCategories(offset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategory> updateCategory(@PathVariable String id, @RequestBody @Valid ServiceCategory category) {
        return ResponseEntity.ok(serviceCategoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        serviceCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

