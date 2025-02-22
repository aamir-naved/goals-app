package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.dto.AccountabilityPartnerDTO;
import com.accountability.accountability_app.dto.PartnerDTO;
import com.accountability.accountability_app.dto.UserDTO;
import com.accountability.accountability_app.model.AccountabilityPartner;
import com.accountability.accountability_app.model.Goal;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.service.AccountabilityPartnerService;
import com.accountability.accountability_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accountability")
public class AccountabilityPartnerController {

    @Autowired
    private AccountabilityPartnerService accountabilityPartnerService;

    @Autowired
    private UserService userService;

    @PostMapping("/send-request")
    public String sendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        return accountabilityPartnerService.sendRequest(senderId, receiverId);
    }

    @PostMapping("/respond-request")
    public String respondToRequest(@RequestParam Long receiverId, @RequestParam Long senderId, @RequestParam boolean accept) {
        return accountabilityPartnerService.respondToRequest(receiverId, senderId, accept);
    }

    @GetMapping("/partners")
    public ResponseEntity<List<PartnerDTO>> getAccountabilityPartners(@RequestParam Long userId) {
        List<PartnerDTO> accountabilityPartners = accountabilityPartnerService.getAccountabilityPartners(userId);
        return ResponseEntity.ok(accountabilityPartners);
    }

    @DeleteMapping("/remove-partner")
    public String removePartner(@RequestParam Long userId, @RequestParam Long partnerId) {
        System.out.println("Received a request to remove partner for userID: " + userId);
        return accountabilityPartnerService.removePartner(userId, partnerId);
    }

    // ✅ New Endpoint to Get All Users Except the Logged-in User
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsersExceptLoggedIn() {
        System.out.println("Getting all users.");
        User loggedInUser = userService.getLoggedInUser();
        System.out.println("Logged in user: " + loggedInUser.getName());
        List<UserDTO> users = userService.getAllUsersExcept(loggedInUser.getId());
        System.out.println("All users");
        System.out.println(users);
        return ResponseEntity.ok(users);
    }

    // ✅ New Endpoint to Fetch Pending Requests for the Logged-in User
    @GetMapping("/pending-requests")
    public ResponseEntity<List<AccountabilityPartnerDTO>> getPendingRequests(@RequestParam Long userId) {
        List<AccountabilityPartnerDTO> pendingRequests = accountabilityPartnerService.getPendingRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    @GetMapping("/partnerGoals")
    public ResponseEntity<List<Goal>> getPartnerGoals(@RequestParam Long userId) {
        System.out.println("Received call in getPartnerGoals");
        List<Goal> partnerGoals = accountabilityPartnerService.getPartnerGoals(userId);
        return ResponseEntity.ok(partnerGoals);
    }
}
