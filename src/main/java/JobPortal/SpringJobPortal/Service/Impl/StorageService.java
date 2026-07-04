package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    ResumeUploadResponse uploadResume(MultipartFile file);

    void deleteResume(String publicId);
}
