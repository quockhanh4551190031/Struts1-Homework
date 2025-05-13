<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Profile - ${viewedUser.username}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <script>
        function toggleMenu(postId) {
            document.getElementById('menu-' + postId).classList.toggle('active');
        }

        document.addEventListener('click', function(event) {
            var menus = document.querySelectorAll('.action-menu');
            menus.forEach(function(menu) {
                if (!menu.contains(event.target)) {
                    menu.classList.remove('active');
                }
            });
        });

        window.onload = function() {
            var titleInput = document.getElementsByName('title')[0];
            var bodyTextarea = document.getElementsByName('body')[0];
            if (titleInput) titleInput.setAttribute('placeholder', 'Tiêu đề...');
            if (bodyTextarea) bodyTextarea.setAttribute('placeholder', 'Viết gì đó...');
        };

        function confirmDeleteAccount() {
            return confirm('Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác.');
        }
    </script>
</head>
<body>
    <div class="header">
        <div class="header-left">
            <a href="index.do" class="login-link">Trang Chủ</a>
        </div>
        <div class="header-right">
            <a href="profile.do" class="profile-link" style="color: black; text-decoration: none; margin-right: 15px;">
                ${sessionScope.username}
            </a>
            <a href="logout.do" class="logout-link" style="padding: 5px 10px; background: #dc3545; color: white; text-decoration: none; border-radius: 4px;">
                Đăng xuất
            </a>
        </div>
    </div>

    <div class="container1">
        <div class="profile-header">
            <h2>${viewedUser.username}</h2>
            <p>Đang theo dõi: ${followingCount} | Người theo dõi: ${followerCount} | Bài viết: ${postCount}</p>
            <c:if test="${viewedUser.username != sessionScope.username}">
                <c:set var="isFollowing" value="false"/>
                <c:forEach var="followedUser" items="${sessionScope.followedUsers}">
                    <c:if test="${followedUser.id == viewedUser.id}">
                        <c:set var="isFollowing" value="true"/>
                    </c:if>
                </c:forEach>
                <button class="follow-button ${isFollowing ? 'unfollow' : ''}" 
                        onclick="toggleFollow(this, '${viewedUser.username}', '${isFollowing ? 'unfollow' : 'follow'}')">
                    ${isFollowing ? 'Hủy theo dõi' : 'Theo dõi'}
                </button>
            </c:if>
            <c:if test="${viewedUser.username == sessionScope.username}">
                <a href="deleteAccount.do" class="delete-account-button" onclick="return confirmDeleteAccount()">
                    Xóa tài khoản
                </a>
            </c:if>
        </div>

        <c:if test="${viewedUser.username == sessionScope.username}">
            <div class="error-message">
                <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
            </div>
            <div class="form-container">
                <html:form action="/doCreatePost">
                    <html:hidden property="source" value="profile"/>
                    <html:text property="title" value=""/>
                    <html:textarea property="body" value=""/>
                    <html:submit value="Đăng"/>
                </html:form>
            </div>
        </c:if>

        <div class="post-list">
            <c:if test="${empty posts}">
                <p class="no-posts">Không có bài viết nào.</p>
            </c:if>
            <c:if test="${not empty posts}">
                <c:forEach var="post" items="${posts}">
                    <div class="post-card">
                        <div class="post-header">
                            <div class="info">
                                <div class="name">${post.username}</div>
                                <div class="meta">
                                    <fmt:formatDate value="${post.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                                </div>
                            </div>
                        </div>
                        <h3>${post.title}</h3>
                        <p>${post.body}</p>
                        <c:if test="${post.username == sessionScope.username}">
                            <div class="action-menu" id="menu-${post.id}">
                                <button class="menu-button" onclick="toggleMenu(${post.id})">⋮</button>
                                <div class="dropdown">
                                    <a href="editPost.do?id=${post.id}&source=profile">Sửa</a>
                                    <a href="deletePost.do?id=${post.id}&source=profile" class="delete" onclick="return confirm('Bạn có chắc muốn xóa bài viết này?')">Xóa</a>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </c:if>
        </div>
    </div>

    <script>
        function toggleFollow(button, followedUsername, currentAction) {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "follow.do?followedUsername=" + followedUsername + "&action=" + currentAction, true);
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    if (currentAction === "follow") {
                        button.textContent = "Hủy theo dõi";
                        button.classList.add("unfollow");
                        button.onclick = function() {
                            toggleFollow(button, followedUsername, "unfollow");
                        };
                    } else {
                        button.textContent = "Theo dõi";
                        button.classList.remove("unfollow");
                        button.onclick = function() {
                            toggleFollow(button, followedUsername, "follow");
                        };
                    }
                    location.reload(); // Tải lại trang để cập nhật số liệu thống kê
                } else if (xhr.readyState === 4) {
                    alert("Có lỗi xảy ra khi thực hiện hành động!");
                }
            };
            xhr.send();
        }
    </script>
</body>
</html>