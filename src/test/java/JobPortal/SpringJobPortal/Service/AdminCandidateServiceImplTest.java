package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.CandidateProfileRepository;
import JobPortal.SpringJobPortal.Repository.JobApplicationRepository;
import JobPortal.SpringJobPortal.Repository.ReferralRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Specifacations.CandidateSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCandidateServiceImplTest {

    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AdminCandidateServiceImpl adminCandidateService;

    @Test
    void getCandidatesReturnsPagedResultsForAdmin() {
        User admin = User.builder().role(RoleType.ADMIN).build();
        CandidateProfile candidate = buildCandidate(1L, "Alice");

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(candidateProfileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(candidate)));

        Page<?> response = adminCandidateService.getCandidates(
                null, null, null, null, null, null, null, 0, 10, "id", "desc");

        assertEquals(1, response.getTotalElements());
        verify(candidateProfileRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getCandidatesAppliesFiltersWhenProvided() {
        User admin = User.builder().role(RoleType.ADMIN).build();
        CandidateProfile candidate = buildCandidate(2L, "Bob");

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(candidateProfileRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(candidate)));

        adminCandidateService.getCandidates(
                "Java",
                "Bangalore",
                "Remote",
                "Tech Corp",
                "30 days",
                3,
                1500000.0,
                0,
                5,
                "expectedCtc",
                "asc"
        );

        verify(candidateProfileRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getCandidatesRejectsNonAdminUsers() {
        User candidateUser = User.builder().role(RoleType.CANDIDATE).build();
        when(currentUserService.getCurrentUser()).thenReturn(candidateUser);

        assertThrows(AccessDeniedException.class, () -> adminCandidateService.getCandidates(
                null, null, null, null, null, null, null, 0, 10, "id", "desc"));
    }

    @Test
    void getCandidateByIdReturnsCompleteProfileForAdmin() {
        User admin = User.builder().role(RoleType.ADMIN).build();
        CandidateProfile candidate = buildCandidate(5L, "Charlie");
        candidate.setUser(User.builder().email("charlie@example.com").role(RoleType.CANDIDATE).build());

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(candidateProfileRepository.findById(5L)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.findByCandidate_Id(5L)).thenReturn(List.of());
        when(referralRepository.findByJobApplication_Candidate_Id(5L)).thenReturn(List.of());

        var response = adminCandidateService.getCandidateById(5L);

        assertEquals(5L, response.getCandidateId());
        assertEquals("Charlie", response.getFullName());
        assertEquals("charlie@example.com", response.getEmail());
        assertNotNull(response.getProfessionalInformation());
    }

    @Test
    void candidateSpecificationBuildsRegisteredCandidateFilter() {
        Specification<CandidateProfile> spec = CandidateSpecification.isRegisteredCandidate();
        assertNotNull(spec);
    }

    private CandidateProfile buildCandidate(Long id, String name) {
        CandidateProfile candidate = new CandidateProfile();
        candidate.setId(id);
        candidate.setName(name);
        candidate.setSkills("Java");
        candidate.setLocation("Bangalore");
        candidate.setPreferredLocation("Remote");
        candidate.setCurrentCompany("Tech Corp");
        candidate.setCurrentDesignation("Engineer");
        candidate.setTotalExperience("5 years");
        candidate.setNoticePeriod("30 days");
        candidate.setExpectedCtc(1200000.0);
        candidate.setResumeFileName("5_resume.pdf");
        candidate.setUser(User.builder().role(RoleType.CANDIDATE).email(name.toLowerCase() + "@example.com").build());
        return candidate;
    }
}
