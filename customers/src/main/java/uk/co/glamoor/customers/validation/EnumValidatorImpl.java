package uk.co.glamoor.customers.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    private Enum<?>[] enumValues;

    @Override
    public void initialize(EnumValidator annotation) {
        enumValues = annotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;  // Allow null if not marked as @NotNull

        return Arrays.stream(enumValues)
                .anyMatch(enumValue -> enumValue.name().equals(value));
    }
}

