package JobPortal.SpringJobPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeMetadataResponse {

    private String resumeUrl;
    private String fileName;
    private LocalDateTime uploadedAt;
    private Long fileSize;
}
