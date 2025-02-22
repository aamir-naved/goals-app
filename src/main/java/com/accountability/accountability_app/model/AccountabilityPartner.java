package com.accountability.accountability_app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accountability_partners",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "partner_id"})) // Prevent duplicates
public class AccountabilityPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"accountabilityPartners", "partnersWithMe"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    @JsonIgnoreProperties({"accountabilityPartners", "partnersWithMe"})
    private User partner;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        REVOKED
    }

    public boolean involvesUser(User targetUser) {
        return user.getId().equals(targetUser.getId()) || partner.getId().equals(targetUser.getId());
    }
}
