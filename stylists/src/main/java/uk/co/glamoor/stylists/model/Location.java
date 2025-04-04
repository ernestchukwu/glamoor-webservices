package uk.co.glamoor.stylists.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Location {

    @Size(max = 10, message = "Type must not exceed 10 characters")
    private String type = "Point";

    @Size(min = 2, max = 2, message = "Coordinates must contain exactly 2 elements")
    private List<Double> coordinates = new ArrayList<>();

}
