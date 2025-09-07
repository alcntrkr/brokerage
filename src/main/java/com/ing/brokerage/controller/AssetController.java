package com.ing.brokerage.controller;

import com.ing.brokerage.service.AssetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetService svc;

    @GetMapping
    public ResponseEntity<?> listAssets(
            Authentication authentication, @RequestParam(required = false) String customerId) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (isCustomer) {
            var id = authentication.getName();
            return ResponseEntity.ok(svc.listAssets(id));
        } else {
            if (customerId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "customerId required for admin"));
            }
            return ResponseEntity.ok(svc.listAssets(customerId));
        }
    }
}
