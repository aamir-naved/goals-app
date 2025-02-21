package com.accountability.accountability_app.service;

import com.accountability.accountability_app.exception.UserAlreadyExistsException;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.UserRepository;
import com.accountability.accountability_app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String registerUser(String email, String name, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return jwtUtil.generateToken(email);
    }

//    public String loginUser(String email, String password) {
//        Optional<User> userOpt = userRepository.findByEmail(email);
//
//        System.out.println("Logged USER ID: " + userOpt.get().getId());
//
//        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        return jwtUtil.generateToken(email);
//    }

    public ResponseEntity<Map<String, Object>> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(email);

        // Creating a response object with token and user details
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

}
