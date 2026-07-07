package JobPortal.SpringJobPortal.Dto;

import JobPortal.SpringJobPortal.Entity.type.ApplicationStatus;
import JobPortal.SpringJobPortal.Entity.type.ReferralStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferralDetailResponseDto {

    private Long referralId;
    private Long applicationId;
    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt;

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;

    private Long jobId;
    private String jobTitle;
    private String jobLocation;
    private CompanySummaryDto jobCompany;

    private String companyName;
    private String contactName;
    private String contactEmail;
    private ReferralStatus referralStatus;
    private String remarks;
    private LocalDate referredDate;
    private LocalDate followUpDate;
    private LocalDate interviewDate;
    private LocalDate joiningDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
