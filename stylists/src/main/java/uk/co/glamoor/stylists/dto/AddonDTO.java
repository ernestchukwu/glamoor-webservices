package uk.co.glamoor.stylists.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddonDTO {

    @Size(max = 50, message = "Addon Id must not exceed 50 characters")
    private String id;
    @Size(max = 100, message = "Addon name must not exceed 100 characters")
    private String name;
    @Size(max = 200, message = "Addon description must not exceed 200 characters")
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
