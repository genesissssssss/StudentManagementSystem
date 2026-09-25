package com.school.model;
import java.time.LocalDate;
public class Student {


    private int id;
    private String name;
    private  String email;
    private String phone;
    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;

    // Constructor for new students
    public Student(String name, String email, String phone, LocalDate dateOfBirth, LocalDate enrollmentDate){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.enrollmentDate = enrollmentDate;
    }

    // Constructor for existing students (with id)
    public Student(int id, String name, String email, String phone,
                   LocalDate dateOfBirth, LocalDate enrollmentDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.enrollmentDate = enrollmentDate;
    }
    //getters
    public int getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public LocalDate getDateOfBirth() {return dateOfBirth; }
    public LocalDate getEnrollmentDate() {return enrollmentDate;}
    public String getPhone() { return phone; }


    //setters for editing dialog to modify fields

    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {this.phone = phone;}
    public  void setDateOfBirth(LocalDate dateOfBirth) {this.dateOfBirth = dateOfBirth;}
    public  void setEnrollmentDate(LocalDate enrollmentDate) {this.enrollmentDate = enrollmentDate;}



}
