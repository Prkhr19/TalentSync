package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.MessageResponseDto;
import JobPortal.SpringJobPortal.Dto.ResumeMetadataResponse;
import JobPortal.SpringJobPortal.Dto.ResumeUploadResponse;
import JobPortal.SpringJobPortal.Service.Impl.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/candidate/resume")
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "Upload candidate resume PDF")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @RequestParam("resume") MultipartFile resume) {
        return ResponseEntity.ok(resumeService.uploadResume(resume));
    }

    @Operation(summary = "Delete candidate resume")
    @DeleteMapping
    public ResponseEntity<MessageResponseDto> deleteResume() {
        return ResponseEntity.ok(resumeService.deleteResume());
    }

    @Operation(summary = "Get candidate resume metadata")
    @GetMapping
    public ResponseEntity<ResumeMetadataResponse> getResumeMetadata() {
        return ResponseEntity.ok(resumeService.getResumeMetadata());
    }
}
