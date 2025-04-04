package uk.co.glamoor.customers.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "contact-us")
public class ContactUs {

    @Id
    private String id;
    private ContactUsMetadata metadata;
    private String name;
    private String email;
    private String message;
    private LocalDateTime time = LocalDateTime.now();

    @Data
    public static class ContactUsMetadata {
        private String customerId;
        private String customerUid;
        private String appVersion;
        private String appBuildNumber;
        private String platform;
    }
}
