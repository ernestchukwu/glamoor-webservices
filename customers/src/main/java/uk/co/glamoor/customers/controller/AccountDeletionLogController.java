package uk.co.glamoor.customers.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.dto.request.AccountDeletionRequest;
import uk.co.glamoor.customers.model.AccountDeletionLog;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;
import uk.co.glamoor.customers.service.AccountDeletionLogService;
import uk.co.glamoor.customers.service.CustomerService;
import uk.co.glamoor.customers.service.api.GatewayService;

import java.util.List;

@RestController
@RequestMapping("/api/customers/account-deletion-logs")
@RequiredArgsConstructor
public class AccountDeletionLogController {

    private final AccountDeletionLogService accountDeletionLogService;
    private final GatewayService gatewayService;
    private final CustomerService customerService;

    @PostMapping
    public Mono<ResponseEntity<Void>> createLog(
            @RequestBody @Valid AccountDeletionRequest accountDeletionRequest) {

        return customerService.getCustomerById(accountDeletionRequest.getCustomerId())
                .flatMap(customer ->
                        gatewayService.deleteUser(customer)
                                .then(accountDeletionLogService.createLog(
                                        accountDeletionRequest.getCustomerId(),
                                        accountDeletionRequest.getReason(),
                                        accountDeletionRequest.getMoreDetails()
                                ))
                                .then(customerService.anonymiseCustomer(accountDeletionRequest.getCustomerId()))
                                .then(Mono.just(ResponseEntity.noContent().build()))
                );

    }

    @GetMapping
    public Mono<ResponseEntity<List<AccountDeletionLog>>> getAllLogs() {
        return accountDeletionLogService.getAllLogs()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/customer/{customerId}")
    public Mono<ResponseEntity<List<AccountDeletionLog>>> getLogsByCustomerId(@PathVariable String customerId) {
        return accountDeletionLogService.getLogsByCustomerId(customerId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/reason/{reason}")
    public Mono<ResponseEntity<List<AccountDeletionLog>>> getLogsByReason(@PathVariable AccountDeletionReasonEnum reason) {
        return accountDeletionLogService.getLogsByReason(reason)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountDeletionLog>> getLogById(@PathVariable String id) {
        return accountDeletionLogService.getLogById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLogById(@PathVariable String id) {
        return accountDeletionLogService.deleteLogById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}

