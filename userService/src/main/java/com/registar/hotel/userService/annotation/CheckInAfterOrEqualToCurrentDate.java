package com.registar.hotel.userService.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckInAfterOrEqualToCurrentDateValidator.class)
public @interface CheckInAfterOrEqualToCurrentDate {
    String message() default "Check-in date must be greater than or equal to the current date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
