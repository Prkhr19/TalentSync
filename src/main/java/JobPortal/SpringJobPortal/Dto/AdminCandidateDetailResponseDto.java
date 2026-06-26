package JobPortal.SpringJobPortal.Dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminCandidateDetailResponseDto {

    private Long candidateId;
    private String fullName;
    private String email;
    private String phoneNo;
    private AdminCandidateProfessionalInfoDto professionalInformation;
    private String resumeUrl;
    private String linkedInUrl;
    private String githubUrl;
    private String portfolioUrl;
    private List<ReferralResponseDto> referralHistory;
    private List<AdminCandidateApplicationHistoryDto> applicationHistory;
}
