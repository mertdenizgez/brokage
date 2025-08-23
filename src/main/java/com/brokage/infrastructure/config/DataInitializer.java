package com.brokage.infrastructure.config;

import com.brokage.application.service.asset.AssetInitializationService;
import com.brokage.domain.entity.Customer;
import com.brokage.domain.enums.Role;
import com.brokage.infrastructure.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AssetInitializationService assetInitializationService;
    
    @Override
    public void run(String... args) {
        initializeData();
    }
    
    private void initializeData() {
        if (customerRepository.count() == 0) {
            log.info("Initializing database with sample data...");
            
            Customer admin = new Customer();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@brokage.com");
            customerRepository.save(admin);
            
            Customer customer1 = new Customer();
            customer1.setUsername("customer1");
            customer1.setPassword(passwordEncoder.encode("password123"));
            customer1.setRole(Role.CUSTOMER);
            customer1.setFirstName("John");
            customer1.setLastName("Doe");
            customer1.setEmail("john.doe@example.com");
            customer1 = customerRepository.save(customer1);
            
            Customer customer2 = new Customer();
            customer2.setUsername("customer2");
            customer2.setPassword(passwordEncoder.encode("password123"));
            customer2.setRole(Role.CUSTOMER);
            customer2.setFirstName("Jane");
            customer2.setLastName("Smith");
            customer2.setEmail("jane.smith@example.com");
            customer2 = customerRepository.save(customer2);
            
            assetInitializationService.initializeCustomerWithTRY(customer1.getId(), new BigDecimal("100000"));
            assetInitializationService.initializeCustomerWithTRY(customer2.getId(), new BigDecimal("50000"));
            
            log.info("Sample data initialized successfully");
            log.info("Admin credentials: admin/admin123");
            log.info("Customer1 credentials: customer1/password123");
            log.info("Customer2 credentials: customer2/password123");
        }
    }
}
