package com.accountability.accountability_app.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(Long goalId) {
        super("Goal with ID " + goalId + " not found");
    }
}
