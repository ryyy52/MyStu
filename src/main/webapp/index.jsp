<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- 管理员自动跳转到仪表盘 --%>
<c:if test="${not empty user and user.role == 'admin'}">
    <% response.sendRedirect(request.getContextPath() + "/dashboard"); %>
</c:if>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>电商网站首页</title>
    <style>
        body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2;}
        .header {background-color: #333; color: white; padding: 6px; text-align: center;} /* 调整标题块上下间距 */
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
        
        /* 轮播图样式 */
        .carousel-container {
            position: relative;
            width: 100%;
            max-width: 100%;
            height: 600px; /* 增加高度，以便看清图片全貌 */
            overflow: hidden;
            border-radius: 12px; /* 增大圆角，更现代 */
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15); /* 增强阴影，更有层次感 */
            margin-bottom: 30px;
            background: #000;
        }
        
        .carousel-slide {
            display: flex;
            transition: transform 0.8s cubic-bezier(0.23, 1, 0.32, 1); /* 更平滑的缓动效果 */
            height: 100%;
        }
        
        .carousel-item {
            flex: 0 0 100%;
            height: 100%;
            display: none;
            justify-content: center;
            align-items: center;
            position: relative;
        }
        
        /* 添加渐变遮罩，增强文字可读性 */
        .carousel-item::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.5) 100%);
            z-index: 1;
        }
        
        .carousel-item.active {
            display: flex;
            animation: fadeIn 0.8s ease;
        }
        
        /* 添加淡入动画 */
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        
        .carousel-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center center;
            transition: transform 0.5s cubic-bezier(0.23, 1, 0.32, 1);
        }
        
        /* 图片悬停放大效果 */
        .carousel-container:hover .carousel-image {
            transform: scale(1.03);
        }
        
        /* 轮播图标题容器 */
        .carousel-caption {
            position: absolute;
            bottom: 80px;
            left: 50px;
            z-index: 5;
            color: white;
            text-align: left;
            max-width: 60%;
        }
        
        .carousel-caption h3 {
            font-size: 36px;
            font-weight: bold;
            margin-bottom: 15px;
            text-shadow: 0 2px 4px rgba(0,0,0,0.5);
            animation: slideUp 0.8s ease;
        }
        
        .carousel-caption p {
            font-size: 18px;
            margin-bottom: 20px;
            text-shadow: 0 2px 4px rgba(0,0,0,0.5);
            animation: slideUp 0.8s ease 0.2s both;
        }
        
        .carousel-caption .btn {
            background: rgba(255, 255, 255, 0.9);
            color: #333;
            padding: 12px 24px;
            font-size: 16px;
            border-radius: 50px;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
            animation: slideUp 0.8s ease 0.4s both;
        }
        
        .carousel-caption .btn:hover {
            background: #fff;
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        /* 标题滑动动画 */
        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* 轮播按钮 */
        .carousel-prev,
        .carousel-next {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            background-color: rgba(255, 255, 255, 0.9); /* 白色背景，半透明 */
            color: #333; /* 深色文字 */
            border: none;
            padding: 18px 22px; /* 增大按钮尺寸 */
            font-size: 28px; /* 增大图标 */
            cursor: pointer;
            border-radius: 50%;
            transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1); /* 平滑过渡 */
            z-index: 10;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* 按钮阴影 */
        }
        
        .carousel-prev:hover,
        .carousel-next:hover {
            background-color: #fff;
            transform: translateY(-50%) scale(1.1); /* 悬停放大 */
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2); /* 增强阴影 */
        }
        
        .carousel-prev {
            left: 30px;
        }
        
        .carousel-next {
            right: 30px;
        }
        
        /* 轮播指示器 */
        .carousel-indicators {
            position: absolute;
            bottom: 30px; /* 增加距离底部的距离 */
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            gap: 15px; /* 增大间距 */
            z-index: 10;
        }
        
        .indicator {
            width: 12px;
            height: 12px;
            background-color: rgba(255, 255, 255, 0.6); /* 半透明白色 */
            border-radius: 50%;
            cursor: pointer;
            transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1); /* 平滑过渡 */
            border: 2px solid transparent;
        }
        
        .indicator:hover {
            background-color: rgba(255, 255, 255, 0.9);
            transform: scale(1.2);
        }
        
        .indicator.active {
            background-color: #fff;
            width: 40px; /* 更长的激活指示器 */
            border-radius: 8px;
            transform: scale(1.1);
        }
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
                <c:choose>
                    <c:when test="${not empty user and user.role == 'admin'}">
                        <a href="${pageContext.request.contextPath}/order/list">订单</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/order/list">我的订单</a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/user/profile">个人中心</a>
                <a href="${pageContext.request.contextPath}/user/logout">退出登录</a>
            </div>
        </div>

    <div class="container">
        <!-- 轮播图 -->
        <div class="carousel-container">
            <div class="carousel-slide">
                <div class="carousel-item active">
                    <img src="${pageContext.request.contextPath}/images/p1.jpg" alt=" " class="carousel-image">
                </div>
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p2.jpg" alt=" " class="carousel-image">
                </div>
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p3.jpg" alt=" " class="carousel-image">
                </div>
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p4.jpg" alt=" " class="carousel-image">
                </div>
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p5.jpg" alt=" " class="carousel-image">
                </div>
            </div>
            <button class="carousel-prev" onclick="changeSlide(-1)"><</button>
            <button class="carousel-next" onclick="changeSlide(1)">></button>
            <div class="carousel-indicators">
                <span class="indicator active" onclick="currentSlide(0)"></span>
                <span class="indicator" onclick="currentSlide(1)"></span>
                <span class="indicator" onclick="currentSlide(2)"></span>
                <span class="indicator" onclick="currentSlide(3)"></span>
                <span class="indicator" onclick="currentSlide(4)"></span>
            </div>
        </div>
        
        <div style="overflow: hidden; display:flex; align-items:center; justify-content:space-between; margin-top: 20px;">
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
    
    <script>
        let slideIndex = 0;
        showSlides();
        
        function changeSlide(n) {
            slideIndex += n;
            showSlides();
        }
        
        function currentSlide(n) {
            slideIndex = n;
            showSlides();
        }
        
        function showSlides() {
            const slides = document.getElementsByClassName("carousel-item");
            const indicators = document.getElementsByClassName("indicator");
            
            if (slideIndex >= slides.length) {
                slideIndex = 0;
            }
            if (slideIndex < 0) {
                slideIndex = slides.length - 1;
            }
            
            for (let i = 0; i < slides.length; i++) {
                slides[i].classList.remove("active");
            }
            
            for (let i = 0; i < indicators.length; i++) {
                indicators[i].classList.remove("active");
            }
            
            slides[slideIndex].classList.add("active");
            indicators[slideIndex].classList.add("active");
        }
        
        // 自动轮播
        setInterval(() => {
            changeSlide(1);
        }, 3000);
    </script>
</body>
</html>