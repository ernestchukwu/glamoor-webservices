package uk.co.glamoor.customers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.glamoor.customers.model.AccountDeletionLog;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;
import uk.co.glamoor.customers.repository.AccountDeletionLogRepository;

@Service
@RequiredArgsConstructor
public class AccountDeletionLogService {

    private final AccountDeletionLogRepository repository;

    public Mono<Void> createLog(String customerId, AccountDeletionReasonEnum reason, String moreDetails) {
        return Mono.fromCallable(() -> {
                    AccountDeletionLog log = new AccountDeletionLog();
                    log.setCustomerId(customerId);
                    log.setDeletionReason(reason);

                    if (reason == AccountDeletionReasonEnum.OTHER) {
                        log.setMoreDetails(moreDetails);
                    }
                    return log;
                })
                .flatMap(repository::save)
                .then();
    }

    public Flux<AccountDeletionLog> getAllLogs() {
        return repository.findAll();
    }

    public Flux<AccountDeletionLog> getLogsByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Flux<AccountDeletionLog> getLogsByReason(AccountDeletionReasonEnum reason) {
        return repository.findByDeletionReason(reason);
    }

    public Mono<AccountDeletionLog> getLogById(String id) {
        return repository.findById(id);
    }

    public Mono<Void> deleteLogById(String id) {
        return repository.deleteById(id).then();
    }
}

