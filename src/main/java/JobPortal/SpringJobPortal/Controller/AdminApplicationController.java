package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.JobApplicationStatusRequestDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationStatusResponseDto;
import JobPortal.SpringJobPortal.Service.Impl.JobApplicationSevice;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/applications")
public class AdminApplicationController {

    private final JobApplicationSevice jobApplicationService;

    @Operation(summary = "Update job application status")
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationStatusResponseDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody JobApplicationStatusRequestDto requestDto) {
        JobApplicationStatusResponseDto response =
                jobApplicationService.updateApplicationStatus(applicationId, requestDto);
        return ResponseEntity.ok(response);
    }
}
