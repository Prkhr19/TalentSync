package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.ReferralRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralStatusRequestDto;
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
@RequestMapping("/admin")
public class AdminReferralController {

    private final ReferralService referralService;

    @Operation(summary = "Create referral for a job application")
    @PostMapping("/applications/{applicationId}/referrals")
    public ResponseEntity<ReferralResponseDto> createReferral(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReferralRequestDto referralRequestDto) {
        ReferralResponseDto response = referralService.createReferral(applicationId, referralRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get referrals by job application")
    @GetMapping("/applications/{applicationId}/referrals")
    public ResponseEntity<List<ReferralResponseDto>> getReferralsByJobApplication(@PathVariable Long applicationId) {
        List<ReferralResponseDto> response = referralService.getReferralsByJobApplication(applicationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all referrals")
    @GetMapping("/referrals")
    public ResponseEntity<List<ReferralResponseDto>> getAllReferrals() {
        List<ReferralResponseDto> response = referralService.getAllReferrals();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get referral by id")
    @GetMapping("/referrals/{id}")
    public ResponseEntity<ReferralResponseDto> getReferralById(@PathVariable Long id) {
        ReferralResponseDto response = referralService.getReferralById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update referral")
    @PutMapping("/referrals/{id}")
    public ResponseEntity<ReferralResponseDto> updateReferral(
            @PathVariable Long id,
            @Valid @RequestBody ReferralRequestDto referralRequestDto) {
        ReferralResponseDto response = referralService.updateReferral(id, referralRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update referral status")
    @PutMapping("/referrals/{id}/status")
    public ResponseEntity<ReferralResponseDto> updateReferralStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReferralStatusRequestDto referralStatusRequestDto) {
        ReferralResponseDto response = referralService.updateReferralStatus(id, referralStatusRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete referral")
    @DeleteMapping("/referrals/{id}")
    public ResponseEntity<ReferralResponseDto> deleteReferral(@PathVariable Long id) {
        ReferralResponseDto response = referralService.deleteReferral(id);
        return ResponseEntity.ok(response);
    }
}
