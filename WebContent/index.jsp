<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Trang chủ</title>
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
            document.getElementsByName('title')[0].setAttribute('placeholder', 'Tiêu đề...');
            document.getElementsByName('body')[0].setAttribute('placeholder', 'Viết gì đó...');
        };

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
                } else if (xhr.readyState === 4) {
                    alert("Có lỗi xảy ra khi thực hiện hành động!");
                }
            };
            xhr.send();
        }
    </script>
</head>
<body>
    <div class="header">
        <div class="header-left">
            <a href="index.do" class="login-link">Trang Chủ</a>
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

    <div class="container index-container">
        <!-- Cột trái: Danh sách người dùng -->
        <div class="sidebar">
            <h3>Danh sách người dùng</h3>
            <c:if test="${empty users}">
                <p>Không có người dùng nào.</p>
            </c:if>
            <c:if test="${not empty users}">
                <ul>
                    <c:forEach var="user" items="${users}">
                        <li>
                            <a href="profile.do?username=${user.username}">${user.username}</a>
                            <c:set var="isFollowing" value="false"/>
                            <c:forEach var="followedUser" items="${sessionScope.followedUsers}">
                                <c:if test="${followedUser.id == user.id}">
                                    <c:set var="isFollowing" value="true"/>
                                </c:if>
                            </c:forEach>
                            <button class="follow-button ${isFollowing ? 'unfollow' : ''}" 
                                    onclick="toggleFollow(this, '${user.username}', '${isFollowing ? 'unfollow' : 'follow'}')">
                                ${isFollowing ? 'Hủy theo dõi' : 'Theo dõi'}
                            </button>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </div>

        <!-- Cột giữa: Đăng bài và danh sách bài viết -->
        <div class="main-content">
            <div style="text-align: center; margin-bottom: 20px;">
                <h2>Đăng bài</h2>
            </div>

            <div class="error-message">
                <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
            </div>
            <div class="form-container">
                <html:form action="/doCreatePost">
                    <html:hidden property="source" value="index"/>
                    <html:text property="title" value=""/>
                    <html:textarea property="body" value=""/>
                    <html:submit value="Đăng"/>
                </html:form>
            </div>

            <div class="post-list">
                <c:if test="${empty posts}">
                    <p class="no-posts">Không có bài viết nào trong cơ sở dữ liệu.</p>
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
                                        <a href="editPost.do?id=${post.id}&source=index">Sửa</a>
                                        <a href="deletePost.do?id=${post.id}&source=index" class="delete" onclick="return confirm('Bạn có chắc muốn xóa bài viết này?')">Xóa</a>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        </div>

        <!-- Cột phải: Thanh tìm kiếm và Kết quả tìm kiếm -->
        <div class="search-results-column">
            <!-- Thanh tìm kiếm -->
            <form action="searchUsers.do" method="get" class="search-form">
                <input type="number" name="minFollowing" placeholder="Số following tối thiểu" min="0" value="${param.minFollowing}"/>
                <input type="number" name="minFollowers" placeholder="Số follower tối thiểu" min="0" value="${param.minFollowers}"/>
                <input type="submit" value="Tìm kiếm"/>
            </form>

            <!-- Kết quả tìm kiếm -->
            <c:if test="${not empty param.minFollowing || not empty param.minFollowers}">
                <h3>Kết quả tìm kiếm</h3>
                <c:if test="${not empty searchResults}">
                    <ul>
                        <c:forEach var="user" items="${searchResults}">
                            <li>
                                <div>
                                    <a href="profile.do?username=${user.username}">${user.username}</a>
                                    <div>
                                        Following: ${user.followingCount}
                                        <br>
                                        Followers: ${user.followerCount}
                                    </div>
                                </div>
                                <c:set var="isFollowing" value="false"/>
                                <c:forEach var="followedUser" items="${sessionScope.followedUsers}">
                                    <c:if test="${followedUser.id == user.id}">
                                        <c:set var="isFollowing" value="true"/>
                                    </c:if>
                                </c:forEach>
                                <button class="follow-button ${isFollowing ? 'unfollow' : ''}" 
                                        onclick="toggleFollow(this, '${user.username}', '${isFollowing ? 'unfollow' : 'follow'}')">
                                    ${isFollowing ? 'Hủy theo dõi' : 'Theo dõi'}
                                </button>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${empty searchResults}">
                    <img src="${pageContext.request.contextPath}/image/notfound.png" alt="Not Found" class="not-found-img"/>
                </c:if>
            </c:if>
        </div>
    </div>
</body>
</html>