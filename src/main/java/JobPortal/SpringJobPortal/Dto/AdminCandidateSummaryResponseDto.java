package JobPortal.SpringJobPortal.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminCandidateSummaryResponseDto {

    private Long candidateId;
    private String fullName;
    private String currentCompany;
    private String currentDesignation;
    private String totalExperience;
    private String skills;
    private String location;
    private String preferredLocation;
    private String noticePeriod;
    private Double expectedCTC;
    private String resumeUrl;
}
