package com.example.socialnetwork.dao;

import com.example.socialnetwork.util.DBConnection;
import com.example.socialnetwork.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // Đăng ký người dùng mới
    public boolean registerUser(String username, String password) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Username đã tồn tại
            }
            String sql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, 'user', CURRENT_TIMESTAMP)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        }
    }

    // Lấy user_id từ username
    public int getUserId(String username) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    // Lấy user_id từ username (phiên bản hỗ trợ giao dịch)
    public int getUserId(Connection conn, String username) throws Exception {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        }
    }

    // Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        }
        return users;
    }

    // Kiểm tra xem user có đang theo dõi user khác không
    public boolean isFollowing(int followingUserId, int followedUserId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM follows WHERE following_user_id = ? AND followed_user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, followingUserId);
            stmt.setInt(2, followedUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    // Follow một người dùng
    public boolean followUser(int followingUserId, int followedUserId) throws Exception {
        if (followingUserId == followedUserId) return false; // Không cho phép tự follow
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO follows (following_user_id, followed_user_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, followingUserId);
            stmt.setInt(2, followedUserId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Unfollow một người dùng
    public boolean unfollowUser(int followingUserId, int followedUserId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM follows WHERE following_user_id = ? AND followed_user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, followingUserId);
            stmt.setInt(2, followedUserId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Đếm số lượng người đang theo dõi (followers)
    public int countFollowers(int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM follows WHERE followed_user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    // Đếm số lượng người mà user đang theo dõi (following)
    public int countFollowing(int userId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM follows WHERE following_user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public User getUserByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                return user;
            }
        }
        return null;
    }

    public List<User> searchUsersByFollowingAndFollower(int minFollowing, int minFollower) {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.id, u.username " +
                      "FROM users u " +
                      "LEFT JOIN (SELECT following_user_id, COUNT(*) as following_count " +
                      "           FROM follows GROUP BY following_user_id) f1 " +
                      "ON u.id = f1.following_user_id " +
                      "LEFT JOIN (SELECT followed_user_id, COUNT(*) as follower_count " +
                      "           FROM follows GROUP BY followed_user_id) f2 " +
                      "ON u.id = f2.followed_user_id " +
                      "WHERE (f1.following_count >= ? OR f1.following_count IS NULL) " +
                      "   OR (f2.follower_count >= ? OR f2.follower_count IS NULL)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, minFollowing);
            stmt.setInt(2, minFollower);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> searchUsersByUsername(String keyword) {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username FROM users WHERE username LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Xóa người dùng dựa trên username
    public void deleteUser(String username) throws Exception {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    // Xóa người dùng dựa trên username (phiên bản hỗ trợ giao dịch)
    public void deleteUser(Connection conn, String username) throws Exception {
        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    // Xóa tất cả quan hệ theo dõi của người dùng
    public void deleteAllFollows(int userId) throws Exception {
        String sql = "DELETE FROM follows WHERE following_user_id = ? OR followed_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // Xóa tất cả quan hệ theo dõi của người dùng (phiên bản hỗ trợ giao dịch)
    public void deleteAllFollows(Connection conn, int userId) throws Exception {
        String sql = "DELETE FROM follows WHERE following_user_id = ? OR followed_user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}