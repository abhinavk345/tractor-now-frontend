
package com.sita.enterprises.TractorNow.service;

public interface UserService {

    // Get user details by ID
    Object getById(Long id);

    // Update user profile
    Object update(
            Long id,
            Object request
    );

    // Change user password
    void changePassword(
            Long id,
            String oldPassword,
            String newPassword
    );

    // Soft delete user account
    void delete(Long id);
}
