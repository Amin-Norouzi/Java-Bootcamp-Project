package com.aminnorouzi.customerservice.controller;

import com.aminnorouzi.customerservice.model.Customer;
import com.aminnorouzi.customerservice.model.CustomerRequest;
import com.aminnorouzi.customerservice.model.account.Account;
import com.aminnorouzi.customerservice.service.CustomerService;
import com.sipios.springsearch.anotation.SearchSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Customer createCustomer(@RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("delete/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @GetMapping("/verify/{id}")
    public boolean verifyCustomer(@PathVariable("id") Long id) {
        return customerService.verifyCustomer(id);
    }

    @PutMapping("/status/{id}")
    public Customer changeCustomerStatus(@PathVariable Long id,
                                         @RequestParam("status") String status) {
        return customerService.changeCustomerStatus(id, status);
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable("id") Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public List<Customer> getCustomersByFilter(@SearchSpec Specification<Customer> specs) {
        return customerService.getCustomersByFilter(specs);
    }

    @GetMapping("/accounts/{customerId}")
    public List<Account> getAccountsByCustomer(@PathVariable("customerId") Long customerId) {
        return customerService.getAccountsByCustomerId(customerId);
    }
}
