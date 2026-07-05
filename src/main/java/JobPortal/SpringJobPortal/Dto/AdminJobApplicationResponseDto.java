package JobPortal.SpringJobPortal.Dto;

import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminJobApplicationResponseDto {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private Double appliedSalary;
    private String appliedJobDescription;
}
