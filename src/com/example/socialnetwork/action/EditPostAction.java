package com.example.socialnetwork.action;

import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.dao.PostDAO;
import com.example.socialnetwork.model.Post;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EditPostAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            request.setAttribute("error", "Vui lòng đăng nhập!");
            return mapping.findForward("login");
        }

        String postId = request.getParameter("id");
        String source = request.getParameter("source"); // Lấy tham số source
        if (postId == null || postId.trim().isEmpty()) {
            session.setAttribute("error", "Không tìm thấy bài viết!"); // Sử dụng session để lưu lỗi
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
        Post post = postDAO.getPostByIdAndUserId(Integer.parseInt(postId), userId);
        if (post == null) {
            session.setAttribute("error", "Bài viết không tồn tại hoặc không thuộc về bạn!");
            if ("profile".equals(source)) {
                response.sendRedirect(request.getContextPath() + "/profile.do");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.do");
            }
            return null;
        }

        request.setAttribute("postId", post.getId());
        request.setAttribute("title", post.getTitle());
        request.setAttribute("body", post.getBody());
        request.setAttribute("source", source); // Truyền source cho editPost.jsp
        return mapping.findForward("edit");
    }
}