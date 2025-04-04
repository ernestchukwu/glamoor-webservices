package uk.co.glamoor.customers.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.model.ContactUs;
import uk.co.glamoor.customers.repository.ContactUsRepository;

@Service
@RequiredArgsConstructor
public class ContactUsService {

    Logger logger = LoggerFactory.getLogger(ContactUsService.class);

    private final ContactUsRepository contactUsRepository;

    public Mono<ContactUs> saveContactRequest(ContactUs contactUs) {
        return contactUsRepository.save(contactUs)
                .doOnNext(saved -> logger.info("Saved contact request: {}", saved.getId()))
                .onErrorMap(e -> new RuntimeException("An unexpected error occurred", e));
    }
}
