package com.example.socialnetwork.action;

import com.example.socialnetwork.dao.FollowDAO;
import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.model.User;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class SearchAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return mapping.findForward("login");
        }

        // Lấy tham số từ form tìm kiếm
        String minFollowingStr = request.getParameter("minFollowing");
        String minFollowersStr = request.getParameter("minFollowers");

        // Mặc định là 0 nếu không nhập
        int minFollowing = 0;
        int minFollowers = 0;

        try {
            if (minFollowingStr != null && !minFollowingStr.trim().isEmpty()) {
                minFollowing = Integer.parseInt(minFollowingStr);
            }
            if (minFollowersStr != null && !minFollowersStr.trim().isEmpty()) {
                minFollowers = Integer.parseInt(minFollowersStr);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Vui lòng nhập số hợp lệ!");
            return mapping.findForward("success");
        }

        // Tìm kiếm người dùng
        UserDAO userDAO = new UserDAO();
        FollowDAO followDAO = new FollowDAO();
        List<User> allUsers = userDAO.getAllUsers();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : allUsers) {
            int followingCount = followDAO.getFollowingCount(user.getId());
            int followerCount = followDAO.getFollowerCount(user.getId());

            // Điều kiện: following >= minFollowing HOẶC followers >= minFollowers
            if (followingCount >= minFollowing || followerCount >= minFollowers) {
                user.setFollowingCount(followingCount); // Lưu số following để hiển thị
                user.setFollowerCount(followerCount);   // Lưu số follower để hiển thị
                filteredUsers.add(user);
            }
        }

        // Lưu kết quả tìm kiếm vào request
        request.setAttribute("searchResults", filteredUsers);
        return mapping.findForward("success");
    }
}