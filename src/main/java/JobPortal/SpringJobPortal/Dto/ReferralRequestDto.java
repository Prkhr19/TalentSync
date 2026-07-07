package JobPortal.SpringJobPortal.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReferralRequestDto {

    @NotBlank
    private String companyName;

    @NotBlank
    private String contactName;

    @Email
    @NotBlank
    private String contactEmail;

    private LocalDate referredDate;

    @Size(max = 500)
    private String remarks;

    private LocalDate followUpDate;


}
