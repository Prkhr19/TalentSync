package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

    List<Referral> findByJobApplicationId(Long jobApplicationId);
}
