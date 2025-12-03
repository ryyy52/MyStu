<%-- 
    订单详情页面
    功能：展示单个订单的详细信息，包括订单基本信息、收货信息、商品列表和操作按钮
    主要组件：
    1. 页面标题 - 显示"订单详情"
    2. 订单基本信息 - 显示订单号、收货人、收货地址和订单总价
    3. 商品列表 - 显示订单中的商品名称、单价、数量和小计
    4. 操作按钮 - 根据订单状态和用户角色显示不同的操作按钮
    
    依赖：
    - JSTL核心标签库 - 用于条件判断和循环遍历
    - 后端提供的数据：
      - ${order} - 订单详情数据，包括订单基本信息和商品项列表
      - ${user} - 当前登录用户信息
    
    订单状态说明：
    - 0: 待付款
    - 1: 待发货
    - 2: 已发货
    - 3: 已完成
    - 4: 已取消
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="zh-CN">
<head>
    <!-- 页面元数据 -->
    <meta charset="UTF-8">
    <title>订单详情</title>
    <style>
        /* 全局样式 */
        body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}
        /* 容器样式 */
        .container{width:80%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}
        /* 标题样式 */
        h1{color:#333;margin:0 0 20px}
        /* 表格样式 */
        table{width:100%;border-collapse:collapse;margin-top:10px}
        /* 表格单元格样式 */
        th,td{border:1px solid #ddd;padding:10px;text-align:left}
        /* 表格表头样式 */
        th{background:#f8f9fa}
        /* 按钮样式 */
        .btn{display:inline-block;padding:6px 12px;background:#3498db;color:#fff;text-decoration:none;border-radius:3px}
        /* 按钮悬停效果 */
        .btn:hover{background:#2980b9}
    </style>
</head>
<body>
    <!-- 主要内容容器 -->
    <div class="container">
        <!-- 页面标题 -->
        <h1>订单详情</h1>
        
        <!-- 订单不存在的情况处理 -->
        <c:if test="${empty order}">
            <p>订单不存在。</p>
            <a class="btn" href="${pageContext.request.contextPath}/order/list">返回订单列表</a> <!-- 返回订单列表按钮 -->
        </c:if>
        
        <!-- 订单存在的情况处理 -->
        <c:if test="${not empty order}">
            <!-- 订单基本信息 -->
            <p>订单号：${order.orderNo}</p>                    <!-- 订单号 -->
            <p>收货人：${order.receiverName}（${order.receiverPhone}）</p> <!-- 收货人姓名和电话 -->
            <p>收货地址：${order.receiverAddress}</p>        <!-- 收货地址 -->
            <p>订单总价：¥ ${order.totalPrice}</p>           <!-- 订单总价，带货币符号 -->
            
            <!-- 商品列表表格 -->
            <table>
                <!-- 表格表头 -->
                <tr>
                    <th>商品名称</th>    <!-- 商品名称列 -->
                    <th>单价</th>      <!-- 商品单价列 -->
                    <th>数量</th>      <!-- 商品数量列 -->
                    <th>小计</th>      <!-- 商品小计列 -->
                </tr>
                
                <!-- 循环遍历订单商品项，显示每个商品 -->
                <c:forEach var="item" items="${order.orderItems}">
                    <tr>
                        <!-- 商品名称 -->
                        <td>${item.product.name}</td>
                        <!-- 商品单价，带货币符号 -->
                        <td>¥ ${item.price}</td>
                        <!-- 商品数量 -->
                        <td>${item.quantity}</td>
                        <!-- 商品小计，单价乘以数量，带货币符号 -->
                        <td>¥ ${item.price * item.quantity}</td>
                    </tr>
                </c:forEach>
            </table>
            
            <!-- 操作按钮区域 -->
            <div style="margin-top: 15px;">
                <!-- 根据订单状态显示不同的操作按钮 -->
                <c:choose>
                    <!-- 待付款状态 -->
                    <c:when test="${order.status == 0}">
                        <!-- 取消订单按钮，带确认提示 -->
                        <a class="btn" href="${pageContext.request.contextPath}/order/cancel?id=${order.id}" onclick="return confirm('确定要取消订单吗？')">取消订单</a>
                        <!-- 立即支付按钮 -->
                        <a class="btn" href="${pageContext.request.contextPath}/order/pay?id=${order.id}">立即支付</a>
                    </c:when>
                    <!-- 待发货状态 -->
                    <c:when test="${order.status == 1}">
                        <!-- 管理员可以看到发货按钮 -->
                        <c:if test="${not empty user and user.role == 'admin'}">
                            <a class="btn" href="${pageContext.request.contextPath}/order/ship?id=${order.id}" onclick="return confirm('确定要发货吗？')">发货</a>
                        </c:if>
                        <!-- 普通用户看到提示信息 -->
                        <c:if test="${empty user or user.role != 'admin'}">
                            <span style="color: #666; margin-left: 10px;">（待发货，管理员处理中）</span>
                        </c:if>
                    </c:when>
                    <!-- 已发货状态 -->
                    <c:when test="${order.status == 2}">
                        <!-- 普通用户可以看到确认收货按钮 -->
                        <c:if test="${empty user or user.role != 'admin'}">
                            <a class="btn" href="${pageContext.request.contextPath}/order/receive?id=${order.id}" onclick="return confirm('确定已收到商品吗？')">确认收货</a>
                        </c:if>
                        <!-- 管理员看到提示信息 -->
                        <c:if test="${not empty user and user.role == 'admin'}">
                            <span style="color: #666; margin-left: 10px;">（已发货，等待用户确认）</span>
                        </c:if>
                    </c:when>
                    <!-- 其他状态：不显示额外按钮 -->
                    <c:otherwise></c:otherwise>
                </c:choose>
                <!-- 返回订单列表按钮，始终显示 -->
                <a class="btn" href="${pageContext.request.contextPath}/order/list">返回订单列表</a>
            </div>
        </c:if>
    </div>
</body>
</html>