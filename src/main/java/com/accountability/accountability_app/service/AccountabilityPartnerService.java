package com.accountability.accountability_app.service;

import com.accountability.accountability_app.model.AccountabilityPartner;
import com.accountability.accountability_app.model.Goal;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.AccountabilityPartnerRepository;
import com.accountability.accountability_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountabilityPartnerService {

    @Autowired
    private AccountabilityPartnerRepository accountabilityPartnerRepository;

    @Autowired
    private UserRepository userRepository;

//    public String sendRequest(Long senderId, Long receiverId) {
//        Optional<User> senderOpt = userRepository.findById(senderId);
//        Optional<User> receiverOpt = userRepository.findById(receiverId);
//
//        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
//            return "User not found.";
//        }
//
//        User sender = senderOpt.get();
//        User receiver = receiverOpt.get();
//
//        List<AccountabilityPartner> existingRequests = accountabilityPartnerRepository.findByUserAndPartner(sender, receiver);
//        for (AccountabilityPartner request : existingRequests) {
//            if (request.getStatus() == AccountabilityPartner.Status.PENDING) {
//                return "Request already sent and is pending.";
//            } else if (request.getStatus() == AccountabilityPartner.Status.REVOKED) {
//
//            }
//        }
//
//        AccountabilityPartner newRequest = new AccountabilityPartner();
//        newRequest.setUser(sender);
//        newRequest.setPartner(receiver);
//        newRequest.setStatus(AccountabilityPartner.Status.PENDING);
//        accountabilityPartnerRepository.save(newRequest);
//
//        return "Request sent successfully.";
//    }

    public String sendRequest(Long senderId, Long receiverId) {
        System.out.println("Sending request from " + senderId + " to " + receiverId);
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return "User not found.";
        }

        User sender = senderOpt.get();
        System.out.println("Sender Name : " + sender.getName());
        User receiver = receiverOpt.get();
        System.out.println("Receiver Name : " + receiver.getName());

        // Check if a partnership already exists
        List<AccountabilityPartner> existingPartnership = accountabilityPartnerRepository.findByUserAndPartner(sender, receiver);
        List<AccountabilityPartner> existingPartnershipReverse = accountabilityPartnerRepository.findByUserAndPartner(receiver, sender);


        for (AccountabilityPartner partnership : existingPartnership){
            System.out.println("Direct Partnership");
            if (partnership.getStatus() == AccountabilityPartner.Status.REVOKED) {
                System.out.println("Partnership status is REVOKED, So, Updating it to PENDING.");
                return updatePartnershipStatus(partnership, AccountabilityPartner.Status.PENDING);
            }
            return checkPartnershipStatus(partnership);
        }

        for (AccountabilityPartner partnership : existingPartnershipReverse){
            System.out.println("Reverse Partnership");
            if (partnership.getStatus() == AccountabilityPartner.Status.REVOKED) {
                System.out.println("Partnership status is REVOKED, So, Updating it to PENDING.");
                return updatePartnershipStatus(partnership, AccountabilityPartner.Status.PENDING);
            }
            return checkPartnershipStatus(partnership);
        }

        // Create a new accountability partner request
        System.out.println("Creating new request");

        return createNewPartnership(sender, receiver);

    }

    private String createNewPartnership(User sender, User receiver) {
        AccountabilityPartner request = new AccountabilityPartner();
        request.setUser(sender);
        request.setPartner(receiver);
        request.setStatus(AccountabilityPartner.Status.PENDING);
        accountabilityPartnerRepository.save(request);
        return "Request sent successfully.";
    }

    // Helper method to check the partnership status
    private String checkPartnershipStatus(AccountabilityPartner partnership) {
        switch (partnership.getStatus()) {
            case PENDING:
                return "Request already sent, but not yet responded.";
            case ACCEPTED:
                return "You are already partners.";
            case REJECTED:
                return "Previous request was rejected. You can send a new request.";
            default:
                return "Unexpected status.";
        }
    }

    private String updatePartnershipStatus(AccountabilityPartner partnership, AccountabilityPartner.Status status) {
        partnership.setStatus(status);
        accountabilityPartnerRepository.save(partnership);
        return status == AccountabilityPartner.Status.PENDING ? "Request Sent. Your partnership is in Pending." : "Unknown status while processing REVOKED one.";
    }

    public String respondToRequest(Long receiverId, Long senderId, boolean accept) {
        System.out.println("Responding to request from " + senderId + " to " + receiverId);

        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (sender == null || receiver == null) {
            return "User not found.";
        }

        // Check if the receiver already has an accepted partner
        System.out.println("First checking if the user is already a active partner...");
        AccountabilityPartner existingPartner = accountabilityPartnerRepository.findByUserOrPartnerAndStatus(receiver, AccountabilityPartner.Status.ACCEPTED);

        if (existingPartner != null && accept) {
            System.out.println("You are already partnered with someone else. Remove your existing partner first to accept a new request.");
            return "You are already partnered with someone else. Remove your existing partner first to accept a new request.";
        }

        // Find the accountability request in both possible directions
        List<AccountabilityPartner> requestList = accountabilityPartnerRepository.findByUserAndPartner(sender, receiver);
        List<AccountabilityPartner> requestReverseList = accountabilityPartnerRepository.findByUserAndPartner(receiver, sender);

        // Merge both lists
        List<AccountabilityPartner> allRequests = new ArrayList<>();
        allRequests.addAll(requestList);
        allRequests.addAll(requestReverseList);

        if (allRequests.isEmpty()) {
            return "No pending request found.";
        }

        // We assume there's only one valid request at a time
        AccountabilityPartner request = allRequests.get(0);

        // If the request is already accepted, no need to process it again.
        if (request.getStatus() == AccountabilityPartner.Status.ACCEPTED) {
            return "You are already partners.";
        }

        // If the request is already revoked, a new request needs to be sent first.
        if (request.getStatus() == AccountabilityPartner.Status.REVOKED) {
            return "Your partnership was revoked, so a new request must be sent first.";
        }

        // Update the status based on the user's response
        request.setStatus(accept ? AccountabilityPartner.Status.ACCEPTED : AccountabilityPartner.Status.REJECTED);
        accountabilityPartnerRepository.save(request);

        return accept ? "Request accepted. You are now partners." : "Request rejected.";
    }

    public String removePartner(Long userId) {
        List<AccountabilityPartner> partnerships = accountabilityPartnerRepository.findByUserIdOrPartnerId(userId, userId);

        if (partnerships.isEmpty()) {
            return "No accountability partner found.";
        }

        for (AccountabilityPartner partnership : partnerships) {
            if (partnership.getStatus() == AccountabilityPartner.Status.ACCEPTED) {
                partnership.setStatus(AccountabilityPartner.Status.REVOKED);
                System.out.println("Status set to REVOKED");
                accountabilityPartnerRepository.save(partnership);
            }
        }

        return "Accountability partnership revoked successfully.";
    }

    public List<AccountabilityPartner> getPendingRequests(Long userId) {
        return accountabilityPartnerRepository.findByPartnerIdAndStatus(userId, AccountabilityPartner.Status.PENDING);
    }

    public AccountabilityPartner getAccountabilityPartner(Long userId) {
        System.out.println("Fetching accountability partner for userId: " + userId);

        // Fetch all partnerships where the user is either 'user' or 'partner'
        List<AccountabilityPartner> partnerships = accountabilityPartnerRepository.findByUserIdOrPartnerId(userId, userId);

        if (partnerships.isEmpty()) {
            return null; // No partner found
        }

        // ✅ Return the first accepted partnership, if any
        for (AccountabilityPartner partner : partnerships) {
            if (partner.getStatus() == AccountabilityPartner.Status.ACCEPTED) {
                return partner; // Return the first active partner
            }
        }

        return null; // If no active partner exists
    }

    public List<Goal> getPartnerGoals(Long userId) {
        System.out.println("Inside service...getPartnerGoals , userId: " + userId);
        return accountabilityPartnerRepository.findPartnerGoals(userId);
    }

}
