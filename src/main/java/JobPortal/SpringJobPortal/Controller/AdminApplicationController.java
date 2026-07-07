package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.JobApplicationStatusRequestDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationStatusResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Service.Impl.JobApplicationSevice;
import JobPortal.SpringJobPortal.Service.Impl.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/applications")
public class AdminApplicationController {

    private final JobApplicationSevice jobApplicationService;
    private final ReferralService referralService;

    @Operation(summary = "Update job application status")
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationStatusResponseDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody JobApplicationStatusRequestDto requestDto) {
        JobApplicationStatusResponseDto response =
                jobApplicationService.updateApplicationStatus(applicationId, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create referral for a job application")
    @PostMapping("/{applicationId}/referrals")
    public ResponseEntity<ReferralResponseDto> createReferral(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReferralRequestDto referralRequestDto) {
        ReferralResponseDto response = referralService.createReferral(applicationId, referralRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get referrals by job application")
    @GetMapping("/{applicationId}/referrals")
    public ResponseEntity<List<ReferralResponseDto>> getReferralsByJobApplication(
            @PathVariable Long applicationId) {
        List<ReferralResponseDto> response = referralService.getReferralsByJobApplication(applicationId);
        return ResponseEntity.ok(response);
    }
}
