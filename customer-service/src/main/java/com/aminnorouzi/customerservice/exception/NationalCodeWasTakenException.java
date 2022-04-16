package com.aminnorouzi.customerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NationalCodeWasTakenException extends RuntimeException {

    public NationalCodeWasTakenException(String message) {
        super(message);
    }
}
