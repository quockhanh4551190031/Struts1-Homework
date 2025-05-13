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

public class ProfileAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) {
            return mapping.findForward("login");
        }

        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        FollowDAO followDAO = new FollowDAO();

        String viewedUsername = request.getParameter("username");
        if (viewedUsername == null || viewedUsername.isEmpty()) {
            viewedUsername = currentUsername;
        }

        User viewedUser = userDAO.getUserByUsername(viewedUsername);
        if (viewedUser == null) {
            return mapping.findForward("failure");
        }

        // Lấy thông báo lỗi từ session nếu có
        String error = (String) session.getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error"); // Xóa sau khi sử dụng
        }

        // Chỉ lấy danh sách bài viết nếu chưa có trong request
        if (request.getAttribute("posts") == null) {
            request.setAttribute("posts", postDAO.getPostsByUserId(viewedUser.getId()));
        }

        int postCount = postDAO.getPostCountByUserId(viewedUser.getId());
        int followingCount = followDAO.getFollowingCount(viewedUser.getId());
        int followerCount = followDAO.getFollowerCount(viewedUser.getId());

        request.setAttribute("viewedUser", viewedUser);
        request.setAttribute("postCount", postCount);
        request.setAttribute("followingCount", followingCount);
        request.setAttribute("followerCount", followerCount);

        return mapping.findForward("success");
    }
}