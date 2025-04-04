package uk.co.glamoor.bookings.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Location {

    private String type = "Point";
    private List<Double> coordinates = new ArrayList<>();

}
