/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Board;
import util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {

    public List<Board> getAllBoards() {
        List<Board> boards = new ArrayList<>();
        String sql = "SELECT id, name, full_name, description FROM boards ORDER BY full_name";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                boards.add(new Board(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("full_name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los tableros: " + e.getMessage());
        }
        return boards;
    }

    public Board getBoardById(int id) {
        String sql = "SELECT id, name, full_name, description FROM boards WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Board(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("full_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tablero por ID: " + e.getMessage());
        }
        return null;
    }

    public Board getBoardByName(String name) {
        String sql = "SELECT id, name, full_name, description FROM boards WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Board(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("full_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tablero por nombre: " + e.getMessage());
        }
        return null;
    }
}