package uk.co.glamoor.customers.model;

import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;
import uk.co.glamoor.customers.enums.AccountDeletionReasonStatus;

@Data
@Document(collection = "account-deletion-reasons")
public class AccountDeletionReason {
	@Id
	private final String id;

	@Indexed(unique = true)
	private final AccountDeletionReasonEnum reason;

	private final String label;

	private final boolean moreDetailsRequired;

	private final AccountDeletionReasonStatus status;

	public AccountDeletionReason(String id,
								 AccountDeletionReasonEnum reason,
								 String label,
								 boolean moreDetailsRequired,
								 AccountDeletionReasonStatus status) {
		this.id = id;
		this.reason = reason;
		this.label = label;
		this.moreDetailsRequired = moreDetailsRequired;
		this.status = status;
	}
}
