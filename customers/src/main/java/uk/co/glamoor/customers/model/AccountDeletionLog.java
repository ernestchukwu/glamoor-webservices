package uk.co.glamoor.customers.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import uk.co.glamoor.customers.enums.AccountDeletionReasonEnum;

import java.time.LocalDateTime;

@Data
@Document(collection = "account-deletions")
public class AccountDeletionLog {

	@Id
	private String id;

	private AccountDeletionReasonEnum deletionReason;

	private String customerId;
	private LocalDateTime timestamp = LocalDateTime.now();

	private String moreDetails;
	
}
