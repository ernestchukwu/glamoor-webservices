package uk.co.glamoor.customers.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecentSearch {

	private String entityId;
	private EntityType entityType;
	private LocalDateTime time;

	enum EntityType {
		SERVICE, SERVICE_CATEGORY, STYLIST
	}
}
