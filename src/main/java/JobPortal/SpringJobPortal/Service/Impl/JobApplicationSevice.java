package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.CandidateProfileReqDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationRequestDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationResponseDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationStatusRequestDto;
import JobPortal.SpringJobPortal.Dto.JobApplicationStatusResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JobApplicationSevice {

    JobApplicationResponseDto applyJob(Long jobId);

    List<JobApplicationResponseDto> getApplicationsByJobId(Long jobId);

    JobApplicationStatusResponseDto updateApplicationStatus(Long applicationId, JobApplicationStatusRequestDto requestDto);
}
