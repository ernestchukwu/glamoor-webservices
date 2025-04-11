package uk.co.glamoor.bookings.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import uk.co.glamoor.bookings.repository.CustomStylistRepository;
import uk.co.glamoor.bookings.repository.StylistRepository;
import uk.co.glamoor.bookings.model.GlamoorService;
import uk.co.glamoor.bookings.exception.EntityNotFoundException;
import uk.co.glamoor.bookings.exception.EntityType;
import uk.co.glamoor.bookings.model.HomeServiceSpecification;
import uk.co.glamoor.bookings.model.ServiceProvider;
import uk.co.glamoor.bookings.model.Stylist;
import uk.co.glamoor.bookings.model.StylistServiceSpecification;

@Service
public class StylistService {
	
	private final StylistRepository stylistRepository;
	private final CustomStylistRepository customStylistRepository;
	
	public StylistService (StylistRepository stylistRepository,
			CustomStylistRepository customStylistRepository) {
		this.stylistRepository = stylistRepository;
		this.customStylistRepository = customStylistRepository;
	}

	public void validateStylistIncludesServiceProvider(String stylistId, String serviceProviderId) {
		if (!stylistRepository.existsByIdAndServiceProviders_Id(stylistId, serviceProviderId)) {
			throw new EntityNotFoundException("Stylist or Stylist-ServiceProvider not found for "
					+ "[stylistId: "+stylistId+", serviceProviderId: "
					+serviceProviderId+"].");
		}
    }
	

	public void addServiceProvider(String stylistId, ServiceProvider serviceProvider) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
		
		serviceProvider.setId(UUID.randomUUID().toString());
		
