<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>我的订单</title>
    <style>
        body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}
        .container{width:80%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}
        h1{color:#333;margin:0 0 20px}
        table{width:100%;border-collapse:collapse}
        th,td{border:1px solid #ddd;padding:10px;text-align:left}
        th{background:#f8f9fa}
        .btn{display:inline-block;padding:6px 12px;background:#3498db;color:#fff;text-decoration:none;border-radius:3px}
        .btn:hover{background:#2980b9}
    </style>
</head>
<body>
    <div class="container">
        <h1>
            <c:choose>
                <c:when test="${not empty user and user.role == 'admin'}">订单</c:when>
                <c:otherwise>我的订单</c:otherwise>
            </c:choose>
        </h1>
        <c:choose>
            <c:when test="${empty orders}">
                <p>暂无订单。</p>
                <a href="${pageContext.request.contextPath}/product/list" class="btn">去逛逛</a>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>订单号</th>
                        <th>总价</th>
                        <th>状态</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>${order.orderNo}</td>
                            <td>¥ ${order.totalPrice}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 0}">待付款</c:when>
                                    <c:when test="${order.status == 1}">待发货</c:when>
                                    <c:when test="${order.status == 2}">已发货</c:when>
                                    <c:when test="${order.status == 3}">已完成</c:when>
                                    <c:when test="${order.status == 4}">已取消</c:when>
                                    <c:otherwise>未知</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${order.createTime}</td>
                            <td>
                                <a class="btn" href="${pageContext.request.contextPath}/order/detail?id=${order.id}">查看详情</a>
                                <c:choose>
                                    <c:when test="${order.status == 0}">
                                        <a class="btn" href="${pageContext.request.contextPath}/order/cancel?id=${order.id}" onclick="return confirm('确定要取消订单吗？')">取消订单</a>
                                        <a class="btn" href="${pageContext.request.contextPath}/order/pay?id=${order.id}">立即支付</a>
                                    </c:when>
                                    <c:when test="${order.status == 1}">
                                        <c:if test="${not empty user and user.role == 'admin'}">
                                            <a class="btn" href="${pageContext.request.contextPath}/order/ship?id=${order.id}" onclick="return confirm('确定要发货吗？')">发货</a>
                                        </c:if>
                                        <c:if test="${empty user or user.role != 'admin'}">
                                            <span style="color: #666; margin-left: 10px;">（待发货，管理员处理中）</span>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${order.status == 2}">
                                        <c:if test="${empty user or user.role != 'admin'}">
                                            <a class="btn" href="${pageContext.request.contextPath}/order/receive?id=${order.id}" onclick="return confirm('确定已收到商品吗？')">确认收货</a>
                                        </c:if>
                                        <c:if test="${not empty user and user.role == 'admin'}">
                                            <span style="color: #666; margin-left: 10px;">（已发货，等待用户确认）</span>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>