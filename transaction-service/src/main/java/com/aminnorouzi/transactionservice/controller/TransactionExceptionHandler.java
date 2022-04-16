package com.aminnorouzi.transactionservice.controller;

import com.aminnorouzi.transactionservice.exception.NotValidTransactionAccountException;
import com.aminnorouzi.transactionservice.exception.TransactionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class TransactionExceptionHandler {

    private static final String ERROR_MESSAGE = "Caught new error: code={}, {}";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TransactionNotFoundException.class)
    public Map<String, String> handleTransactionNotFoundException(TransactionNotFoundException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotValidTransactionAccountException.class)
    public Map<String, String> handleNotValidTransactionAccountException(NotValidTransactionAccountException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleAllExceptions(Exception exception) {
        Map<String, String> response = generateResponse("Internal Server Error", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    private Map<String, String> generateResponse(String code, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        return response;
    }
}
