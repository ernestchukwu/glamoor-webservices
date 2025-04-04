package uk.co.glamoor.stylists.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.glamoor.stylists.config.StylistsConfig;
import uk.co.glamoor.stylists.model.ServiceCategory;
import uk.co.glamoor.stylists.repository.ServiceCategoryRepository;

import java.util.List;

@Service
public class ServiceCategoryService {

	private final StylistsConfig stylistsConfig;
	
    private final ServiceCategoryRepository serviceCategoryRepository;
    
    public ServiceCategoryService(
    		ServiceCategoryRepository serviceCategoryRepository,
    		StylistsConfig stylistsConfig) {
    	this.serviceCategoryRepository = serviceCategoryRepository;
    	this.stylistsConfig = stylistsConfig;
    }

    public ServiceCategory addCategory(ServiceCategory category) {
    	if (serviceCategoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category name already exists.");
        }
        return serviceCategoryRepository.save(category);
        
    }

    public ServiceCategory getCategory(String id) {
        return serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public List<ServiceCategory> getCategories(int offset) {
        return serviceCategoryRepository.findAll(PageRequest.of(
        		offset / stylistsConfig.getServiceCategoryRequestBatchSize(),
                stylistsConfig.getServiceCategoryRequestBatchSize())).getContent();
    }

    public ServiceCategory updateCategory(String id, ServiceCategory category) {
        ServiceCategory existingCategory = getCategory(id);
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setImages(category.getImages());
        return serviceCategoryRepository.save(existingCategory);
    }

    public void deleteCategory(String id) {
        ServiceCategory category = getCategory(id);
        serviceCategoryRepository.delete(category);
    }
}

