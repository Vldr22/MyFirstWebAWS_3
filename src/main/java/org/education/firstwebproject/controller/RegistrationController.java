package org.education.firstwebproject.controller;

import jakarta.validation.Valid;
import org.education.firstwebproject.dto.User;
import org.education.firstwebproject.service.userService.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping()
    public String addUser(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        User user = new User(userForm.getUsername(), userForm.getPassword());

        if (!userService.saveUser(user)) {
            model.addAttribute("username", "Пользователь с таким именем уже существует");
            return "registration";
        }

        model.addAttribute("successMessage", "Регистрация прошла успешно!");
        return "redirect:/login";
    }
}