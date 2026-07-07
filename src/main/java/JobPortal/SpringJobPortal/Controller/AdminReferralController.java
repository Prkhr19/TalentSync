package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.ReferralDetailResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralStatusRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralSummaryResponseDto;
import JobPortal.SpringJobPortal.Service.Impl.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/referrals")
public class AdminReferralController {

    private final ReferralService referralService;

    @Operation(summary = "Get all referrals")
    @GetMapping
    public ResponseEntity<List<ReferralSummaryResponseDto>> getAllReferrals() {
        List<ReferralSummaryResponseDto> response = referralService.getAllReferrals();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get referral by id")
    @GetMapping("/{id}")
    public ResponseEntity<ReferralDetailResponseDto> getReferralById(@PathVariable Long id) {
        ReferralDetailResponseDto response = referralService.getReferralById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update referral")
    @PutMapping("/{id}")
    public ResponseEntity<ReferralResponseDto> updateReferral(
            @PathVariable Long id,
            @Valid @RequestBody ReferralRequestDto referralRequestDto) {
        ReferralResponseDto response = referralService.updateReferral(id, referralRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update referral status")
    @PatchMapping("/{id}/status")
    @PutMapping("/{id}/status")
    public ResponseEntity<ReferralResponseDto> updateReferralStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReferralStatusRequestDto referralStatusRequestDto) {
        ReferralResponseDto response = referralService.updateReferralStatus(id, referralStatusRequestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete referral")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReferralResponseDto> deleteReferral(@PathVariable Long id) {
        ReferralResponseDto response = referralService.deleteReferral(id);
        return ResponseEntity.ok(response);
    }
}
