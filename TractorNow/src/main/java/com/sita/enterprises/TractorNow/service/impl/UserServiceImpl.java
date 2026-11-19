package com.sita.enterprises.TractorNow.service.impl;

import com.sita.enterprises.TractorNow.entity.User;
import com.sita.enterprises.TractorNow.repository.UserRepository;
import com.sita.enterprises.TractorNow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Object getById(Long id) {

        User user =
                findUser(id);

        return toResponse(user);
    }

    @Override
    public Object update(
            Long id,
            Object request
    ) {

        /*
         * Replace Object with UserUpdateRequest
         * when that DTO is created.
         */

        throw new UnsupportedOperationException(
                "Use UserUpdateRequest for profile update"
        );
    }

    @Override
    public void changePassword(
            Long id,
            String oldPassword,
            String newPassword
    ) {

        User user =
                findUser(id);

        if (!passwordEncoder.matches(
                oldPassword,
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        if (newPassword == null
                || newPassword.length() < 8) {

            throw new RuntimeException(
                    "Password must contain at least 8 characters"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {

        User user =
                findUser(id);

        user.setActive(false);

        userRepository.save(user);
    }

    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    private Map<String, Object> toResponse(
            User user
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "id",
                user.getId()
        );

        response.put(
                "fullName",
                user.getFullName()
        );

        response.put(
                "mobile",
                user.getMobile()
        );

        response.put(
                "email",
                user.getEmail()
        );

        response.put(
                "role",
                user.getRole()
        );

        response.put(
                "village",
                user.getVillage()
        );

        response.put(
                "district",
                user.getDistrict()
        );

        response.put(
                "state",
                user.getState()
        );

        response.put(
                "pincode",
                user.getPincode()
        );

        response.put(
                "latitude",
                user.getLatitude()
        );

        response.put(
                "longitude",
                user.getLongitude()
        );

        response.put(
                "profileImage",
                user.getProfileImage()
        );

        response.put(
                "mobileVerified",
                user.getMobileVerified()
        );

        response.put(
                "emailVerified",
                user.getEmailVerified()
        );

        return response;
    }
}