package uk.co.glamoor.customers.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;

@Data
public class AccountDeletionRequest {
    private final @NotBlank String customerId;
    private final AccountDeletionReasonEnum reason;
    private final String moreDetails;
}
