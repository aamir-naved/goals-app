//package com.accountability.accountability_app.dto;
//
//import com.accountability.accountability_app.model.Goal;
//
//import java.time.LocalDate;
//
//public class GoalDTO {
//    private Long id;
//    private String title;
//    private String description;
//    private LocalDate deadline;
//    private boolean completed;
//    private int progress;
//
//    // No User field here to prevent recursion
//
//    public GoalDTO(Goal goal) {
//        this.id = goal.getId();
//        this.title = goal.getTitle();
//        this.description = goal.getDescription();
//        this.deadline = goal.getDeadline();
//        this.completed = goal.isCompleted();
//        this.progress = goal.getProgress();
//    }
//}
