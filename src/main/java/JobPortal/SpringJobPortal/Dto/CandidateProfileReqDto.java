package JobPortal.SpringJobPortal.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CandidateProfileReqDto {


    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 10)
    private String phoneNo;

    @NotBlank
    private String skills;

    private String resumeUrl;

    @NotBlank
    private String experience;

    @NotBlank
    private String location;

    @NotBlank
    private String linkedInUrl;

    @NotBlank
    private String totalExperience;


    private String currentCompany;

    @NotBlank
    private String currentDesignation;

    @NotBlank
    private String highestQualification;

    @NotNull
    @Positive
    private Integer graduationYear;


    @Positive
    private Double currentCtc;

    @NotNull
    @Positive
    private Double expectedCtc;

    @NotBlank
    private String noticePeriod;

}
