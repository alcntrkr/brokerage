package com.ing.brokerage.service;

import com.ing.brokerage.entity.Customer;
import com.ing.brokerage.repository.CustomerRepository;
import com.ing.brokerage.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    private final CustomerRepository repo;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @Value("${admin.password:adminpass}")
    private String adminPasswordRaw; // used only at startup if admin user not created

    public AuthService(CustomerRepository repo, CustomerService customerService, JwtUtil jwtUtil) {
        this.repo = repo; this.customerService = customerService; this.jwtUtil = jwtUtil;
    }

    public String login(String id, String password) {
        if ("admin".equals(id)) {
            // admin special case - seed check elsewhere; for demo allow password 'adminpass' or property
            if (adminPasswordRaw.equals(password)) {
                return jwtUtil.generateToken("admin","ROLE_ADMIN");
            } else return null;
        }
        Optional<Customer> c = repo.findById(id);
        if (c.isEmpty()) return null;
        if (customerService.checkPassword(c.get(), password)) {
            return jwtUtil.generateToken(id, "ROLE_CUSTOMER");
        }
        return null;
    }
}
