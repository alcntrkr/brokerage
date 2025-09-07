package com.ing.brokerage;

import com.ing.brokerage.repository.CustomerRepository;
import com.ing.brokerage.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DataJpaTest
public class CustomerServiceUnitTests {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void testCreateCustomerAndCheckPassword() {
        var encoder = new BCryptPasswordEncoder();
        var svc = new CustomerService(customerRepository, encoder);
        var c = svc.createCustomer("u1","secret","User One");
        Assertions.assertNotNull(c);
        Assertions.assertTrue(encoder.matches("secret", c.getPasswordHash()));
    }
}
