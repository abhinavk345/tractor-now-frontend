package com.sita.enterprises.TractorNow.service.impl;

import com.tractornow.dto.auth.LoginRequest;
import com.tractornow.dto.auth.RegisterRequest;
import com.tractornow.dto.auth.SendOtpRequest;
import com.tractornow.dto.auth.VerifyOtpRequest;
import com.tractornow.entity.User;
import com.tractornow.enums.UserRole;
import com.tractornow.repository.UserRepository;
import com.tractornow.service.AuthService;
import com.tractornow.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    public Object registerFarmer(RegisterRequest request) {

        validateRegistration(request);

        checkDuplicateUser(request);

        User user = buildUser(request, UserRole.FARMER);

        user.setMobileVerified(false);
        user.setEmailVerified(false);
        user.setActive(true);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();

        response.put("id", savedUser.getId());
        response.put("fullName", savedUser.getFullName());
        response.put("mobile", savedUser.getMobile());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole());
        response.put("message",
                "Farmer registration successful. Please verify your OTP.");

        return response;
    }

    @Override
    public Object registerOwner(RegisterRequest request) {

        validateRegistration(request);

        checkDuplicateUser(request);

        User user = buildUser(request, UserRole.EQUIPMENT_OWNER);

        user.setMobileVerified(false);
        user.setEmailVerified(false);
        user.setActive(true);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();

        response.put("id", savedUser.getId());
        response.put("fullName", savedUser.getFullName());
        response.put("mobile", savedUser.getMobile());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole());
        response.put("message",
                "Equipment owner registration successful. Please verify your OTP.");

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object login(LoginRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }

        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Mobile number or email is required"
            );
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        User user = userRepository
                .findByEmailOrMobile(
                        request.getUsername(),
                        request.getUsername()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email/mobile or password"
                        ));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalStateException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email/mobile or password"
            );
        }

        Map<String, Object> response = new HashMap<>();

        response.put("id", user.getId());
        response.put("fullName", user.getFullName());
        response.put("mobile", user.getMobile());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("mobileVerified", user.getMobileVerified());
        response.put("emailVerified", user.getEmailVerified());

        /*
         * JWT token should be generated here once JwtService
         * is added to the project.
         *
         * Example:
         *
         * String token = jwtService.generateToken(user);
         * response.put("token", token);
         */

        response.put(
                "message",
                "Login successful"
        );

        return response;
    }

    @Override
    public Object sendOtp(SendOtpRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "OTP request cannot be null"
            );
        }

        if (isBlank(request.getMobile())
                && isBlank(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Mobile number or email is required"
            );
        }

        String otp = otpService.generateAndSaveOtp(
                request.getMobile(),
                request.getEmail()
        );

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                "OTP sent successfully"
        );

        response.put(
                "expiresInSeconds",
                300
        );

        /*
         * Do NOT return OTP in production.
         * This is useful only during local development.
         */
        response.put("developmentOtp", otp);

        return response;
    }

    @Override
    public Object verifyOtp(VerifyOtpRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "OTP verification request cannot be null"
            );
        }

        if (isBlank(request.getOtp())) {
            throw new IllegalArgumentException(
                    "OTP is required"
            );
        }

        if (!request.getOtp().matches("\\d{6}")) {
            throw new IllegalArgumentException(
                    "OTP must contain exactly 6 digits"
            );
        }

        boolean verified = otpService.verifyOtp(
                request.getMobile(),
                request.getEmail(),
                request.getOtp()
        );

        if (!verified) {
            throw new IllegalArgumentException(
                    "Invalid or expired OTP"
            );
        }

        User user = null;

        if (!isBlank(request.getMobile())) {

            user = userRepository
                    .findByMobile(request.getMobile())
                    .orElse(null);
        }

        if (user == null && !isBlank(request.getEmail())) {

            user = userRepository
                    .findByEmail(request.getEmail())
                    .orElse(null);
        }

        if (user != null) {

            if (!isBlank(request.getMobile())) {
                user.setMobileVerified(true);
            }

            if (!isBlank(request.getEmail())) {
                user.setEmailVerified(true);
            }

            userRepository.save(user);
        }

        Map<String, Object> response = new HashMap<>();

        response.put(
                "verified",
                true
        );

        response.put(
                "message",
                "OTP verified successfully"
        );

        return response;
    }

    private User buildUser(
            RegisterRequest request,
            UserRole role
    ) {

        User user = new User();

        user.setFullName(request.getFullName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());

        user.setVillage(request.getVillage());
        user.setDistrict(request.getDistrict());
        user.setState(request.getState());
        user.setPincode(request.getPincode());

        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        user.setRole(role);

        return user;
    }

    private void validateRegistration(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration request cannot be null"
            );
        }

        if (isBlank(request.getFullName())) {
            throw new IllegalArgumentException(
                    "Full name is required"
            );
        }

        if (isBlank(request.getMobile())) {
            throw new IllegalArgumentException(
                    "Mobile number is required"
            );
        }

        if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
            throw new IllegalArgumentException(
                    "Invalid Indian mobile number"
            );
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (!request.getEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new IllegalArgumentException(
                    "Invalid email address"
            );
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (!request.getPassword().equals(
                request.getConfirmPassword()
        )) {
            throw new IllegalArgumentException(
                    "Password and confirm password do not match"
            );
        }
    }

    private void checkDuplicateUser(RegisterRequest request) {

        if (userRepository.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException(
                    "Mobile number already registered"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}