package com.registar.hotel.userService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserLoggedOutException extends RuntimeException {

    public UserLoggedOutException(String message) {
        super(message);
    }

    public UserLoggedOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
