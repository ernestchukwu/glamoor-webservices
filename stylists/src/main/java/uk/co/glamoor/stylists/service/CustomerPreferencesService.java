package uk.co.glamoor.stylists.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.glamoor.stylists.model.CustomerPreferences;
import uk.co.glamoor.stylists.repository.CustomerPreferencesRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerPreferencesService {

    private final CustomerPreferencesRepository customerPreferencesRepository;

    public Optional<CustomerPreferences> findCustomerPreferences(String customerId) {
        return customerPreferencesRepository.findById(customerId);
    }

    public void addPreferredService(String customerId, List<String> serviceIds) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(serviceIds, "serviceIds cannot be null");

        CustomerPreferences customerPreferences = customerPreferencesRepository.findById(customerId)
                .orElseGet(() -> {
                    CustomerPreferences customerPreferences1 = new CustomerPreferences();
                    customerPreferences1.setId(customerId);
                    return customerPreferences1;
                });

        Map<String, CustomerPreferences.ServicePreference> preferenceMap = customerPreferences.getServicePreferences()
                .stream()
                .collect(Collectors.toMap(
                        CustomerPreferences.ServicePreference::getServiceId,
                        Function.identity()));

        for (String serviceId : serviceIds) {
            preferenceMap.compute(serviceId, (id, pref) -> {
                if (pref != null) {
                    pref.setLevel(pref.getLevel() + 1);
                    return pref;
                } else {
                    CustomerPreferences.ServicePreference newPref = new CustomerPreferences.ServicePreference();
                    newPref.setServiceId(serviceId);
                    return newPref;
                }
            });
        }

        customerPreferences.setServicePreferences(new ArrayList<>(preferenceMap.values()));

        customerPreferencesRepository.save(customerPreferences);
    }

    public void removePreferredService(String customerId, List<String> serviceIds) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(serviceIds, "serviceIds cannot be null");

        customerPreferencesRepository.findById(customerId).ifPresent(customerPreferences -> {
            Iterator<CustomerPreferences.ServicePreference> iterator =
                    customerPreferences.getServicePreferences().iterator();

            while (iterator.hasNext()) {
                CustomerPreferences.ServicePreference pref = iterator.next();
                if (serviceIds.contains(pref.getServiceId())) {
                    if (pref.getLevel() > 1) {
                        pref.setLevel(pref.getLevel() - 1);
                    } else {
                        iterator.remove();
                    }
                }
            }

            customerPreferencesRepository.save(customerPreferences);
        });
    }
}
