package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.CandidateApplicationResposeDto;
import JobPortal.SpringJobPortal.Dto.CandidateProfileReqDto;
import JobPortal.SpringJobPortal.Service.Impl.CandidateProfileService;
import JobPortal.SpringJobPortal.Service.Impl.JobServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/candidate")
public class CandidateProfileController {
    private final CandidateProfileService candidateProfileService;
    private final JobServices jobServices;


    @Operation(summary = "Candidate update Profile before applying")
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateProfileReqDto> updateProfile(
            @Valid @ModelAttribute CandidateProfileReqDto candidateProfileReqDto) {
        CandidateProfileReqDto updateProfile = candidateProfileService.updateProfile(candidateProfileReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(updateProfile);
    }

    @Operation(summary = "Applied Application")
    @GetMapping("/applicationStatus")
    public ResponseEntity<List<CandidateApplicationResposeDto>> myApplications(){

        List<CandidateApplicationResposeDto> response = candidateProfileService.myApplication();

        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Candidate Save job")
    @PostMapping("/jobs/{jobId}/saveJob")
    public ResponseEntity<?> saveJob(@PathVariable Long jobId){
        return ResponseEntity.ok(jobServices.saveJob(jobId));
    }

}
