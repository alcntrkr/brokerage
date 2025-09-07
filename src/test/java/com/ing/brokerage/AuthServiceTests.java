package com.ing.brokerage;

import com.ing.brokerage.entity.Customer;
import com.ing.brokerage.repository.CustomerRepository;
import com.ing.brokerage.security.JwtUtil;
import com.ing.brokerage.service.AuthService;
import com.ing.brokerage.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "adminPasswordRaw", "adminpass");
    }

    @Test
    void testLoginAsAdminWithCorrectPassword() {
        when(jwtUtil.generateToken("admin", "ROLE_ADMIN")).thenReturn("admin-jwt-token");

        String token = authService.login("admin", "adminpass");

        assertEquals("admin-jwt-token", token);
    }

    @Test
    void testLoginAsAdminWithWrongPassword() {
        String token = authService.login("admin", "wrongpass");

        assertNull(token);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testLoginAsCustomerWithValidCredentials() {
        Customer mockCustomer = new Customer();
        mockCustomer.setId("john");

        when(customerRepository.findById("john")).thenReturn(Optional.of(mockCustomer));
        when(customerService.checkPassword(mockCustomer, "secret")).thenReturn(true);
        when(jwtUtil.generateToken("john", "ROLE_CUSTOMER")).thenReturn("customer-jwt-token");

        String token = authService.login("john", "secret");

        assertEquals("customer-jwt-token", token);
    }

    @Test
    void testLoginAsCustomerWithInvalidPassword() {
        Customer mockCustomer = new Customer();
        mockCustomer.setId("john");

        when(customerRepository.findById("john")).thenReturn(Optional.of(mockCustomer));
        when(customerService.checkPassword(mockCustomer, "wrong")).thenReturn(false);

        String token = authService.login("john", "wrong");

        assertNull(token);
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void testLoginWithUnknownUser() {
        when(customerRepository.findById("nobody")).thenReturn(Optional.empty());

        String token = authService.login("nobody", "nopass");

        assertNull(token);
        verifyNoInteractions(customerService);
        verifyNoInteractions(jwtUtil);
    }
}
