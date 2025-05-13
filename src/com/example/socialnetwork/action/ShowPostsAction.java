package com.example.socialnetwork.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.example.socialnetwork.dao.FollowDAO;
import com.example.socialnetwork.dao.PostDAO;
import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class ShowPostsAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return mapping.findForward("login");
        }

        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        FollowDAO followDAO = new FollowDAO();

        User currentUser = userDAO.getUserByUsername(username);
        if (currentUser == null) {
            return mapping.findForward("login");
        }

        // Lấy thông báo lỗi từ session nếu có
        String error = (String) session.getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error");
        }

        // Chỉ lấy danh sách bài viết nếu chưa có trong request
        if (request.getAttribute("posts") == null) {
            request.setAttribute("posts", postDAO.getAllPosts());
        }

        // Lấy danh sách tất cả người dùng và loại bỏ người dùng hiện tại
        List<User> allUsers = userDAO.getAllUsers();
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> !user.getUsername().equals(username))
            .collect(Collectors.toList());
        request.setAttribute("users", filteredUsers);

        // Lấy danh sách người dùng mà người dùng hiện tại đang theo dõi
        session.setAttribute("followedUsers", followDAO.getFollowedUsers(currentUser.getId()));

        // Giữ kết quả tìm kiếm nếu có
        if (request.getAttribute("searchResults") != null) {
            request.setAttribute("searchResults", request.getAttribute("searchResults"));
        }

        return mapping.findForward("success");
    }
}