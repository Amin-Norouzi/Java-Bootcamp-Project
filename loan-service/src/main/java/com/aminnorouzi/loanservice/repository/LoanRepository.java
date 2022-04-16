package com.aminnorouzi.loanservice.repository;

import com.aminnorouzi.loanservice.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByAccountIdEquals(Long accountId);
}