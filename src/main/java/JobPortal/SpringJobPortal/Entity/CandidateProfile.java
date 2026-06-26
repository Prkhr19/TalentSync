package JobPortal.SpringJobPortal.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String phoneNo;

    private String skills;

    @Column(name = "resume_url")
    private String resumeFileName;

    @Column
    private String experience;

    @Column
    private String Education;

    @Column
    private String location;

    private String preferredLocation;

    private String linkedInUrl;

    private String githubUrl;

    private String portfolioUrl;

    private String totalExperience;

    private String currentCompany;

    private String currentDesignation;

    private String highestQualification;

    private Integer graduationYear;

    private Double currentCtc;

    private Double expectedCtc;

    private String noticePeriod;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

   }
