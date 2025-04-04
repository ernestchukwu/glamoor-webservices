package uk.co.glamoor.notifications.exception;

@SuppressWarnings("serial")
public class EntityNotFoundException extends RuntimeException {

	public EntityNotFoundException(String message) {
		super(message);
	}
	
	public EntityNotFoundException(String id, EntityType entityType) {
		super(getEntityName(entityType) + " with ID: [" + id + "] not found.");
	}

	
	private static String getEntityName(EntityType entityType) {
		switch(entityType) {
			case NOTIFICATION:
				return "Notification";
			case DEVICE:
				return "Device";
			case USER:
				return "User";
			default:
				return "";
		}
	}
}
