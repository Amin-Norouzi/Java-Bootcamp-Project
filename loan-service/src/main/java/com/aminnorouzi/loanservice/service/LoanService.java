package com.aminnorouzi.loanservice.service;

import com.aminnorouzi.loanservice.client.AccountClient;
import com.aminnorouzi.loanservice.exception.IllegalLoanStatusException;
import com.aminnorouzi.loanservice.exception.LoanNotFoundException;
import com.aminnorouzi.loanservice.exception.LoanPaymentNotAvailableException;
import com.aminnorouzi.loanservice.exception.NotValidLoanAccountException;
import com.aminnorouzi.loanservice.model.Loan;
import com.aminnorouzi.loanservice.model.LoanRequest;
import com.aminnorouzi.loanservice.model.Rate;
import com.aminnorouzi.loanservice.model.Status;
import com.aminnorouzi.loanservice.repository.LoanRepository;
import com.aminnorouzi.loanservice.util.LoanCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountClient accountClient;

    public Loan createLoan(LoanRequest request) {
        verifyAccount(request.getAccountId());

        Loan loan = Loan.builder()
                .amount(request.getAmount())
                .installment(calculate(request))
                .totalCount(request.getTotalCount())
                .remainingCount(request.getTotalCount())
                .rate(request.getRate())
                .type(request.getType())
                .status(Status.OPEN)
                .accountId(request.getAccountId())
                .createdAt(LocalDate.now())
                .build();

        Loan saved = loanRepository.save(loan);

        log.info("Created new loan: {}", saved);
        return saved;
    }

    public Loan getLoanById(Long id) {
        Loan found = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(String.format("Loan: %s not found!", id)));

        log.info("Found a loan: id={}, {}", id, found);
        return found;
    }

    public List<Loan> getAllLoans() {
        List<Loan> found = loanRepository.findAll();

        log.info("Found all loans: {}", found);
        return found;
    }

    public List<Loan> getLoansByAccountId(Long accountId) {
        verifyAccount(accountId);

        List<Loan> found = loanRepository.findByAccountIdEquals(accountId);

        log.info("Found an account loans: accountId={}, {}", accountId, found);
        return found;
    }

    public BigDecimal calculate(BigDecimal amount, Integer count, String rate) {
        BigDecimal loan = LoanCalculator.getInstallment(amount, count, Rate.valueOf(rate.toUpperCase()).getPercentage());

        log.info("Calculated a loan: {}", loan);
        return loan;
    }

    @Transactional
    public Loan pay(Long accountId, Long loanId) {
        Loan loan = getLoanById(loanId);

        validateAccount(accountId, loan);
        validateLoan(loan);

        withdraw(accountId, loan.getInstallment());
        updateLoanRemainingCount(loan);

        Loan updated = updateAndSaveLoan(loan);

        log.info("Paid a loan: accountId={}, {}", accountId, updated);
        return updated;
    }

    private void validateAccount(Long accountId, Loan loan) {
        verifyAccount(accountId);
        if (!loan.getAccountId().equals(accountId)) {
            throw new NotValidLoanAccountException(String.format("Account: %s dose not belong to this loan!", accountId));
        }
    }

    private void validateLoan(Loan loan) {
        if (loan.getStatus().equals(Status.CLOSED)) {
            throw new IllegalLoanStatusException(String.format("Loan: %s is closed!", loan.getId()));
        }
    }

    private BigDecimal calculate(LoanRequest request) {
        return calculate(request.getAmount(),
                request.getTotalCount(),
                request.getRate().toString());
    }

    private void withdraw(Long id, BigDecimal amount) {
        try {
            accountClient.withdraw(id, amount, "Loan payment");
        } catch (RuntimeException exception) {
            throw new LoanPaymentNotAvailableException(String.format("Account: %s is not available for withdrawal!", id));
        }
    }

    private Loan updateAndSaveLoan(Loan loan) {
        Status status = checkLoanStatus(loan);
        if (status != null) {
            loan.setStatus(status);
        }

        return loanRepository.save(loan);
    }

    private void updateLoanRemainingCount(Loan loan) {
        loan.setRemainingCount(loan.getRemainingCount() - 1);
    }

    private Status checkLoanStatus(Loan loan) {
        if (loan.getRemainingCount() == (loan.getTotalCount() - 1)) {
            return Status.PAYING;
        }

        if (loan.getRemainingCount() == 0) {
            return Status.CLOSED;
        }

        return null;
    }

    private void verifyAccount(Long id) {
        boolean exists = accountClient.verifyAccount(id);
        if (!exists) {
            throw new NotValidLoanAccountException(String.format("Account: %s not found!", id));
        }
    }
}
