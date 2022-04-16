package com.aminnorouzi.loanservice.controller;

import com.aminnorouzi.loanservice.exception.IllegalLoanStatusException;
import com.aminnorouzi.loanservice.exception.LoanNotFoundException;
import com.aminnorouzi.loanservice.exception.LoanPaymentNotAvailableException;
import com.aminnorouzi.loanservice.exception.NotValidLoanAccountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class LoanExceptionHandler {

    private static final String ERROR_MESSAGE = "Caught new error: code={}, {}";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LoanNotFoundException.class)
    public Map<String, String> handleLoanNotFoundException(LoanNotFoundException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotValidLoanAccountException.class)
    public Map<String, String> handleNotValidLoanAccountException(NotValidLoanAccountException exception) {
        Map<String, String> response = generateResponse("Not Found", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalLoanStatusException.class)
    public Map<String, String> handleIllegalLoanStatusException(IllegalLoanStatusException exception) {
        Map<String, String> response = generateResponse("Bad Request", exception.getMessage());

        log.error(ERROR_MESSAGE, response.get("code"), exception.getMessage());
        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoanPaymentNotAvailableException.class)
    public Map<String, String> handleLoanPaymentNotAvailableException(LoanPaymentNotAvailableException exception) {
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
