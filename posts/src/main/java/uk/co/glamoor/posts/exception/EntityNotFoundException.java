package uk.co.glamoor.posts.exception;

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
			case POST:
				return "Post";
			default:
				return "";
		}
	}
}
