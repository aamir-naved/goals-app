package com.accountability.accountability_app.dto;

import java.util.List;

public class PartnerDTO {
    private Long id;
    private String name;
    private String email;
    private List<GoalDTO> goals;

    public PartnerDTO(Long id, String name, String email, List<GoalDTO> goals) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.goals = goals;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<GoalDTO> getGoals() { return goals; }
    public void setGoals(List<GoalDTO> goals) { this.goals = goals; }
}
