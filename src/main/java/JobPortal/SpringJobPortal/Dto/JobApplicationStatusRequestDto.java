package JobPortal.SpringJobPortal.Dto;

import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationStatusRequestDto {

    @NotNull
    private ApplicationStatus status;
}
