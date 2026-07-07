package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.CandidateProfileReqDto;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.Job;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import JobPortal.SpringJobPortal.Entity.type.JobStatus;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.JobRepository;
import JobPortal.SpringJobPortal.Repository.UserRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Dto.JobApplicationRequestDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationResponseDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationStatusRequestDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationStatusResponseDto;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Repository.CandidateProfileRepository;
import JobPortal.SpringJobPortal.Repository.JobApplicationRepository;
import JobPortal.SpringJobPortal.Service.Impl.JobApplicationSevice;
import JobPortal.SpringJobPortal.Exception.InvalidApplicationStatusException;
import JobPortal.SpringJobPortal.Exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationSeviceImpl implements JobApplicationSevice {
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;
    private final JobRepository jobRepository;


    @Override
    public JobApplicationResponseDto applyJob(Long jobId) {

        User user = currentUserService.getCurrentUser();


        if (user.getRole() != RoleType.CANDIDATE) {
            throw new AccessDeniedException("Only candidates can apply for jobs");
        }

        CandidateProfile profile = user.getCandidateProfile();

        validateCandidateProfile(profile);

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new BadCredentialsException("Job not found with this id"));


        if (job.getStatus() != JobStatus.OPEN) {
            throw new IllegalArgumentException("This job is now closed");
        }

        CandidateProfile candidate = candidateProfileRepository.findByUserUserId(user.getUserId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (jobApplicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new AccessDeniedException("User already applied for this job");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setAppliedSalary(job.getSalary());
        application.setAppliedJobDescription(job.getDescription());
        application.setStatus(ApplicationStatus.APPLIED);

        jobApplicationRepository.save(application);

        return JobApplicationResponseDto.builder()
                .message("Applied Successfully")
                .name(candidate.getName())
                .appliedJobDescription(job.getDescription())
                .status(ApplicationStatus.APPLIED)
                .appliedSalary(job.getSalary())
                .build();


    }

    @Override
    public List<JobApplicationResponseDto> getApplicationsByJobId(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        List<JobApplication> applications = jobApplicationRepository.findAllByJobId(jobId);

        return applications.stream()
                .map(application -> JobApplicationResponseDto.builder()
                        .name(application.getCandidate().getName())
                        .status(application.getStatus())
                        .appliedSalary(application.getAppliedSalary())
                        .appliedJobDescription(application.getAppliedJobDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public JobApplicationStatusResponseDto updateApplicationStatus(
            Long applicationId,
            @Valid JobApplicationStatusRequestDto requestDto) {
        validateAdminAccess();

        if (requestDto.getStatus() == ApplicationStatus.REFERRED) {
            throw new InvalidApplicationStatusException(
                    "Application status REFERRED can only be set by creating a referral.");
        }

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        application.setStatus(requestDto.getStatus());
        JobApplication updatedApplication = jobApplicationRepository.save(application);

        return JobApplicationStatusResponseDto.builder()
                .applicationId(updatedApplication.getId())
                .status(updatedApplication.getStatus())
                .message("Application status updated successfully")
                .build();
    }

    private void validateAdminAccess() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() != RoleType.ADMIN) {
            throw new AccessDeniedException("Unauthorized access");
        }
    }

    private void validateCandidateProfile(CandidateProfile profile) {
        if (profile.getPhoneNo() == null || profile.getResumeUrl() == null || profile.getSkills() == null
                || profile.getEducation() == null || profile.getLinkedInUrl() == null
                || profile.getTotalExperience() == null || profile.getCurrentCompany() == null
                || profile.getCurrentDesignation() == null || profile.getHighestQualification() == null
                || profile.getGraduationYear() == null || profile.getCurrentCtc() == null
                || profile.getExpectedCtc() == null || profile.getNoticePeriod() == null) {
            throw new IllegalArgumentException("Complele your profile to start applying");
        }
    }
}



