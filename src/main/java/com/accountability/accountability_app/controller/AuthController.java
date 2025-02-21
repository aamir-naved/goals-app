package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.service.AuthService;
import com.accountability.accountability_app.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;



    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.registerUser(request.getEmail(), request.getName(), request.getPassword());
    }

//    @PostMapping("/login")
//    public String login(@RequestBody LoginRequest request) {
//        System.out.println("Login call received");
//        return authService.loginUser(request.getEmail(), request.getPassword());
//    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        System.out.println("Login call received");
        return authService.loginUser(request.getEmail(), request.getPassword());
    }
}

@Getter
@Setter
@AllArgsConstructor
class RegisterRequest {
    private String email;
    private String name;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

@Getter
@Setter
@AllArgsConstructor
class LoginRequest {
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String email;
    private String password;
}
