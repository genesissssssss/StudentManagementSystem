package com.school.model;

public class Course {

    private int id;
    private String code;
    private String name;
    private String instructor;
    private int credits;

    //for new courses
    public Course(String code, String name, String instructors, int credits){
        this.code = code;
        this.code = name;
        this.instructor = instructors;
        this.credits = credits;
    }
    //for existing courses
    public Course(int id, String code, String name, String instructors, int credits){
        this.id = id;
        this.code = code;
        this.name = name;
        this.instructor = instructors;
        this.credits = credits;
    }
    public int getId() {return  id; }
    public String getCode() {return code;}
    public String getName() {return name;}
    public String getInstructor() {return instructor;}
    public int getCredits() {return credits;}

    public void setCode(String code) {this.code = code; }
    public void setName(String name) {this.name = name; }
    public void setInstructors(String instructors) {this.instructor = instructors;}
    public void setCredits(int credits) {this.credits = credits;}
}
