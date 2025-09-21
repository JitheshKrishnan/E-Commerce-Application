package com.example.ecommerce.backend.config;

import com.example.ecommerce.backend.model.User;
import com.example.ecommerce.backend.model.UserRole;
import com.example.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userRepository.existsByEmail("admin@ecommerce.com")) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail("admin@ecommerce.com");
            admin.setHashedPassword(passwordEncoder.encode("admin123")); // Change this!
            admin.setRole(UserRole.ADMIN);
            admin.setIsActive(true);
            admin.setAddress("System Address");

            userRepository.save(admin);
            System.out.println("Default admin user created: admin@ecommerce.com / admin123");
        }

        // Create default support user if not exists
        if (!userRepository.existsByEmail("support@ecommerce.com")) {
            User support = new User();
            support.setName("Support Staff");
            support.setEmail("support@ecommerce.com");
            support.setHashedPassword(passwordEncoder.encode("support123"));
            support.setRole(UserRole.SUPPORT);
            support.setIsActive(true);

            userRepository.save(support);
            System.out.println("Default support user created: support@ecommerce.com / support123");
        }
    }
}