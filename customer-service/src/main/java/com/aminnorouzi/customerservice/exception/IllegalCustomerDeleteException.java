package com.aminnorouzi.customerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalCustomerDeleteException extends RuntimeException {

    public IllegalCustomerDeleteException(String message) {
        super(message);
    }
}
