<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>电商网站首页</title>
    <style>
        body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2;}
        .header {background-color: #333; color: white; padding: 10px; text-align: center;}
        .nav {background-color: #3498db; overflow: hidden;}
        .nav a {float: left; display: block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}
        .nav a:hover {background-color: #2980b9;}
        .nav-right {float: right;}
        .container {padding: 20px;}
        .btn {display: inline-block; padding: 8px 15px; background-color: #3498db; color: white; text-decoration: none; border: none; border-radius: 3px; cursor: pointer;}
        .btn:hover {background-color: #2980b9;}
        .search-form {float: right; margin-top: 10px;}
        .search-form input {padding: 8px; border: 1px solid #ddd; border-radius: 3px;}
        .search-form button {padding: 8px 15px; background-color: #3498db; color: white; border: none; border-radius: 3px; cursor: pointer;}
        .intro {background:#fff; padding:20px; border-radius:6px; box-shadow:0 0 10px rgba(0,0,0,0.08);}
    </style>
</head>
<body>
    <div class="header">
        <h1>电商网站</h1>
    </div>

    <div class="nav">
        <a href="${pageContext.request.contextPath}/index.jsp">首页</a>
        <a href="${pageContext.request.contextPath}/product/list">商品列表</a>
        <c:if test="${not empty user and user.role == 'admin'}">
            <a href="${pageContext.request.contextPath}/category/list">分类管理</a>
        </c:if>
        <div class="nav-right">
            <a href="${pageContext.request.contextPath}/cart/view">购物车</a>
            <a href="${pageContext.request.contextPath}/order/list">我的订单</a>
            <a href="${pageContext.request.contextPath}/user/profile">个人中心</a>
            <a href="${pageContext.request.contextPath}/user/logout">退出登录</a>
        </div>
    </div>

    <div class="container">
        <div style="overflow: hidden; display:flex; align-items:center; justify-content:space-between;">
            <h2>欢迎来到电商网站</h2>
            <div>
                <form class="search-form" action="${pageContext.request.contextPath}/product/search" method="post" style="display:inline-block; margin-right:10px;">
                    <input type="text" name="keyword" placeholder="搜索商品...">
                    <button type="submit">搜索</button>
                </form>
                <c:if test="${not empty user and user.role == 'admin'}">
                    <a href="${pageContext.request.contextPath}/product/add" class="btn">添加商品</a>
                </c:if>
            </div>
        </div>
        <div class="intro">
            <p>从上方导航进入商品列表、购物车、我的订单与个人中心。支持分类筛选、搜索、加购与结算。</p>
            <p>点击“商品列表”进入完整的商品浏览页面。</p>
            <a href="${pageContext.request.contextPath}/product/list" class="btn">进入商品列表</a>
        </div>
    </div>
</body>
</html>