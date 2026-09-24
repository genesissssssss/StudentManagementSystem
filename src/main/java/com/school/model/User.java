package com.school.model;

public class User {

    public enum  Role{ADMIN, STUDENT}

    private int id;
    private String username;
    private String passwordHash;
    private Role user_role;
    private Integer studentId;

    public User(int id, String username, String passwordHash, Role user_role, Integer studentId){
        this.id =id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.user_role = user_role;
        this.studentId = studentId;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return user_role; }
    public Integer getStudentId() { return studentId; }

    public boolean isAdmin() { return user_role == Role.ADMIN; }
    public boolean isStudent() { return user_role == Role.STUDENT; }

    @Override
    public String toString(){
        return "User{id=" + id + ", username=" + username + "', role" + user_role + "}";
    }

}
