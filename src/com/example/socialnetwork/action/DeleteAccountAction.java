package com.example.socialnetwork.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.dao.PostDAO;
import com.example.socialnetwork.util.DBConnection;
import java.sql.Connection;

public class DeleteAccountAction extends Action {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // Kiểm tra nếu người dùng chưa đăng nhập
        if (username == null) {
            return mapping.findForward("login");
        }

        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();
        Connection conn = null;
        try {
            // Bắt đầu giao dịch
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Lấy userId từ username
            int userId = userDAO.getUserId(conn, username);
            if (userId == -1) {
                request.setAttribute("error", "Không tìm thấy người dùng: " + username);
                conn.rollback();
                return mapping.findForward(ERROR);
            }

            // Xóa tất cả bài viết của người dùng
            postDAO.deletePostsByUsername(conn, username);

            // Xóa tất cả quan hệ theo dõi (following và followers)
            userDAO.deleteAllFollows(conn, userId);

            // Xóa tài khoản người dùng
            userDAO.deleteUser(conn, username);

            // Xác nhận giao dịch
            conn.commit();

            // Đăng xuất: Xóa session
            session.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                conn.rollback();
            }
            request.setAttribute("error", "Có lỗi xảy ra khi xóa tài khoản: " + e.getMessage());
            return mapping.findForward(ERROR);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

        return mapping.findForward(SUCCESS);
    }
}