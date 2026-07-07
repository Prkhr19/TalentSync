package JobPortal.SpringJobPortal.Dto;

import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import JobPortal.SpringJobPortal.Entity.type.ReferralStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferralSummaryResponseDto {

    private Long referralId;
    private Long applicationId;
    private String candidateName;
    private String jobTitle;
    private String companyName;
    private ApplicationStatus applicationStatus;
    private ReferralStatus referralStatus;
    private LocalDateTime referredAt;
}
