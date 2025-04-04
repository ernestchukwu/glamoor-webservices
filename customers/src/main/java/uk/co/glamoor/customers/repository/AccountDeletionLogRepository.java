package uk.co.glamoor.customers.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import uk.co.glamoor.customers.model.AccountDeletionLog;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;

@Repository
public interface AccountDeletionLogRepository extends ReactiveMongoRepository<AccountDeletionLog, String> {

    Flux<AccountDeletionLog> findByCustomerId(String customerId);

    Flux<AccountDeletionLog> findByDeletionReason(AccountDeletionReasonEnum reason);
}

