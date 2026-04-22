package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", userService.findByRole(User.Role.STUDENT).size());
        model.addAttribute("totalEmployers", userService.findByRole(User.Role.EMPLOYER).size());
        model.addAttribute("totalJobs", jobService.countActiveJobs());
        model.addAttribute("totalApplications", applicationService.countTotal());
        model.addAttribute("recentUsers", userService.findAllUsers().stream()
            .sorted((a, b) -> b.getCreatedAt() != null && a.getCreatedAt() != null
                ? b.getCreatedAt().compareTo(a.getCreatedAt()) : 0).limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(required = false) User.Role role, Model model) {
        model.addAttribute("users", role != null
            ? userService.findByRole(role) : userService.findAllUsers());
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", User.Role.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleUserStatus(id);
        ra.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String manageJobs(Model model) {
        model.addAttribute("jobs", jobService.searchJobs(null,null,null,null,null,0,100).getContent());
        return "admin/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String adminDeleteJob(@PathVariable Long id, RedirectAttributes ra) {
        Job job = jobService.findById(id);
        jobService.deleteJob(id, job.getEmployer());
        ra.addFlashAttribute("success", "Job removed.");
        return "redirect:/admin/jobs";
    }
}
