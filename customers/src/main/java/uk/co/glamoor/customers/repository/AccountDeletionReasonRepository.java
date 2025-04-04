package uk.co.glamoor.customers.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import uk.co.glamoor.customers.model.AccountDeletionReason;
import uk.co.glamoor.customers.enums.AccountDeletionReasonStatus;

@Repository
public interface AccountDeletionReasonRepository extends ReactiveMongoRepository<AccountDeletionReason, String> {

    Flux<AccountDeletionReason> findByStatus(AccountDeletionReasonStatus status);
}
