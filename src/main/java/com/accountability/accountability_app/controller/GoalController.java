package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.dto.GoalRequest;
import com.accountability.accountability_app.exception.UnauthorizedRequestException;
import com.accountability.accountability_app.model.Goal;
import com.accountability.accountability_app.model.User;
import com.accountability.accountability_app.repository.UserRepository;
import com.accountability.accountability_app.service.GoalService;
import com.accountability.accountability_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:5174")
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RestController
@RequestMapping("/auth/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody GoalRequest goalRequest) {


        User user = userRepository.findById(goalRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User userTest = userService.getLoggedInUser();
        if (user.getId() != userTest.getId()) {
            throw new UnauthorizedRequestException("Zyada Tez ban raha hai??");
        }

        Goal goal = new Goal();
        goal.setTitle(goalRequest.getTitle());
        goal.setDescription(goalRequest.getDescription());
        goal.setDeadline(goalRequest.getDeadline());
        goal.setCompleted(goalRequest.isCompleted());
        goal.setProgress(goalRequest.getProgress());
        goal.setUser(user); // Set user manually

        Goal savedGoal = goalService.createGoal(goal);

        return ResponseEntity.ok(savedGoal);
    }

    @GetMapping("/{userId}")
    public List<Goal> getUserGoals(@PathVariable Long userId) {
        System.out.println("Request recieive for /userId/"+userId);
        User userTest = userService.getLoggedInUser();
        if (userId != userTest.getId()) {
            throw new UnauthorizedRequestException("Zyada Tez ban raha hai??");
        }
        return goalService.getUserGoals(userId);
    }

    @GetMapping("/goal/{goalId}")
    public Optional<Goal> getGoalById(@PathVariable Long goalId) {
        // Retrieve the logged-in user
        Long loggedInUserId = userService.getLoggedInUser().getId();

        // Check if the goal belongs to the logged-in user
        Goal goal = goalService.getGoalById(goalId).orElseThrow(() -> new UnauthorizedRequestException("Goal not found"));

        if (!goal.getUser().getId().equals(loggedInUserId)) {
            throw new UnauthorizedRequestException("You are not authorized to view this goal.");
        }

        return Optional.of(goal);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long goalId, @RequestBody GoalRequest goalRequest) {
        User user = userRepository.findById(goalRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User userTest = userService.getLoggedInUser();
        if (user.getId() != userTest.getId()) {
            throw new UnauthorizedRequestException("Zyada Tez ban raha hai??");
        }

        Goal updatedGoal = new Goal();
        updatedGoal.setTitle(goalRequest.getTitle());
        updatedGoal.setDescription(goalRequest.getDescription());
        updatedGoal.setDeadline(goalRequest.getDeadline());
        updatedGoal.setCompleted(goalRequest.isCompleted());
        updatedGoal.setProgress(goalRequest.getProgress());
        updatedGoal.setUser(user); // Ensure the user is correctly set

        Goal savedGoal = goalService.updateGoal(goalId, updatedGoal);
        return ResponseEntity.ok(savedGoal);
    }

    @DeleteMapping("/{goalId}")
    public String deleteGoal(@PathVariable Long goalId) {
        // Retrieve the logged-in user
        Long loggedInUserId = userService.getLoggedInUser().getId();

        // Fetch the goal and check if it belongs to the logged-in user
        Goal goal = goalService.getGoalById(goalId).orElseThrow(() -> new UnauthorizedRequestException("Goal not found"));

        if (!goal.getUser().getId().equals(loggedInUserId)) {
            throw new UnauthorizedRequestException("You are not authorized to delete this goal.");
        }

        // Call the service method to delete the goal
        goalService.deleteGoal(goalId);
        return "Goal deleted successfully!";
    
    }
}
