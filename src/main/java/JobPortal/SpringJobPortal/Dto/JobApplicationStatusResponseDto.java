package JobPortal.SpringJobPortal.Dto;

import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationStatusResponseDto {

    private Long applicationId;
    private ApplicationStatus status;
    private String message;
}
