package util;

import java.io.BufferedReader;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:persona3_board.db"; // Database file name

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Read SQL script from resources
            InputStream is = DatabaseManager.class.getResourceAsStream("/sql/boards.sql");
            if (is == null) {
                System.err.println("Error: boards.sql no encontrado en src/sql/");
                return;
            }
            String sqlScript = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            // Execute SQL statements
            // Split script by semicolon to execute individual statements
            String[] statements = sqlScript.split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement.trim());
                }
            }
            System.out.println("Base de datos inicializada o verificada.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        } catch (UncheckedIOException e) {
            System.err.println("Error al leer el script SQL: " + e.getMessage());
        }
    }

    // Call this method once when the application starts
    public static void main(String[] args) {
        initializeDatabase();
    }
}