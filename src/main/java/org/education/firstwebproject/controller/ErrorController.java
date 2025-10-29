package org.education.firstwebproject.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.education.firstwebproject.utils.FlashAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            log.error("Error {} occurred. Message: {}", statusCode, message);
            if (exception != null) {
                log.error("Exception: ", (Throwable) exception);
            }

            model.addAttribute(FlashAttributes.ERROR, statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute(FlashAttributes.ERROR, "Page not found");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute(FlashAttributes.ERROR, "An unexpected error occurred");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute(FlashAttributes.ERROR, "Access denied");
            } else {
                model.addAttribute(FlashAttributes.ERROR, "An error occurred");
            }
        }
        return "error";
    }
}
