package com.aminnorouzi.loanservice.controller;

import com.aminnorouzi.loanservice.model.Loan;
import com.aminnorouzi.loanservice.model.LoanRequest;
import com.aminnorouzi.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/loans")
public class LoanController {

    private final LoanService loanService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Loan createLoan(@RequestBody LoanRequest request) {
        return loanService.createLoan(request);
    }

    @GetMapping()
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @GetMapping("/{accountId}")
    public List<Loan> getLoansByAccountId(@PathVariable("accountId") Long accountId) {
        return loanService.getLoansByAccountId(accountId);
    }

    @PutMapping("/pay")
    public Loan pay(@RequestParam("accountId") Long accountId, @RequestParam("loanId") Long loanId) {
        return loanService.pay(accountId, loanId);
    }

    @GetMapping("/calculate")
    public BigDecimal calculate(@RequestParam("amount") BigDecimal amount,
                                @RequestParam("count") Integer count,
                                @RequestParam("rate") String rate) {
        return loanService.calculate(amount, count, rate);
    }
}
