package com.school.dao;

import com.school.config.DBConnection;
import com.school.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    //CREATE
    public int addStudent(Student s) {
        String sql = "INSERT INTO students (name, email, phone, date_of_birth, enrollment_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, s.getName());
            pstmt.setString(2, s.getEmail());
            pstmt.setString(3, s.getPhone());
            pstmt.setDate(4, s.getDateOfBirth() != null
                    ? Date.valueOf(s.getDateOfBirth()) : null);
            pstmt.setDate(5, Date.valueOf(s.getEnrollmentDate()));

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
        return -1;
    }

    // READ ALL
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
        return list;
    }

    // READ(one)
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRowToStudent(rs);
        } catch (SQLException e) {
            System.err.println("Error getting student: " + e.getMessage());
        }
        return null;
    }

    // READ search
    public List<Student> searchByName(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRowToStudent(rs));
        } catch (SQLException e) {
            System.err.println("Error searching: " + e.getMessage());
        }
        return list;
    }

    //UPDATE
    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET name=?, email=?, phone=?, " +
                "date_of_birth=?, enrollment_date=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getName());
            pstmt.setString(2, s.getEmail());
            pstmt.setString(3, s.getPhone());
            pstmt.setDate(4, s.getDateOfBirth() != null
                    ? Date.valueOf(s.getDateOfBirth()) : null);
            pstmt.setDate(5, Date.valueOf(s.getEnrollmentDate()));
            pstmt.setInt(6, s.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    //DELETE
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    // HELPER
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Date dobSql = rs.getDate("date_of_birth");
        LocalDate dob = (dobSql != null) ? dobSql.toLocalDate() : null;

        Date enrollSql = rs.getDate("enrollment_date");
        LocalDate enroll = (enrollSql != null) ? enrollSql.toLocalDate() : null;

        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                dob,
                enroll
        );
    }
}