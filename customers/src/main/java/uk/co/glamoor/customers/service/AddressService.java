package uk.co.glamoor.customers.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.model.Address;
import uk.co.glamoor.customers.repository.AddressRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Mono<Address> saveAddress(Address address) {
        return addressRepository.save(address);
    }

    public Flux<Address> getAddressesByCustomer(String customerId) {
        return addressRepository.findByCustomer(customerId);
    }

    public Mono<Address> updateAddress(String id, Address updatedAddress) {
        return addressRepository.findById(id)
                .flatMap(address -> {
                    address.setCustomer(updatedAddress.getCustomer());
                    address.setPostcode(updatedAddress.getPostcode());
                    address.setAddress1(updatedAddress.getAddress1());
                    address.setAddress2(updatedAddress.getAddress2());
                    address.setCity(updatedAddress.getCity());
                    address.setCounty(updatedAddress.getCounty());
                    address.setCountry(updatedAddress.getCountry());

                    return addressRepository.save(address);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Address with ID " + id + " not found.")));
    }

    public Mono<Void> deleteAddress(String id) {
        return addressRepository.deleteById(id);
    }
}
