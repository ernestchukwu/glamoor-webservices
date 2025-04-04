package uk.co.glamoor.customers.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.co.glamoor.customers.model.AccountDeletionReason;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;
import uk.co.glamoor.customers.enums.AccountDeletionReasonStatus;
import uk.co.glamoor.customers.repository.AccountDeletionReasonRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountDeletionReasonSeeder implements CommandLineRunner {

    private final AccountDeletionReasonRepository repository;

    @Override
    public void run(String... args) {
        List<AccountDeletionReason> reasons = List.of(
                new AccountDeletionReason(null, AccountDeletionReasonEnum.FOUND_BETTER_ALTERNATIVE, "Found a Better Alternative", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.NO_LONGER_NEED_THE_SERVICE, "No Longer Need the Service", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.PRICING_ISSUES, "Pricing Issues", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.SERVICE_QUALITY_CONCERNS, "Service Quality Concerns", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.PRIVACY_CONCERNS, "Privacy Concerns", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.SWITCHING_TO_COMPETITOR, "Switching to a Competitor", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.TECHNICAL_ISSUES, "Technical Issues", false, AccountDeletionReasonStatus.ACTIVE),
                new AccountDeletionReason(null, AccountDeletionReasonEnum.OTHER, "Other", true, AccountDeletionReasonStatus.ACTIVE)
        );

        repository.deleteAll();  // Clear existing data
        repository.saveAll(reasons);
    }
}

