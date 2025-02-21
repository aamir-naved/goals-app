package com.accountability.accountability_app.service;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Method to retrieve the currently logged-in user
    public User getLoggedInUser() {
        Long loggedInUserId = getLoggedInUserIdFromSessionOrToken();
        return userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Retrieve the logged-in user's ID directly from the SecurityContext (Authentication object)
    private Long getLoggedInUserIdFromSessionOrToken() {
        // Retrieve the authentication object from Spring Security's SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Authentication context is not available.");
        }

        // Get the principal object, which is usually the logged-in user's details (could be a username, userId, etc.)
        Object principal = authentication.getPrincipal();

        // Check if the principal is a String (username, email, etc.)
        if (principal instanceof String) {
            // If the principal is a String (like username or email), query the user by it
            String email = (String) principal;
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        // If it's a different type (e.g., custom UserDetails), you can inspect and handle accordingly
        else if (principal instanceof UserDetails) {
            // If it's a UserDetails object, extract the user ID (depending on your setup)
            UserDetails userDetails = (UserDetails) principal;
            return userRepository.findByEmail(userDetails.getUsername()) // Assuming username is the email
                    .map(User::getId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new RuntimeException("Unexpected principal type: " + principal.getClass().getName());
        }
    }

    public List<User> getAllUsersExcept(Long userId) {
        return userRepository.findByIdNot(userId);
    }
}
