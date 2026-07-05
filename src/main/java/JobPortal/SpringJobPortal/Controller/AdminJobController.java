package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.*;
import JobPortal.SpringJobPortal.Service.Impl.JobServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/jobs")
public class AdminJobController {

    private final JobServices jobService;

    @Operation(summary = "Create a job for a company")
    @PostMapping
    public ResponseEntity<JobResponseDto> createJob(@Valid @RequestBody JobRequestDto jobRequestDto) {
        JobResponseDto response = jobService.createJob(jobRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a job")
    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDto> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDto jobRequestDto) {
        return ResponseEntity.ok(jobService.updateJob(id, jobRequestDto));
    }

    @Operation(summary = "Update job status")
    @PutMapping("/{id}/status")
    public ResponseEntity<JobStatusResponseDto> updateJobStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobStatusRequestDto jobStatusRequestDto) {
        return ResponseEntity.ok(jobService.updateJobStatus(id, jobStatusRequestDto));
    }

    @Operation(summary = "Patch job salary")
    @PatchMapping("/{id}")
    public ResponseEntity<JobPatchResponseDto> patchJob(
            @PathVariable Long id,
            @Valid @RequestBody JobPatchRequestDto jobPatchRequestDto) {
        return ResponseEntity.ok(jobService.patchJob(id, jobPatchRequestDto));
    }
}
