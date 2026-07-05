package JobPortal.SpringJobPortal.Service.Impl;

import JobPortal.SpringJobPortal.Dto.CompanyRequestDto;
import JobPortal.SpringJobPortal.Dto.CompanyResponseDto;
import JobPortal.SpringJobPortal.Dto.CompanySummaryDto;

import java.util.List;

public interface CompanyService {

    CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto);

    List<CompanySummaryDto> getAllCompanies();
}
