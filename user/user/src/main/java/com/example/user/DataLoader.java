package com.example.user;

import com.example.user.model.*;
import com.example.user.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {

    private static final List<String> DEFAULT_ROLE_NAMES = List.of("ADMIN", "USER", "TRAINER");

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
        List<Role> roles = roleRepository.findAll().stream()
                .sorted(Comparator.comparing(Role::getRoleId))
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            roles = DEFAULT_ROLE_NAMES.stream()
                    .map(Role::new)
                    .map(roleRepository::save)
                    .collect(Collectors.toList());
        } else {
            List<Role> blankRoles = roles.stream()
                    .filter(role -> role.getName() == null || role.getName().isBlank())
                    .collect(Collectors.toList());
            Map<String, Role> roleByName = roles.stream()
                    .filter(role -> role.getName() != null && !role.getName().isBlank())
                    .collect(Collectors.toMap(Role::getName, role -> role, (left, right) -> left));

            boolean updated = false;
            int blankIndex = 0;
            for (String requiredRoleName : DEFAULT_ROLE_NAMES) {
                if (roleByName.containsKey(requiredRoleName)) {
                    continue;
                }

                if (blankIndex < blankRoles.size()) {
                    Role role = blankRoles.get(blankIndex++);
                    role.setName(requiredRoleName);
                    roleByName.put(requiredRoleName, role);
                    updated = true;
                } else {
                    roleByName.put(requiredRoleName, roleRepository.save(new Role(requiredRoleName)));
                }
            }

            if (updated) {
                roleRepository.saveAll(roles);
            }

            roles = roleRepository.findAll().stream()
                    .sorted(Comparator.comparing(Role::getRoleId))
                    .collect(Collectors.toList());
        }

        Map<String, Role> roleByName = roles.stream()
                .filter(role -> role.getName() != null && !role.getName().isBlank())
                .collect(Collectors.toMap(Role::getName, role -> role, (left, right) -> left));

        Role adminRole = roleByName.get("ADMIN");
        Role userRole = roleByName.get("USER");
        Role trainerRole = roleByName.get("TRAINER");

        if (adminRole == null || userRole == null || trainerRole == null) {
            throw new IllegalStateException("User Service: roles se nisu uspjele inicijalizirati ispravno.");
        }

        if (userRepository.count() == 0) {
            User admin = userRepository.save(new User("admin@sporthub.ba", "hashed_pass_1", adminRole));
            User user1 = userRepository.save(new User("ana@email.com", "hashed_pass_2", userRole));
            User trainer = userRepository.save(new User("petar@email.com", "hashed_pass_3", trainerRole));

            authTokenRepository.save(new AuthToken(admin, "token-abc-123", LocalDateTime.now().plusHours(2)));
            authTokenRepository.save(new AuthToken(user1, "token-xyz-456", LocalDateTime.now().plusHours(1)));

            auditLogRepository.save(new AuditLog(admin, "LOGIN", LocalDateTime.now()));
            auditLogRepository.save(new AuditLog(user1, "RESERVATION_CREATED", LocalDateTime.now()));

            System.out.println("✅ User Service: testni podaci uspješno učitani.");
            return;
        }

        System.out.println("ℹ️ User Service: role podaci provjereni i popravljeni ako je trebalo.");
    }
}
