package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.AdminCandidateDetailResponseDto;
import JobPortal.SpringJobPortal.Dto.AdminCandidateSummaryResponseDto;
import org.springframework.data.domain.Page;

public interface AdminCandidateService {

    Page<AdminCandidateSummaryResponseDto> getCandidates(
            String skills,
            String location,
            String preferredLocation,
            String currentCompany,
            String noticePeriod,
            Integer minimumExperience,
            Double maximumExpectedCTC,
            int page,
            int size,
            String sortBy,
            String direction
    );

    AdminCandidateDetailResponseDto getCandidateById(Long candidateId);
}
