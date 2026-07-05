package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.CompanyRequestDto;
import JobPortal.SpringJobPortal.Dto.CompanyResponseDto;
import JobPortal.SpringJobPortal.Dto.CompanySummaryDto;
import JobPortal.SpringJobPortal.Entity.Company;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.CompanyRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.CompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CurrentUserService currentUserService;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        validateAdminAccess();

        if (companyRepository.existsByCompanyNameIgnoreCase(companyRequestDto.getCompanyName())) {
            throw new IllegalArgumentException("company already exists with this name");
        }

        Company company = new Company();
        company.setCompanyName(companyRequestDto.getCompanyName());
        company.setDiscription(companyRequestDto.getDescription());
        company.setLocation(companyRequestDto.getLocation());
        company.setWebSite(companyRequestDto.getWebsite());

        Company savedCompany = companyRepository.save(company);

        return CompanyResponseDto.builder()
                .message("Company created successfully")
                .id(savedCompany.getId())
                .companyName(savedCompany.getCompanyName())
                .description(savedCompany.getDiscription())
                .location(savedCompany.getLocation())
                .website(savedCompany.getWebSite())
                .build();
    }

    @Override
    public List<CompanySummaryDto> getAllCompanies() {
        validateAdminAccess();

        return companyRepository.findAllByOrderByCompanyNameAsc()
                .stream()
                .map(company -> CompanySummaryDto.builder()
                        .id(company.getId())
                        .companyName(company.getCompanyName())
                        .location(company.getLocation())
                        .website(company.getWebSite())
                        .build())
                .toList();
    }

    private void validateAdminAccess() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() != RoleType.ADMIN) {
            throw new AccessDeniedException("Unauthorized access");
        }
    }
}
