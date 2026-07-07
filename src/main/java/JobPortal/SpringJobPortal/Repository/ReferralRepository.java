package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

    List<Referral> findByJobApplicationId(Long jobApplicationId);

    List<Referral> findByJobApplication_Candidate_Id(Long candidateId);

    @Query("""
            SELECT r FROM Referral r
            JOIN FETCH r.jobApplication ja
            JOIN FETCH ja.candidate c
            JOIN FETCH ja.job j
            JOIN FETCH j.company
            ORDER BY r.createdAt DESC
            """)
    List<Referral> findAllWithDetails();

    @Query("""
            SELECT r FROM Referral r
            JOIN FETCH r.jobApplication ja
            JOIN FETCH ja.candidate c
            LEFT JOIN FETCH c.user
            JOIN FETCH ja.job j
            JOIN FETCH j.company
            WHERE r.id = :id
            """)
    Optional<Referral> findByIdWithDetails(@Param("id") Long id);
}
