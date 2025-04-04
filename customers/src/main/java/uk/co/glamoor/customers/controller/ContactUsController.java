package uk.co.glamoor.customers.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import uk.co.glamoor.customers.dto.response.ContactUsRequest;
import uk.co.glamoor.customers.mapper.ContactUsMapper;
import uk.co.glamoor.customers.service.ContactUsService;


@RestController
@RequestMapping("/api/customers/contact-us")
@RequiredArgsConstructor
public class ContactUsController {

    private final ContactUsService contactUsService;

    @PostMapping
    public Mono<ResponseEntity<Void>> submitContactForm(
            @RequestBody @Valid ContactUsRequest request) {

        return Mono.fromCallable(() -> ContactUsMapper.toContactUs(request))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap((contactUs -> contactUsService.saveContactRequest(contactUs)
                        .thenReturn(ResponseEntity.noContent().build())));
    }
}
