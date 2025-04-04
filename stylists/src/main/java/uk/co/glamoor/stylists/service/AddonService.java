package uk.co.glamoor.stylists.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.glamoor.stylists.config.StylistsConfig;
import uk.co.glamoor.stylists.model.Addon;
import uk.co.glamoor.stylists.repository.AddonRepository;

import java.util.List;

@Service
public class AddonService {

    private final AddonRepository addonRepository;
    private final StylistsConfig stylistsConfig;
    
    public AddonService(AddonRepository addonRepository,
                        StylistsConfig stylistsConfig) {
    	this.addonRepository = addonRepository;
    	this.stylistsConfig = stylistsConfig;
    }

    public Addon addAddon(Addon addon) {
    	if (addonRepository.existsByName(addon.getName())) {
            throw new RuntimeException("Addon name already exists.");
        }
        return addonRepository.save(addon);
    }

    public Addon getAddon(String id) {
        return addonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Addon not found with id: " + id));
    }

    public List<Addon> getAddons(int offset) {
        return addonRepository.findAll(PageRequest.of(
        		offset / stylistsConfig.getAddonRequestBatchSize(),
                stylistsConfig.getAddonRequestBatchSize())).getContent();
    }

    public Addon updateAddon(String id, Addon addon) {
        Addon existingAddon = getAddon(id);
        existingAddon.setName(addon.getName());
        existingAddon.setDescription(addon.getDescription());
        return addonRepository.save(existingAddon);
    }

    public void deleteAddon(String id) {
        Addon addon = getAddon(id);
        addonRepository.delete(addon);
    }
}

