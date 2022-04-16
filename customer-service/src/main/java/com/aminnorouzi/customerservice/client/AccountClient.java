package com.aminnorouzi.customerservice.client;

import com.aminnorouzi.customerservice.model.account.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/api/v1/accounts/{customerId}")
    List<Account> getAccountsByCustomer(@PathVariable("customerId") Long customerId);
}
