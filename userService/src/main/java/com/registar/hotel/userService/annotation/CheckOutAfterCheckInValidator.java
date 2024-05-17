package com.registar.hotel.userService.annotation;

import com.registar.hotel.userService.model.BookingDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class CheckOutAfterCheckInValidator implements ConstraintValidator<CheckOutAfterCheckIn, BookingDTO> {

    @Override
    public void initialize(CheckOutAfterCheckIn constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDTO bookingDTO, ConstraintValidatorContext context) {
        if (bookingDTO == null) {
            return true; // Let @NotNull handle this case
        }
        return bookingDTO.getCheckOutDate() == null ||
               bookingDTO.getCheckInDate() == null ||
               bookingDTO.getCheckOutDate().isAfter(bookingDTO.getCheckInDate());
    }
}
