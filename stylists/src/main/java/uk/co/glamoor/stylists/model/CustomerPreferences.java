package uk.co.glamoor.stylists.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="customer-preferences")
public class CustomerPreferences {
    @Id
    private String id;
    private List<ServicePreference> servicePreferences = new ArrayList<>();

    @Data
    public static class ServicePreference {
        private String serviceId;
        private int level = 1;
    }
}
