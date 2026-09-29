package com.school.dao;

import com.school.config.DBConnection;
import com.school.model.Enrollment;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    private static final String BASE_QUERY =
            "SELECT e.id, e.grade, e.enrolledAt, " +
            "       s.id AS student_id, s.name AS student_name, " +
            "       c.id AS course_id, c.code AS course_code, c.name AS course_name " +
            "FROM enrollments e " +
            "JOIN students s ON e.student_id = s.id " +
            "JOIN courses  c ON e.course_id  = c.id ";

    //CREATE
    public int addEnrollment(Enrollment e){
        String sql = "INSERT INTO enrollments (student_id, course_id, grade) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

                pstmt.setInt(1, e.getStudentId());
                pstmt.setInt(2, e.getCourseId());

                if (e.getGrade() == null) {
                    pstmt.setNull(3, Types.DECIMAL);
                }else{
                    pstmt.setBigDecimal(3, e.getGrade());
                }

                int rows = pstmt.executeUpdate();
                if (rows > 0 ) {
                    ResultSet keys = pstmt.getGeneratedKeys();
                    if (keys.next()) return keys.getInt(1);
                }

        } catch (SQLException ex) {
                System.err.println("Error adding enrollment: " + ex.getMessage());
        }return -1;
    }

    //READ all, with JOINs
    public  List<Enrollment> getAllEnrollments(){
        List<Enrollment> list = new ArrayList<>();
        String sql = BASE_QUERY + "ORDER BY e.enrolled_at DESC, e.id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()) list.add(mapRow(rs));
        }catch (SQLException e) {
            System.err.println("Error loading enrollments: " + e.getMessage());
        }
        return list;
    }

    //READ (for student)
    public List<Enrollment> getEnrollmentByStudent(int studentId){
        List<Enrollment> list = new ArrayList<>();
        String sql = BASE_QUERY + "WHERE e.student_id = ? ORDER BY c.code";

        try (Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) list.add(mapRow(rs));
        }catch (SQLException e){
            System.err.println("Error loading students enrollments:  " + e.getMessage());
        }
        return list;
    }

    //Search by student name or course code
    public List<Enrollment> searchEnrollments (String keyword) {
        List<Enrollment> list = new ArrayList<>();
        String sql = BASE_QUERY +
                "WHERE s.name LIKE ? OR c.code LIKE ? OR c.name LIKE ? " +
                "ORDER BY e.enrolled_at DESC";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) list.add(mapRow(rs));
        }catch (SQLException e) {
            System.err.println("Error searching enrollments: " +e.getMessage());
        }
        return list;
    }

    //UPDATE grade only
    public boolean updateGrade(int enrollmentId, BigDecimal grade) {
        String sql = "UPDATE enrollments SET grade = ? WHERE id = ? ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            if(grade == null) pstmt.setNull(1, Types.DECIMAL);
            else pstmt.setBigDecimal(1, grade);
            pstmt.setInt(2, enrollmentId);

            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            System.err.println("Error updating grade: " + e.getMessage());
            return false;
        }
    }

    //DELETE

    public boolean deleteEnrollment(int id){
        String sql = "DELETE FROM enrollments WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
           PreparedStatement pstmt  = conn.prepareStatement(sql)){

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e) {
            System.err.println("Error deleting enrollment: " + e.getMessage());
            return false;
        }
    }

    //HELPER
    private Enrollment mapRow(ResultSet rs) throws  SQLException{
        BigDecimal grade = rs.getBigDecimal("grade");
        Date enrolledSql = rs.getDate("enrolled_at");
        LocalDate enrolled = enrolledSql != null ? enrolledSql.toLocalDate() : null;

        return new Enrollment(
                rs.getInt("id"),
                rs.getInt("student_id"), rs.getString("student_name"),
                rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                grade, enrolled
        );
    }

}
