package com.jobportal.controller;

import com.jobportal.dto.RegisterDto;
import com.jobportal.entity.*;
import com.jobportal.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;
    private final UserService userService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("recentJobs", jobService.findRecentJobs());
        model.addAttribute("totalJobs", jobService.countActiveJobs());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "You've been logged out.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        model.addAttribute("roles", new User.Role[]{User.Role.STUDENT, User.Role.EMPLOYER});
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto registerDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (!registerDto.passwordsMatch()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", new User.Role[]{User.Role.STUDENT, User.Role.EMPLOYER});
            return "auth/register";
        }
        userService.register(registerDto);
        redirectAttributes.addFlashAttribute("success", "Account created! Please log in.");
        return "redirect:/login";
    }
}
