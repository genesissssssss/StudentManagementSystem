package com.school.service;

import com.school.dao.UserDAO;
import com.school.model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Attempts to log in. Returns the User on success, null on failure.
     */
    public User login(String username, String plainPassword) {
        if (username == null || username.isBlank()) return null;
        if (plainPassword == null || plainPassword.isEmpty()) return null;

        User user = userDAO.findByUsername(username.trim());
        if (user == null) return null;  // user doesn't exist

        if (PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    /**
     * Registers a new user with a hashed password.
     */
    public boolean register(String username, String plainPassword, User.Role user_role, Integer studentId) {
        if (username == null || username.isBlank()) return false;
        if (plainPassword == null || plainPassword.length() < 6) return false;

        String hash = PasswordUtil.hashPassword(plainPassword);
        int id = userDAO.createUser(username.trim(), hash, user_role, studentId);
        return id > 0;
    }

    /**
     * Changes a user's password.
     */
    public boolean changePassword(int userId, String newPlainPassword) {
        if (newPlainPassword == null || newPlainPassword.length() < 6) return false;
        String hash = PasswordUtil.hashPassword(newPlainPassword);
        userDAO.updatePassword(userId, hash);
        return true;
    }
}