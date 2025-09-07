package com.ing.brokerage.service;

import com.ing.brokerage.entity.Customer;
import com.ing.brokerage.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository repo;
    private final PasswordEncoder encoder;

    public Customer createCustomer(String id, String rawPassword, String name) {
        Customer c = new Customer(id, encoder.encode(rawPassword), name);
        return repo.save(c);
    }

    public boolean checkPassword(Customer c, String rawPassword) {
        return encoder.matches(rawPassword, c.getPasswordHash());
    }
}
