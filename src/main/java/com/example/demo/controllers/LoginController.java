package com.example.demo.controllers;

import com.example.demo.entities.Doctor;
import com.example.demo.repositories.DoctorRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private final DoctorRepository doctorRepository;

    @Autowired
    public LoginController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
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
        // Знаходимо користувача по змінній username
        Doctor doctor = doctorRepository.findByUsername(username);

        // Перевіряємо чи користувач існує і чи був правильно введений пароль
        if (doctor != null && doctor.getPassword().equals(password)) {
            // Користувач успішно автентифікований, зберігаємо ім'я користувача в сесії
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            // Перенаправлення на домашню сторінку
            return "redirect:/home";
        } else {
            // Невірне ім'я користувача або пароль
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping(value = "/position")
    @ResponseBody
    public PositionResponse getPosition(HttpServletRequest request) {
        // Отримуємо поточне автентифіковане ім'я користувача з сеансу
        String authenticatedUsername = getAuthenticatedUsername(request);

        // Вивід у консоль для перевірки автентифікованого імені користувача
        System.out.println("Authenticated Username: " + authenticatedUsername);

        // Отримуємо користувача зі сховища або бази даних на основі імені користувача
        Doctor authenticatedDoctor = doctorRepository.findByUsername(authenticatedUsername);

        // Вивід у консоль для перевірки значення автентифікованого користувача та поля position
        System.out.println("Retrieved Doctor: " + authenticatedDoctor);
        if (authenticatedDoctor != null) {
            String userPosition = authenticatedDoctor.getPosition();
            System.out.println("Doctor Position: " + userPosition);

            if ("admin".equalsIgnoreCase(userPosition)) {
                return new PositionResponse("admin");
            } else if ("senior".equalsIgnoreCase(userPosition)) {
                return new PositionResponse("senior");
            } else if ("doctor".equalsIgnoreCase(userPosition)) {
                return new PositionResponse("doctor");
        }
        }
        // Повернути position за замовчуванням, якщо позицію користувача не розпізнано
        return new PositionResponse("unknown");
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
            // Анулювати сесію для виходу користувача з системи
            session.invalidate();
        }
        return "redirect:/login";
    }

    public record PositionResponse(@JsonProperty("position") String position) {
    }

}
