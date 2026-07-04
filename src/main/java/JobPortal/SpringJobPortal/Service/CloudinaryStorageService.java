package JobPortal.SpringJobPortal.Service;

import JobPortal.SpringJobPortal.Dto.ResumeUploadResponse;
import JobPortal.SpringJobPortal.Service.Impl.StorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageService implements StorageService {

    private static final String RESUME_FOLDER = "talentsync/resumes";

    private final Cloudinary cloudinary;

    @Override
    public ResumeUploadResponse uploadResume(MultipartFile file) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "raw",
                    "folder", RESUME_FOLDER,
                    "public_id", uniqueFileName
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            log.info("Resume uploaded to Cloudinary: publicId={}", result.get("public_id"));

            return ResumeUploadResponse.builder()
                    .secureUrl((String) result.get("secure_url"))
                    .publicId((String) result.get("public_id"))
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .build();
        } catch (IOException e) {
            log.error("Failed to upload resume to Cloudinary", e);
            throw new IllegalArgumentException("Failed to upload resume");
        }
    }

    @Override
    public void deleteResume(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "raw")
            );

            log.info("Resume deleted from Cloudinary: publicId={}, result={}", publicId, result.get("result"));
        } catch (Exception e) {
            log.error("Failed to delete resume from Cloudinary: publicId={}", publicId, e);
            throw new IllegalArgumentException("Failed to delete resume");
        }
    }
}
