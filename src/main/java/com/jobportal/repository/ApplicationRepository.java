package com.jobportal.repository;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicant(User applicant);

    List<Application> findByApplicantOrderByAppliedAtDesc(User applicant);

    List<Application> findByJob(Job job);

    List<Application> findByJobOrderByAppliedAtDesc(Job job);

    Optional<Application> findByApplicantAndJob(User applicant, Job job);

    boolean existsByApplicantAndJob(User applicant, Job job);

    List<Application> findByJobAndStatus(Job job, Application.Status status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.applicant = :applicant")
    long countByApplicant(@Param("applicant") User applicant);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer = :employer")
    long countByEmployer(@Param("employer") User employer);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer = :employer AND a.status = :status")
    long countByEmployerAndStatus(@Param("employer") User employer, @Param("status") Application.Status status);

    @Query("SELECT COUNT(a) FROM Application a")
    long countTotal();

    @Query("""
        SELECT a FROM Application a 
        WHERE a.job.employer = :employer
        ORDER BY a.appliedAt DESC
    """)
    List<Application> findByJobEmployerOrderByAppliedAtDesc(@Param("employer") User employer);
}
