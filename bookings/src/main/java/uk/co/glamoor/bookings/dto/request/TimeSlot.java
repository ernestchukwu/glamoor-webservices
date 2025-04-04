package uk.co.glamoor.bookings.dto.request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalTime;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;

@TimeSlot.ValidTimeRange
public record TimeSlot(@NotNull(message = "Start time must not be null.") LocalTime start,
                       @NotNull(message = "End time must not be null.") LocalTime end) {

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
            if (timeSlot == null || timeSlot.start() == null || timeSlot.end() == null) {
                return true; // Null values are handled by @NotNull
            }
            return timeSlot.start().isBefore(timeSlot.end());
        }

    }


}