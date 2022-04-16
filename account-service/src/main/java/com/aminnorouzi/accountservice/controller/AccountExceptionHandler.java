package com.aminnorouzi.accountservice.controller;

import com.aminnorouzi.accountservice.exception.IllegalAccountStatusException;
import com.aminnorouzi.accountservice.exception.NotEnoughAccountBalanceException;
import com.aminnorouzi.accountservice.exception.NotValidAccountCustomerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class AccountExceptionHandler {

    private static final String ERROR_MESSAGE = "Caught new error: code={}, {}";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public Map<String, String> handleAccountNotFoundException(AccountNotFoundException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotValidAccountCustomerException.class)
    public Map<String, String> handleNotValidAccountCustomerException(NotValidAccountCustomerException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalAccountStatusException.class)
    public Map<String, String> handleIllegalAccountStatusException(IllegalAccountStatusException exception) {
        Map<String, String> response = generateResponse("Bad Request", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughAccountBalanceException.class)
    public Map<String, String> handleNotEnoughAccountBalanceException(NotEnoughAccountBalanceException exception) {
        Map<String, String> response = generateResponse("Bad Request", exception.getMessage());

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
