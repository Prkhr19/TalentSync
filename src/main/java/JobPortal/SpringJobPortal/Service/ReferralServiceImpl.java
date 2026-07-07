package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.ReferralDetailResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralStatusRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralSummaryResponseDto;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import JobPortal.SpringJobPortal.Entity.Referral;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import JobPortal.SpringJobPortal.Entity.type.ReferralStatus;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Exception.ResourceNotFoundException;
import JobPortal.SpringJobPortal.Mapper.ReferralMapper;
import JobPortal.SpringJobPortal.Repository.JobApplicationRepository;
import JobPortal.SpringJobPortal.Repository.ReferralRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.ReferralService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;
    private final ReferralMapper referralMapper;

    @Transactional
    @Override
    public ReferralResponseDto createReferral(Long applicationId, @Valid ReferralRequestDto referralRequestDto) {
        validateAdminAccess();

        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        validateJobApplicationCandidate(jobApplication);

        LocalDate referredDate = referralRequestDto.getReferredDate() != null
                ? referralRequestDto.getReferredDate()
                : LocalDate.now();

        Referral referral = Referral.builder()
                .jobApplication(jobApplication)
                .companyName(referralRequestDto.getCompanyName())
                .contactName(referralRequestDto.getContactName())
                .contactEmail(referralRequestDto.getContactEmail())
                .referredDate(referredDate)
                .status(ReferralStatus.REFERRED)
                .remarks(referralRequestDto.getRemarks())
                .followUpDate(referralRequestDto.getFollowUpDate())
                .build();

        jobApplication.setStatus(ApplicationStatus.REFERRED);

        Referral savedReferral = referralRepository.save(referral);
        jobApplicationRepository.save(jobApplication);

        return referralMapper.toResponseDto(savedReferral, "Referral created successfully");
    }

    @Override
    public ReferralDetailResponseDto getReferralById(Long id) {
        validateAdminAccess();

        Referral referral = referralRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found"));

        return referralMapper.toDetailDto(referral);
    }

    @Override
    public List<ReferralSummaryResponseDto> getAllReferrals() {
        validateAdminAccess();

        return referralRepository.findAllWithDetails()
                .stream()
                .map(referralMapper::toSummaryDto)
                .toList();
    }

    @Override
    public List<ReferralResponseDto> getReferralsByJobApplication(Long applicationId) {
        validateAdminAccess();

        if (!jobApplicationRepository.existsById(applicationId)) {
            throw new ResourceNotFoundException("Job application not found");
        }

        return referralRepository.findByJobApplicationId(applicationId)
                .stream()
                .map(referralMapper::toResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public ReferralResponseDto updateReferral(Long id, @Valid ReferralRequestDto referralRequestDto) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found"));

        referral.setCompanyName(referralRequestDto.getCompanyName());
        referral.setContactName(referralRequestDto.getContactName());
        referral.setContactEmail(referralRequestDto.getContactEmail());
        referral.setRemarks(referralRequestDto.getRemarks());
        referral.setFollowUpDate(referralRequestDto.getFollowUpDate());
        if (referralRequestDto.getReferredDate() != null) {
            referral.setReferredDate(referralRequestDto.getReferredDate());
        }

        Referral updatedReferral = referralRepository.save(referral);

        return referralMapper.toResponseDto(updatedReferral, "Referral updated successfully");
    }

    @Transactional
    @Override
    public ReferralResponseDto updateReferralStatus(Long id, ReferralStatusRequestDto referralStatusRequestDto) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found"));

        referral.setStatus(referralStatusRequestDto.getStatus());

        Referral updatedReferral = referralRepository.save(referral);

        return referralMapper.toResponseDto(updatedReferral, "Referral status updated successfully");
    }

    @Transactional
    @Override
    public ReferralResponseDto deleteReferral(Long id) {
        validateAdminAccess();

        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found"));

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
}
