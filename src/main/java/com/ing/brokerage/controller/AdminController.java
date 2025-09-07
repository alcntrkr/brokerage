package com.ing.brokerage.controller;

import com.ing.brokerage.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final OrderService orderService;

    @PostMapping("/match/{id}")
    public ResponseEntity<?> matchOrder(@PathVariable Long id) {
        try {
            var o = orderService.matchOrder(id);
            return ResponseEntity.ok(o);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
