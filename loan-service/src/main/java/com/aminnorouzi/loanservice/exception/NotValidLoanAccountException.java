package com.aminnorouzi.loanservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotValidLoanAccountException extends RuntimeException {

    public NotValidLoanAccountException(String message) {
        super(message);
    }
}
