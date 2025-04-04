package uk.co.glamoor.customers.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ContactUsRequest {

    @NotNull(message = "Metadata is required")
    @Valid
    private ContactUsRequestMetadata metadata;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Name contains invalid characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
    private String message;

    @Data
    public static class ContactUsRequestMetadata {

        @NotBlank(message = "Customer ID is required")
        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Customer ID contains invalid characters")
        private String customerId;

        @NotBlank(message = "Customer UID is required")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Customer UID contains invalid characters")
        private String customerUid;

        @NotBlank(message = "App version is required")
        @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Version must be in semver format (X.Y.Z)")
        private String appVersion;

        @NotBlank(message = "Build number is required")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Build number contains invalid characters")
        private String appBuildNumber;

        @NotBlank(message = "Platform is required")
        @Pattern(regexp = "^(ios|android|web|unknown)$", message = "Platform must be iOS, Android or Web")
        private String platform;
    }
}
