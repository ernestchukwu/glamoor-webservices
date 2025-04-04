package uk.co.glamoor.customers.exception;

import uk.co.glamoor.customers.enums.EntityType;

import java.util.Objects;

public class EntityNotFoundException extends RuntimeException {

	public EntityNotFoundException(String id, EntityType entityType) {
		super(getEntityName(entityType) + " with ID: [" + id + "] not found.");
	}

	
	private static String getEntityName(EntityType entityType) {
        if (Objects.requireNonNull(entityType) == EntityType.CUSTOMER) {
            return "Customer";
        }
        return "";
    }
}
