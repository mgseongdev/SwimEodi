package com.swimeodi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/auth")
    public ResponseEntity<Map<String, String>> auth(@RequestBody Map<String, String> body) {
        if (adminPassword.equals(body.get("password"))) {
            return ResponseEntity.ok(Map.of("token", adminPassword));
        }
        return ResponseEntity.status(401).body(Map.of("error", "비밀번호가 틀렸습니다"));
    }
}
