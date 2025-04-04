package uk.co.glamoor.customers.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.model.Address;
import uk.co.glamoor.customers.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/api/customers/addresses")
@Validated
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/{customerId}")
    public Mono<ResponseEntity<Address>> createAddress(
            @PathVariable String customerId,
            @RequestBody @Valid Address address) {

        return Mono.just(address)
                .doOnNext(addr -> {
                    addr.setId(null);
                    addr.setCustomer(customerId);
                })
                .flatMap(addressService::saveAddress) // Assuming saveAddress now returns Mono<Address>
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()));
    }

    @GetMapping("/{customerId}")
    public Mono<ResponseEntity<List<Address>>> getAddressesByCustomer(@PathVariable String customerId) {
        return addressService.getAddressesByCustomer(customerId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Address>> updateAddress(
            @PathVariable String id,
            @RequestBody @Valid Address updatedAddress) {

        return addressService.updateAddress(id, updatedAddress)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAddress(@PathVariable String id) {
        return addressService.deleteAddress(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
