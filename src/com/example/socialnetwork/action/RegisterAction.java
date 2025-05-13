package com.example.socialnetwork.action;

import com.example.socialnetwork.form.RegistrationForm;
import com.example.socialnetwork.dao.UserDAO;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        RegistrationForm registrationForm = (RegistrationForm) form;
        String username = registrationForm.getUsername();
        String password = registrationForm.getPassword();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
            return mapping.findForward("failure");
        }

        UserDAO userDAO = new UserDAO();
        boolean registered = userDAO.registerUser(username, password);
        if (registered) {
            return mapping.findForward("success");
        } else {
            request.setAttribute("error", "Tên đăng nhập đã tồn tại!");
            return mapping.findForward("failure");
        }
    }
}