package JobPortal.SpringJobPortal.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminCandidateProfessionalInfoDto {

    private String currentCompany;
    private String currentDesignation;
    private String totalExperience;
    private String skills;
    private String highestQualification;
    private Integer graduationYear;
    private Double currentCtc;
    private Double expectedCtc;
    private String noticePeriod;
    private String location;
    private String preferredLocation;
    private String experience;
    private String education;
}
