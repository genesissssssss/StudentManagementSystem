package com.school.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class DBConnection {

        private static final Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        private static final String URL = getEnv("DB_URL", "jdbc:mysql://localhost:3306/real_school");
        private static final String USER = getEnv("DB_USER", "root");
        private static final String PASSWORD = getEnv("DB_PASSWORD", null);

        private static String getEnv(String key, String defaultValue) {
            String value = dotenv.get(key);
            if (value == null) value = System.getenv(key);
            return value != null ? value : defaultValue;
        }

        public static Connection getConnection() throws SQLException {
            if (PASSWORD == null) {
                throw new IllegalStateException("DB_PASSWORD is not set! Check your .env file.");
            }
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}
