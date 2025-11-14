package org.education.firstwebproject.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.education.firstwebproject.model.CommonResponse;
import org.education.firstwebproject.model.request.AuthRequest;
import org.education.firstwebproject.model.response.LoginResponse;
import org.education.firstwebproject.service.auth.AuthService;
import org.education.firstwebproject.exception.messages.Messages;
import org.education.firstwebproject.service.security.RateLimit;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @RateLimit(requests = 5, window = 1, key = "auth:login")
    public CommonResponse<LoginResponse> login(@Valid @RequestBody AuthRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request, response);
        return CommonResponse.success(loginResponse);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @RateLimit(requests = 3, window = 10, key = "auth:register")
    public CommonResponse<String> register(@Valid @RequestBody AuthRequest request) {
        authService.register(request);
        return CommonResponse.success(Messages.REGISTRATION_SUCCESS);
    }

    @PostMapping("/logout")
    public CommonResponse<String> logout(HttpServletResponse response) {
        authService.logout(response);
        return CommonResponse.success(Messages.LOGOUT_SUCCESS);
    }
}