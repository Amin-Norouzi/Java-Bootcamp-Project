package com.aminnorouzi.customerservice.controller;

import com.aminnorouzi.customerservice.exception.CustomerNotFoundException;
import com.aminnorouzi.customerservice.exception.IllegalCustomerDeleteException;
import com.aminnorouzi.customerservice.exception.IllegalNationalCodeException;
import com.aminnorouzi.customerservice.exception.NationalCodeWasTakenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomerExceptionHandler {

    private static final String ERROR_MESSAGE = "Caught new error: code={}, {}";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFoundException.class)
    public Map<String, String> handleCustomerNotFoundException(CustomerNotFoundException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalCustomerDeleteException.class)
    public Map<String, String> handleIllegalCustomerDeleteException(IllegalCustomerDeleteException exception) {
        Map<String, String> response = generateResponse("Bad Request", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(NationalCodeWasTakenException.class)
    public Map<String, String> handleNationalCodeWasTakenException(NationalCodeWasTakenException exception) {
        Map<String, String> response = generateResponse("Conflict", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalNationalCodeException.class)
    public Map<String, String> handleIllegalNationalCodeException(IllegalNationalCodeException exception) {
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
