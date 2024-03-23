package com.registar.hotel.userService.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CheckInAfterOrEqualToCurrentDateValidator implements ConstraintValidator<CheckInAfterOrEqualToCurrentDate, LocalDate> {

    @Override
    public void initialize(CheckInAfterOrEqualToCurrentDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate checkInDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate currentDate = LocalDate.now();
        return checkInDate != null && !checkInDate.isBefore(currentDate);
    }
}
