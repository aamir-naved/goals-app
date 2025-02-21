package com.accountability.accountability_app.service;

import com.accountability.accountability_app.exception.GoalNotFoundException;
import com.accountability.accountability_app.model.Goal;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.GoalRepository;
import com.accountability.accountability_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    public Goal createGoal(Goal goal) {
        System.out.println("Create goal service class");

        // Log authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated user: " + authentication.getName());

        // Debug incoming goal object
        System.out.println("Received goal: " + goal);
        System.out.println("Received user: " + goal.getUser());
        System.out.println("Received user ID: " + (goal.getUser() != null ? goal.getUser().getId() : "NULL"));

        // Check if user object exists
        if (goal.getUser() == null || goal.getUser().getId() == null) {
            throw new RuntimeException("User ID is required to create a goal.");
        }

        // Fetch user from database
        User user = userRepository.findById(goal.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        goal.setUser(user); // Ensure user is set properly

        return goalRepository.save(goal);
    }



    public List<Goal> getUserGoals(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    public Optional<Goal> getGoalById(Long goalId) {
        return goalRepository.findById(goalId);
    }

//    public Goal updateGoal(Long goalId, Goal updatedGoal) {
//        return goalRepository.findById(goalId).map(goal -> {
//            goal.setTitle(updatedGoal.getTitle());
//            goal.setDescription(updatedGoal.getDescription());
//            goal.setDeadline(updatedGoal.getDeadline()); // Updated here
//            goal.setCompleted(updatedGoal.isCompleted());
//            goal.setProgress(updatedGoal.getProgress());
//            return goalRepository.save(goal);
//        }).orElseThrow(() -> new RuntimeException("Goal not found"));
//    }

    public Goal updateGoal(Long goalId, Goal updatedGoal) {
        return goalRepository.findById(goalId).map(goal -> {
            goal.setTitle(updatedGoal.getTitle());
            goal.setDescription(updatedGoal.getDescription());
            goal.setDeadline(updatedGoal.getDeadline());
            goal.setCompleted(updatedGoal.isCompleted());
            goal.setProgress(updatedGoal.getProgress());

            // Ensure user is set correctly (if not, fetch the user)
            if (updatedGoal.getUser() != null) {
                goal.setUser(updatedGoal.getUser());
            }

            return goalRepository.save(goal);
        }).orElseThrow(() -> new GoalNotFoundException(goalId));
    }


    public void     deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new GoalNotFoundException(goalId);
        }
        goalRepository.deleteById(goalId);
    }
}
