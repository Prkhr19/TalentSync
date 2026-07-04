package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.MessageResponseDto;
import JobPortal.SpringJobPortal.Dto.ResumeMetadataResponse;
import JobPortal.SpringJobPortal.Dto.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    ResumeUploadResponse uploadResume(MultipartFile resume);

    MessageResponseDto deleteResume();

    ResumeMetadataResponse getResumeMetadata();
}
