package com.brokage.infrastructure.repository;

import com.brokage.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
