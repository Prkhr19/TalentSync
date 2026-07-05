package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.Job;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByCandidateAndJob(CandidateProfile candidateProfile, Job job);

    @Query("""
            SELECT ja FROM JobApplication ja
            JOIN FETCH ja.candidate c
            LEFT JOIN FETCH c.user
            WHERE ja.job.id = :jobId
            ORDER BY ja.appliedAt DESC
            """)
    List<JobApplication> findAllByJobId(@Param("jobId") Long jobId);

    List<JobApplication> findByCandidate_Id(Long candidateId);
}