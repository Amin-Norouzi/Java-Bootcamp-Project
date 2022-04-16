package com.aminnorouzi.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalAccountStatusException extends RuntimeException {

    public IllegalAccountStatusException(String message) {
        super(message);
    }
}
