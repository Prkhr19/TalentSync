package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.*;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import JobPortal.SpringJobPortal.Entity.Referral;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.CandidateProfileRepository;
import JobPortal.SpringJobPortal.Repository.JobApplicationRepository;
import JobPortal.SpringJobPortal.Repository.ReferralRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.AdminCandidateService;
import JobPortal.SpringJobPortal.Service.Specifacations.CandidateSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCandidateServiceImpl implements AdminCandidateService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "location", "preferredLocation", "expectedCtc",
            "totalExperience", "currentCompany", "noticePeriod"
    );

    private final CandidateProfileRepository candidateProfileRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ReferralRepository referralRepository;
    private final CurrentUserService currentUserService;

    @Override
    public Page<AdminCandidateSummaryResponseDto> getCandidates(
            String skills,
            String location,
            String preferredLocation,
            String currentCompany,
            String noticePeriod,
            Integer minimumExperience,
            Double maximumExpectedCTC,
            int page,
            int size,
            String sortBy,
            String direction) {

        validateAdminAccess();

        Specification<CandidateProfile> spec = Specification.where(CandidateSpecification.isRegisteredCandidate());

        if (skills != null && !skills.isBlank()) {
            spec = spec.and(CandidateSpecification.hasSkills(skills));
        }
        if (location != null && !location.isBlank()) {
            spec = spec.and(CandidateSpecification.hasLocation(location));
        }
        if (preferredLocation != null && !preferredLocation.isBlank()) {
            spec = spec.and(CandidateSpecification.hasPreferredLocation(preferredLocation));
        }
        if (currentCompany != null && !currentCompany.isBlank()) {
            spec = spec.and(CandidateSpecification.hasCurrentCompany(currentCompany));
        }
        if (noticePeriod != null && !noticePeriod.isBlank()) {
            spec = spec.and(CandidateSpecification.hasNoticePeriod(noticePeriod));
        }
        if (minimumExperience != null) {
            spec = spec.and(CandidateSpecification.hasMinimumExperience(minimumExperience));
        }
        if (maximumExpectedCTC != null) {
            spec = spec.and(CandidateSpecification.hasMaximumExpectedCtc(maximumExpectedCTC));
        }

        int resolvedPage = page >= 0 ? page : 0;
        int resolvedSize = size > 0 ? size : 10;
        String resolvedSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, Sort.by(sortDirection, resolvedSortBy));

        return candidateProfileRepository.findAll(spec, pageable).map(this::mapToSummaryDto);
    }

    @Override
    public AdminCandidateDetailResponseDto getCandidateById(Long candidateId) {
        validateAdminAccess();

        CandidateProfile candidateProfile = candidateProfileRepository.findById(candidateId)
                .orElseThrow(() -> new BadCredentialsException("Candidate not found"));

        validateRegisteredCandidate(candidateProfile);

        List<JobApplication> applications = jobApplicationRepository.findByCandidate_Id(candidateId);
        List<Referral> referrals = referralRepository.findByJobApplication_Candidate_Id(candidateId);

        return AdminCandidateDetailResponseDto.builder()
                .candidateId(candidateProfile.getId())
                .fullName(candidateProfile.getName())
                .email(candidateProfile.getUser() != null ? candidateProfile.getUser().getEmail() : null)
                .phoneNo(candidateProfile.getPhoneNo())
                .professionalInformation(mapToProfessionalInfo(candidateProfile))
                .resumeUrl(candidateProfile.getResumeUrl())
                .linkedInUrl(candidateProfile.getLinkedInUrl())
                .githubUrl(candidateProfile.getGithubUrl())
                .portfolioUrl(candidateProfile.getPortfolioUrl())
                .referralHistory(referrals.stream().map(this::mapToReferralDto).toList())
                .applicationHistory(applications.stream().map(this::mapToApplicationHistoryDto).toList())
                .build();
    }

    private void validateAdminAccess() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() != RoleType.ADMIN) {
            throw new AccessDeniedException("Unauthorized access");
        }
    }

    private void validateRegisteredCandidate(CandidateProfile candidateProfile) {
        if (candidateProfile.getUser() == null || candidateProfile.getUser().getRole() != RoleType.CANDIDATE) {
            throw new BadCredentialsException("Candidate not found");
        }
    }

    private AdminCandidateSummaryResponseDto mapToSummaryDto(CandidateProfile candidateProfile) {
        return AdminCandidateSummaryResponseDto.builder()
                .candidateId(candidateProfile.getId())
                .fullName(candidateProfile.getName())
                .currentCompany(candidateProfile.getCurrentCompany())
                .currentDesignation(candidateProfile.getCurrentDesignation())
                .totalExperience(candidateProfile.getTotalExperience())
                .skills(candidateProfile.getSkills())
                .location(candidateProfile.getLocation())
                .preferredLocation(candidateProfile.getPreferredLocation())
                .noticePeriod(candidateProfile.getNoticePeriod())
                .expectedCTC(candidateProfile.getExpectedCtc())
                .resumeUrl(candidateProfile.getResumeUrl())
                .build();
    }

    private AdminCandidateProfessionalInfoDto mapToProfessionalInfo(CandidateProfile candidateProfile) {
        return AdminCandidateProfessionalInfoDto.builder()
                .currentCompany(candidateProfile.getCurrentCompany())
                .currentDesignation(candidateProfile.getCurrentDesignation())
                .totalExperience(candidateProfile.getTotalExperience())
                .skills(candidateProfile.getSkills())
                .highestQualification(candidateProfile.getHighestQualification())
                .graduationYear(candidateProfile.getGraduationYear())
                .currentCtc(candidateProfile.getCurrentCtc())
                .expectedCtc(candidateProfile.getExpectedCtc())
                .noticePeriod(candidateProfile.getNoticePeriod())
                .location(candidateProfile.getLocation())
                .preferredLocation(candidateProfile.getPreferredLocation())
                .experience(candidateProfile.getExperience())
                .education(candidateProfile.getEducation())
                .build();
    }

    private ReferralResponseDto mapToReferralDto(Referral referral) {
        return ReferralResponseDto.builder()
                .id(referral.getId())
                .jobApplicationId(referral.getJobApplication().getId())
                .companyName(referral.getCompanyName())
                .recruiterName(referral.getRecruiterName())
                .recruiterEmail(referral.getRecruiterEmail())
                .referredDate(referral.getReferredDate())
                .status(referral.getStatus())
                .remarks(referral.getRemarks())
                .followUpDate(referral.getFollowUpDate())
                .interviewDate(referral.getInterviewDate())
                .joiningDate(referral.getJoiningDate())
                .createdAt(referral.getCreatedAt())
                .updatedAt(referral.getUpdatedAt())
                .build();
    }

    private AdminCandidateApplicationHistoryDto mapToApplicationHistoryDto(JobApplication application) {
        return AdminCandidateApplicationHistoryDto.builder()
                .applicationId(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .appliedSalary(application.getAppliedSalary())
                .build();
    }
}
