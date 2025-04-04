package uk.co.glamoor.stylists.model.availability_request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
@TimeSlot.ValidTimeRange
public class TimeSlot {
	
	@NotNull(message = "Start time must not be null.")
	private LocalTime start;
	
	@NotNull(message = "End time must not be null.")
	private LocalTime end;

	public TimeSlot(){}

	
	public TimeSlot(LocalTime start, LocalTime end) {
		this.start = start;
		this.end = end;
	}
	
	public void validate() {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Invalid time slot: Start must be before End.");
        }
    }
	
	public long getDuration() {
		return Math.abs(ChronoUnit.MINUTES.between(start, end));
	}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Constraint(validatedBy = TimeRangeValidator.class)
    public @interface ValidTimeRange {
        String message() default "Start time must be before end time.";
        Class<?>[] groups() default {};
        Class<? extends jakarta.validation.Payload>[] payload() default {};
    }

    public static class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, TimeSlot> {

        @Override
        public boolean isValid(TimeSlot timeSlot, ConstraintValidatorContext context) {
            if (timeSlot == null || timeSlot.getStart() == null || timeSlot.getEnd() == null) {
                return true; // Null values are handled by @NotNull
            }
            return timeSlot.getStart().isBefore(timeSlot.getEnd());
        }

        @Override
        public void initialize(ValidTimeRange constraintAnnotation) {}
    }
    
	
}