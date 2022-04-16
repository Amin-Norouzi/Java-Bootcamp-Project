package com.aminnorouzi.customerservice.repository;

import com.aminnorouzi.customerservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, Long>,
        JpaSpecificationExecutor<Customer> {

    boolean existsByIdEquals(Long id);

    boolean existsByNationalCodeEquals(String nationalCode);
}