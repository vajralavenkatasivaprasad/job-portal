package com.jobportal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Async
    public void sendApplicationConfirmation(String to, String name, String jobTitle) {
        if (mailSender == null) {
            log.info("[EMAIL SKIPPED] Application confirmation to {} for job '{}'", to, jobTitle);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Application Submitted – " + jobTitle);
            msg.setText(String.format(
                "Dear %s,\n\nYour application for '%s' has been received.\n\n" +
                "We'll notify you once the employer reviews it.\n\nBest regards,\nJobPortal Team",
                name, jobTitle));
            mailSender.send(msg);
            log.info("Confirmation email sent to {}", to);
        } catch (Exception e) {
            log.warn("Could not send confirmation email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendStatusUpdateNotification(String to, String name, String jobTitle, String status) {
        if (mailSender == null) {
            log.info("[EMAIL SKIPPED] Status update to {} – {} for '{}'", to, status, jobTitle);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Application Status Update – " + jobTitle);
            msg.setText(String.format(
                "Dear %s,\n\nYour application for '%s' has been updated.\n" +
                "New Status: %s\n\nLog in to view details.\n\nBest regards,\nJobPortal Team",
                name, jobTitle, status.replace("_", " ")));
            mailSender.send(msg);
        } catch (Exception e) {
            log.warn("Could not send status email to {}: {}", to, e.getMessage());
        }
    }
}
