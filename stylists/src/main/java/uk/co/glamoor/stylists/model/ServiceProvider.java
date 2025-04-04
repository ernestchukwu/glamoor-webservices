package uk.co.glamoor.stylists.model;

import java.util.List;

import lombok.Data;


@Data
public class ServiceProvider {
	
    private String id;
    private String stylistId;
    private Boolean doesHomeService;
    private String firstName;
    private String lastName;
	private String email;
	private Phone phone;
    private String photo;
    private List<String> services;

}
