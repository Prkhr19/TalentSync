package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.ReferralRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralStatusRequestDto;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import JobPortal.SpringJobPortal.Entity.Referral;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import JobPortal.SpringJobPortal.Entity.type.ReferralStatus;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.JobApplicationRepository;
import JobPortal.SpringJobPortal.Repository.ReferralRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.ReferralService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    @Override
    public ReferralResponseDto createReferral(Long applicationId, @Valid ReferralRequestDto referralRequestDto) {
        validateAdminAccess();

        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BadCredentialsException("Job application not found"));

        validateJobApplicationCandidate(jobApplication);

        LocalDate referredDate = referralRequestDto.getReferredDate() != null
                ? referralRequestDto.getReferredDate()
                : LocalDate.now();

        Referral referral = Referral.builder()
                .jobApplication(jobApplication)
                .companyName(referralRequestDto.getCompanyName())
                .recruiterName(referralRequestDto.getRecruiterName())
                .recruiterEmail(referralRequestDto.getRecruiterEmail())
                .referredDate(referredDate)
                .status(ReferralStatus.REFERRED)
                .remarks(referralRequestDto.getRemarks())
                .followUpDate(referralRequestDto.getFollowUpDate())
                .interviewDate(referralRequestDto.getInterviewDate())
                .joiningDate(referralRequestDto.getJoiningDate())
                .build();

        Referral savedReferral = referralRepository.save(referral);

        if (jobApplication.getStatus() != ApplicationStatus.REFERRED) {
            jobApplication.setStatus(ApplicationStatus.REFERRED);
            jobApplicationRepository.save(jobApplication);
        }

        ReferralResponseDto response = mapToResponseDto(savedReferral);
        response.setMessage("Referral created successfully");
        return response;
    }

    @Override
    public ReferralResponseDto getReferralById(Long id) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Referral not found"));

        return mapToResponseDto(referral);
    }

    @Override
    public List<ReferralResponseDto> getAllReferrals() {
        validateAdminAccess();

        return referralRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public List<ReferralResponseDto> getReferralsByJobApplication(Long applicationId) {
        validateAdminAccess();

        if (!jobApplicationRepository.existsById(applicationId)) {
            throw new BadCredentialsException("Job application not found");
        }

        return referralRepository.findByJobApplicationId(applicationId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public ReferralResponseDto updateReferral(Long id, @Valid ReferralRequestDto referralRequestDto) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Referral not found"));

        referral.setCompanyName(referralRequestDto.getCompanyName());
        referral.setRecruiterName(referralRequestDto.getRecruiterName());
        referral.setRecruiterEmail(referralRequestDto.getRecruiterEmail());
        referral.setRemarks(referralRequestDto.getRemarks());
        referral.setFollowUpDate(referralRequestDto.getFollowUpDate());
        referral.setInterviewDate(referralRequestDto.getInterviewDate());
        referral.setJoiningDate(referralRequestDto.getJoiningDate());

        if (referralRequestDto.getReferredDate() != null) {
            referral.setReferredDate(referralRequestDto.getReferredDate());
        }

        Referral updatedReferral = referralRepository.save(referral);

        ReferralResponseDto response = mapToResponseDto(updatedReferral);
        response.setMessage("Referral updated successfully");
        return response;
    }

    @Transactional
    @Override
    public ReferralResponseDto updateReferralStatus(Long id, ReferralStatusRequestDto referralStatusRequestDto) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Referral not found"));

        referral.setStatus(referralStatusRequestDto.getStatus());

        Referral updatedReferral = referralRepository.save(referral);

        ReferralResponseDto response = mapToResponseDto(updatedReferral);
        response.setMessage("Referral status updated successfully");
        return response;
    }

    @Transactional
    @Override
    public ReferralResponseDto deleteReferral(Long id) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Referral not found"));

        referralRepository.delete(referral);

        return ReferralResponseDto.builder()
                .message("Referral deleted successfully")
                .id(id)
                .build();
    }

    private void validateAdminAccess() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() != RoleType.ADMIN) {
            throw new AccessDeniedException("Unauthorized access");
        }
    }

    private void validateJobApplicationCandidate(JobApplication jobApplication) {
        CandidateProfile candidate = jobApplication.getCandidate();

        if (candidate == null || candidate.getUser() == null) {
            throw new IllegalArgumentException("Job application does not belong to a valid candidate");
        }
    }

    private ReferralResponseDto mapToResponseDto(Referral referral) {
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
}
