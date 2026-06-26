package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long>, JpaSpecificationExecutor<CandidateProfile> {
    Optional<CandidateProfile> findByUserUserId(Long userId);


}