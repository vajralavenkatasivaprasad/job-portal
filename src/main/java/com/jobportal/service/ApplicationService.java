package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final EmailService emailService;

    public Application apply(User applicant, Job job, String coverLetter) {
        if (applicationRepository.existsByApplicantAndJob(applicant, job)) {
            throw new IllegalStateException("You have already applied to this job");
        }
        Application app = Application.builder()
            .applicant(applicant)
            .job(job)
            .coverLetter(coverLetter)
            .resumePath(applicant.getResumePath())
            .status(Application.Status.APPLIED)
            .build();
        Application saved = applicationRepository.save(app);

        // Send confirmation email asynchronously
        emailService.sendApplicationConfirmation(applicant.getEmail(), applicant.getFullName(), job.getTitle());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Application> findByApplicant(User applicant) {
        return applicationRepository.findByApplicantOrderByAppliedAtDesc(applicant);
    }

    @Transactional(readOnly = true)
    public List<Application> findByJob(Job job) {
        return applicationRepository.findByJobOrderByAppliedAtDesc(job);
    }

    @Transactional(readOnly = true)
    public List<Application> findByJobEmployer(User employer) {
        return applicationRepository.findByJobEmployerOrderByAppliedAtDesc(employer);
    }

    @Transactional(readOnly = true)
    public Application findById(Long id) {
        return applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    public Application updateStatus(Long appId, Application.Status newStatus, String note, User employer) {
        Application app = findById(appId);
        if (!app.getJob().getEmployer().getId().equals(employer.getId())) {
            throw new UnauthorizedException("Not authorized to update this application");
        }
        app.setStatus(newStatus);
        if (note != null && !note.isBlank()) app.setEmployerNote(note);
        Application updated = applicationRepository.save(app);

        // Notify applicant
        emailService.sendStatusUpdateNotification(
            app.getApplicant().getEmail(),
            app.getApplicant().getFullName(),
            app.getJob().getTitle(),
            newStatus.name()
        );
        return updated;
    }

    public void withdraw(Long appId, User applicant) {
        Application app = findById(appId);
        if (!app.getApplicant().getId().equals(applicant.getId())) {
            throw new UnauthorizedException("Not authorized");
        }
        app.setStatus(Application.Status.WITHDRAWN);
        applicationRepository.save(app);
    }

    @Transactional(readOnly = true)
    public boolean hasApplied(User applicant, Job job) {
        return applicationRepository.existsByApplicantAndJob(applicant, job);
    }

    @Transactional(readOnly = true)
    public long countByApplicant(User applicant) {
        return applicationRepository.countByApplicant(applicant);
    }

    @Transactional(readOnly = true)
    public long countByEmployer(User employer) {
        return applicationRepository.countByEmployer(employer);
    }

    @Transactional(readOnly = true)
    public long countShortlistedByEmployer(User employer) {
        return applicationRepository.countByEmployerAndStatus(employer, Application.Status.SHORTLISTED);
    }

    @Transactional(readOnly = true)
    public long countTotal() {
        return applicationRepository.countTotal();
    }
}
