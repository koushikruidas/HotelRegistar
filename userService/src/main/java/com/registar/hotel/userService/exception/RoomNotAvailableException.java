package com.registar.hotel.userService.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String message) {
        super(message);
    }

    public RoomNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
