package com.accountability.accountability_app.service;

import com.accountability.accountability_app.dto.AccountabilityPartnerDTO;
import com.accountability.accountability_app.dto.GoalDTO;
import com.accountability.accountability_app.dto.PartnerDTO;
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
import java.util.stream.Collectors;

@Service
public class AccountabilityPartnerService {

    @Autowired
    private AccountabilityPartnerRepository accountabilityPartnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalService goalService;

    public String sendRequest(Long senderId, Long receiverId) {
        System.out.println("Sending request from " + senderId + " to " + receiverId);

        /*
        so, If sender is user3, and receiver is user 2,
        then user3 wants to make user 2 his accountablity master,
        meaning user3 is willing to show his goals to user2,
        user 2 can access goals of user 3,

        Basically user3 ke goals dikhenge user 2 ko
        so user 3 or say the sender is slave,
        and user 2, the receiver is master.
         */
        Long slave = senderId;
        Long master = receiverId;
        Optional<User> slaveOpt = userRepository.findById(slave);
        Optional<User> masterOpt = userRepository.findById(master);

        if (slaveOpt.isEmpty() || masterOpt.isEmpty()) {
            return "User not found.";
        }

        User slaveUser = slaveOpt.get();
        User masterUser = masterOpt.get();

        // Check if a partnership already exists
        List<AccountabilityPartner> existingPartnership = accountabilityPartnerRepository.findByUserAndPartner(masterUser, slaveUser);
//        List<AccountabilityPartner> existingPartnershipReverse = accountabilityPartnerRepository.findByUserAndPartner(receiver, sender);

        for (AccountabilityPartner partnership : existingPartnership) {
            if (partnership.getStatus() == AccountabilityPartner.Status.REVOKED || partnership.getStatus() == AccountabilityPartner.Status.REJECTED) {
                return updatePartnershipStatus(partnership, AccountabilityPartner.Status.PENDING);
            }
            return checkPartnershipStatus(partnership);
        }

//        for (AccountabilityPartner partnership : existingPartnershipReverse) {
//            if (partnership.getStatus() == AccountabilityPartner.Status.REVOKED) {
//                return updatePartnershipStatus(partnership, AccountabilityPartner.Status.PENDING);
//            }
//            return checkPartnershipStatus(partnership);
//        }

        return createNewPartnership(slaveUser, masterUser);
    }

    private String createNewPartnership(User slaveUser, User masterUser) {
        AccountabilityPartner request = new AccountabilityPartner();
        request.setUser(masterUser);
        request.setPartner(slaveUser);
        request.setStatus(AccountabilityPartner.Status.PENDING);
        try{
            accountabilityPartnerRepository.save(request);
        }catch (Exception e){
            return "Excetpion: " + e;
        }

        return "Request sent successfully.";
    }

    private String checkPartnershipStatus(AccountabilityPartner partnership) {
        return switch (partnership.getStatus()) {
            case PENDING -> "Request already sent, but not yet responded.";
            case ACCEPTED -> "You are already partners.";
            case REJECTED -> "Previous request was rejected. You can send a new request.";
            default -> "Unexpected status.";
        };
    }

    private String updatePartnershipStatus(AccountabilityPartner partnership, AccountabilityPartner.Status status) {
        partnership.setStatus(status);
        try
        {
            accountabilityPartnerRepository.save(partnership);
        }
        catch (Exception e){
            return "Excetpion: " + e;
        }
        return status == AccountabilityPartner.Status.PENDING ? "Request Sent. Your partnership is in Pending." : "Unknown status while processing REVOKED one.";
    }

