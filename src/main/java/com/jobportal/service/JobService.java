package com.jobportal.service;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob(Job job, User employer) {
        job.setEmployer(employer);
        job.setCompanyName(employer.getCompanyName() != null ? employer.getCompanyName() : "Unknown");
        return jobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public Job findById(Long id) {
        return jobRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Job> searchJobs(String keyword, String location, Job.Category category,
                                 Job.JobType jobType, Job.ExperienceLevel experienceLevel,
                                 int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String loc = (location != null && !location.isBlank()) ? location.trim() : null;
        return jobRepository.searchJobs(kw, loc, category, jobType, experienceLevel, pageable);
    }

    @Transactional(readOnly = true)
    public List<Job> findByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    @Transactional(readOnly = true)
    public List<Job> findRecentJobs() {
        return jobRepository.findTop6ByActiveOrderByCreatedAtDesc(true);
    }

    public Job updateJob(Long jobId, Job updates, User employer) {
        Job job = findById(jobId);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("You can only edit your own jobs");
        }
        job.setTitle(updates.getTitle());
        job.setDescription(updates.getDescription());
        job.setLocation(updates.getLocation());
        job.setCategory(updates.getCategory());
        job.setJobType(updates.getJobType());
        job.setExperienceLevel(updates.getExperienceLevel());
        job.setSkillsRequired(updates.getSkillsRequired());
        job.setSalaryMin(updates.getSalaryMin());
        job.setSalaryMax(updates.getSalaryMax());
        job.setVacancies(updates.getVacancies());
        job.setApplicationDeadline(updates.getApplicationDeadline());
        return jobRepository.save(job);
    }

    public void toggleJobStatus(Long jobId, User employer) {
        Job job = findById(jobId);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("Not authorized");
        }
        job.setActive(!job.isActive());
        jobRepository.save(job);
    }

    public void deleteJob(Long jobId, User requester) {
        Job job = findById(jobId);
        boolean isAdmin = requester.getRole() == User.Role.ADMIN;
        boolean isOwner = job.getEmployer().getId().equals(requester.getId());
        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Not authorized to delete this job");
        }
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public long countActiveJobs() {
        return jobRepository.countByActive(true);
    }

    @Transactional(readOnly = true)
    public long countByEmployer(User employer) {
        return jobRepository.countByEmployer(employer);
    }
}
