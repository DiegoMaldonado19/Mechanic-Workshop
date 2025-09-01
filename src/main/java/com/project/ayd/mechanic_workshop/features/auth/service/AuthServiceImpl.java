package com.project.ayd.mechanic_workshop.features.auth.service;

import com.project.ayd.mechanic_workshop.features.auth.dto.*;
import com.project.ayd.mechanic_workshop.features.auth.entity.*;
import com.project.ayd.mechanic_workshop.features.auth.repository.*;
import com.project.ayd.mechanic_workshop.features.auth.security.CustomUserDetailsService;
import com.project.ayd.mechanic_workshop.features.auth.security.JwtTokenProvider;
import com.project.ayd.mechanic_workshop.features.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final UserTypeRepository userTypeRepository;
    private final GenderRepository genderRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorAuthService twoFactorAuthService;
    private final CustomUserDetailsService userDetailsService;

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findActiveByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

            if (!user.isAccountNonLocked()) {
                throw new BadCredentialsException("Account is temporarily locked. Try again later.");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            resetFailedAttempts(user);

            String twoFactorCode = SecurityUtils.generateTwoFactorCode();
            user.setTwoFactorCode(twoFactorCode);
            user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);

            twoFactorAuthService.sendTwoFactorCode(user.getPerson().getEmail(), twoFactorCode);

            return AuthResponse.builder()
                    .username(user.getUsername())
                    .userType(user.getUserType().getName())
                    .fullName(user.getPerson().getFirstName() + " " + user.getPerson().getLastName())
                    .requiresTwoFactor(true)
                    .message("Two-factor authentication code sent to your email")
                    .build();

        } catch (AuthenticationException ex) {
            handleFailedLogin(request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public AuthResponse verifyTwoFactor(TwoFactorAuthRequest request) {
        User user = userRepository.findActiveByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username"));

        if (!user.isTwoFactorCodeValid() || !request.getCode().equals(user.getTwoFactorCode())) {
            throw new BadCredentialsException("Invalid or expired two-factor code");
        }

        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Cargar UserDetails usando el CustomUserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Crear authentication con UserDetails
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L) // 1 hour
                .username(user.getUsername())
                .userType(user.getUserType().getName())
                .fullName(user.getPerson().getFirstName() + " " + user.getPerson().getLastName())
                .userCui(user.getPerson().getCui())
                .requiresTwoFactor(false)
                .message("Authentication successful")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (personRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        if (personRepository.existsByCui(request.getCui())) {
            throw new IllegalArgumentException("CUI is already registered");
        }

        if (personRepository.existsByNit(request.getNit())) {
            throw new IllegalArgumentException("NIT is already registered");
        }

        Person person = new Person();
        person.setCui(request.getCui());
        person.setNit(request.getNit());
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        person.setBirthDate(request.getBirthDate());

        if (request.getGenderId() != null) {
            Gender gender = genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid gender ID"));
            person.setGender(gender);
        }

        person = personRepository.save(person);

        UserType userType = userTypeRepository.findById(request.getUserTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user type ID"));

        User user = new User();
        user.setPerson(person);
        user.setUserType(userType);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        userRepository.save(user);

        return AuthResponse.builder()
                .username(user.getUsername())
                .userType(user.getUserType().getName())
                .fullName(person.getFirstName() + " " + person.getLastName())
                .message("User registered successfully")
                .build();
    }

    @Override
    public void logout(String token) {
        // Token blacklisting could be implemented here if needed
        log.info("User logged out successfully");
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String tokenType = tokenProvider.getTokenTypeFromToken(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BadCredentialsException("Invalid token type");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Cargar UserDetails usando el CustomUserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Crear authentication con UserDetails
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(3600L)
                .username(user.getUsername())
                .userType(user.getUserType().getName())
                .fullName(user.getPerson().getFirstName() + " " + user.getPerson().getLastName())
                .message("Token refreshed successfully")
                .build();
    }

    @Override
    @Transactional
    public void sendTwoFactorCode(String username) {
        User user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String twoFactorCode = SecurityUtils.generateTwoFactorCode();
        user.setTwoFactorCode(twoFactorCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        twoFactorAuthService.sendTwoFactorCode(user.getPerson().getEmail(), twoFactorCode);
    }

    @Override
    public boolean validateTwoFactorCode(String username, String code) {
        User user = userRepository.findActiveByUsername(username)
                .orElse(null);

        return user != null &&
                user.isTwoFactorCodeValid() &&
                code.equals(user.getTwoFactorCode());
    }

    private void handleFailedLogin(String username) {
        userRepository.findActiveByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            }

            userRepository.save(user);
        });
    }

    private void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
    }
}