package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.MessageResponseDto;
import JobPortal.SpringJobPortal.Dto.ResumeMetadataResponse;
import JobPortal.SpringJobPortal.Dto.ResumeUploadResponse;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Exception.ResourceNotFoundException;
import JobPortal.SpringJobPortal.Repository.CandidateProfileRepository;
import JobPortal.SpringJobPortal.Security.CurrentUserAuth.CurrentUserService;
import JobPortal.SpringJobPortal.Service.Impl.ResumeService;
import JobPortal.SpringJobPortal.Service.Impl.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final CurrentUserService currentUserService;
    private final CandidateProfileRepository candidateProfileRepository;
    private final StorageService storageService;

    @Override
    public ResumeUploadResponse uploadResume(MultipartFile resume) {
        validateResumeFile(resume);

        CandidateProfile candidateProfile = getAuthenticatedCandidateProfile();

        if (candidateProfile.getResumePublicId() != null) {
            storageService.deleteResume(candidateProfile.getResumePublicId());
        }

        ResumeUploadResponse uploadResponse = storageService.uploadResume(resume);

        candidateProfile.setResumeUrl(uploadResponse.getSecureUrl());
        candidateProfile.setResumePublicId(uploadResponse.getPublicId());
        candidateProfile.setResumeFileName(uploadResponse.getFileName());
        candidateProfile.setResumeFileSize(uploadResponse.getFileSize());
        candidateProfile.setResumeUploadedAt(LocalDateTime.now());

        candidateProfileRepository.save(candidateProfile);

        log.info("Resume saved for candidateId={}", candidateProfile.getId());

        return uploadResponse;
    }

    @Override
    public MessageResponseDto deleteResume() {
        CandidateProfile candidateProfile = getAuthenticatedCandidateProfile();

        if (candidateProfile.getResumeUrl() == null) {
            throw new ResourceNotFoundException("Resume not found");
        }

        storageService.deleteResume(candidateProfile.getResumePublicId());
        clearResumeMetadata(candidateProfile);
        candidateProfileRepository.save(candidateProfile);

        log.info("Resume deleted for candidateId={}", candidateProfile.getId());

        return MessageResponseDto.builder()
                .message("Resume deleted successfully")
                .build();
    }

    @Override
    public ResumeMetadataResponse getResumeMetadata() {
        CandidateProfile candidateProfile = getAuthenticatedCandidateProfile();

        if (candidateProfile.getResumeUrl() == null) {
            throw new ResourceNotFoundException("Resume not found");
        }

        return ResumeMetadataResponse.builder()
                .resumeUrl(candidateProfile.getResumeUrl())
                .fileName(candidateProfile.getResumeFileName())
                .uploadedAt(candidateProfile.getResumeUploadedAt())
                .fileSize(candidateProfile.getResumeFileSize())
                .build();
    }

    private CandidateProfile getAuthenticatedCandidateProfile() {
        User user = currentUserService.getCurrentUser();

        if (user.getRole() != RoleType.CANDIDATE) {
            throw new AccessDeniedException("Only candidates can manage resumes");
        }

        return candidateProfileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Candidate profile not found"));
    }

    private void validateResumeFile(MultipartFile resume) {
        if (resume == null) {
            throw new IllegalArgumentException("Resume file is required");
        }

        if (resume.isEmpty()) {
            throw new IllegalArgumentException("Resume file cannot be empty");
        }

        if (!isPdf(resume)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        if (resume.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Resume file size must not exceed 5 MB");
        }
    }

    private boolean isPdf(MultipartFile resume) {
        String contentType = resume.getContentType();
        String originalFilename = resume.getOriginalFilename();

        return PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)
                || (originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf"));
    }

    private void clearResumeMetadata(CandidateProfile candidateProfile) {
        candidateProfile.setResumeUrl(null);
        candidateProfile.setResumePublicId(null);
        candidateProfile.setResumeFileName(null);
        candidateProfile.setResumeFileSize(null);
        candidateProfile.setResumeUploadedAt(null);
    }
}
