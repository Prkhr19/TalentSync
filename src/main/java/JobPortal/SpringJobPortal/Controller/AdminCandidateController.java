package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.AdminCandidateDetailResponseDto;
import JobPortal.SpringJobPortal.Dto.AdminCandidateSummaryResponseDto;
import JobPortal.SpringJobPortal.Service.Impl.AdminCandidateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminCandidateController {

    private final AdminCandidateService adminCandidateService;

    @Operation(summary = "Browse and filter registered candidates")
    @GetMapping("/candidates")
    public ResponseEntity<Page<AdminCandidateSummaryResponseDto>> getCandidates(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String preferredLocation,
            @RequestParam(required = false) String currentCompany,
            @RequestParam(required = false) String noticePeriod,
            @RequestParam(required = false) Integer minimumExperience,
            @RequestParam(required = false) Double maximumExpectedCTC,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<AdminCandidateSummaryResponseDto> response = adminCandidateService.getCandidates(
                skills,
                location,
                preferredLocation,
                currentCompany,
                noticePeriod,
                minimumExperience,
                maximumExpectedCTC,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get candidate profile details")
    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<AdminCandidateDetailResponseDto> getCandidateById(@PathVariable Long candidateId) {
        AdminCandidateDetailResponseDto response = adminCandidateService.getCandidateById(candidateId);
        return ResponseEntity.ok(response);
    }
}
