package com.accountability.accountability_app.service;

import com.accountability.accountability_app.dto.UserDTO;
import com.accountability.accountability_app.exception.UserAlreadyExistsException;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.UserRepository;
import com.accountability.accountability_app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            return buildErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        UserDTO userDTO = new UserDTO(userOpt.get().getId(),userOpt.get().getName(),userOpt.get().getEmail());

        String token = jwtUtil.generateToken(email);

        // Creating a response object with token and user details
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userDTO);

        return ResponseEntity.ok(response);
    }

    // Helper method to build a structured JSON response
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("status", status.value());

        return new ResponseEntity<>(body, status);
    }

}
