package com.jobportal.controller;

import com.jobportal.entity.*;
import com.jobportal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserService userService;
    private final ApplicationService applicationService;

    @GetMapping("/jobs")
    public String listJobs(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String location,
                           @RequestParam(required = false) Job.Category category,
                           @RequestParam(required = false) Job.JobType jobType,
                           @RequestParam(required = false) Job.ExperienceLevel experienceLevel,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        Page<Job> jobPage = jobService.searchJobs(keyword, location, category, jobType, experienceLevel, page, 9);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalElements", jobPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedJobType", jobType);
        model.addAttribute("selectedExperience", experienceLevel);
        model.addAttribute("categories", Job.Category.values());
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
        return "jobs/list";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername());
            model.addAttribute("currentUser", user);
            if (user.getRole() == User.Role.STUDENT) {
                model.addAttribute("hasApplied", applicationService.hasApplied(user, job));
            }
        }
        return "jobs/detail";
    }
}
