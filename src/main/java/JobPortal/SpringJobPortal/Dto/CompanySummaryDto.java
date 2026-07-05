package JobPortal.SpringJobPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanySummaryDto {

    private Long id;
    private String companyName;
    private String location;
    private String website;
}
