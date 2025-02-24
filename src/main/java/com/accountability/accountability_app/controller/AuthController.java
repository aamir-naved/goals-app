package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        System.out.println("📝 Registration attempt for email: " + request.getEmail());
        String response = authService.registerUser(request.getEmail(), request.getName(), request.getPassword());
        System.out.println("✅ Registration response: " + response);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        System.out.println("🔑 Login request received for email: " + request.getEmail());
        ResponseEntity<Map<String, Object>> response = authService.loginUser(request.getEmail(), request.getPassword());
        System.out.println("✅ Login response: " + response.getStatusCode());
        return response;
    }
}

@Getter
@Setter
@AllArgsConstructor
class RegisterRequest {
    private String email;
    private String name;
    private String password;
}

@Getter
@Setter
@AllArgsConstructor
class LoginRequest {
    private String email;
    private String password;
}
