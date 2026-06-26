package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.ReferralRequestDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralStatusRequestDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ReferralService {

    ReferralResponseDto createReferral(Long applicationId, @Valid ReferralRequestDto referralRequestDto);

    ReferralResponseDto getReferralById(Long id);

    List<ReferralResponseDto> getAllReferrals();

    List<ReferralResponseDto> getReferralsByJobApplication(Long applicationId);

    ReferralResponseDto updateReferral(Long id, @Valid ReferralRequestDto referralRequestDto);

    ReferralResponseDto updateReferralStatus(Long id, ReferralStatusRequestDto referralStatusRequestDto);

    ReferralResponseDto deleteReferral(Long id);
}
