package com.accountability.accountability_app.repository;

import com.accountability.accountability_app.model.AccountabilityPartner;
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


    List<AccountabilityPartner> findByPartnerIdAndStatus(Long partnerId, AccountabilityPartner.Status status);


    List<AccountabilityPartner> findByPartnerAndStatus(User partner, AccountabilityPartner.Status status);
}
