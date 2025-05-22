package dao;

import models.Thread;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThreadDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int createThread(Thread thread) {
        String sql = "INSERT INTO threads (board_id, subject, name, email, comment, image_path, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int id = -1;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, thread.getBoardId());
            pstmt.setString(2, thread.getSubject());
            pstmt.setString(3, thread.getName());
            pstmt.setString(4, thread.getEmail());
            pstmt.setString(5, thread.getComment());
            pstmt.setString(6, thread.getImagePath());
            pstmt.setString(7, LocalDateTime.now().format(FORMATTER)); // Set current timestamp

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear hilo: " + e.getMessage());
        }
        return id;
    }

    public List<Thread> getThreadsByBoardId(int boardId) {
        List<Thread> threads = new ArrayList<>();
        // Ordena por creado más recientemente (los hilos más nuevos aparecen arriba)
        String sql = "SELECT id, board_id, subject, name, email, comment, image_path, created_at FROM threads WHERE board_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    threads.add(new Thread(
                            rs.getInt("id"),
                            rs.getInt("board_id"),
                            rs.getString("subject"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("comment"),
                            rs.getString("image_path"),
                            LocalDateTime.parse(rs.getString("created_at"), FORMATTER)
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener hilos por ID de tablero: " + e.getMessage());
        }
        return threads;
    }

    public Thread getThreadById(int threadId) {
        String sql = "SELECT id, board_id, subject, name, email, comment, image_path, created_at FROM threads WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, threadId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Thread(
                            rs.getInt("id"),
                            rs.getInt("board_id"),
                            rs.getString("subject"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("comment"),
                            rs.getString("image_path"),
                            LocalDateTime.parse(rs.getString("created_at"), FORMATTER)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener hilo por ID: " + e.getMessage());
        }
        return null;
    }
}