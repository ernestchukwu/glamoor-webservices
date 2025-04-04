package uk.co.glamoor.stylists.dto;

import jakarta.validation.constraints.Size;

public class GlamoorServiceDTO {

    @Size(max = 50, message = "GlamoorService ID must not exceed 50 characters")
    private String id;
    @Size(max = 100, message = "GlamoorService name must not exceed 100 characters")
    private String name;
    @Size(max = 200, message = "GlamoorService description must not exceed 200 characters")
    private String description;
    @Size(max = 50, message = "GlamoorService categoryId must not exceed 50 characters")
    private String categoryId;

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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
