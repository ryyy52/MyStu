<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
    <style>
        body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}
        .container{width:80%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}
        h1{color:#333;margin:0 0 20px}
        table{width:100%;border-collapse:collapse;margin-top:10px}
        th,td{border:1px solid #ddd;padding:10px;text-align:left}
        th{background:#f8f9fa}
        .btn{display:inline-block;padding:6px 12px;background:#3498db;color:#fff;text-decoration:none;border-radius:3px}
        .btn:hover{background:#2980b9}
    </style>
</head>
<body>
    <div class="container">
        <h1>订单详情</h1>
        <c:if test="${empty order}">
            <p>订单不存在。</p>
            <a class="btn" href="${pageContext.request.contextPath}/order/list">返回订单列表</a>
        </c:if>
        <c:if test="${not empty order}">
            <p>订单号：${order.orderNo}</p>
            <p>收货人：${order.receiverName}（${order.receiverPhone}）</p>
            <p>收货地址：${order.receiverAddress}</p>
            <p>订单总价：¥ ${order.totalPrice}</p>
            <table>
                <tr>
                    <th>商品名称</th>
                    <th>单价</th>
                    <th>数量</th>
                    <th>小计</th>
                </tr>
                <c:forEach var="item" items="${order.orderItems}">
                    <tr>
                        <td>${item.product.name}</td>
                        <td>¥ ${item.price}</td>
                        <td>${item.quantity}</td>
                        <td>¥ ${item.price * item.quantity}</td>
                    </tr>
                </c:forEach>
            </table>
            <div style="margin-top: 15px;">
                <a class="btn" href="${pageContext.request.contextPath}/order/list">返回订单列表</a>
            </div>
        </c:if>
    </div>
</body>
</html>