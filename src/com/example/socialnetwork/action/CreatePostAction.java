package com.example.socialnetwork.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.example.socialnetwork.dao.PostDAO;
import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.form.PostForm;
import com.example.socialnetwork.model.User;

public class CreatePostAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return null;
        }

        UserDAO userDAO = new UserDAO();
        User currentUser = userDAO.getUserByUsername(username);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return null;
        }

        PostForm postForm = (PostForm) form;
        String title = postForm.getTitle();
        String body = postForm.getBody();
        String source = postForm.getSource();

        // Kiểm tra dữ liệu đầu vào
        PostDAO postDAO = new PostDAO();
        if (title == null || title.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            session.setAttribute("error", "Tiêu đề và nội dung không được để trống!");
            if ("profile".equals(source)) {
                response.sendRedirect(request.getContextPath() + "/profile.do");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.do");
            }
            return null;
        }

        // Tạo bài viết
        try {
            postDAO.createPost(title, body, currentUser.getId());
        } catch (Exception e) {
            session.setAttribute("error", "Có lỗi xảy ra khi đăng bài: " + e.getMessage());
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