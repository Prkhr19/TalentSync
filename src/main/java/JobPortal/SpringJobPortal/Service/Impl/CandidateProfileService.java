package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.CandidateApplicationResposeDto;
import JobPortal.SpringJobPortal.Dto.CandidateProfileReqDto;

import java.util.List;

public interface CandidateProfileService {

    CandidateProfileReqDto getProfile();

    CandidateProfileReqDto updateProfile(CandidateProfileReqDto candidateProfileReqDto);

    List<CandidateApplicationResposeDto> myApplication();
}
