<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sửa bài viết</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <script>
        window.onload = function() {
            document.getElementsByName('title')[0].setAttribute('placeholder', 'Tiêu đề...');
            document.getElementsByName('body')[0].setAttribute('placeholder', 'Nội dung...');
        };
    </script>
</head>
<body>
    <div class="header">
        <div class="header-left">
            <a href="index.do" class="login-link">Trang chủ</a>
        </div>
        <div class="header-right">
            <a href="profile.do" class="profile-link username-link" style="color: black; text-decoration: none; margin-right: 15px;">
                ${sessionScope.username}
            </a>
            <a href="logout.do" class="logout-link" style="padding: 5px 10px; background: #dc3545; color: white; text-decoration: none; border-radius: 4px;">
                Đăng xuất
            </a>
        </div>
    </div>

    <div class="container1">
        <h2>Sửa bài viết</h2>
        <div class="error-message">
            <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
        </div>
        <div class="form-container">
            <html:form action="/updatePost">
                <html:hidden property="id" value="${postId}"/>
                <html:hidden property="source" value="${requestScope.source}"/> <!-- Truyền source -->
                <html:text property="title" value="${title}"/>
                <html:textarea property="body" value="${body}"/>
                <html:submit value="Cập nhật"/>
            </html:form>
            <a href="${requestScope.source == 'profile' ? 'profile.do' : 'index.do'}" class="back-link">Quay lại</a>
        </div>
    </div>
</body>
</html>