package com.ing.brokerage.repository;

import com.ing.brokerage.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByCustomerIdAndCreateDateBetween(String customerId, Instant from, Instant to);
}
