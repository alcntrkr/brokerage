package com.ing.brokerage.controller;

import com.ing.brokerage.controller.model.CreateOrderRequest;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.entity.OrderSide;
import com.ing.brokerage.entity.OrderStatus;
import com.ing.brokerage.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Object> createOrder(Authentication authentication, @Valid @RequestBody CreateOrderRequest req) {
        String customerId;
        var isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (isCustomer) {
            customerId = authentication.getName();
        } else {
            if (req.customerId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "customerId required for admin"));
            }
            customerId = req.customerId;
        }
        var o = new Order(customerId, req.asset, OrderSide.valueOf(req.side), req.size, req.price, OrderStatus.PENDING);
        try {
            var saved = orderService.createOrder(o);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listOrders(Authentication authentication,
                                        @RequestParam(required = false) String customerId,
                                        @RequestParam(required = false) Long fromEpochMillis,
                                        @RequestParam(required = false) Long toEpochMillis) {
        var isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (isCustomer) {
            var id = authentication.getName();
            var response = orderService.listOrders(
                    id,
                    fromEpochMillis == null ? null : Instant.ofEpochMilli(fromEpochMillis),
                    toEpochMillis == null ? null : Instant.ofEpochMilli(toEpochMillis)
            );
            return ResponseEntity.ok(response);
        } else {
            if (customerId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "customerId required for admin"));
            }
            var response = orderService.listOrders(
                    customerId,
                    fromEpochMillis == null ? null : Instant.ofEpochMilli(fromEpochMillis),
                    toEpochMillis == null ? null : Instant.ofEpochMilli(toEpochMillis)
            );
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(Authentication authentication, @PathVariable Long id) {
        var isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        try {
            var o = orderService.getOrder(id).orElseThrow();
            if (isCustomer && !o.getCustomerId().equals(authentication.getName()))
                return ResponseEntity.status(403).build();
            orderService.cancelOrder(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
