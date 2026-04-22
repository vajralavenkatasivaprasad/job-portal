package com.jobportal.controller;

import com.jobportal.entity.*;
import com.jobportal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private User getCurrentUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername());
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = getCurrentUser(ud);
        model.addAttribute("user", user);
        model.addAttribute("applications", applicationService.findByApplicant(user));
        model.addAttribute("totalApplied", applicationService.countByApplicant(user));
        model.addAttribute("recentJobs", jobService.findRecentJobs());
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("user", getCurrentUser(ud));
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails ud,
                                @ModelAttribute User updates,
                                RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        userService.updateProfile(user, updates);
        ra.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/student/profile";
    }

    @PostMapping("/resume/upload")
    public String uploadResume(@AuthenticationPrincipal UserDetails ud,
                               @RequestParam("resumeFile") MultipartFile file,
                               RedirectAttributes ra) {
        try {
            User user = getCurrentUser(ud);
            userService.uploadResume(user, file, uploadDir);
            ra.addFlashAttribute("success", "Resume uploaded successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/applications")
    public String applications(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = getCurrentUser(ud);
        model.addAttribute("applications", applicationService.findByApplicant(user));
        model.addAttribute("user", user);
        return "student/applications";
    }

    @PostMapping("/apply/{jobId}")
    public String applyForJob(@PathVariable Long jobId,
                              @RequestParam(required = false) String coverLetter,
                              @AuthenticationPrincipal UserDetails ud,
                              RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        Job job = jobService.findById(jobId);
        applicationService.apply(user, job, coverLetter);
        ra.addFlashAttribute("success", "Application submitted successfully!");
        return "redirect:/student/applications";
    }

    @PostMapping("/applications/{appId}/withdraw")
    public String withdraw(@PathVariable Long appId,
                           @AuthenticationPrincipal UserDetails ud,
                           RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        applicationService.withdraw(appId, user);
        ra.addFlashAttribute("success", "Application withdrawn.");
        return "redirect:/student/applications";
    }
}
