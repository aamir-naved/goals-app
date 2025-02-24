package com.accountability.accountability_app.service;

import com.accountability.accountability_app.dto.UserDTO;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Retrieve the currently logged-in user
    public User getLoggedInUser() {
        Long loggedInUserId = getLoggedInUserIdFromSessionOrToken();
        return userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Retrieve logged-in user's ID from SecurityContext
    private Long getLoggedInUserIdFromSessionOrToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String email) {
            return getUserIdByEmail(email);
        } else if (principal instanceof UserDetails userDetails) {
            return getUserIdByEmail(userDetails.getUsername()); // Assuming username is email
        } else {
            throw new RuntimeException("Unexpected principal type: " + (principal != null ? principal.getClass().getName() : "null"));
        }
    }

    // Helper method to get user ID by email
    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // Retrieve all users except the logged-in user
    public List<UserDTO> getAllUsersExcept(Long userId) {
        return Optional.ofNullable(userRepository.findByIdNot(userId))
                .orElse(List.of()) // Return empty list if null
                .stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
