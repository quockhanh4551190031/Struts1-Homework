package com.example.socialnetwork.dao;

import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    // Tạo bài viết mới
	public void createPost(String title, String body, int userId) throws Exception {
	    try (Connection conn = DBConnection.getConnection()) {
	        String sql = "INSERT INTO posts (title, body, user_id, status, created_at) VALUES (?, ?, ?, 'published', CURRENT_TIMESTAMP)";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setString(1, title);
	        stmt.setString(2, body);
	        stmt.setInt(3, userId);
	        stmt.executeUpdate();
	    }
	}

    // Lấy danh sách bài viết của user, bao gồm username
    public List<Post> getPostsByUserId(int userId) throws Exception {
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.id, p.title, p.body, p.created_at, u.username " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.id " +
                        "WHERE p.user_id = ? AND p.status = 'published' " +
                        "ORDER BY p.created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setBody(rs.getString("body"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setUsername(rs.getString("username"));
                posts.add(post);
            }
        }
        return posts;
    }

    // Lấy tất cả bài viết (cho hiển thị như Facebook)
    public List<Post> getAllPosts() throws Exception {
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.id, p.title, p.body, p.created_at, u.username " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.id " +
                        "WHERE p.status = 'published' " +
                        "ORDER BY p.created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setBody(rs.getString("body"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setUsername(rs.getString("username"));
                posts.add(post);
            }
        }
        return posts;
    }

    // Lấy bài viết theo ID và user_id
    public Post getPostByIdAndUserId(int postId, int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.id, p.title, p.body, u.username " +
                        "FROM posts p " +
                        "JOIN users u ON p.user_id = u.id " +
                        "WHERE p.id = ? AND p.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setBody(rs.getString("body"));
                post.setUsername(rs.getString("username"));
                return post;
            }
            return null;
        }
    }

    // Cập nhật bài viết
    public boolean updatePost(int postId, String title, String body, int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE posts SET title = ?, body = ? WHERE id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, body);
            stmt.setInt(3, postId);
            stmt.setInt(4, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Xóa bài viết
    public boolean deletePost(int postId, int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM posts WHERE id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Đếm số bài viết của user
    public int getPostCountByUserId(int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM posts WHERE user_id = ? AND status = 'published'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    // Xóa tất cả bài viết của một người dùng dựa trên username
    public void deletePostsByUsername(String username) throws Exception {
        UserDAO userDAO = new UserDAO();
        int userId = userDAO.getUserId(username);
        if (userId == -1) {
            throw new Exception("Không tìm thấy người dùng: " + username);
        }

        String sql = "DELETE FROM posts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // Xóa tất cả bài viết của một người dùng dựa trên username (phiên bản hỗ trợ giao dịch)
    public void deletePostsByUsername(Connection conn, String username) throws Exception {
        UserDAO userDAO = new UserDAO();
        int userId = userDAO.getUserId(conn, username);
        if (userId == -1) {
            throw new Exception("Không tìm thấy người dùng: " + username);
        }

        String sql = "DELETE FROM posts WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

}