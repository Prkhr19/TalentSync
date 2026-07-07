package JobPortal.SpringJobPortal.Mapper;

import JobPortal.SpringJobPortal.Dto.CompanySummaryDto;
import JobPortal.SpringJobPortal.Dto.ReferralDetailResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralResponseDto;
import JobPortal.SpringJobPortal.Dto.ReferralSummaryResponseDto;
import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.Company;
import JobPortal.SpringJobPortal.Entity.Job;
import JobPortal.SpringJobPortal.Entity.JobApplication;
import JobPortal.SpringJobPortal.Entity.Referral;
import JobPortal.SpringJobPortal.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReferralMapper {

    public ReferralResponseDto toResponseDto(Referral referral) {
        return ReferralResponseDto.builder()
                .id(referral.getId())
                .jobApplicationId(referral.getJobApplication().getId())
                .companyName(referral.getCompanyName())
                .contactName(referral.getContactName())
                .contactEmail(referral.getContactEmail())
                .referredDate(referral.getReferredDate())
                .status(referral.getStatus())
                .remarks(referral.getRemarks())
                .followUpDate(referral.getFollowUpDate())
                .interviewDate(referral.getInterviewDate())
                .joiningDate(referral.getJoiningDate())
                .createdAt(referral.getCreatedAt())
                .updatedAt(referral.getUpdatedAt())
                .build();
    }

    public ReferralResponseDto toResponseDto(Referral referral, String message) {
        ReferralResponseDto response = toResponseDto(referral);
        response.setMessage(message);
        return response;
    }

    public ReferralSummaryResponseDto toSummaryDto(Referral referral) {
        JobApplication application = referral.getJobApplication();
        CandidateProfile candidate = application.getCandidate();
        Job job = application.getJob();

        return ReferralSummaryResponseDto.builder()
                .referralId(referral.getId())
                .applicationId(application.getId())
                .candidateName(candidate != null ? candidate.getName() : null)
                .jobTitle(job != null ? job.getTitle() : null)
                .companyName(referral.getCompanyName())
                .applicationStatus(application.getStatus())
                .referralStatus(referral.getStatus())
                .referredAt(referral.getCreatedAt())
                .build();
    }

    public ReferralDetailResponseDto toDetailDto(Referral referral) {
        JobApplication application = referral.getJobApplication();
        CandidateProfile candidate = application.getCandidate();
        Job job = application.getJob();
        Company jobCompany = job != null ? job.getCompany() : null;
        User candidateUser = candidate != null ? candidate.getUser() : null;

        return ReferralDetailResponseDto.builder()
                .referralId(referral.getId())
                .applicationId(application.getId())
                .applicationStatus(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .candidateId(candidate != null ? candidate.getId() : null)
                .candidateName(candidate != null ? candidate.getName() : null)
                .candidateEmail(candidateUser != null ? candidateUser.getEmail() : null)
                .candidatePhone(candidate != null ? candidate.getPhoneNo() : null)
                .jobId(job != null ? job.getId() : null)
                .jobTitle(job != null ? job.getTitle() : null)
                .jobLocation(job != null ? job.getLocation() : null)
                .jobCompany(jobCompany != null ? toCompanySummary(jobCompany) : null)
                .companyName(referral.getCompanyName())
                .contactName(referral.getContactName())
                .contactEmail(referral.getContactEmail())
                .referralStatus(referral.getStatus())
                .remarks(referral.getRemarks())
                .referredDate(referral.getReferredDate())
                .followUpDate(referral.getFollowUpDate())
                .interviewDate(referral.getInterviewDate())
                .joiningDate(referral.getJoiningDate())
                .createdAt(referral.getCreatedAt())
                .updatedAt(referral.getUpdatedAt())
                .build();
    }

    private CompanySummaryDto toCompanySummary(Company company) {
        return CompanySummaryDto.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .location(company.getLocation())
                .website(company.getWebSite())
                .build();
    }
}
