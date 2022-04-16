package com.aminnorouzi.accountservice.service;

import com.aminnorouzi.accountservice.client.CustomerClient;
import com.aminnorouzi.accountservice.client.TransactionClient;
import com.aminnorouzi.accountservice.exception.AccountNotFoundException;
import com.aminnorouzi.accountservice.exception.IllegalAccountStatusException;
import com.aminnorouzi.accountservice.exception.NotEnoughAccountBalanceException;
import com.aminnorouzi.accountservice.exception.NotValidAccountCustomerException;
import com.aminnorouzi.accountservice.model.Account;
import com.aminnorouzi.accountservice.model.AccountRequest;
import com.aminnorouzi.accountservice.model.Status;
import com.aminnorouzi.accountservice.model.Type;
import com.aminnorouzi.accountservice.model.customer.Customer;
import com.aminnorouzi.accountservice.model.transaction.Transaction;
import com.aminnorouzi.accountservice.model.transaction.TransactionRequest;
import com.aminnorouzi.accountservice.repository.AccountRepository;
import com.aminnorouzi.accountservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aminnorouzi.accountservice.model.transaction.Status.FAILED;
import static com.aminnorouzi.accountservice.model.transaction.Status.SUCCEED;
import static com.aminnorouzi.accountservice.model.transaction.Type.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final TransactionClient transactionClient;

    public Account createAccount(AccountRequest request) {
        verifyCustomers(request.getCustomerIds());

        Account account = Account.builder()
                .customerIds(request.getCustomerIds())
                .title(generateTitle(request))
                .balance(request.getBalance())
                .type(request.getType())
                .currency(request.getCurrency())
                .status(Status.OPEN)
                .createdAt(LocalDate.now())
                .build();

        Account saved = accountRepository.save(account);

        log.info("Created new account: {}", saved);
        return saved;
    }

    public boolean verifyAccount(Long id) {
        boolean verified = accountRepository.existsByIdEquals(id);

        log.info("Verified an account: id={}, {}", id, verified);
        return verified;
    }

    public Account changeAccountStatus(Long id, String status) {
        Account account = getAccountById(id);
        validateAccountStatus(account);

        account.setStatus(Status.valueOf(status.toUpperCase()));
        if (account.getStatus().equals(Status.CLOSED)) {
            account.setClosedAt(LocalDate.now());
        }

        Account updated = accountRepository.save(account);

        log.info("Changed an account status: id={}, {}", id, updated);
        return updated;
    }

    public BigDecimal showAccountBalance(Long id) {
        Account account = getAccountById(id);

        log.info("Showed an account balance: id={}, {}", id, account);
        return account.getBalance();
    }

    public Account getAccountById(Long id) {
        Account found = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(String.format("Account: %s not found!", id)));

        log.info("Found an account: id={}, {}", id, found);
        return found;
    }

    public List<Account> getAllAccounts() {
        List<Account> found = accountRepository.findAll();

        log.info("Found all accounts: {}", found);
        return found;
    }

    public List<Account> getAccountsByCustomerId(Long customerId) {
        Customer customer = getCustomerById(customerId);

        List<Account> found = accountRepository.findByCustomerId(customerId);
        found.forEach(account -> account.setTitle(generateTitle(customer.getFullName(), account.getType())));

        log.info("Found a customer accounts: customerId={}, {}", customerId, found);
        return found;
    }

    public List<Customer> getCustomersByAccountId(Long accountId) {
        List<Customer> found = new ArrayList<>();

        List<Long> customerIds = getAccountById(accountId).getCustomerIds();
        customerIds.forEach(id -> found.add(getCustomerById(id)));

        log.info("Found an account customers: accountId={}, {}", accountId, found);
        return found;
    }

    @Transactional
    public Transaction deposit(Long id, BigDecimal amount, String note) {
        try {
            Account account = getAndValidateAccountForDeposit(id);
            updateAccountBalance(account, amount);

            Transaction transaction = logTransaction(new TransactionRequest(id, id, amount, note, DEPOSIT, SUCCEED));

            log.info("Created new transaction: {}", transaction);
            return transaction;
        } catch (IllegalAccountStatusException exception) {
            logTransaction(new TransactionRequest(id, id, amount, note, DEPOSIT, FAILED));

            throw exception;
        }
    }

    @Transactional
    public Transaction withdraw(Long id, BigDecimal amount, String note) {
        try {
            Account account = getAndValidateAccountForWithdrawal(id, amount);
            updateAccountBalance(account, amount.negate());

            Transaction transaction = logTransaction(new TransactionRequest(id, id, amount, note, WITHDRAWAL, SUCCEED));

            log.info("Created new transaction: {}", transaction);
            return transaction;
        } catch (IllegalAccountStatusException | NotEnoughAccountBalanceException exception) {
            logTransaction(new TransactionRequest(id, id, amount, note, WITHDRAWAL, FAILED));

            throw exception;
        }
    }

    @Transactional
    public Transaction transfer(Long senderId, Long receiverId, BigDecimal amount, String note) {
        try {
            Account sender = getAndValidateAccountForWithdrawal(senderId, amount);
            Account receiver = getAndValidateAccountForDeposit(receiverId);

            updateAccountBalance(sender, amount.negate());
            updateAccountBalance(receiver, amount);

            Transaction transaction = logTransaction(new TransactionRequest(senderId, receiverId, amount, note, TRANSFER, SUCCEED));

            log.info("Created new transaction: {}", transaction);
            return transaction;
        } catch (IllegalAccountStatusException | NotEnoughAccountBalanceException exception) {
            logTransaction(new TransactionRequest(senderId, receiverId, amount, note, TRANSFER, FAILED));

            throw exception;
        }
    }

    private Account getAndValidateAccountForDeposit(Long id) {
        Account account = getAccountById(id);

        if (account.getStatus().equals(Status.CLOSED)
                || account.getStatus().equals(Status.BLOCKED)
                || account.getStatus().equals(Status.DEPOSIT_BLOCKED)) {
            throw new IllegalAccountStatusException(String.format("Account: %s is not available for deposit!", id));
        }

        return account;
    }

    private Account getAndValidateAccountForWithdrawal(Long id, BigDecimal amount) {
        Account account = getAccountById(id);

        if (account.getStatus().equals(Status.CLOSED)
                || account.getStatus().equals(Status.BLOCKED)
                || account.getStatus().equals(Status.WITHDRAWAL_BLOCKED)) {
            throw new IllegalAccountStatusException(String.format("Account: %s is not available for withdrawal!", id));
        }

        if (account.getBalance().doubleValue() < amount.doubleValue()) {
            throw new NotEnoughAccountBalanceException(String.format("Account: %s does not have enough balance!", id));
        }

        return account;
    }

    private void updateAccountBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);
    }

    private void validateAccountStatus(Account account) {
        if (account.getStatus().equals(Status.CLOSED)) {
            throw new IllegalAccountStatusException(String.format("Account: %s is closed!", account.getId()));
        }
    }

    private String generateTitle(AccountRequest request) {
        Customer customer = getCustomerById(request.getCustomerIds().get(0));
        return generateTitle(customer.getFullName(), request.getType());
    }

    private String generateTitle(String fullName, Type type) {
        return StringUtils.generateTitle(fullName, type);
    }

    private void verifyCustomers(List<Long> customerIds) {
        customerIds.forEach(id -> {
            boolean exists = customerClient.verifyCustomer(id);
            if (!exists) {
                throw new NotValidAccountCustomerException(String.format("Customer: %s not found!", id));
            }
        });
    }

    private Customer getCustomerById(Long customerId) {
        return Objects.requireNonNull(customerClient.getCustomerById(customerId));
    }

    private Transaction logTransaction(TransactionRequest request) {
        return Objects.requireNonNull(transactionClient.createTransaction(request));
    }
}
