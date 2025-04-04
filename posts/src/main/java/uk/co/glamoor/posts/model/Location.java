package uk.co.glamoor.posts.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Location {

	@Size(max = 10, message = "Type must not exceed 10 characters")
	private String type = "Point";

	@Size(min = 2, max = 2, message = "Coordinates must contain exactly 2 elements")
	private List<Double> coordinates = new ArrayList<>();

}
