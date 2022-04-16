package com.aminnorouzi.loanservice.client;

import com.aminnorouzi.loanservice.model.transaction.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PutMapping("/api/v1/accounts/withdraw")
    Transaction withdraw(@RequestParam("id") Long id,
                         @RequestParam("amount") BigDecimal amount,
                         @RequestParam(value = "note", required = false, defaultValue = "withdraw") String note);

    @GetMapping("/api/v1/accounts/verify/{id}")
    boolean verifyAccount(@PathVariable("id") Long id);
}
