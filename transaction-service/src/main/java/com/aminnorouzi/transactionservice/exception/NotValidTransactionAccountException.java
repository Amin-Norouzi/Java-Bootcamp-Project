package com.aminnorouzi.transactionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotValidTransactionAccountException extends RuntimeException {

    public NotValidTransactionAccountException(String message) {
        super(message);
    }
}
