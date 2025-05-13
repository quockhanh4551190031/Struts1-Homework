package com.example.socialnetwork.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.example.socialnetwork.util.DBConnection;
import com.example.socialnetwork.model.User;

public class FollowDAO {
    public void follow(int followerId, int followedId) throws SQLException {
        String sql = "INSERT INTO follows (following_user_id, followed_user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            stmt.executeUpdate();
        }
    }

    public void unfollow(int followerId, int followedId) throws SQLException {
        String sql = "DELETE FROM follows WHERE following_user_id = ? AND followed_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followedId);
            stmt.executeUpdate();
        }
    }

    public List<User> getFollowedUsers(int followerId) throws SQLException {
        List<User> followedUsers = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN follows f ON u.id = f.followed_user_id WHERE f.following_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                followedUsers.add(user);
            }
        }
        return followedUsers;
    }

    public int getFollowingCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM follows WHERE following_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getFollowerCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM follows WHERE followed_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<User> getUsersByFollowCriteria(int minFollowing, int minFollowers) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, " +
                     "COUNT(f1.followed_user_id) AS following_count, " +
                     "COUNT(f2.following_user_id) AS follower_count " +
                     "FROM users u " +
                     "LEFT JOIN follows f1 ON u.id = f1.following_user_id " +
                     "LEFT JOIN follows f2 ON u.id = f2.followed_user_id " +
                     "GROUP BY u.id, u.username " +
                     "HAVING COUNT(f1.followed_user_id) >= ? OR COUNT(f2.following_user_id) >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, minFollowing);
            stmt.setInt(2, minFollowers);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFollowingCount(rs.getInt("following_count"));
                user.setFollowerCount(rs.getInt("follower_count"));
                users.add(user);
            }
        }
        return users;
    }
}