package com.school.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Enrollment {

    private  int id;
    private int studentId;
    private String studentName;
    private int courseId;
    private String courseCode;
    private String courseName;
    private BigDecimal grade;
    private LocalDate enrolledAt;
    private int courseCredits;

    //Constructor for existing rows
    public Enrollment(int id,
                      int studentId, String studentName,
                      int courseId, String courseCode, String courseName, int courseCredits,
                      BigDecimal grade, LocalDate enrolledAt) {

        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseCredits = courseCredits;
        this.grade = grade;
        this.enrolledAt = enrolledAt;
    }

    // Constructor for new enrollment (DB generates/fetches the id and names )
    public Enrollment(int studentId, int courseId, BigDecimal grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
    }

    public int getId() { return  id; }
    public int getStudentId() {return studentId; }
    public String getStudentName() { return studentName; }
    public int getCourseId() {return courseId; }
    public String getCourseCode() {return  courseCode; }
    public String getCourseName() {return courseName; }
    public BigDecimal getGrade() {return grade; }
    public LocalDate getEnrolledAt() {return enrolledAt; }
    public int getCourseCredits() { return courseCredits; }

    public void setGrade(BigDecimal grade) {this.grade = grade;}
}
