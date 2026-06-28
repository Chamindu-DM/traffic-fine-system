package com.trafficfine.authservice.service;

import com.trafficfine.authservice.entity.User;
import com.trafficfine.authservice.repository.UserRepository;
import com.trafficfine.common.dto.LoginRequest;
import com.trafficfine.common.dto.LoginResponse;
import com.trafficfine.common.exception.BusinessRuleException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessRuleException("Invalid admin credentials. Please try again."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessRuleException("Invalid admin credentials. Please try again.");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getRole().name(), jwtService.getExpirationMs());
    }
}
