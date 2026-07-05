package JobPortal.SpringJobPortal.Controller;

import JobPortal.SpringJobPortal.Dto.CompanyRequestDto;
import JobPortal.SpringJobPortal.Dto.CompanyResponseDto;
import JobPortal.SpringJobPortal.Dto.CompanySummaryDto;
import JobPortal.SpringJobPortal.Service.Impl.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/admin/company", "/admin/companies"})
public class AdminCompanyController {

    private final CompanyService companyService;

    @Operation(summary = "List all companies for admin job creation")
    @GetMapping
    public ResponseEntity<List<CompanySummaryDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(summary = "Create a new company")
    @PostMapping
    public ResponseEntity<CompanyResponseDto> createCompany(@Valid @RequestBody CompanyRequestDto companyRequestDto) {
        CompanyResponseDto response = companyService.createCompany(companyRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
