package uk.co.glamoor.customers.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import uk.co.glamoor.customers.model.UserSettings;

@Data
public class CustomerRequest {

	@NotBlank(message = "Uid is required.")
	@Size(max = 50, message = "Uid must not exceed 50 characters.")
	@Pattern(
			regexp = "^[a-zA-Z0-9_-]+$",
			message = "UID can only contain letters, numbers, underscores, and hyphens, and must not contain spaces."
	)
	private final String uid;

	@NotBlank(message = "First name is required.")
	@Size(max = 100, message = "First name must not exceed 100 characters.")
	@Pattern(
			regexp = "^[\\p{L} .'-]+$",
			message = "First name can only contain letters, spaces, hyphens, apostrophes, and periods."
	)
	private final String firstName;

	@Size(max = 100, message = "Last name must not exceed 100 characters.")
	@Pattern(
			regexp = "^[\\p{L} .'-]+$",
			message = "First name can only contain letters, spaces, hyphens, apostrophes, and periods."
	)
	private final String lastName;

	@Email(message = "Email must be a valid email address.")
	@NotBlank(message = "Email is required.")
	@Size(max = 255, message = "Email must not exceed 255 characters.")
	private final String email;

	@NotBlank(message = "Account provider is required.")
	@Pattern(
			regexp = "^(facebook|google|apple|emailAndPassword)$",
			message = "Account provider must be either 'facebook' or 'google'."
	)
	private final String accountProvider;

	private final String profilePicture;

	private final @Valid Phone phone;

	private final Boolean emailVerified = false;

	private final @Valid PaymentCard lastUsedPaymentCard;

	private final @Valid UserSettings settings;


	public record Phone(
			@NotBlank(message = "Phone number is required.") @Size(min = 7, max = 15, message = "Phone number must be between 7 and 15 characters.") @Pattern(
					regexp = "^[+0-9]+$",
					message = "Phone number can only contain digits and an optional '+' prefix."
			) String number, @NotBlank(message = "Country code is required.") @Pattern(
			regexp = "^\\+?[0-9]{1,5}$",
			message = "Country code must start with an optional '+' followed by up to 5 digits."
	) String countryCode,
			@NotBlank(message = "Country ISO code is required.") @Size(min = 2, max = 2, message = "Country ISO code must be exactly 2 characters.") @Pattern(
					regexp = "^[A-Z]{2}$",
					message = "Country ISO code must consist of 2 uppercase letters (e.g., US, IN)."
			) String countryISOCode) {
	}

	@Data
	public static class PaymentCard {
		@NotBlank(message = "Payment method ID is required")
		private String paymentMethodId;

		@Pattern(regexp = "\\d{4}", message = "Last 4 digits must be exactly 4 digits")
		private String last4;

		@Pattern(regexp = "0[1-9]|1[0-2]", message = "Expiration month must be between 01 and 12")
		private String expMonth;

		@Pattern(regexp = "\\d{4}", message = "Expiration year must be a 4-digit year")
		private String expYear;

		@NotBlank(message = "Card brand is required")
		private String brand;

	}
}
