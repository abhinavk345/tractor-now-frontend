package com.sita.enterprises.TractorNow.service.impl;
import com.sita.enterprises.TractorNow.dto.auth.*;
import com.sita.enterprises.TractorNow.entity.PasswordResetChallenge;
import com.sita.enterprises.TractorNow.entity.User;

import com.sita.enterprises.TractorNow.enums.UserRole;
import com.sita.enterprises.TractorNow.exception.InvalidOtpException;
import com.sita.enterprises.TractorNow.repository.PasswordResetChallengeRepository;
import com.sita.enterprises.TractorNow.repository.UserRepository;
import com.sita.enterprises.TractorNow.service.AuthService;
import com.sita.enterprises.TractorNow.service.OtpDeliveryService;
import com.sita.enterprises.TractorNow.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    private final PasswordResetChallengeRepository challengeRepository;

    private final OtpDeliveryService otpDeliveryService;


    private final SecureRandom secureRandom;

    @Value("${app.password-reset.otp-secret}")
    private String otpSecret;

    private static final int OTP_EXPIRY_MINUTES = 5;

    private static final int MAX_OTP_ATTEMPTS = 5;

    private static final int RESEND_COOLDOWN_SECONDS = 30;

    private static final int RESET_TOKEN_EXPIRY_MINUTES = 10;

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

        // 1. Validate request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Login request cannot be null"
            );
        }

        // 2. Validate username
        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Mobile number or email is required"
            );
        }

        // 3. Validate password
        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        String username = request.getUsername().trim();

        // 4. Find user by email or mobile number
        User user = userRepository
                .findByEmailOrMobile(username, username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email/mobile or password"
                        )
                );

        // 5. Check account status
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalStateException(
                    "User account is inactive"
            );
        }

        // 6. Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email/mobile or password"
            );
        }

        // 7. Prepare response
        Map<String, Object> response = new HashMap<>();

        response.put("id", user.getId());
        response.put("fullName", user.getFullName());
        response.put("mobile", user.getMobile());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("mobileVerified", user.getMobileVerified());
        response.put("emailVerified", user.getEmailVerified());

        // 8. JWT token generation can be added here
        // String token = jwtService.generateToken(user);
        // response.put("token", token);

        response.put("message", "Login successful");

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

    // =========================================
    // 1. SEND OTP
    // =========================================

    @Override
    @Transactional
    public void sendPasswordResetOtp(
            ForgotPasswordRequest request) {

        String method = request.method().toUpperCase();

        String identifier = normalizeIdentifier(
                method,
                request.identifier()
        );

        validateIdentifier(method, identifier);

        LocalDateTime now = LocalDateTime.now();

        // Find registered user
        Optional<User> userOptional =
                findUser(method, identifier);

        // Find previous challenge
        PasswordResetChallenge challenge =
                challengeRepository
                        .findFirstByMethodAndIdentifierOrderByIdDesc(method, identifier)
                        .orElseGet(() -> {
                            PasswordResetChallenge c =
                                    new PasswordResetChallenge();

                            c.setMethod(method);
                            c.setIdentifier(identifier);
                            c.setAttempts(0);

                            return c;
                        });

        // Enforce resend cooldown
        if (challenge.getLastSentAt() != null &&
                challenge.getLastSentAt()
                        .plusSeconds(RESEND_COOLDOWN_SECONDS)
                        .isAfter(now)) {

            return;
        }

        // Generate cryptographically secure OTP
        String otp = generateOtp();

        // Reset challenge state
        challenge.setUserId(
                userOptional.map(User::getId).orElse(null)
        );

        challenge.setOtpHash(hashOtp(otp));

        challenge.setOtpExpiresAt(
                now.plusMinutes(OTP_EXPIRY_MINUTES)
        );

        challenge.setLastSentAt(now);

        challenge.setVerifiedAt(null);

        challenge.setAttempts(0);

        challenge.setResetTokenHash(null);

        challenge.setResetTokenExpiresAt(null);

        challenge.setUsedAt(null);

        challengeRepository.saveAndFlush(challenge);

        // Deliver OTP only if account exists
        if (userOptional.isPresent()) {

            if ("MOBILE".equals(method)) {
                otpDeliveryService.sendSms(identifier, otp);
            } else {
                otpDeliveryService.sendEmail(identifier, otp);
            }
        }

        // Same response for registered and unregistered accounts.
        // Never log OTP or identifier.
    }


    // =========================================
    // 2. VERIFY OTP
    // =========================================

    @Override
    @Transactional(noRollbackFor = InvalidOtpException.class)
    public String verifyPasswordResetOtp(
            VerifyOtpRequest request) {

        String method = request.getMethod().toUpperCase();

        String identifier = normalizeIdentifier(
                method,
                request.getIdentifier()
        );

        LocalDateTime now = LocalDateTime.now();

        PasswordResetChallenge challenge =
                challengeRepository
                        .findFirstByMethodAndIdentifierOrderByIdDesc(method, identifier)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired OTP."
                                )
                        );

        // Check account existence
        if (challenge.getUserId() == null) {
            throw new IllegalArgumentException(
                    "Invalid or expired OTP."
            );
        }

        // Check OTP already consumed or verified
        if (challenge.getVerifiedAt() != null ||
                challenge.getUsedAt() != null) {

            throw new IllegalArgumentException(
                    "Invalid or expired OTP."
            );
        }

        // Check maximum attempts
        if (challenge.getAttempts() >= MAX_OTP_ATTEMPTS) {
            throw new IllegalArgumentException(
                    "Maximum OTP verification attempts exceeded."
            );
        }

        // Check expiry
        if (challenge.getOtpExpiresAt() == null ||
                !challenge.getOtpExpiresAt().isAfter(now)) {

            throw new IllegalArgumentException(
                    "OTP has expired."
            );
        }

        // Compare hashed OTP
        String suppliedHash = hashOtp(request.getOtp());

        if (!MessageDigest.isEqual(
                challenge.getOtpHash()
                        .getBytes(StandardCharsets.UTF_8),
                suppliedHash.getBytes(StandardCharsets.UTF_8))) {

            challenge.setAttempts(challenge.getAttempts() + 1);

            challengeRepository.saveAndFlush(challenge);

            throw new InvalidOtpException(
                    "Invalid or expired OTP."
            );
        }

        // Generate secure random reset token
        String resetToken = generateResetToken();

        // Store only SHA-256 hash
        challenge.setResetTokenHash(
                hashResetToken(resetToken)
        );

        challenge.setResetTokenExpiresAt(
                now.plusMinutes(RESET_TOKEN_EXPIRY_MINUTES)
        );

        challenge.setVerifiedAt(now);

        // OTP cannot be reused
        challenge.setOtpHash(null);

        challengeRepository.save(challenge);

        // Returned internally to controller.
        // Controller must set HttpOnly cookie.
        return resetToken;
    }


    // =========================================
    // 3. RESET PASSWORD
    // =========================================

    @Override
    @Transactional
    public void resetPassword(
            String resetToken,
            ResetPasswordRequest request) {

        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid or expired reset session."
            );
        }

        if (!request.newPassword().equals(
                request.confirmPassword())) {

            throw new IllegalArgumentException(
                    "Passwords do not match."
            );
        }

        // Hash incoming token
        String tokenHash = hashResetToken(resetToken);

        PasswordResetChallenge challenge =
                challengeRepository
                        .findByTokenHashForUpdate(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired reset session."
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        // Validate token
        if (challenge.getUsedAt() != null ||
                challenge.getVerifiedAt() == null ||
                challenge.getResetTokenExpiresAt() == null ||
                !challenge.getResetTokenExpiresAt().isAfter(now)) {

            throw new IllegalArgumentException(
                    "Invalid or expired reset session."
            );
        }

        // Find user
        User user = userRepository
                .findById(challenge.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found."
                        )
                );

        // Hash password using BCrypt
        String encodedPassword =
                passwordEncoder.encode(request.newPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        // Invalidate every outstanding reset challenge
        challengeRepository.invalidateAllForUser(
                user.getId()
        );

        // Invalidate current challenge explicitly
        challenge.setUsedAt(now);

        challenge.setResetTokenHash(null);

        challenge.setResetTokenExpiresAt(null);

        challenge.setOtpHash(null);

        challengeRepository.save(challenge);
    }


    // =========================================
    // 4. GENERATE SIX-DIGIT OTP
    // =========================================

    private String generateOtp() {

        int otp = secureRandom.nextInt(1_000_000);

        return String.format("%06d", otp);
    }


    // =========================================
    // 5. GENERATE RESET TOKEN
    // =========================================

    private String generateResetToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


    // =========================================
    // 6. HASH OTP USING HMAC-SHA256
    // =========================================

    private String hashOtp(String otp) {

        try {

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec key = new SecretKeySpec(
                    otpSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );

            mac.init(key);

            byte[] hash = mac.doFinal(
                    otp.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "OTP hashing failed."
            );
        }
    }


    // =========================================
    // 7. HASH RESET TOKEN USING SHA-256
    // =========================================

    private String hashResetToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Reset token hashing failed."
            );
        }
    }


    // =========================================
    // 8. FIND USER
    // =========================================

    private Optional<User> findUser(
            String method,
            String identifier) {

        if ("MOBILE".equals(method)) {
            return userRepository.findByMobile(identifier);
        }

        return userRepository.findByEmail(identifier);
    }


    // =========================================
    // 9. NORMALIZE IDENTIFIER
    // =========================================

    private String normalizeIdentifier(
            String method,
            String identifier) {

        String value = identifier.trim();

        if ("EMAIL".equals(method)) {
            return value.toLowerCase();
        }

        return value;
    }


    // =========================================
    // 10. VALIDATE IDENTIFIER
    // =========================================

    private void validateIdentifier(
            String method,
            String identifier) {

        if ("MOBILE".equals(method) &&
                !identifier.matches("^[6-9]\\d{9}$")) {

            throw new IllegalArgumentException(
                    "Invalid mobile number."
            );
        }

        if ("EMAIL".equals(method) &&
                !identifier.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                )) {

            throw new IllegalArgumentException(
                    "Invalid email address."
            );
        }
    }
}