package JobPortal.SpringJobPortal.Repository;

import JobPortal.SpringJobPortal.Entity.AdminProfile;
import JobPortal.SpringJobPortal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {
    Optional<AdminProfile> findByUser(User user);

    Optional<AdminProfile> findByUserUserId(Long userId);
}
