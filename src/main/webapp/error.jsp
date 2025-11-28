<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>错误页面 - 电子商务系统</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
        }
        .error-container {
            width: 60%;
            margin: 50px auto;
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .error-icon {
            font-size: 80px;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        .error-title {
            font-size: 32px;
            color: #333;
            margin-bottom: 15px;
        }
        .error-message {
            font-size: 18px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .error-details {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 30px;
            text-align: left;
            font-family: monospace;
            font-size: 14px;
        }
        .btn-group {
            display: flex;
            justify-content: center;
            gap: 15px;
        }
        .btn {
            display: inline-block;
            padding: 12px 24px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .btn-secondary {
            background-color: #95a5a6;
        }
        .btn-secondary:hover {
            background-color: #7f8c8d;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1 class="error-title">系统错误</h1>
        
        <c:choose>
            <c:when test="${not empty errorCode}">
                <c:choose>
                    <c:when test="${errorCode == 404}">
                        <h2>页面未找到</h2>
                        <p class="error-message">抱歉，您访问的页面不存在或已被删除。</p>
                    </c:when>
                    <c:when test="${errorCode == 500}">
                        <h2>服务器内部错误</h2>
                        <p class="error-message">服务器遇到了意外情况，无法完成您的请求。</p>
                    </c:when>
                    <c:when test="${errorCode == 403}">
                        <h2>访问被拒绝</h2>
                        <p class="error-message">您没有权限访问此页面，请先登录或联系管理员。</p>
                    </c:when>
                    <c:otherwise>
                        <h2>错误代码：${errorCode}</h2>
                        <p class="error-message">发生了一个意外错误，请稍后再试。</p>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <p class="error-message">${empty errorMessage ? '系统遇到了一个问题，请稍后再试。' : errorMessage}</p>
            </c:otherwise>
        </c:choose>
        
        <c:if test="${not empty errorDetails and showDetails}">
            <div class="error-details">
                <strong>错误详情：</strong><br>
                ${errorDetails}
            </div>
        </c:if>
        
        <div class="btn-group">
            <a href="javascript:history.back()" class="btn">返回上一页</a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">返回首页</a>
            <a href="javascript:location.reload()" class="btn">刷新页面</a>
        </div>
        
        <div style="margin-top: 30px; font-size: 14px; color: #999;">
            如果问题持续存在，请联系技术支持或稍后再试。
        </div>
    </div>
</body>
</html>