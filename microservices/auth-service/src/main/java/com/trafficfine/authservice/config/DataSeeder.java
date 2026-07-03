package com.trafficfine.authservice.config;

import com.trafficfine.authservice.entity.Role;
import com.trafficfine.authservice.entity.User;
import com.trafficfine.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAuthData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(new User(
                        "Senior Police Admin",
                        "admin",
                        passwordEncoder.encode("admin123"),
                        Role.ADMIN
                ));
            }
        };
    }
}
