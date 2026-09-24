package com.school;

import com.school.model.User;
import com.school.service.AuthService;
import com.school.service.PasswordUtil;

public class TestAuth {
    public static void main(String[] args) {
        AuthService auth = new AuthService();

        // 1. Create an admin user with a properly hashed password
        // This replaces the placeholder we inserted via SQL.
        System.out.println("Creating admin user...");
        boolean created = auth.register("admin", "admin123", User.Role.ADMIN, null);
        System.out.println("Created: " + created);

        // 2. Try logging in with the correct password
        System.out.println("\nTesting login with correct password...");
        User user = auth.login("admin", "admin123");
        System.out.println("Login result: " + (user != null ? "SUCCESS → " + user : "FAILED"));

        // 3. Try logging in with a wrong password
        System.out.println("\nTesting login with wrong password...");
        User bad = auth.login("admin", "wrongpass");
        System.out.println("Login result: " + (bad != null ? "SUCCESS (BUG!)" : "FAILED (correct)"));

        // 4. Peek at the hash so you can see what BCrypt produces
        System.out.println("\nA sample BCrypt hash of 'admin123':");
        System.out.println(PasswordUtil.hashPassword("admin123"));
    }
}