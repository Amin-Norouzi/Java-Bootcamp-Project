package com.aminnorouzi.customerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalNationalCodeException extends RuntimeException {

    public IllegalNationalCodeException(String message) {
        super(message);
    }
}
