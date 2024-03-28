package com.registar.hotel.userService.exception;

public class GuestNotFoundException extends RuntimeException {

    public GuestNotFoundException(String message) {
        super(message);
    }

    public GuestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
