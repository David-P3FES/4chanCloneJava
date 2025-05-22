package dao;

import models.Post;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int createPost(Post post) {
        String sql = "INSERT INTO posts (thread_id, name, email, comment, image_path, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        int id = -1;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, post.getThreadId());
            pstmt.setString(2, post.getName());
            pstmt.setString(3, post.getEmail());
            pstmt.setString(4, post.getComment());
            pstmt.setString(5, post.getImagePath());
            pstmt.setString(6, LocalDateTime.now().format(FORMATTER)); // Set current timestamp

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear post: " + e.getMessage());
        }
        return id;
    }

    public List<Post> getPostsByThreadId(int threadId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, thread_id, name, email, comment, image_path, created_at FROM posts WHERE thread_id = ? ORDER BY created_at ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, threadId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(
                            rs.getInt("id"),
                            rs.getInt("thread_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("comment"),
                            rs.getString("image_path"),
                            LocalDateTime.parse(rs.getString("created_at"), FORMATTER)
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener posts por ID de hilo: " + e.getMessage());
        }
        return posts;
    }
}