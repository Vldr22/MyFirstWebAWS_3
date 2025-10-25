package org.education.firstwebproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.exceptionHandler.UserAlreadyExistsException;
import org.education.firstwebproject.model.User;
import org.education.firstwebproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("userForm") @Valid User userForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try {
            User user = new User(userForm.getUsername(), userForm.getPassword());
            userService.registerUser(user);

            log.info("User registered successfully: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("success",
                    "Регистрация прошла успешно! Теперь вы можете войти.");
            return "redirect:/login";

        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed - user already exists: {}", userForm.getUsername());
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь с таким именем уже существует!");
            return "redirect:/registration";

        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", userForm.getUsername(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Произошла ошибка при регистрации. Попробуйте позже.");
            return "redirect:/registration";
        }
    }
}