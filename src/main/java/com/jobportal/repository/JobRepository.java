package com.jobportal.repository;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployer(User employer);

    List<Job> findByEmployerAndActive(User employer, boolean active);

    Page<Job> findByActive(boolean active, Pageable pageable);

    @Query("""
        SELECT j FROM Job j WHERE j.active = true
        AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:category IS NULL OR j.category = :category)
        AND (:jobType IS NULL OR j.jobType = :jobType)
        AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel)
        ORDER BY j.createdAt DESC
    """)
    Page<Job> searchJobs(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("category") Job.Category category,
        @Param("jobType") Job.JobType jobType,
        @Param("experienceLevel") Job.ExperienceLevel experienceLevel,
        Pageable pageable
    );

    long countByActive(boolean active);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.employer = :employer")
    long countByEmployer(@Param("employer") User employer);

    List<Job> findTop6ByActiveOrderByCreatedAtDesc(boolean active);
}
