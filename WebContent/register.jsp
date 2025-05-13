<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đăng ký</title>
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
        <h2>Đăng ký</h2>
        <div class="error-message">
            <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
        </div>
        <div class="form-container register">
            <html:form action="/doRegister">
                <html:text property="username" value=""/>
                <html:password property="password" value=""/>
                <html:submit value="Đăng ký"/>
            </html:form>
            <a href="login.do" class="login-link">Quay lại đăng nhập</a>
        </div>
    </div>
</body>
</html>