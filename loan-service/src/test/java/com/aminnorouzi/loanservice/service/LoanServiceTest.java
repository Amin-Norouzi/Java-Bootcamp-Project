package com.aminnorouzi.loanservice.service;

import com.aminnorouzi.loanservice.client.AccountClient;
import com.aminnorouzi.loanservice.exception.IllegalLoanStatusException;
import com.aminnorouzi.loanservice.exception.LoanNotFoundException;
import com.aminnorouzi.loanservice.exception.LoanPaymentNotAvailableException;
import com.aminnorouzi.loanservice.exception.NotValidLoanAccountException;
import com.aminnorouzi.loanservice.model.*;
import com.aminnorouzi.loanservice.model.transaction.Transaction;
import com.aminnorouzi.loanservice.repository.LoanRepository;
import com.aminnorouzi.loanservice.util.LoanCalculator;
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
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private AccountClient accountClient;
    @InjectMocks
    private LoanService loanService;

    @Test
    void shouldCreateLoan() {
        // given
        long accountId = 10;
        LoanRequest request = LoanRequest.builder()
                .amount(BigDecimal.valueOf(20_000_000))
                .totalCount(12)
                .rate(Rate.EIGHTEEN)
                .type(Type.CREDIT_CARD)
                .accountId(accountId)
                .build();

        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        BigDecimal calculatedInstallment = LoanCalculator.getInstallment(
                request.getAmount(),
                request.getTotalCount(),
                request.getRate().getPercentage());

        Loan loan = Loan.builder()
                .amount(request.getAmount())
                .installment(calculatedInstallment)
                .totalCount(request.getTotalCount())
                .remainingCount(request.getTotalCount())
                .rate(request.getRate())
                .type(request.getType())
                .status(Status.OPEN)
                .accountId(request.getAccountId())
                .createdAt(LocalDate.now())
                .build();

        // when
        loanService.createLoan(request);

        // then
        ArgumentCaptor<Loan> loanArgumentCaptor = ArgumentCaptor.forClass(Loan.class);

        verify(loanRepository, times(1)).save(loanArgumentCaptor.capture());

        Loan capturedLoan = loanArgumentCaptor.getValue();

        assertThat(capturedLoan).isEqualTo(loan);
    }

    @Test
    void shouldNotCreateLoanWhenAccountsAreInvalid() {
        // given
        long accountId = 10;
        LoanRequest request = LoanRequest.builder()
                .amount(BigDecimal.valueOf(20_000_000))
                .totalCount(12)
                .rate(Rate.EIGHTEEN)
                .type(Type.CREDIT_CARD)
                .accountId(accountId)
                .build();

        given(accountClient.verifyAccount(accountId))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(NotValidLoanAccountException.class)
                .hasMessageContaining(String.format("Account: %s not found!", accountId));

        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldGetLoanById() {
        // given
        long id = 10;
        given(loanRepository.findById(id))
                .willReturn(Optional.of(new Loan()));

        // when
        // then
        loanService.getLoanById(id);

        verify(loanRepository, times(1)).findById(id);
    }

    @Test
    void shouldNotGetLoanByIdWhenNotFound() {
        // given
        long id = 10;
        given(loanRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> loanService.getLoanById(id))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining(String.format("Loan: %s not found!", id));

        verify(loanRepository, times(1)).findById(id);
    }

    @Test
    void shouldGetAllLoans() {
        // when
        loanService.getAllLoans();

        // then
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    void shouldGetLoansByAccountId() {
        // given
        long accountId = 10;
        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        given(loanRepository.findByAccountIdEquals(accountId))
                .willReturn(List.of(new Loan()));

        // when
        loanService.getLoansByAccountId(accountId);

        // then
        verify(loanRepository, times(1)).findByAccountIdEquals(accountId);
        verify(accountClient, times(1)).verifyAccount(accountId);
    }

    @Test
    void shouldNotGetLoansByAccountIdWhenAccountNotFound() {
        // given
        long accountId = 10;
        given(accountClient.verifyAccount(accountId))
                .willReturn(false);

        // when
        assertThatThrownBy(() -> loanService.getLoansByAccountId(accountId))
                .isInstanceOf(NotValidLoanAccountException.class)
                .hasMessageContaining(String.format("Account: %s not found!", accountId));

        // then
        verify(loanRepository, never()).findByAccountIdEquals(accountId);
        verify(accountClient, times(1)).verifyAccount(accountId);
    }

    @Test
    void shouldCalculate() {
        // given
        BigDecimal amount = BigDecimal.valueOf(20_000_000);
        int count = 12;
        String rate = "four";
        BigDecimal calculated = LoanCalculator.getInstallment(
                amount,
                count,
                Rate.valueOf(rate.toUpperCase()).getPercentage());

        // when
        // then
        assertThat(loanService.calculate(amount, count, rate)).isEqualTo(calculated);
    }

    @Test
    void shouldNotCalculateWhenRateIsInvalid() {
        // given
        BigDecimal amount = BigDecimal.valueOf(20_000_000);
        int count = 12;
        String rate = "four???";

        // when
        // then
        assertThatThrownBy(() -> loanService.calculate(amount, count, rate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("No enum constant %s.FOUR???", Rate.class.getName()));
    }

    @Test
    void shouldPay() {
        // given
        long accountId = 10;
        String note = "Loan payment";
        BigDecimal amount = BigDecimal.valueOf(100000);
        given(loanRepository.findById(any(Long.class)))
                .willReturn(Optional.of(Loan.builder()
                        .accountId(accountId)
                        .status(Status.OPEN)
                        .totalCount(12)
                        .remainingCount(10)
                        .installment(BigDecimal.valueOf(100000))
                        .build()));

        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        given(accountClient.withdraw(accountId, amount, note))
                .willReturn(new Transaction());

        // when
        // then
        loanService.pay(accountId, any(Long.class));

        verify(loanRepository, times(1)).findById(any(Long.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
        verify(accountClient, times(1)).verifyAccount(accountId);
        verify(accountClient, times(1)).withdraw(accountId, amount, note);
    }

    @Test
    void shouldNotPayWhenLoanNotFound() {
        // given
        long id = 10;
        given(loanRepository.findById(id))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> loanService.pay(any(Long.class), id))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining(String.format("Loan: %s not found!", id));

        verify(loanRepository, times(1)).findById(id);
        verify(loanRepository, never()).save(any(Loan.class));
        verify(accountClient, never()).verifyAccount(any(Long.class));
        verify(accountClient, never()).withdraw(any(Long.class), any(BigDecimal.class), any(String.class));
    }

    @Test
    void shouldNotPayWhenAccountNotFound() {
        // given
        long accountId = 10;
        given(loanRepository.findById(any(Long.class)))
                .willReturn(Optional.of(Loan.builder()
                        .status(Status.OPEN)
                        .build()));

        given(accountClient.verifyAccount(accountId))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> loanService.pay(accountId, any(Long.class)))
                .isInstanceOf(NotValidLoanAccountException.class)
                .hasMessageContaining(String.format("Account: %s not found!", accountId));

        verify(loanRepository, times(1)).findById(any(Long.class));
        verify(loanRepository, never()).save(any(Loan.class));
        verify(accountClient, times(1)).verifyAccount(accountId);
        verify(accountClient, never()).withdraw(any(Long.class), any(BigDecimal.class), any(String.class));
    }

    @Test
    void shouldNotPayWhenLoanIsClosed() {
        // given
        long loanId = 10, accountId = 1;
        given(loanRepository.findById(loanId))
                .willReturn(Optional.of(Loan.builder()
                        .id(loanId)
                        .accountId(accountId)
                        .status(Status.CLOSED)
                        .build()));

        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> loanService.pay(accountId, loanId))
                .isInstanceOf(IllegalLoanStatusException.class)
                .hasMessageContaining(String.format("Loan: %s is closed!", loanId));

        verify(loanRepository, times(1)).findById(loanId);
        verify(loanRepository, never()).save(any(Loan.class));
        verify(accountClient, times(1)).verifyAccount(accountId);
        verify(accountClient, never()).withdraw(any(Long.class), any(BigDecimal.class), any(String.class));
    }

    @Test
    void shouldNotPayWhenPaymentNotAvailable() {
        // given
        long accountId = 10;
        String note = "Loan payment";
        BigDecimal amount = BigDecimal.valueOf(100000);
        given(loanRepository.findById(any(Long.class)))
                .willReturn(Optional.of(Loan.builder()
                        .accountId(accountId)
                        .status(Status.OPEN)
                        .installment(BigDecimal.valueOf(100000))
                        .build()));

        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        given(accountClient.withdraw(accountId, amount, note))
                .willThrow(RuntimeException.class);

        // when
        // then
        assertThatThrownBy(() -> loanService.pay(accountId, any(Long.class)))
                .isInstanceOf(LoanPaymentNotAvailableException.class)
                .hasMessageContaining(String.format("Account: %s is not available for withdrawal!", accountId));

        verify(loanRepository, times(1)).findById(any(Long.class));
        verify(loanRepository, never()).save(any(Loan.class));
        verify(accountClient, times(1)).verifyAccount(accountId);
        verify(accountClient, times(1)).withdraw(accountId, amount, note);
    }

    @Test
    void shouldNotPayWhenAccountIsInvalid() {
        // given
        long accountId = 10;
        String note = "Loan payment";
        BigDecimal amount = BigDecimal.valueOf(100000);
        given(loanRepository.findById(any(Long.class)))
                .willReturn(Optional.of(Loan.builder()
                        .accountId(4L)
                        .status(Status.OPEN)
                        .installment(BigDecimal.valueOf(100000))
                        .build()));

        given(accountClient.verifyAccount(accountId))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> loanService.pay(accountId, any(Long.class)))
                .isInstanceOf(NotValidLoanAccountException.class)
                .hasMessageContaining(String.format("Account: %s dose not belong to this loan!", accountId));

        verify(loanRepository, times(1)).findById(any(Long.class));
        verify(loanRepository, never()).save(any(Loan.class));
        verify(accountClient, times(1)).verifyAccount(accountId);
        verify(accountClient, never()).withdraw(accountId, amount, note);
    }
}