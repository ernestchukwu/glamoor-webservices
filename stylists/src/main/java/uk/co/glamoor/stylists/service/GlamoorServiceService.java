package uk.co.glamoor.stylists.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.glamoor.stylists.config.StylistsConfig;
import uk.co.glamoor.stylists.mapper.GlamoorJsonMapper;
import uk.co.glamoor.stylists.model.GlamoorService;
import uk.co.glamoor.stylists.model.ServiceCategory;
import uk.co.glamoor.stylists.repository.GlamoorServiceRepository;
import uk.co.glamoor.stylists.repository.ServiceCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GlamoorServiceService {
	
    private final GlamoorServiceRepository glamoorServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final MessagingService messagingService;
    private final StylistsConfig stylistsConfig;
    
    public GlamoorServiceService(GlamoorServiceRepository glamoorServiceRepository,
    		ServiceCategoryRepository serviceCategoryRepository,
    		MessagingService messagingService, StylistsConfig stylistsConfig) {
    	
    	this.glamoorServiceRepository = glamoorServiceRepository;
    	this.serviceCategoryRepository = serviceCategoryRepository;
    	this.messagingService = messagingService;
    	this.stylistsConfig = stylistsConfig;
    }

    public GlamoorService addGlamoorService(GlamoorService service) {
    	Optional<ServiceCategory> category = serviceCategoryRepository.findById(service.getCategoryId());
        if (category.isEmpty()) {
            throw new RuntimeException("Category ID " + service.getCategoryId() + " does not exist.");
        }
        if (serviceCategoryRepository.existsByName(service.getName())) {
            throw new RuntimeException("Service name already exists.");
        }
        return glamoorServiceRepository.save(service);
    }

    public GlamoorService getGlamoorService(String id) {
        return glamoorServiceRepository.findById(id)
                .orElseThrow(
                		() -> new RuntimeException("Service not found with id: " + id));
    }

    public List<GlamoorService> getServices(int offset) {
        return glamoorServiceRepository.findAll(PageRequest.of(
        		offset / stylistsConfig.getServiceRequestBatchSize(), stylistsConfig.getServiceRequestBatchSize())).getContent();
    }

    public GlamoorService updateService(String id, GlamoorService service) {
    	GlamoorService existingService = getGlamoorService(id);
        existingService.setName(service.getName());
        existingService.setDescription(service.getDescription());
        existingService.setCategoryId(service.getCategoryId());
        existingService = glamoorServiceRepository.save(existingService);
        
        messagingService.sendMessage(MessagingService.BOOKING_EXCHANGE, 
        		MessagingService.BOOKINGS_SERVICES_UPDATE_ROUTING_KEY, 
        		GlamoorJsonMapper.toJson(existingService));
        
        return existingService;
    }

    public void deleteService(String id) {
    	GlamoorService service = getGlamoorService(id);
        glamoorServiceRepository.delete(service);
    }
    
    public List<GlamoorService> getServicesByCategory(String categoryId, int offset) {
    	if (categoryId != null && !categoryId.isEmpty()) {
            return glamoorServiceRepository.findByCategoryId(categoryId, 
            		PageRequest.of(offset / stylistsConfig.getServiceRequestBatchSize(),
                            stylistsConfig.getServiceRequestBatchSize()))
            		.getContent();
        } else {
            return glamoorServiceRepository.findAll(PageRequest.of(
            		offset / stylistsConfig.getServiceRequestBatchSize(),
                            stylistsConfig.getServiceRequestBatchSize()))
            		.getContent();
        }
    }
}

