package com.example.socialnetwork.action;

import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.dao.PostDAO;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeletePostAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return null;
        }

        String postId = request.getParameter("id");
        String source = request.getParameter("source"); // Lấy tham số source
        if (postId == null || postId.trim().isEmpty()) {
            session.setAttribute("error", "Không tìm thấy bài viết!");
            if ("profile".equals(source)) {
                response.sendRedirect(request.getContextPath() + "/profile.do");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.do");
            }
            return null;
        }

        UserDAO userDAO = new UserDAO();
        int userId = userDAO.getUserId(username);
        if (userId == -1) {
            session.setAttribute("error", "Người dùng không tồn tại!");
            if ("profile".equals(source)) {
                response.sendRedirect(request.getContextPath() + "/profile.do");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.do");
            }
            return null;
        }

        PostDAO postDAO = new PostDAO();
        boolean deleted = postDAO.deletePost(Integer.parseInt(postId), userId);
        if (!deleted) {
            session.setAttribute("error", "Không thể xóa bài viết!");
            if ("profile".equals(source)) {
                response.sendRedirect(request.getContextPath() + "/profile.do");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.do");
            }
            return null;
        }

        // Redirect tùy theo nguồn gốc
        if ("profile".equals(source)) {
            response.sendRedirect(request.getContextPath() + "/profile.do");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.do");
        }
        return null;
    }
}