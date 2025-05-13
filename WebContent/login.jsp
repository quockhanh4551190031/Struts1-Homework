<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đăng nhập</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <script>
        window.onload = function() {
            document.getElementsByName('username')[0].setAttribute('placeholder', 'Tên đăng nhập...');
            document.getElementsByName('password')[0].setAttribute('placeholder', 'Mật khẩu...');
        };
    </script>
</head>
<body>
    <div class="container1">
        <h2>Đăng nhập</h2>
        <div class="error-message">
            <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
        </div>
        <div class="form-container login">
            <html:form action="/doLogin">
                <html:text property="username" value=""/>
                <html:password property="password" value=""/>
                <html:submit value="Đăng nhập"/>
            </html:form>
            <a href="register.do" class="register-link">Đăng ký tài khoản mới</a>
        </div>
    </div>
</body>
</html>