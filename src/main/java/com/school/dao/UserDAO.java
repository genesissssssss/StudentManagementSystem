package com.school.dao;

import com.school.config.DBConnection;
import com.school.model.User;

import java.sql.*;

public class UserDAO {

    /**
     * Looks up a user by username. Returns null if not found.
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, user_role, student_id " +
                "FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserts a new user. Returns the generated ID.
     */
    public int createUser(String username, String passwordHash, User.Role user_role, Integer studentId) {
        String sql = "INSERT INTO users (username, password_hash, user_role, student_id) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, user_role.name());  // Enum -> String

            if (studentId == null) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, studentId);
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return -1;  // signal failure
    }

    /**
     * Updates a user's password hash.
     */
    public void updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }

    /**
     * Helper: converts a ResultSet row into a User object.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        User.Role role = User.Role.valueOf(rs.getString("user_role"));

        // student_id can be NULL — use getObject to distinguish
        Integer studentId = (Integer) rs.getObject("student_id");

        return new User(id, username, passwordHash, role, studentId);
    }
}