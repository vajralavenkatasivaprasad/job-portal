package com.jobportal.controller;

import com.jobportal.entity.*;
import com.jobportal.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    private User getCurrentUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername());
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = getCurrentUser(ud);
        model.addAttribute("user", user);
        model.addAttribute("jobs", jobService.findByEmployer(user));
        model.addAttribute("totalJobs", jobService.countByEmployer(user));
        model.addAttribute("totalApplications", applicationService.countByEmployer(user));
        model.addAttribute("shortlisted", applicationService.countShortlistedByEmployer(user));
        model.addAttribute("recentApplications", applicationService.findByJobEmployer(user).stream().limit(5).toList());
        return "employer/dashboard";
    }

    @GetMapping("/jobs")
    public String myJobs(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = getCurrentUser(ud);
        model.addAttribute("jobs", jobService.findByEmployer(user));
        model.addAttribute("user", user);
        return "employer/jobs";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("categories", Job.Category.values());
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
        return "employer/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(@Valid @ModelAttribute Job job, BindingResult result,
                            @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Job.Category.values());
            model.addAttribute("jobTypes", Job.JobType.values());
            model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
            return "employer/job-form";
        }
        User user = getCurrentUser(ud);
        jobService.createJob(job, user);
        ra.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal UserDetails ud) {
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        model.addAttribute("categories", Job.Category.values());
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id, @Valid @ModelAttribute Job job,
                            BindingResult result, @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Job.Category.values());
            model.addAttribute("jobTypes", Job.JobType.values());
            model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
            return "employer/job-form";
        }
        User user = getCurrentUser(ud);
        jobService.updateJob(id, job, user);
        ra.addFlashAttribute("success", "Job updated successfully!");
        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        jobService.deleteJob(id, user);
        ra.addFlashAttribute("success", "Job deleted.");
        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails ud,
                            RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        jobService.toggleJobStatus(id, user);
        ra.addFlashAttribute("success", "Job status updated.");
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs/{id}/applicants")
    public String viewApplicants(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails ud,
                                 Model model) {
        User user = getCurrentUser(ud);
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.findByJob(job));
        model.addAttribute("statuses", Application.Status.values());
        return "employer/applicants";
    }

    @PostMapping("/applications/{appId}/status")
    public String updateApplicationStatus(@PathVariable Long appId,
                                          @RequestParam Application.Status status,
                                          @RequestParam(required = false) String note,
                                          @AuthenticationPrincipal UserDetails ud,
                                          RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        Application app = applicationService.updateStatus(appId, status, note, user);
        ra.addFlashAttribute("success", "Application status updated to " + status.name().replace("_", " "));
        return "redirect:/employer/jobs/" + app.getJob().getId() + "/applicants";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("user", getCurrentUser(ud));
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails ud,
                                @ModelAttribute User updates, RedirectAttributes ra) {
        User user = getCurrentUser(ud);
        userService.updateProfile(user, updates);
        ra.addFlashAttribute("success", "Company profile updated!");
        return "redirect:/employer/profile";
    }
}
