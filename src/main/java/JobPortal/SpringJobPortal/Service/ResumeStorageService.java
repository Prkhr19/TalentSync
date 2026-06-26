package JobPortal.SpringJobPortal.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ResumeStorageService {

    @Value("${app.resume.upload-dir:uploads/resumes}")
    private String uploadDir;

    public String storeResume(MultipartFile file, Long userId) {
        validatePdf(file);

        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String fileName = userId + "_" + System.currentTimeMillis() + ".pdf";
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to store resume");
        }
    }

    public Resource loadResume(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new BadCredentialsException("Resume not found");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Failed to load resume");
        }
    }

    public void deleteResume(String fileName) {
        if (fileName == null) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(uploadDir).resolve(fileName).normalize());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to delete resume");
        }
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume PDF is required");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        boolean isPdf = "application/pdf".equals(contentType)
                || (originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf"));

        if (!isPdf) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
    }
}
