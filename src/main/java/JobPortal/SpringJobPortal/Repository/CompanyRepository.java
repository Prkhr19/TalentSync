package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCompanyNameIgnoreCase(String companyName);

    List<Company> findAllByOrderByCompanyNameAsc();
}