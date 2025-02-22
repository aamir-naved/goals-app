package com.accountability.accountability_app.repository;

import com.accountability.accountability_app.model.AccountabilityPartner;
import com.accountability.accountability_app.model.Goal;
import com.accountability.accountability_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountabilityPartnerRepository extends JpaRepository<AccountabilityPartner, Long> {
    Optional<AccountabilityPartner> findByUser(User user);
//    Optional<AccountabilityPartner> findByUserAndPartner(User user, User partner);
//    Optional<AccountabilityPartner> findByUserIdOrPartnerId(Long userId, Long partnerId);
    List<AccountabilityPartner> findByUserAndPartner(User user, User partner);

    @Query("SELECT ap FROM AccountabilityPartner ap WHERE (ap.user = :user OR ap.partner = :user) AND ap.status = 'ACCEPTED'")
    AccountabilityPartner findByUserOrPartnerAndStatus(@Param("user") User user, @Param("status") AccountabilityPartner.Status status);

//    List<AccountabilityPartner> findByPartnerIdAndStatus(Long partnerId, AccountabilityPartner.Status status);

    List<AccountabilityPartner> findByUserIdOrPartnerId(Long userId, Long partnerId);

    @Query("SELECT g FROM Goal g WHERE g.user.id = " +
            "(SELECT ap.partner.id FROM AccountabilityPartner ap " +
            "WHERE ap.user.id = :userId AND ap.status = 'ACCEPTED')")
    List<Goal> findPartnerGoals(@Param("userId") Long userId);


    List<AccountabilityPartner> findByUserIdAndStatus(Long userId, AccountabilityPartner.Status status);

    // Find all partnerships where the user is either the requester or the partner
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT ap.partner.id FROM AccountabilityPartner ap WHERE ap.user.id = :userId AND ap.status = 'ACCEPTED')")
    List<User> findAllAcceptedPartners(@Param("userId") Long userId);

    // Find all accountability partnerships where the user has requested a partner
    List<AccountabilityPartner> findByUserAndStatus(User user, AccountabilityPartner.Status status);

    // Find all accountability partnerships where the user is a partner (i.e., received requests)
    List<AccountabilityPartner> findByPartnerAndStatus(User partner, AccountabilityPartner.Status status);

    // Find an active accountability partnership between two specific users
    @Query("SELECT ap FROM AccountabilityPartner ap WHERE " +
            "(ap.user = :user1 AND ap.partner = :user2 OR ap.user = :user2 AND ap.partner = :user1) " +
            "AND ap.status = 'ACCEPTED'")
    Optional<AccountabilityPartner> findExistingPartnership(@Param("user1") User user1, @Param("user2") User user2);

    // Fetch all partners who accepted a request from the given user
    @Query("SELECT ap.partner FROM AccountabilityPartner ap WHERE ap.user = :user AND ap.status = 'ACCEPTED'")
    List<User> findPartnersByUser(@Param("user") User user);

    // Fetch all users who sent a request that the given user accepted
    @Query("SELECT ap.user FROM AccountabilityPartner ap WHERE ap.partner = :user AND ap.status = 'ACCEPTED'")
    List<User> findUsersWhoSelectedAsPartner(@Param("user") User user);

    // Fetch goals of all users who are partners to the given user
    @Query("SELECT g FROM Goal g WHERE g.user IN " +
            "(SELECT ap.partner FROM AccountabilityPartner ap WHERE ap.user = :user AND ap.status = 'ACCEPTED')")
    List<Goal> findGoalsOfAcceptedPartners(@Param("user") User user);


}
