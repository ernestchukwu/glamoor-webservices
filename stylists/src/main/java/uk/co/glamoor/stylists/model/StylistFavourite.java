package uk.co.glamoor.stylists.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@Document(collection = "stylist-favourites")
public class StylistFavourite {
	@Id
	private String id;
	private String customer;
	private String stylist;
	private LocalDateTime timeCreated = LocalDateTime.now();
}