    public String respondToRequest(Long receiverId, Long senderId, boolean accept) {
        System.out.println("Responding to request from " + senderId + " to " + receiverId);

        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> receiverOpt = userRepository.findById(receiverId);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            return "User not found.";
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        // Find pending request
        List<AccountabilityPartner> requestList = accountabilityPartnerRepository.findByUserAndPartner(sender, receiver);
//        List<AccountabilityPartner> requestReverseList = accountabilityPartnerRepository.findByUserAndPartner(receiver, sender);

        List<AccountabilityPartner> allRequests = new ArrayList<>();
        allRequests.addAll(requestList);
//        allRequests.addAll(requestReverseList);

        if (allRequests.isEmpty()) {
            return "No pending request found.";
        }

        AccountabilityPartner request = allRequests.get(0);

        if (request.getStatus() == AccountabilityPartner.Status.ACCEPTED) {
            return "You are already partners.";
        }

        if (request.getStatus() == AccountabilityPartner.Status.REVOKED) {
            request.setStatus(accept ? AccountabilityPartner.Status.PENDING : AccountabilityPartner.Status.REJECTED);
            String msg = accept ? " A new request has been sent!" : " Request rejected again!";
            try{
                accountabilityPartnerRepository.save(request);
            }catch (Exception e){
                return "Exception : " + e;
            }
            return "Your partnership was revoked earlier." + msg;
        }

        request.setStatus(accept ? AccountabilityPartner.Status.ACCEPTED : AccountabilityPartner.Status.REJECTED);
        try{
            accountabilityPartnerRepository.save(request);
        }catch (Exception e){
            return "Exception : " + e;
        }

        return accept ? "Request accepted. You are now partners." : "Request rejected.";
    }

    public String removePartner(Long userId, Long partnerId) {
        List<AccountabilityPartner> partnerships = accountabilityPartnerRepository.findByUserAndPartner(
                userRepository.findById(userId).orElse(null),
                userRepository.findById(partnerId).orElse(null)
        );

        if (partnerships.isEmpty()) {
            return "No accountability partner found.";
        }

        for (AccountabilityPartner partnership : partnerships) {
            if (partnership.getStatus() == AccountabilityPartner.Status.ACCEPTED) {
                partnership.setStatus(AccountabilityPartner.Status.REVOKED);
                try{
                    accountabilityPartnerRepository.save(partnership);
                }catch (Exception e){
                    return "Exception : " + e;
                }
            }
        }

        return "Accountability partnership revoked successfully.";
    }

    public List<AccountabilityPartnerDTO> getPendingRequests(Long userId) {
        List<AccountabilityPartner> pendingRequests =
                accountabilityPartnerRepository.findByUserIdAndStatus(userId, AccountabilityPartner.Status.PENDING);

        return pendingRequests.stream()
                .map(partner -> new AccountabilityPartnerDTO(
                        partner.getId(),
                        partner.getUser().getId(), partner.getUser().getName(), partner.getUser().getEmail(),
                        partner.getPartner().getId(), partner.getPartner().getName(), partner.getPartner().getEmail()
                ))
                .collect(Collectors.toList());
    }

    public List<PartnerDTO> getAccountabilityPartners(Long userId) {
        System.out.println("Fetching all accountability partners for userId: " + userId);
        User user = userRepository.findById(userId).orElse(null);

        // when i am using this, i want to know,
        /*
        those users, whom i sent request, and they accepted it.
        meaning , in the accountability table,
        my userid is userid, and whoever's partnerid is there, they are my partners.
         */
        if (user == null) return new ArrayList<>();
        List<User> partners = accountabilityPartnerRepository.findAllAcceptedPartners(userId);


        return partners.stream().map(this::convertToPartnerDTO).collect(Collectors.toList());
    }

    private PartnerDTO convertToPartnerDTO(User user) {
        List<GoalDTO> goalDTOs = user.getGoals().stream()
                .map(goal -> new GoalDTO(goal.getId(), goal.getTitle(), goal.getDescription(),
                        goal.getDeadline().toString(), goal.isCompleted(), goal.getProgress()))
                .collect(Collectors.toList());

        return new PartnerDTO(user.getId(), user.getName(), user.getEmail(), goalDTOs);
    }

    public List<Goal> getPartnerGoals(Long partnerId) {
        System.out.println("Fetching goals of this partner , basically a user: userId: " + partnerId);
//        User user = userRepository.findById(userId).orElse(null);
        List<Goal> goals = goalService.getUserGoals(partnerId);
        if (goals == null) return new ArrayList<>();

        return goals;
    }
}
