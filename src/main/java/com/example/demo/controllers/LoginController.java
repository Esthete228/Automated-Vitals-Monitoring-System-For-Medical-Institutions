package com.example.demo.controllers;

import com.example.demo.entities.Department;
import com.example.demo.entities.Doctor;
import com.example.demo.services.DoctorService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final DoctorService doctorService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(DoctorService doctorService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.doctorService = doctorService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
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
        // Retrieve the user details based on the entered username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Check if the entered password matches the encoded password from the user details
        if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
            // User successfully authenticated, save the username in the session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            // Redirect to the home page
            return "redirect:/home";
        } else {
            // Invalid username or password
            System.out.println("Login failed for username: " + username);
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping(value = "/position")
    @ResponseBody
    public PositionResponse getPosition(HttpServletRequest request) {
        // Отримуємо поточне автентифіковане ім'я користувача з сеансу
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Вивід у консоль для перевірки автентифікованого імені користувача
        System.out.println("Authenticated Username: " + authenticatedUsername);

        // Отримуємо користувача зі сховища або бази даних на основі імені користувача
        Doctor authenticatedDoctor = doctorService.findByUsername(authenticatedUsername);

        // Вивід у консоль для перевірки значення автентифікованого користувача та поля position
        System.out.println("Retrieved Doctor: " + authenticatedDoctor);
        if (authenticatedDoctor != null) {
            String userPosition = authenticatedDoctor.getPosition();
            Department userDepartment = authenticatedDoctor.getDepartment();
            System.out.println("Doctor Position: " + userPosition);
            System.out.println("Doctor Department: " + userDepartment);

            return new PositionResponse(userPosition, userDepartment);
        }
        // Повернути position за замовчуванням, якщо позицію користувача не розпізнано
        return new PositionResponse("unknown", null);
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Анулювати сесію для виходу користувача з системи
            session.invalidate();
        }
        return "redirect:/login";
    }

    public record PositionResponse(@JsonProperty("position") String position, @JsonProperty("department") Department department) {
    }
}