package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Data
@Document(collection="service-categories")
public class ServiceCategory {

    @Id
    private String id;
    @Indexed(unique = true)
    @NotBlank
    @NotNull
    private String name;
    private String description;
    private List<String> images;

}
