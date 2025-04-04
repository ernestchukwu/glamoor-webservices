package uk.co.glamoor.customers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.model.AccountDeletionReason;
import uk.co.glamoor.customers.enums.AccountDeletionReasonStatus;
import uk.co.glamoor.customers.repository.AccountDeletionReasonRepository;

import java.util.List;

@RestController
@RequestMapping("/api/customers/account-deletion-reasons")
@RequiredArgsConstructor
public class AccountDeletionReasonController {

    private final AccountDeletionReasonRepository repository;

    @GetMapping
    public Mono<ResponseEntity<List<AccountDeletionReason>>> getActiveReasons() {
        return repository.findByStatus(AccountDeletionReasonStatus.ACTIVE)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountDeletionReason>> getReasonById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

