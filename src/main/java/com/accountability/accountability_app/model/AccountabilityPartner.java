package com.accountability.accountability_app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accountability_partners")
public class AccountabilityPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    @JsonBackReference
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name = "partner_id", nullable = false)
//    private User partner;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"accountabilityPartner"}) // Prevent infinite recursion
    private User user;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    @JsonIgnoreProperties({"accountabilityPartner"}) // Prevent infinite recursion
    private User partner;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // Default status is PENDING

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner = partner;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        REVOKED
    }

    // Add a helper method to check if a user is in this accountability relationship
    public boolean involvesUser(User targetUser) {
        return user.getId().equals(targetUser.getId()) || partner.getId().equals(targetUser.getId());
    }
}
