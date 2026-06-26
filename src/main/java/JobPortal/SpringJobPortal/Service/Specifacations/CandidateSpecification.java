package JobPortal.SpringJobPortal.Service.Specifacations;

import JobPortal.SpringJobPortal.Entity.CandidateProfile;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import org.springframework.data.jpa.domain.Specification;

public class CandidateSpecification {

    public static Specification<CandidateProfile> isRegisteredCandidate() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("user").get("role"), RoleType.CANDIDATE);
    }

    public static Specification<CandidateProfile> hasSkills(String skills) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("skills")), "%" + skills.toLowerCase() + "%");
    }

    public static Specification<CandidateProfile> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<CandidateProfile> hasPreferredLocation(String preferredLocation) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("preferredLocation")), "%" + preferredLocation.toLowerCase() + "%");
    }

    public static Specification<CandidateProfile> hasCurrentCompany(String currentCompany) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("currentCompany")), "%" + currentCompany.toLowerCase() + "%");
    }

    public static Specification<CandidateProfile> hasNoticePeriod(String noticePeriod) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("noticePeriod")), "%" + noticePeriod.toLowerCase() + "%");
    }

    public static Specification<CandidateProfile> hasMinimumExperience(Integer minimumExperience) {
        String experienceValue = String.valueOf(minimumExperience);
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("totalExperience")), experienceValue + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("totalExperience")), "%" + experienceValue + " %"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("totalExperience")), "%" + experienceValue + " year%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("experience")), experienceValue + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("experience")), "%" + experienceValue + " %"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("experience")), "%" + experienceValue + " year%")
        );
    }

    public static Specification<CandidateProfile> hasMaximumExpectedCtc(Double maximumExpectedCtc) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("expectedCtc")),
                criteriaBuilder.lessThanOrEqualTo(root.get("expectedCtc"), maximumExpectedCtc)
        );
    }
}
