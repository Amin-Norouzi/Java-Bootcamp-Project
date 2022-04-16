package com.aminnorouzi.customerservice.service;

import com.aminnorouzi.customerservice.client.AccountClient;
import com.aminnorouzi.customerservice.exception.CustomerNotFoundException;
import com.aminnorouzi.customerservice.exception.IllegalCustomerDeleteException;
import com.aminnorouzi.customerservice.exception.IllegalNationalCodeException;
import com.aminnorouzi.customerservice.exception.NationalCodeWasTakenException;
import com.aminnorouzi.customerservice.model.Customer;
import com.aminnorouzi.customerservice.model.CustomerRequest;
import com.aminnorouzi.customerservice.model.Status;
import com.aminnorouzi.customerservice.model.account.Account;
import com.aminnorouzi.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;

    public Customer createCustomer(CustomerRequest request) {
        validateNationalCode(request.getNationalCode());

        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .nationalCode(request.getNationalCode())
                .phoneNumber(request.getPhoneNumber())
                .type(request.getType())
                .status(Status.ACTIVE)
                .birthDate(request.getBirthDate())
                .createdAt(LocalDate.now())
                .build();

        Customer saved = customerRepository.save(customer);

        log.info("Created new customer: {}", saved);
        return saved;
    }

    public void deleteCustomer(Long id) {
        List<Account> accounts = getAccountsByCustomerId(id);
        if (!accounts.isEmpty()) {
            throw new IllegalCustomerDeleteException(String.format("Customer: %s has some accounts!", id));
        }

        customerRepository.deleteById(id);

        log.info("Deleted a customer: id={}", id);
    }

    public boolean verifyCustomer(Long id) {
        boolean verified = customerRepository.existsByIdEquals(id);

        log.info("Verified a customer: id={}, {}", id, verified);
        return verified;
    }

    public Customer changeCustomerStatus(Long id, String status) {
        Customer customer = getCustomerById(id);
        customer.setStatus(Status.valueOf(status.toUpperCase()));

        Customer updated = customerRepository.save(customer);

        log.info("Changed a customer status: id={}, {}", id, updated);
        return updated;
    }

    public Customer getCustomerById(Long id) {
        Customer found = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer: %s not found!", id)));

        log.info("Found a customer: id={}, {}", id, found);
        return found;
    }

    public List<Customer> getCustomersByFilter(Specification<Customer> specs) {
        List<Customer> found = customerRepository.findAll(Specification.where(specs));

        log.info("Found customers: filter={}, {}", specs, found);
        return found;
    }


    public List<Account> getAccountsByCustomerId(Long customerId) {
        boolean exists = verifyCustomer(customerId);
        if (!exists) {
            throw new CustomerNotFoundException(String.format("Customer: %s not found!", customerId));
        }

        List<Account> found = accountClient.getAccountsByCustomer(customerId);

        log.info("Found a customer accounts: customerId={}, {}", customerId, found);
        return found;
    }

    private void validateNationalCode(String nationalCode) {
        boolean exists = customerRepository.existsByNationalCodeEquals(nationalCode);
        if (exists) {
            throw new NationalCodeWasTakenException(
                    String.format("National code: %s is already taken!", nationalCode));
        }

        if (nationalCode == null || nationalCode.isEmpty()) {
            throw new IllegalNationalCodeException("National code can not be null or empty!");
        }

        if (!nationalCode.matches("\\d{10}")) {
            throw new IllegalNationalCodeException("National code must be 10 digits only!");
        }
    }
}