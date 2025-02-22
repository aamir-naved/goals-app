package com.accountability.accountability_app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Goal> goals;

    // Users this user has sent requests to
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AccountabilityPartner> accountabilityPartners;

    // Users who have sent requests to this user
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AccountabilityPartner> partnersWithMe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<AccountabilityPartner> getAccountabilityPartners() {
        return accountabilityPartners;
    }

    public void setAccountabilityPartners(List<AccountabilityPartner> accountabilityPartners) {
        this.accountabilityPartners = accountabilityPartners;
    }

    public List<AccountabilityPartner> getPartnersWithMe() {
        return partnersWithMe;
    }

    public void setPartnersWithMe(List<AccountabilityPartner> partnersWithMe) {
        this.partnersWithMe = partnersWithMe;
    }
}
