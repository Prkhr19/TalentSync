package JobPortal.SpringJobPortal.Config;

import JobPortal.SpringJobPortal.Entity.AdminProfile;
import JobPortal.SpringJobPortal.Entity.User;
import JobPortal.SpringJobPortal.Entity.type.RoleType;
import JobPortal.SpringJobPortal.Repository.AdminProfileRepository;
import JobPortal.SpringJobPortal.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class adminSeed implements CommandLineRunner {

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProfileRepository adminProfileRepository;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.existsByEmail(adminEmail)){
            return;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(RoleType.ADMIN);
        admin.setIsActive(true);

        admin = userRepository.save(admin);

        AdminProfile profile = new AdminProfile();
        profile.setUser(admin);
        adminProfileRepository.save(profile);
    }
}
