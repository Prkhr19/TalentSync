package JobPortal.SpringJobPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadResponse {

    private String secureUrl;
    private String publicId;
    private String fileName;
    private Long fileSize;
}
