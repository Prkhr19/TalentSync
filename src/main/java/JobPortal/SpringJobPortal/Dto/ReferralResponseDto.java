package JobPortal.SpringJobPortal.Dto;

import JobPortal.SpringJobPortal.Entity.type.ReferralStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReferralResponseDto {

    private String message;
    private Long id;
    private Long jobApplicationId;
    private String companyName;
    private String contactName;
    private String contactEmail;
    private LocalDate referredDate;
    private ReferralStatus status;
    private String remarks;
    private LocalDate followUpDate;
    private LocalDate interviewDate;
    private LocalDate joiningDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
