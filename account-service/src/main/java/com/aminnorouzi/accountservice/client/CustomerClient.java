package com.aminnorouzi.accountservice.client;

import com.aminnorouzi.accountservice.model.customer.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/verify/{id}")
    boolean verifyCustomer(@PathVariable("id") Long id);

    @GetMapping("/api/v1/customers/{id}")
    Customer getCustomerById(@PathVariable("id") Long id);
}
