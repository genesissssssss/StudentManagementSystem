🎓 Student Management System

A desktop application for managing students, courses, and enrollments — built with **Java 17**, **JavaFX**, and **MySQL**. Features role-based access for admins and students, hashed password authentication, and a modern styled UI.

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![License](https://img.shields.io/badge/License-MIT-green)

Features

Admin
- Manage students, courses, and enrollments (full CRUD)
- Live search across all tables
- Create/delete user accounts and reset passwords
- Assign grades to enrollments
- Cascade-safe deletes (deleting a student removes their enrollments and login)

Student
- Personal profile view
- Enrolled courses with credits, grades, and pass/fail status
- Auto-computed GWA (General Weighted Average)

Security
- BCrypt password hashing (never stores plain passwords)
- Prepared statements (SQL injection protection)
- Secrets loaded from environment variables — no passwords in source
- Role-based routing after login

Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| GUI | JavaFX 21 |
| Database | MySQL 8 |
| Build | Maven |
| Auth | jBCrypt |
| Config | dotenv-java |




