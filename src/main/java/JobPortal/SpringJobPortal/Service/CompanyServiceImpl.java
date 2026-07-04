package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.CompanyRequestDto;
import JobPortal.SpringJobPortal.Dto.CompanyResponseDto;
import JobPortal.SpringJobPortal.Entity.AdminProfile;
import JobPortal.SpringJobPortal.Entity.Company;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.AdminProfileRepository;
import JobPortal.SpringJobPortal.Repository.CompanyRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.CompanyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CurrentUserService currentUserService;
    private final AdminProfileRepository adminProfileRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {

        User user = currentUserService.getCurrentUser();
        RoleType role = user.getRole();

        if (role != RoleType.ADMIN) {
            throw new AccessDeniedException("Unauthorized access");
        }

        AdminProfile adminProfile = adminProfileRepository.findByUser(user)
                .orElseThrow(() -> new BadCredentialsException("Admin profile not found"));

        if (adminProfile.getCompany() != null) {

            Long existingCompanyId = adminProfile.getCompany().getId();
            if (existingCompanyId != null && companyRepository.existsById(existingCompanyId)) {
                throw new IllegalArgumentException("Company already present");
            }

            adminProfile.setCompany(null);
            adminProfileRepository.save(adminProfile);
        }

        if (companyRepository.existsByCompanyNameIgnoreCase(companyRequestDto.getCompanyName())) {
            throw new IllegalArgumentException("company already exists with this name");
        }

        Company company = new Company();
        company.setCompanyName(companyRequestDto.getCompanyName());
        company.setDiscription(companyRequestDto.getDescription());
        company.setLocation(companyRequestDto.getLocation());
        company.setWebSite(companyRequestDto.getWebsite());

        Company savedCompany = companyRepository.save(company);

        adminProfile.setCompany(savedCompany);

        adminProfileRepository.save(adminProfile);

        return CompanyResponseDto.builder()
                .message("Commpany created successfully")
                .companyName(companyRequestDto.getCompanyName())
                .description(companyRequestDto.getDescription())
                .build();
    }
}
