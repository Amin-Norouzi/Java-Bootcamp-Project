package com.aminnorouzi.accountservice.service;

import com.aminnorouzi.accountservice.client.CustomerClient;
import com.aminnorouzi.accountservice.client.TransactionClient;
import com.aminnorouzi.accountservice.exception.AccountNotFoundException;
import com.aminnorouzi.accountservice.exception.IllegalAccountStatusException;
import com.aminnorouzi.accountservice.exception.NotEnoughAccountBalanceException;
import com.aminnorouzi.accountservice.exception.NotValidAccountCustomerException;
import com.aminnorouzi.accountservice.model.*;
import com.aminnorouzi.accountservice.model.customer.Customer;
import com.aminnorouzi.accountservice.model.transaction.Transaction;
import com.aminnorouzi.accountservice.model.transaction.TransactionRequest;
import com.aminnorouzi.accountservice.repository.AccountRepository;
import com.aminnorouzi.accountservice.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private TransactionClient transactionClient;
    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {
        // given
        AccountRequest request = AccountRequest.builder()
                .customerIds(List.of(10L, 11L))
                .type(Type.CHECKING)
                .currency(Currency.IRR)
                .balance(BigDecimal.ZERO)
                .build();

        given(customerClient.verifyCustomer(any(Long.class)))
                .willReturn(true);

        given(customerClient.getCustomerById(any(Long.class)))
                .willReturn(Customer.builder()
                        .fullName("test")
                        .build());

        String generatedTitle = StringUtils.generateTitle(
                customerClient.getCustomerById(request.getCustomerIds().get(0)).getFullName(),
                request.getType());

        Account account = Account.builder()
                .title(generatedTitle)
                .balance(request.getBalance())
                .status(Status.OPEN)
                .type(request.getType())
                .currency(request.getCurrency())
                .createdAt(LocalDate.now())
                .customerIds(request.getCustomerIds())
                .build();

        // when
        accountService.createAccount(request);

        // then
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepository, times(1)).save(accountArgumentCaptor.capture());

        Account capturedAccount = accountArgumentCaptor.getValue();

        assertThat(capturedAccount).isEqualTo(account);
    }

    @Test
    void shouldNotCreateAccountWhenCustomersAreInvalid() {
        // given
        AccountRequest request = AccountRequest.builder()
                .customerIds(List.of(1L, 20L))
                .type(Type.CHECKING)
                .currency(Currency.IRR)
                .balance(BigDecimal.ZERO)
                .build();

        given(customerClient.verifyCustomer(any(Long.class)))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(NotValidAccountCustomerException.class)
                .hasMessageContaining(String.format("Customer: %s not found!", request.getCustomerIds().get(0)));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldVerifyAccount() {
        // given
        long id = 10;
        given(accountRepository.existsByIdEquals(id))
                .willReturn(true);

        // when
        // then
        assertThat(accountService.verifyAccount(id)).isTrue();

        verify(accountRepository, times(1)).existsByIdEquals(id);
    }

    @Test
    void shouldNotVerifyAccountWhenNotFound() {
        // given
        long id = 10;
        given(accountRepository.existsByIdEquals(id))
                .willReturn(false);

        // when
        // then
        assertThat(accountService.verifyAccount(id)).isFalse();

        verify(accountRepository, times(1)).existsByIdEquals(id);
    }

    @Test
    void shouldChangeAccountStatus() {
        // given
        long id = 10;
        String status = "deposit_blocked";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .build()));

        // when
        // then
        accountService.changeAccountStatus(id, status);

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldChangeAccountStatusAndAccountClosedAtWhenNewStatusIsClosed() {
        // given
        long id = 10;
        String status = "closed";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .build()));

        // when
        // then
        accountService.changeAccountStatus(id, status);

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void shouldNotChangeAccountStatusWhenAccountNotFound() {
        // given
        long id = 10;
        String status = "deposit_blocked";
        given(accountRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> accountService.changeAccountStatus(id, status))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", id));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldNotChangeAccountStatusWhenIsClosed() {
        // given
        long id = 10;
        String status = "deposit_blocked";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .id(id)
                        .status(Status.CLOSED)
                        .build()));

        // when
        // then
        assertThatThrownBy(() -> accountService.changeAccountStatus(id, status))
                .isInstanceOf(IllegalAccountStatusException.class)
                .hasMessageContaining(String.format("Account: %s is closed!", id));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldNotChangeAccountStatusWhenIsInvalid() {
        // given
        long id = 10;
        String status = "deposit_blocked???";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .build()));

        // when
        // then
        assertThatThrownBy(() -> accountService.changeAccountStatus(id, status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("No enum constant %s.DEPOSIT_BLOCKED???", Status.class.getName()));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldShowAccountBalance() {
        // given
        long id = 10;
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .balance(BigDecimal.ZERO)
                        .build()));

        // when
        // then
        assertThat(accountService.showAccountBalance(id)).isEqualTo(BigDecimal.ZERO);

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void shouldNotShowAccountBalanceWhenAccountNotFound() {
        // given
        long id = 10;
        given(accountRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> accountService.showAccountBalance(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", id));

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void shouldGetAccountById() {
        // given
        long id = 10;
        given(accountRepository.findById(id))
                .willReturn(Optional.of(new Account()));

        // when
        // then
        accountService.getAccountById(id);

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void shouldNotGetAccountByIdWhenNotFound() {
        // given
        long id = 10;
        given(accountRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> accountService.getAccountById(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", id));

        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    void shouldGetAllAccounts() {
        // when
        accountService.getAllAccounts();

        // then
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAccountsByCustomerId() {
        // given
        long customerId = 10;
        given(customerClient.getCustomerById(customerId))
                .willReturn(new Customer());

        // when
        accountService.getAccountsByCustomerId(customerId);

        // then
        verify(accountRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void shouldGetCustomersByAccountId() {
        // given
        long id = 10;
        List<Long> customerIds = List.of(1L, 2L);
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .customerIds(customerIds)
                        .build()));

        given(customerClient.getCustomerById(any(Long.class)))
                .willReturn(Customer.builder()
                        .fullName("test")
                        .build());

        // when
        accountService.getCustomersByAccountId(id);

        // then
        verify(accountRepository, times(1)).findById(id);
        verify(customerClient, times(2)).getCustomerById(any(Long.class));
    }

    @Test
    void shouldNotGetCustomersByAccountIdWhenAccountNotFound() {
        // given
        long id = 10;
        given(accountRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> accountService.getCustomersByAccountId(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", id));

        // then
        verify(accountRepository, times(1)).findById(id);
        verify(customerClient, never()).getCustomerById(any(Long.class));
    }

    @Test
    void shouldDeposit() {
        // given
        long id = 10;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        accountService.deposit(id, amount, note);

        // then
        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(transactionClient, times(1)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotDepositWhenAccountNotFound() {
        // given
        long id = 10;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> accountService.deposit(id, amount, note))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", id));

        // then
        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, never()).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotDepositWhenAccountIsNotAvailable() {
        // given
        List<Long> ids = List.of(10L, 11L, 12L);
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(ids.get(0)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(0))
                        .status(Status.CLOSED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(1)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(1))
                        .status(Status.BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(2)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(2))
                        .status(Status.DEPOSIT_BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        ids.forEach(id -> assertThatThrownBy(() -> accountService.deposit(id, amount, note))
                .isInstanceOf(IllegalAccountStatusException.class)
                .hasMessageContaining(String.format("Account: %s is not available for deposit!", id)));

        // then
        verify(accountRepository, times(3)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, times(3)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldWithdraw() {
        // given
        long id = 10;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .balance(BigDecimal.valueOf(20000))
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        accountService.withdraw(id, amount, note);

        // then
        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(transactionClient, times(1)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotWithdrawWhenAccountNotFound() {
        // given
        long id = 10;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> accountService.withdraw(id, amount, note))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", id));

        // then
        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, never()).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotWithdrawWhenAccountIsNotAvailable() {
        // given
        List<Long> ids = List.of(10L, 11L, 12L);
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(ids.get(0)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(0))
                        .status(Status.CLOSED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(1)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(1))
                        .status(Status.BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(2)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(2))
                        .status(Status.WITHDRAWAL_BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        ids.forEach(id -> assertThatThrownBy(() -> accountService.withdraw(id, amount, note))
                .isInstanceOf(IllegalAccountStatusException.class)
                .hasMessageContaining(String.format("Account: %s is not available for withdrawal!", id)));

        // then
        verify(accountRepository, times(3)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, times(3)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotWithdrawWhenAccountBalanceNotEnough() {
        // given
        long id = 10;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(id))
                .willReturn(Optional.of(Account.builder()
                        .id(id)
                        .status(Status.OPEN)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        assertThatThrownBy(() -> accountService.withdraw(id, amount, note))
                .isInstanceOf(NotEnoughAccountBalanceException.class)
                .hasMessageContaining((String.format("Account: %s does not have enough balance!", id)));

        // then
        verify(accountRepository, times(1)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, times(1)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldTransfer() {
        // given
        long senderId = 10, receiverId = 11;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(senderId))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .balance(BigDecimal.valueOf(20000))
                        .build()));

        given(accountRepository.findById(receiverId))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        accountService.transfer(senderId, receiverId, amount, note);

        // then
        verify(accountRepository, times(2)).findById(any(Long.class));
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionClient, times(1)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotTransferWhenSenderAccountNotFound() {
        // given
        long senderId = 10, receiverId = 11;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(senderId))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> accountService.transfer(senderId, receiverId, amount, note))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", senderId));

        // then
        verify(accountRepository, times(1)).findById(senderId);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, never()).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotTransferWhenReceiverAccountNotFound() {
        // given
        long senderId = 10, receiverId = 11;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(senderId))
                .willReturn(Optional.of(Account.builder()
                        .status(Status.OPEN)
                        .balance(BigDecimal.valueOf(20000))
                        .build()));

        given(accountRepository.findById(receiverId))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> accountService.transfer(senderId, receiverId, amount, note))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining(String.format("Account: %s not found!", receiverId));

        // then
        verify(accountRepository, times(2)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, never()).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotTransferWhenSenderAccountIsNotAvailable() {
        // given
        List<Long> ids = List.of(10L, 11L, 12L);
        long receiverId = 13;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(ids.get(0)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(0))
                        .status(Status.CLOSED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(1)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(1))
                        .status(Status.BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(2)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(2))
                        .status(Status.WITHDRAWAL_BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        ids.forEach(id -> assertThatThrownBy(() -> accountService.transfer(id, receiverId, amount, note))
                .isInstanceOf(IllegalAccountStatusException.class)
                .hasMessageContaining(String.format("Account: %s is not available for withdrawal!", id)));

        // then
        verify(accountRepository, times(3)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, times(3)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotTransferWhenReceiverAccountIsNotAvailable() {
        // given
        List<Long> ids = List.of(10L, 11L, 12L);
        long senderId = 13;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(senderId))
                .willReturn(Optional.of(Account.builder()
                        .id(senderId)
                        .status(Status.OPEN)
                        .balance(BigDecimal.valueOf(20000))
                        .build()));

        given(accountRepository.findById(ids.get(0)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(0))
                        .status(Status.CLOSED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(1)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(1))
                        .status(Status.BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(accountRepository.findById(ids.get(2)))
                .willReturn(Optional.of(Account.builder()
                        .id(ids.get(2))
                        .status(Status.DEPOSIT_BLOCKED)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        ids.forEach(id -> assertThatThrownBy(() -> accountService.transfer(senderId, id, amount, note))
                .isInstanceOf(IllegalAccountStatusException.class)
                .hasMessageContaining(String.format("Account: %s is not available for deposit!", id)));

        // then
        verify(accountRepository, times(6)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, times(3)).createTransaction(any(TransactionRequest.class));
    }

    @Test
    void shouldNotTransferWhenSenderAccountBalanceNotEnough() {
        // given
        long senderId = 10, receiverId = 11;
        BigDecimal amount = BigDecimal.valueOf(10000);
        String note = "test";
        given(accountRepository.findById(senderId))
                .willReturn(Optional.of(Account.builder()
                        .id(senderId)
                        .status(Status.OPEN)
                        .balance(BigDecimal.ZERO)
                        .build()));

        given(transactionClient.createTransaction(any(TransactionRequest.class)))
                .willReturn(new Transaction());

        // when
        assertThatThrownBy(() -> accountService.transfer(senderId, receiverId, amount, note))
                .isInstanceOf(NotEnoughAccountBalanceException.class)
                .hasMessageContaining((String.format("Account: %s does not have enough balance!", senderId)));

        // then
        verify(accountRepository, times(1)).findById(any(Long.class));
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionClient, times(1)).createTransaction(any(TransactionRequest.class));
    }
}