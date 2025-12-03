<%-- 
    电商网站首页
    功能：
    1. 展示网站欢迎信息和导航
    2. 显示轮播图广告
    3. 提供商品搜索功能
    4. 引导用户进入商品列表
    
    特殊逻辑：
    - 管理员用户自动跳转到仪表盘页面
    
    主要组件：
    1. 顶部导航栏 - 根据用户角色显示不同功能入口
    2. 轮播图 - 自动切换的图片展示
    3. 搜索表单 - 用于商品搜索
    4. 欢迎信息区域 - 引导用户使用系统功能
    
    依赖：
    - JSTL核心标签库 - 用于条件判断和导航链接生成
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- 管理员用户自动跳转到仪表盘页面 --%>
<c:if test="${not empty user and user.role == 'admin'}">
    <% response.sendRedirect(request.getContextPath() + "/dashboard"); %>
</c:if>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <!-- 页面元数据 -->
    <meta charset="UTF-8">
    <title>电商网站首页</title>
    <style>
        /* 全局样式 */
        body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2;}
        
        /* 页面头部样式 */
        .header {background-color: #333; color: white; padding: 6px; text-align: center;} /* 调整标题块上下间距 */
        
        /* 导航栏样式 */
        .nav {background-color: #3498db; overflow: hidden;}
        .nav a {float: left; display: block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}
        .nav a:hover {background-color: #2980b9;}
        .nav-right {float: right;}
        
        /* 容器样式 */
        .container {padding: 20px;}
        
        /* 按钮样式 */
        .btn {display: inline-block; padding: 8px 15px; background-color: #3498db; color: white; text-decoration: none; border: none; border-radius: 3px; cursor: pointer;}
        .btn:hover {background-color: #2980b9;}
        
        /* 搜索表单样式 */
        .search-form {float: right; margin-top: 10px;}
        .search-form input {padding: 8px; border: 1px solid #ddd; border-radius: 3px;}
        .search-form button {padding: 8px 15px; background-color: #3498db; color: white; border: none; border-radius: 3px; cursor: pointer;}
        
        /* 欢迎信息区域样式 */
        .intro {background:#fff; padding:20px; border-radius:6px; box-shadow:0 0 10px rgba(0,0,0,0.08);}
        
        /* ======================================== */
        /* 轮播图样式 */
        /* ======================================== */
        .carousel-container {
            position: relative;         /* 相对定位，作为轮播项的定位容器 */
            width: 100%;               /* 宽度100%，响应式 */
            max-width: 100%;           /* 最大宽度100% */
            height: 600px;             /* 轮播图高度 */
            overflow: hidden;          /* 隐藏超出容器的内容 */
            border-radius: 12px;       /* 圆角边框 */
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15); /* 阴影效果 */
            margin-bottom: 30px;       /* 底部外边距 */
            background: #000;          /* 背景色，防止图片加载慢时显示白色 */
        }
        
        /* 轮播图滑动容器 */
        .carousel-slide {
            display: flex;             /* 使用flex布局 */
            transition: transform 0.8s cubic-bezier(0.23, 1, 0.32, 1); /* 平滑的过渡动画 */
            height: 100%;             /* 高度100%，与容器一致 */
        }
        
        /* 单个轮播项 */
        .carousel-item {
            flex: 0 0 100%;           /* 每个轮播项占满容器宽度 */
            height: 100%;             /* 高度100%，与容器一致 */
            display: none;            /* 默认隐藏 */
            justify-content: center;  /* 内容居中 */
            align-items: center;      /* 内容居中 */
            position: relative;       /* 相对定位，作为图片和文字的定位容器 */
        }
        
        /* 轮播项渐变遮罩，增强文字可读性 */
        .carousel-item::before {
            content: '';              /* 伪元素，用于渐变遮罩 */
            position: absolute;       /* 绝对定位，覆盖整个轮播项 */
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.5) 100%); /* 从上到下的渐变遮罩 */
            z-index: 1;              /* 层级1，位于图片之上，文字之下 */
        }
        
        /* 激活状态的轮播项 */
        .carousel-item.active {
            display: flex;            /* 显示激活的轮播项 */
            animation: fadeIn 0.8s ease; /* 淡入动画 */
        }
        
        /* 淡入动画 */
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        
        /* 轮播图片样式 */
        .carousel-image {
            width: 100%;              /* 宽度100%，自适应容器 */
            height: 100%;             /* 高度100%，自适应容器 */
            object-fit: cover;        /* 保持图片比例，覆盖整个容器 */
            object-position: center center; /* 图片居中显示 */
            transition: transform 0.5s cubic-bezier(0.23, 1, 0.32, 1); /* 悬停放大动画 */
        }
        
        /* 轮播图片悬停放大效果 */
        .carousel-container:hover .carousel-image {
            transform: scale(1.03);   /* 图片放大1.03倍 */
        }
        
        /* 轮播图标题容器 */
        .carousel-caption {
            position: absolute;       /* 绝对定位，位于轮播项底部 */
            bottom: 80px;           /* 距离底部80px */
            left: 50px;            /* 距离左侧50px */
            z-index: 5;             /* 层级5，位于遮罩之上 */
            color: white;           /* 文字颜色白色 */
            text-align: left;       /* 文字左对齐 */
            max-width: 60%;         /* 最大宽度60% */
        }
        
        /* 轮播图标题样式 */
        .carousel-caption h3 {
            font-size: 36px;         /* 字体大小36px */
            font-weight: bold;       /* 加粗 */
            margin-bottom: 15px;     /* 底部外边距15px */
            text-shadow: 0 2px 4px rgba(0,0,0,0.5); /* 文字阴影 */
            animation: slideUp 0.8s ease; /* 滑动动画 */
        }
        
        /* 轮播图描述文字样式 */
        .carousel-caption p {
            font-size: 18px;         /* 字体大小18px */
            margin-bottom: 20px;     /* 底部外边距20px */
            text-shadow: 0 2px 4px rgba(0,0,0,0.5); /* 文字阴影 */
            animation: slideUp 0.8s ease 0.2s both; /* 延迟0.2秒的滑动动画 */
        }
        
        /* 轮播图按钮样式 */
        .carousel-caption .btn {
            background: rgba(255, 255, 255, 0.9); /* 半透明白色背景 */
            color: #333;             /* 文字颜色深色 */
            padding: 12px 24px;     /* 内边距 */
            font-size: 16px;         /* 字体大小16px */
            border-radius: 50px;     /* 圆角50px，胶囊形状 */
            text-decoration: none;   /* 无下划线 */
            display: inline-block;   /* 行内块元素 */
            transition: all 0.3s ease; /* 所有属性过渡效果 */
            animation: slideUp 0.8s ease 0.4s both; /* 延迟0.4秒的滑动动画 */
        }
        
        /* 轮播图按钮悬停效果 */
        .carousel-caption .btn:hover {
            background: #fff;        /* 白色背景 */
            transform: translateY(-2px); /* 向上移动2px */
            box-shadow: 0 4px 15px rgba(0,0,0,0.2); /* 阴影效果 */
        }
        
        /* 标题滑动动画 */
        @keyframes slideUp {
            from {
                opacity: 0;          /* 初始透明度0 */
                transform: translateY(30px); /* 初始位置向下30px */
            }
            to {
                opacity: 1;          /* 结束透明度1 */
                transform: translateY(0); /* 结束位置0 */
            }
        }
        
        /* 轮播控制按钮 */
        .carousel-prev,
        .carousel-next {
            position: absolute;       /* 绝对定位，位于轮播图两侧 */
            top: 50%;               /* 垂直居中 */
            transform: translateY(-50%); /* 垂直居中调整 */
            background-color: rgba(255, 255, 255, 0.9); /* 半透明白色背景 */
            color: #333;             /* 文字颜色深色 */
            border: none;            /* 无边框 */
            padding: 18px 22px;     /* 内边距，增大按钮尺寸 */
            font-size: 28px;         /* 字体大小28px，增大图标 */
            cursor: pointer;         /* 鼠标指针为手型 */
            border-radius: 50%;      /* 圆角50%，圆形按钮 */
            transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1); /* 平滑过渡效果 */
            z-index: 10;             /* 层级10，最上层 */
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* 按钮阴影 */
        }
        
        /* 轮播控制按钮悬停效果 */
        .carousel-prev:hover,
        .carousel-next:hover {
            background-color: #fff;        /* 白色背景 */
            transform: translateY(-50%) scale(1.1); /* 放大1.1倍 */
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2); /* 增强阴影 */
        }
        
        /* 上一张按钮位置 */
        .carousel-prev {
            left: 30px;              /* 距离左侧30px */
        }
        
        /* 下一张按钮位置 */
        .carousel-next {
            right: 30px;             /* 距离右侧30px */
        }
        
        /* 轮播指示器容器 */
        .carousel-indicators {
            position: absolute;       /* 绝对定位，位于轮播图底部 */
            bottom: 30px;            /* 距离底部30px */
            left: 50%;              /* 水平居中 */
            transform: translateX(-50%); /* 水平居中调整 */
            display: flex;           /* flex布局 */
            gap: 15px;               /* 指示器之间的间距 */
            z-index: 10;             /* 层级10，上层 */
        }
        
        /* 单个轮播指示器 */
        .indicator {
            width: 12px;             /* 指示器宽度 */
            height: 12px;            /* 指示器高度 */
            background-color: rgba(255, 255, 255, 0.6); /* 半透明白色背景 */
            border-radius: 50%;      /* 圆角50%，圆形指示器 */
            cursor: pointer;         /* 鼠标指针为手型 */
            transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1); /* 平滑过渡 */
            border: 2px solid transparent; /* 透明边框 */
        }
        
        /* 轮播指示器悬停效果 */
        .indicator:hover {
            background-color: rgba(255, 255, 255, 0.9); /* 更不透明的白色背景 */
            transform: scale(1.2);   /* 放大1.2倍 */
        }
        
        /* 激活状态的轮播指示器 */
        .indicator.active {
            background-color: #fff;   /* 白色背景 */
            width: 40px;             /* 宽度增大，变为长方形 */
            border-radius: 8px;      /* 圆角8px，胶囊形状 */
            transform: scale(1.1);   /* 放大1.1倍 */
        }
    </style>
</head>
<body>
    <!-- 页面头部 -->
    <div class="header">
        <h1>电商网站</h1>
    </div>

    <!-- 导航栏 -->
    <div class="nav">
            <a href="${pageContext.request.contextPath}/index.jsp">首页</a> <!-- 首页链接 -->
            <a href="${pageContext.request.contextPath}/product/list">商品列表</a> <!-- 商品列表链接 -->
            <!-- 只有管理员可以看到分类管理链接 -->
            <c:if test="${not empty user and user.role == 'admin'}">
                <a href="${pageContext.request.contextPath}/category/list">分类管理</a>
            </c:if>
            <div class="nav-right">
                <a href="${pageContext.request.contextPath}/cart/view">购物车</a> <!-- 购物车链接 -->
                <!-- 根据用户角色显示不同的订单链接 -->
                <c:choose>
                    <!-- 管理员显示"订单"链接 -->
                    <c:when test="${not empty user and user.role == 'admin'}">
                        <a href="${pageContext.request.contextPath}/order/list">订单</a>
                    </c:when>
                    <!-- 普通用户显示"我的订单"链接 -->
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/order/list">我的订单</a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/user/profile">个人中心</a> <!-- 个人中心链接 -->
                <a href="${pageContext.request.contextPath}/user/logout">退出登录</a> <!-- 退出登录链接 -->
            </div>
        </div>

    <!-- 主要内容区域 -->
    <div class="container">
        <!-- 轮播图组件 -->
        <div class="carousel-container">
            <div class="carousel-slide">
                <!-- 轮播项1 -->
                <div class="carousel-item active">
                    <img src="${pageContext.request.contextPath}/images/p1.jpg" alt="轮播图1" class="carousel-image">
                </div>
                <!-- 轮播项2 -->
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p2.jpg" alt="轮播图2" class="carousel-image">
                </div>
                <!-- 轮播项3 -->
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p3.jpg" alt="轮播图3" class="carousel-image">
                </div>
                <!-- 轮播项4 -->
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p4.jpg" alt="轮播图4" class="carousel-image">
                </div>
                <!-- 轮播项5 -->
                <div class="carousel-item">
                    <img src="${pageContext.request.contextPath}/images/p5.jpg" alt="轮播图5" class="carousel-image">
                </div>
            </div>
            <!-- 轮播控制按钮：上一张 -->
            <button class="carousel-prev" onclick="changeSlide(-1)"><</button>
            <!-- 轮播控制按钮：下一张 -->
            <button class="carousel-next" onclick="changeSlide(1)">></button>
            <!-- 轮播指示器 -->
            <div class="carousel-indicators">
                <span class="indicator active" onclick="currentSlide(0)"></span> <!-- 指示器1 -->
                <span class="indicator" onclick="currentSlide(1)"></span> <!-- 指示器2 -->
                <span class="indicator" onclick="currentSlide(2)"></span> <!-- 指示器3 -->
                <span class="indicator" onclick="currentSlide(3)"></span> <!-- 指示器4 -->
                <span class="indicator" onclick="currentSlide(4)"></span> <!-- 指示器5 -->
            </div>
        </div>
        
        <!-- 页面标题和搜索区域 -->
        <div style="overflow: hidden; display:flex; align-items:center; justify-content:space-between; margin-top: 20px;">
            <h2>欢迎来到电商网站</h2> <!-- 页面标题 -->
            <div>
                <!-- 商品搜索表单 -->
                <form class="search-form" action="${pageContext.request.contextPath}/product/search" method="post" style="display:inline-block; margin-right:10px;">
                    <input type="text" name="keyword" placeholder="搜索商品..."> <!-- 搜索输入框 -->
                    <button type="submit">搜索</button> <!-- 搜索按钮 -->
                </form>
                <!-- 只有管理员可以看到添加商品按钮 -->
                <c:if test="${not empty user and user.role == 'admin'}">
                    <a href="${pageContext.request.contextPath}/product/add" class="btn">添加商品</a>
                </c:if>
            </div>
        </div>
        
        <!-- 欢迎信息区域 -->
        <div class="intro">
            <p>从上方导航进入商品列表、购物车、我的订单与个人中心。支持分类筛选、搜索、加购与结算。</p>
            <p>点击“商品列表”进入完整的商品浏览页面。</p>
            <a href="${pageContext.request.contextPath}/product/list" class="btn">进入商品列表</a> <!-- 进入商品列表按钮 -->
        </div>
    </div>
    
    <!-- 轮播图控制脚本 -->
    <script>
        // 轮播图索引，初始为0
        let slideIndex = 0;
        // 页面加载时调用showSlides()函数，显示初始轮播图
        showSlides();
        
        /**
         * 切换轮播图函数
         * @param {number} n - 切换数量，-1表示上一张，1表示下一张
         */
        function changeSlide(n) {
            slideIndex += n;  // 更新轮播图索引
            showSlides();     // 显示当前索引的轮播图
        }
        
        /**
         * 跳转到指定轮播图函数
         * @param {number} n - 指定的轮播图索引
         */
        function currentSlide(n) {
            slideIndex = n;   // 直接设置轮播图索引
            showSlides();     // 显示指定索引的轮播图
        }
        
        /**
         * 显示轮播图函数
         * 负责切换轮播图的显示状态和指示器的激活状态
         */
        function showSlides() {
            // 获取所有轮播项
            const slides = document.getElementsByClassName("carousel-item");
            // 获取所有指示器
            const indicators = document.getElementsByClassName("indicator");
            
            // 处理轮播图索引越界情况
            if (slideIndex >= slides.length) {
                slideIndex = 0;  // 如果索引超出最大值，重置为0
            }
            if (slideIndex < 0) {
                slideIndex = slides.length - 1;  // 如果索引小于0，重置为最大值
            }
            
            // 隐藏所有轮播项
            for (let i = 0; i < slides.length; i++) {
                slides[i].classList.remove("active");
            }
            
            // 取消所有指示器的激活状态
            for (let i = 0; i < indicators.length; i++) {
                indicators[i].classList.remove("active");
            }
            
            // 显示当前索引的轮播项
            slides[slideIndex].classList.add("active");
            // 激活当前索引的指示器
            indicators[slideIndex].classList.add("active");
        }
        
        // 设置自动轮播，每3秒自动切换到下一张
        setInterval(() => {
            changeSlide(1);  // 调用changeSlide函数，参数为1，表示下一张
        }, 3000);
    </script>
</body>
</html>