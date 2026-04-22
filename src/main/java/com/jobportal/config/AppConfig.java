package com.jobportal.config;

import com.jobportal.entity.*;
import com.jobportal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
@Slf4j
public class AppConfig {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepo, JobRepository jobRepo,
                                      ApplicationRepository appRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() > 0) return; // Already seeded

            log.info("Seeding demo data...");

            // Admin
            User admin = userRepo.save(User.builder()
                .fullName("Admin User").email("admin@jobportal.com")
                .password(encoder.encode("admin123")).role(User.Role.ADMIN).build());

            // Employers
            User emp1 = userRepo.save(User.builder()
                .fullName("Sarah Johnson").email("employer@techcorp.com")
                .password(encoder.encode("employer123")).role(User.Role.EMPLOYER)
                .companyName("TechCorp Solutions").industry("IT Software")
                .companyDescription("Leading software solutions provider").build());

            User emp2 = userRepo.save(User.builder()
                .fullName("Mark Wilson").email("hr@financeplus.com")
                .password(encoder.encode("employer123")).role(User.Role.EMPLOYER)
                .companyName("Finance Plus").industry("Finance")
                .companyDescription("Top financial services firm").build());

            // Students
            User std1 = userRepo.save(User.builder()
                .fullName("Ravi Kumar").email("student@gmail.com")
                .password(encoder.encode("student123")).role(User.Role.STUDENT)
                .skills("Java, Spring Boot, MySQL").experience("1 year").build());

            User std2 = userRepo.save(User.builder()
                .fullName("Priya Sharma").email("priya@gmail.com")
                .password(encoder.encode("student123")).role(User.Role.STUDENT)
                .skills("React, Node.js, MongoDB").experience("Fresher").build());

            // Jobs
            Job j1 = jobRepo.save(Job.builder()
                .title("Java Backend Developer").description("We are looking for an experienced Java developer to join our growing team. You will work on scalable microservices using Spring Boot and contribute to product architecture decisions.")
                .companyName("TechCorp Solutions").location("Bangalore")
                .category(Job.Category.IT_SOFTWARE).jobType(Job.JobType.FULL_TIME)
                .experienceLevel(Job.ExperienceLevel.JUNIOR)
                .skillsRequired("Java, Spring Boot, MySQL, REST API")
                .salaryMin(new BigDecimal("400000")).salaryMax(new BigDecimal("700000"))
                .vacancies(3).applicationDeadline(LocalDate.now().plusDays(30))
                .employer(emp1).active(true).build());

            Job j2 = jobRepo.save(Job.builder()
                .title("React Frontend Developer").description("Join our UI team to build world-class interfaces. You will collaborate with designers and backend engineers to deliver exceptional user experiences.")
                .companyName("TechCorp Solutions").location("Remote")
                .category(Job.Category.IT_SOFTWARE).jobType(Job.JobType.REMOTE)
                .experienceLevel(Job.ExperienceLevel.MID_LEVEL)
                .skillsRequired("React, TypeScript, CSS, HTML")
                .salaryMin(new BigDecimal("500000")).salaryMax(new BigDecimal("900000"))
                .vacancies(2).applicationDeadline(LocalDate.now().plusDays(20))
                .employer(emp1).active(true).build());

            Job j3 = jobRepo.save(Job.builder()
                .title("Financial Analyst Intern").description("Great opportunity for fresh graduates to gain hands-on experience in financial modeling, reporting, and business analysis in a fast-paced environment.")
                .companyName("Finance Plus").location("Mumbai")
                .category(Job.Category.FINANCE).jobType(Job.JobType.INTERNSHIP)
                .experienceLevel(Job.ExperienceLevel.FRESHER)
                .skillsRequired("Excel, Financial Modeling, Communication")
                .salaryMin(new BigDecimal("15000")).salaryMax(new BigDecimal("25000"))
                .vacancies(5).applicationDeadline(LocalDate.now().plusDays(15))
                .employer(emp2).active(true).build());

            Job j4 = jobRepo.save(Job.builder()
                .title("DevOps Engineer").description("We need a skilled DevOps Engineer to manage CI/CD pipelines, cloud infrastructure, and monitoring. You will help scale our infrastructure to handle millions of users.")
                .companyName("TechCorp Solutions").location("Hyderabad")
                .category(Job.Category.IT_SOFTWARE).jobType(Job.JobType.FULL_TIME)
                .experienceLevel(Job.ExperienceLevel.SENIOR)
                .skillsRequired("Docker, Kubernetes, AWS, Jenkins, Linux")
                .salaryMin(new BigDecimal("1000000")).salaryMax(new BigDecimal("1800000"))
                .vacancies(1).applicationDeadline(LocalDate.now().plusDays(25))
                .employer(emp1).active(true).build());

            // Sample application
            appRepo.save(Application.builder()
                .applicant(std1).job(j1).status(Application.Status.SHORTLISTED)
                .coverLetter("I am very interested in this position and believe my Spring Boot skills are a great match.").build());

            appRepo.save(Application.builder()
                .applicant(std2).job(j2).status(Application.Status.APPLIED)
                .coverLetter("I have built several React applications and would love to join TechCorp.").build());

            log.info("✅ Demo data seeded successfully!");
            log.info("📌 Login credentials:");
            log.info("   Admin:    admin@jobportal.com / admin123");
            log.info("   Employer: employer@techcorp.com / employer123");
            log.info("   Student:  student@gmail.com / student123");
        };
    }
}
