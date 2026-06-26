package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.CandidateApplicationResposeDto;
import JobPortal.SpringJobPortal.Dto.CandidateProfileReqDto;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.CandidateProfileRepository;
import JobPortal.SpringJobPortal.Repository.JobApplicationRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final CurrentUserService currentUserService;
    private final JobApplicationRepository jobApplicationRepository;
    private final ResumeStorageService resumeStorageService;


    @Override
    public CandidateProfileReqDto updateProfile(CandidateProfileReqDto candidateProfileReqDto, MultipartFile resume) {


        User user = currentUserService.getCurrentUser();
        CandidateProfile candidateProfile = candidateProfileRepository.findByUserUserId(user.getUserId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (resume != null && !resume.isEmpty()) {
            if (candidateProfile.getResumeFileName() != null) {
                resumeStorageService.deleteResume(candidateProfile.getResumeFileName());
            }
            candidateProfile.setResumeFileName(resumeStorageService.storeResume(resume, user.getUserId()));
        } else if (candidateProfile.getResumeFileName() == null) {
            throw new IllegalArgumentException("Resume PDF is required");
        }

        candidateProfile.setUser(candidateProfile.getUser());
        candidateProfile.setName(candidateProfileReqDto.getName());
        candidateProfile.setPhoneNo(candidateProfileReqDto.getPhoneNo());
        candidateProfile.setSkills(candidateProfileReqDto.getSkills());
        candidateProfile.setLocation(candidateProfileReqDto.getLocation());
        candidateProfile.setExperience(candidateProfileReqDto.getExperience());
        candidateProfile.setEducation(candidateProfileReqDto.getEducation());
        candidateProfile.setLinkedInUrl(candidateProfileReqDto.getLinkedInUrl());
        candidateProfile.setTotalExperience(candidateProfileReqDto.getTotalExperience());
        candidateProfile.setCurrentCompany(candidateProfileReqDto.getCurrentCompany());
        candidateProfile.setCurrentDesignation(candidateProfileReqDto.getCurrentDesignation());
        candidateProfile.setHighestQualification(candidateProfileReqDto.getHighestQualification());
        candidateProfile.setGraduationYear(candidateProfileReqDto.getGraduationYear());
        candidateProfile.setCurrentCtc(candidateProfileReqDto.getCurrentCtc());
        candidateProfile.setExpectedCtc(candidateProfileReqDto.getExpectedCtc());
        candidateProfile.setNoticePeriod(candidateProfileReqDto.getNoticePeriod());

        candidateProfileRepository.save(candidateProfile);


        return CandidateProfileReqDto.builder()
                .name(candidateProfile.getName())
                .phoneNo(candidateProfile.getPhoneNo())
                .resumeFileName(candidateProfile.getResumeFileName())
                .location(candidateProfile.getLocation())
                .experience(candidateProfile.getExperience())
                .skills(candidateProfile.getSkills())
                .education(candidateProfile.getEducation())
                .linkedInUrl(candidateProfile.getLinkedInUrl())
                .totalExperience(candidateProfile.getTotalExperience())
                .currentCompany(candidateProfile.getCurrentCompany())
                .currentDesignation(candidateProfile.getCurrentDesignation())
                .highestQualification(candidateProfile.getHighestQualification())
                .graduationYear(candidateProfile.getGraduationYear())
                .currentCtc(candidateProfile.getCurrentCtc())
                .expectedCtc(candidateProfile.getExpectedCtc())
                .noticePeriod(candidateProfile.getNoticePeriod())
                .build();
    }

    @Override
    public Resource getResume() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() != RoleType.CANDIDATE) {
            throw new AccessDeniedException("Unauthorized access");
        }

        CandidateProfile candidateProfile = candidateProfileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (candidateProfile.getResumeFileName() == null) {
            throw new BadCredentialsException("Resume not found");
        }

        return resumeStorageService.loadResume(candidateProfile.getResumeFileName());
    }

    @Override
    public List<CandidateApplicationResposeDto> myApplication() {

        User user = currentUserService.getCurrentUser();

        RoleType roles = user.getRole();

        if (roles != RoleType.CANDIDATE) {
            throw new AccessDeniedException("Unauthorized access");
        }


        List<JobApplication> app = jobApplicationRepository.findByCandidate_Id(user.getCandidateProfile().getId());

        List<CandidateApplicationResposeDto> response = app.stream().map(apps ->CandidateApplicationResposeDto.builder()
                .message("Your Applications")
                .description(apps.getAppliedJobDescription())
                .salary(apps.getAppliedSalary())
                .status(apps.getStatus())

                .build()).toList();

        return response;
    }
}

