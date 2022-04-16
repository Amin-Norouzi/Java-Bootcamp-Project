package com.aminnorouzi.accountservice.repository;

import com.aminnorouzi.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // SELECT a FROM Account AS a JOIN FETCH a.customerIds ci WHERE ci=?1
    // run above query to get only requester id in customer ids list
    @Query("SELECT a FROM Account AS a INNER JOIN a.customerIds ci WHERE ci=?1")
    List<Account> findByCustomerId(Long customerId);

    boolean existsByIdEquals(Long id);
}