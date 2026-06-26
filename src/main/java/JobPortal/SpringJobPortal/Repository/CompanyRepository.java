package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCompanyNameIgnoreCase(String companyName);

    Optional<Company> findFirstByRecruiters_User_UserId(Long userId);
}