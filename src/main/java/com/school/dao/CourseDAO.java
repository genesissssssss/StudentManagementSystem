package com.school.dao;

import com.school.config.DBConnection;
import com.school.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    //Create
    public int addCourse(Course c) {
        String sql = "INSERT INTO courses (code, name, instructor, credits) " +
                "VALUES (?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, c.getCode());
            pstmt.setString(2, c.getName());
            pstmt.setString(3, c.getInstructor());
            pstmt.setInt(4, c.getCredits());

            int rows = pstmt.executeUpdate();
            if (rows > 0){
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);

            }

        }  catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
        return -1;

    }

    //READ ALL
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY code";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapRowToCourse(rs));
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
        return list;
    }

    //READ ONE
    public Course getCourseById(int id){
        String sql = "SELECT * FROM courses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRowToCourse(rs);
        } catch (SQLException e){
            System.err.println("Error getting course: " + e.getMessage());
        }
        return null;

    }

    //SEARCH
    public List<Course> searchCourses(String keyword){
    List<Course> list = new ArrayList<>();
    String sql = "SELECT * FROM courses" +
                 "WHERE code LIKE ? OR name LIKE ? OR instructor LIKE ?" +
                 "ORDER BY code";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)){

        String pattern = "%" + keyword + "%";
        pstmt.setString(1, pattern);
        pstmt.setString(2, pattern);
        pstmt.setString(3, pattern);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) list.add(mapRowToCourse(rs));
        } catch (SQLException e){
        System.err.println("Error searching courses: " + e.getMessage());
        }
    return list;
    }

    //UPDATE
    public boolean updateCourse(Course c) {
        String sql = "UPDATE courses SET code=?, name=?, instructor=?, credits=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getCode());
            pstmt.setString(2, c.getName());
            pstmt.setString(3, c.getInstructor());
            pstmt.setInt(4, c.getCredits());
            pstmt.setInt(5, c.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        }
    }

    //HELPER
    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("instructor"),
                rs.getInt("credits")
        );
    }


}


