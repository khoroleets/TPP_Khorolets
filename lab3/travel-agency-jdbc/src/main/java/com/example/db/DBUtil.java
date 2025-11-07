package com.example.db;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
            Properties p = new Properties();
            if (in == null) throw new RuntimeException("jdbc.properties not found in classpath");
            p.load(in);
            url = p.getProperty("jdbc.url");
            user = p.getProperty("jdbc.user");
            password = p.getProperty("jdbc.password");
            String driver = p.getProperty("jdbc.driver");
            if (driver != null && !driver.isEmpty()) Class.forName(driver);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to load DB config: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) try { r.close(); } catch (Exception ignored) {}
        }
    }
}
