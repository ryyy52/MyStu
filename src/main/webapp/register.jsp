<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.ecommerce.utils.CSRFTokenUtils" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户注册</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
        }
        .container {
            width: 300px;
            margin: 100px auto;
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            color: #333;
        }
        input[type="text"], input[type="password"], input[type="email"] {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            box-sizing: border-box;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background-color: #2980b9;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 10px;
            border: 1px solid #f5c6cb;
            border-radius: 3px;
            margin-bottom: 15px;
            text-align: center;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 10px;
            border: 1px solid #c3e6cb;
            border-radius: 3px;
            margin-bottom: 15px;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>用户注册</h1>
        
        <!-- 错误消息显示 -->
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        
        <!-- 成功消息显示 -->
        <c:if test="${not empty successMessage}">
            <div class="success-message">${successMessage}</div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/user/register" method="post">
            <input type="hidden" name="csrfToken" value="<%= CSRFTokenUtils.getCSRFToken(session) %>">
            <input type="text" name="username" placeholder="用户名" required maxlength="20" pattern="[a-zA-Z0-9_]{3,20}" title="用户名必须是3-20位的字母、数字或下划线"><br>
            <input type="password" name="password" placeholder="密码" required maxlength="20" minlength="6"><br>
            <input type="email" name="email" placeholder="邮箱" required maxlength="50"><br>
            <input type="text" name="phone" placeholder="电话" maxlength="11" pattern="1[3-9]\d{9}" title="请输入正确的手机号"><br>
            <input type="text" name="address" placeholder="地址" maxlength="200"><br>
            <input type="submit" value="注册">
        </form>
        <p style="text-align: center; margin-top: 20px;">
            已有账号？<a href="${pageContext.request.contextPath}/login.jsp">立即登录</a>
        </p>
    </div>
</body>
</html>