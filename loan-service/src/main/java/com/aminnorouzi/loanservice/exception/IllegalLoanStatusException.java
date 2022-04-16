package com.aminnorouzi.loanservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalLoanStatusException extends RuntimeException {

    public IllegalLoanStatusException(String message) {
        super(message);
    }
}
