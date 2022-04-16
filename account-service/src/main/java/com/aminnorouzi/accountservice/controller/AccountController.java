package com.aminnorouzi.accountservice.controller;

import com.aminnorouzi.accountservice.model.Account;
import com.aminnorouzi.accountservice.model.AccountRequest;
import com.aminnorouzi.accountservice.model.customer.Customer;
import com.aminnorouzi.accountservice.model.transaction.Transaction;
import com.aminnorouzi.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Account createAccount(@RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/verify/{id}")
    public boolean verifyAccount(@PathVariable("id") Long id) {
        return accountService.verifyAccount(id);
    }

    @PutMapping("/status/{id}")
    public Account changeAccountStatus(@PathVariable Long id,
                                       @RequestParam("status") String status) {
        return accountService.changeAccountStatus(id, status);
    }

    @GetMapping("/balance/{id}")
    public BigDecimal showAccountBalance(@PathVariable("id") Long id) {
        return accountService.showAccountBalance(id);
    }

    @GetMapping()
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{customerId}")
    public List<Account> getAccountsByCustomerId(@PathVariable("customerId") Long customerId) {
        return accountService.getAccountsByCustomerId(customerId);
    }

    @GetMapping("/customers/{accountId}")
    public List<Customer> getCustomersByAccount(@PathVariable("accountId") Long accountId) {
        return accountService.getCustomersByAccountId(accountId);
    }

    @PutMapping("/deposit")
    public Transaction deposit(@RequestParam("id") Long id,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam(value = "note", required = false, defaultValue = "deposit") String note) {
        return accountService.deposit(id, amount, note);
    }

    @PutMapping("/withdraw")
    public Transaction withdraw(@RequestParam("id") Long id,
                                @RequestParam("amount") BigDecimal amount,
                                @RequestParam(value = "note", required = false, defaultValue = "withdraw") String note) {
        return accountService.withdraw(id, amount, note);
    }

    @PutMapping("/transfer")
    public Transaction transfer(@RequestParam("senderId") Long senderId,
                                @RequestParam("receiverId") Long receiverId,
                                @RequestParam("amount") BigDecimal amount,
                                @RequestParam(value = "note", required = false, defaultValue = "transfer") String note) {
        return accountService.transfer(senderId, receiverId, amount, note);
    }
}
