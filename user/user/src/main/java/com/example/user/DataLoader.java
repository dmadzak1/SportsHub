package com.example.user;

import com.example.user.model.*;
import com.example.user.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthTokenRepository authTokenRepository;
    private final AuditLogRepository auditLogRepository;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository,
                      AuthTokenRepository authTokenRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authTokenRepository = authTokenRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void run(String... args) {
        // Provjeri da li podaci već postoje
        if (roleRepository.count() > 0) {
            System.out.println("ℹ️ User Service: podaci već postoje, preskačem unos.");
            return;
        }

        // Kreiranje uloga
        Role adminRole   = roleRepository.save(new Role("ADMIN"));
        Role userRole    = roleRepository.save(new Role("USER"));
        Role trainerRole = roleRepository.save(new Role("TRAINER"));

        // Kreiranje korisnika
        User admin   = userRepository.save(new User("admin@sporthub.ba", "hashed_pass_1", adminRole));
        User user1   = userRepository.save(new User("ana@email.com", "hashed_pass_2", userRole));
        User trainer = userRepository.save(new User("petar@email.com", "hashed_pass_3", trainerRole));

        // Kreiranje tokena
        authTokenRepository.save(new AuthToken(admin, "token-abc-123", LocalDateTime.now().plusHours(2)));
        authTokenRepository.save(new AuthToken(user1, "token-xyz-456", LocalDateTime.now().plusHours(1)));

        // Kreiranje audit logova
        auditLogRepository.save(new AuditLog(admin, "LOGIN"));
        auditLogRepository.save(new AuditLog(user1, "RESERVATION_CREATED"));

        System.out.println("✅ User Service: testni podaci uspješno učitani.");
    }
}