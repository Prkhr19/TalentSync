package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.CandidateApplicationResposeDto;
import JobPortal.SpringJobPortal.Dto.CandidateProfileReqDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CandidateProfileService {

    CandidateProfileReqDto updateProfile(CandidateProfileReqDto candidateProfileReqDto, MultipartFile resume);

    Resource getResume();

    List<CandidateApplicationResposeDto> myApplication();
}