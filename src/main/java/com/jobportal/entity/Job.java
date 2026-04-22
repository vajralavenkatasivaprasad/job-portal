package com.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3-150 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 50, message = "Description must be at least 50 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String companyName;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotBlank(message = "Job category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @NotBlank(message = "Job type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;

    @Column(columnDefinition = "TEXT")
    private String skillsRequired;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;

    private Integer vacancies;
    private LocalDate applicationDeadline;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Many jobs belong to one employer (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    // One job has many applications
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    public enum Category {
        IT_SOFTWARE, FINANCE, MARKETING, HEALTHCARE, EDUCATION,
        ENGINEERING, DESIGN, SALES, HR, LEGAL, OPERATIONS, OTHER
    }

    public enum JobType {
        FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT, FREELANCE, REMOTE
    }

    public enum ExperienceLevel {
        FRESHER, JUNIOR, MID_LEVEL, SENIOR, LEAD, MANAGER
    }
}
