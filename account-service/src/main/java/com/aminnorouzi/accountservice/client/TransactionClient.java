package com.aminnorouzi.accountservice.client;

import com.aminnorouzi.accountservice.model.transaction.Transaction;
import com.aminnorouzi.accountservice.model.transaction.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service")
public interface TransactionClient {

    @PostMapping("/api/v1/transactions")
    Transaction createTransaction(@RequestBody TransactionRequest request);
}
