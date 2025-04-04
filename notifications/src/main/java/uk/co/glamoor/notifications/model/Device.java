package uk.co.glamoor.notifications.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "devices")
public class Device {
	
	private String user;
	@Id
	private String deviceToken;
	private LocalDateTime timeCreated = LocalDateTime.now();

}
