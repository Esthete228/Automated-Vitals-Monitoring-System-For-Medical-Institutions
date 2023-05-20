package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotEmpty;

@Controller
@Validated
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
    public String login(
            @NotEmpty @RequestParam String username,
            @NotEmpty @RequestParam String password,
            HttpServletRequest request,
            Model model
    ) {
        // Find the user by username
        User user = userRepository.findByUsername(username);

        // Check if the user exists and the password is correct
        if (user != null && user.getPassword().equals(password)) {
            // User authenticated successfully, store the username in the session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            // Redirect to the home page or any other page
            return "redirect:/home";
        } else {
            // Invalid username or password, add an error message to the model
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping(value = "/position")
    @ResponseBody
    public PositionResponse getPosition(HttpServletRequest request) {
        // Retrieve the currently authenticated username from the session
        String authenticatedUsername = getAuthenticatedUsername(request);

        // Add a debug log to check the authenticated username
        System.out.println("Authenticated Username: " + authenticatedUsername);

        // Retrieve the user from the repository or database based on the username
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername);

        // Add debug logs to check the value of the authenticated user and the position field
        System.out.println("Retrieved User: " + authenticatedUser);
        if (authenticatedUser != null) {
            String userPosition = authenticatedUser.getPosition();
            System.out.println("User Position: " + userPosition);

            if ("admin".equalsIgnoreCase(userPosition)) {
                return new PositionResponse("admin");
            } else if ("doctor".equalsIgnoreCase(userPosition)) {
                return new PositionResponse("doctor");
            }
        }

        return new PositionResponse("unknown"); // Return a default position if the user's position is not recognized
    }

    private String getAuthenticatedUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("username");
        }
        return null;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Invalidate the session to logout the user
            session.invalidate();
        }
        return "redirect:/login";
    }

}