		stylist.getServiceProviders().add(serviceProvider);
		stylistRepository.save(stylist);
		
	}
	
	public void updateServiceProvider(String stylistId, ServiceProvider serviceProvider) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
				
		List<ServiceProvider> serviceProviders = stylist.getServiceProviders();
		
		boolean found = false;
		
	    for (int i = 0; i < serviceProviders.size(); i++) {
	        if (serviceProviders.get(i).getId().equals(serviceProvider.getId())) {
	            serviceProviders.set(i, serviceProvider);
	            found = true;
	            break;
	        }
	    }
	    
	    if (!found) {
	        throw new EntityNotFoundException(serviceProvider.getId(), EntityType.SERVICE_PROVIDER);
	    }
		
		stylistRepository.save(stylist);
	}
	
	public void removeServiceProvider(String stylistId, String serviceProviderId) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
				
		boolean removed = stylist.getServiceProviders().removeIf(provider -> provider.getId().equals(serviceProviderId));
		if (!removed) {
		    throw new EntityNotFoundException(serviceProviderId, EntityType.SERVICE_PROVIDER);
		}
	    
		stylistRepository.save(stylist);
	}
	
	public void addServiceSpecification(String stylistId, StylistServiceSpecification serviceSpecification) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
		
		serviceSpecification.setId(UUID.randomUUID().toString());
		
		stylist.getServiceSpecifications().add(serviceSpecification);
		stylistRepository.save(stylist);

	}
	
	public void updateServiceSpecification(String stylistId,
			StylistServiceSpecification serviceSpecification) {
		
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
				
		List<StylistServiceSpecification> serviceSpecifications = stylist.getServiceSpecifications();
		
		boolean found = false;
		
	    for (int i = 0; i < serviceSpecifications.size(); i++) {
	        if (serviceSpecifications.get(i).getId().equals(serviceSpecification.getId())) {
	        	serviceSpecifications.set(i, serviceSpecification);
	            found = true;
	            break;
	        }
	    }
	    
	    if (!found) {
	        throw new EntityNotFoundException(serviceSpecification.getId(), EntityType.SERVICE_SPECIFICATION);
	    }
		
		stylistRepository.save(stylist);
	}
	
	public void removeServiceSpecification(String stylistId, String serviceSpecificationId) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
				
		boolean removed = stylist.getServiceSpecifications().removeIf(spec -> spec.getId().equals(serviceSpecificationId));
		if (!removed) {
		    throw new EntityNotFoundException(serviceSpecificationId, EntityType.SERVICE_SPECIFICATION);
		}
	    
		stylistRepository.save(stylist);
	}
	
	public void addHomeServiceSpecification(String stylistId, HomeServiceSpecification homeServiceSpecification) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
		
		homeServiceSpecification.setId(UUID.randomUUID().toString());
		
		stylist.getHomeServiceSpecifications().add(homeServiceSpecification);
		stylistRepository.save(stylist);
		
	}
	
	public void updateHomeServiceSpecification(String stylistId,
			HomeServiceSpecification homeServiceSpecification) {
		
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
				
		List<HomeServiceSpecification> homeServiceSpecifications = stylist.getHomeServiceSpecifications();
		
		boolean found = false;
		
	    for (int i = 0; i < homeServiceSpecifications.size(); i++) {
	        if (homeServiceSpecifications.get(i).getId().equals(homeServiceSpecification.getId())) {
	        	homeServiceSpecifications.set(i, homeServiceSpecification);
	            found = true;
	            break;
	        }
	    }
	    
	    if (!found) {
	        throw new EntityNotFoundException(homeServiceSpecification.getId(), EntityType.HOME_SERVICE_SPECIFICATION);
	    }
		
		stylistRepository.save(stylist);
		
	}
	
	public void removeHomeServiceSpecification(String stylistId, String homeServiceSpecificationId) {
		Stylist stylist = stylistRepository.findById(stylistId).orElseThrow(
				() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
				
		boolean removed = stylist.getHomeServiceSpecifications().removeIf(spec -> spec.getId().equals(homeServiceSpecificationId));
		if (!removed) {
		    throw new EntityNotFoundException(homeServiceSpecificationId, EntityType.HOME_SERVICE_SPECIFICATION);
		}
	    
		stylistRepository.save(stylist);
	}
	
	public void addStylist(Stylist stylist) {
		stylistRepository.save(stylist);
	}
	
	public void updateStylist(Stylist update) {
		
		Stylist stylist = stylistRepository.findById(update.getId())
				.orElseThrow(() -> new EntityNotFoundException(update.getId(), EntityType.STYLIST));
		
		applyUpdate(stylist, update);
		
		stylistRepository.save(stylist);
	}
	
	public void anonymiseStylist(String stylistId) {
		Stylist stylist = stylistRepository.findById(stylistId)
				.orElseThrow(() -> new EntityNotFoundException(stylistId, EntityType.STYLIST));
		
		applyUpdate(stylist, getAnonymousStylist());
		
		stylistRepository.save(stylist);
	}
	
	public void updateService(GlamoorService service) {
		customStylistRepository.updateService(service);
	}
	
	private Stylist getAnonymousStylist() {
    	Stylist anonymous = new Stylist();
    	
    	anonymous.setFirstName("Anonymous");
    	anonymous.setLogo("");
    	anonymous.setEmail("N/A");
    	anonymous.setAddress(null);
    	anonymous.setPhone(null);
    	anonymous.setVat(null);
    	anonymous.setMinAdvanceBookingTimeMinutes(null);
    	
    	anonymous.setHomeServiceSpecifications(new ArrayList<>());
    	anonymous.setServiceProviders(new ArrayList<>());
    	anonymous.setServiceSpecifications(new ArrayList<>());
    	
    	return anonymous;
    }
	
	private void applyUpdate(Stylist stylist, Stylist update) {
    	if (update.getFirstName() != null) stylist.setFirstName(update.getFirstName());
		if (update.getLastName() != null) stylist.setLastName(update.getLastName());
		if (update.getLogo() != null) stylist.setLogo(update.getLogo());
		if (update.getEmail() != null) stylist.setEmail(update.getEmail());
		if (update.getAddress() != null) stylist.setAddress(update.getAddress());
		if (update.getPhone() != null) stylist.setPhone(update.getPhone());
		if (update.getVat() != null) stylist.setVat(update.getVat());
		if (update.getMinAdvanceBookingTimeMinutes() != null)
			stylist.setMinAdvanceBookingTimeMinutes(
				update.getMinAdvanceBookingTimeMinutes());
		
		if (update.getHomeServiceSpecifications() != null && !update.getHomeServiceSpecifications().isEmpty())
			stylist.setHomeServiceSpecifications(update.getHomeServiceSpecifications());
		if (update.getServiceProviders() != null && !update.getServiceProviders().isEmpty())
			stylist.setServiceProviders(update.getServiceProviders());
		if (update.getServiceSpecifications() != null && !update.getServiceSpecifications().isEmpty())
			stylist.setServiceSpecifications(update.getServiceSpecifications());
    }
}
