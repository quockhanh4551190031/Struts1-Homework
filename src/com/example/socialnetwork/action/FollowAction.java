package com.example.socialnetwork.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.example.socialnetwork.dao.FollowDAO;
import com.example.socialnetwork.dao.UserDAO;
import com.example.socialnetwork.model.User;

public class FollowAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) {
            return mapping.findForward("login");
        }

        String followedUsername = request.getParameter("followedUsername");
        String action = request.getParameter("action");

        UserDAO userDAO = new UserDAO();
        FollowDAO followDAO = new FollowDAO();

        User currentUser = userDAO.getUserByUsername(currentUsername);
        User followedUser = userDAO.getUserByUsername(followedUsername);

        if (currentUser == null || followedUser == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        // Ngăn người dùng tự theo dõi chính mình
        if (currentUser.getId() == followedUser.getId()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        if ("follow".equals(action)) {
            followDAO.follow(currentUser.getId(), followedUser.getId());
        } else if ("unfollow".equals(action)) {
            followDAO.unfollow(currentUser.getId(), followedUser.getId());
        }

        session.setAttribute("followedUsers", followDAO.getFollowedUsers(currentUser.getId()));
        response.setStatus(HttpServletResponse.SC_OK);
        return null;
    }
}