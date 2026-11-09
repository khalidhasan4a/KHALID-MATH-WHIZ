package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Opens a JDBC connection to the math_whiz_db MySQL database. */
public class DBConnection {

    // >>> Change USER and PASSWORD to your own MySQL credentials <<<
    private static final String URL =
            "jdbc:mysql://localhost:3306/math_whiz_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Reload the Maven project.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
