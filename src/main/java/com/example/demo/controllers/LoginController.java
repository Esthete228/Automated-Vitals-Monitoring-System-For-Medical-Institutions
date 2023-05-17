package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final UserRepository userRepository;

    @Autowired
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        // Find the user by username
        User user = userRepository.findByUsername(username);

        // Check if the user exists and the password is correct
        if (user != null && user.getPassword().equals(password)) {
            // User authenticated successfully, redirect to the home page or any other page
            return "redirect:/home";
        } else {
            // Invalid username or password, add an error message to the model
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping
    public PositionResponse getPosition() {
        // Retrieve the user's position from your authentication or session management logic
        String userPosition = getCurrentUserPosition();

        // Create a response object with the user's position
        PositionResponse response = new PositionResponse(userPosition);

        return response;
    }

    private String getCurrentUserPosition() {
        // Implement your logic to retrieve the user's position here
        // This could involve checking the user's role or any other mechanism you use for user authorization
        // Return the user's position as a string (e.g., "admin", "doctor", etc.)
        return "supreme";
    }

    // Response object to represent the user's position
    private static class PositionResponse {
        private String position;

        public PositionResponse(String position) {
            this.position = position;
        }

        // Getter for position

        public String getPosition() {
            return position;
        }
    }
}