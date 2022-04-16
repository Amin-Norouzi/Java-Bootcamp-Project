package com.aminnorouzi.transactionservice.controller;

import com.aminnorouzi.transactionservice.model.Transaction;
import com.aminnorouzi.transactionservice.model.TransactionRequest;
import com.aminnorouzi.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Transaction createTransaction(@RequestBody TransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable("id") Long id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping()
    public List<Transaction> getTransactionByAccount(@RequestParam("accountId") Long accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }
}
