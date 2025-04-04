package uk.co.glamoor.bookings.exception;

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
			case STYLIST:
				return "Stylist";
			case CUSTOMER:
				return "Customer";
			case BOOKING:
				return "Booking";
			case AVAILABILITY:
				return "Availability";
			case TIMESLOT:
				return "Timeslot";
			case HOME_SERVICE_SPECIFICATION:
				return "Homeservice Specification";
			case SERVICE_PROVIDER:
				return "Service Provider";
			case SERVICE_SPECIFICATION:
				return "Service Specification";
			default:
				return "";
		}
	}
}
