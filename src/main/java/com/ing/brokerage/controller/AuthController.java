package com.ing.brokerage.controller;

import com.ing.brokerage.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> req) {
        var id = req.get("id");
        var password = req.get("password");
        var token = authService.login(id, password);
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error","invalid credentials"));
        }
        return ResponseEntity.ok(Map.of("token", token));
    }
}
