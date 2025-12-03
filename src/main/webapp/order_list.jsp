<%-- 
    订单列表页面
    功能：展示用户的订单列表，支持不同用户角色查看不同内容和操作
    主要组件：
    1. 页面标题 - 根据用户角色显示"订单"或"我的订单"
    2. 订单列表表格 - 显示订单的基本信息
    3. 订单状态显示 - 显示订单的当前状态（待付款、待发货等）
    4. 订单操作按钮 - 根据订单状态和用户角色显示不同的操作按钮
    
    依赖：
    - JSTL核心标签库 - 用于条件判断和循环遍历
    - 后端提供的数据：
      - ${orders} - 订单列表数据
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
    <title>我的订单</title>
    <style>
        /* 全局样式 */
        body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}
        /* 容器样式 */
        .container{width:80%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}
        /* 标题样式 */
        h1{color:#333;margin:0 0 20px}
        /* 表格样式 */
        table{width:100%;border-collapse:collapse}
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
        <!-- 页面标题：根据用户角色显示不同标题 -->
        <h1>
            <c:choose>
                <!-- 管理员用户显示"订单" -->
                <c:when test="${not empty user and user.role == 'admin'}">订单</c:when>
                <!-- 普通用户显示"我的订单" -->
                <c:otherwise>我的订单</c:otherwise>
            </c:choose>
        </h1>
        
        <!-- 订单列表展示：根据订单列表是否为空显示不同内容 -->
        <c:choose>
            <!-- 订单列表为空时的显示 -->
            <c:when test="${empty orders}">
                <p>暂无订单。</p>
                <a href="${pageContext.request.contextPath}/product/list" class="btn">去逛逛</a> <!-- 引导用户去商品列表 -->
            </c:when>
            <!-- 订单列表不为空时的显示 -->
            <c:otherwise>
                <!-- 订单列表表格 -->
                <table>
                    <!-- 表格表头 -->
                    <tr>
                        <th>订单号</th>    <!-- 订单号列 -->
                        <th>总价</th>      <!-- 订单总价列 -->
                        <th>状态</th>      <!-- 订单状态列 -->
                        <th>创建时间</th>  <!-- 订单创建时间列 -->
                        <th>操作</th>      <!-- 操作按钮列 -->
                    </tr>
                    
                    <!-- 循环遍历订单列表，显示每个订单 -->
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <!-- 订单号 -->
                            <td>${order.orderNo}</td>
                            <!-- 订单总价，带货币符号 -->
                            <td>¥ ${order.totalPrice}</td>
                            <!-- 订单状态：根据状态值显示不同文本 -->
                            <td>
                                <c:choose>
                                    <c:when test="${order.status == 0}">待付款</c:when>      <!-- 状态0：待付款 -->
                                    <c:when test="${order.status == 1}">待发货</c:when>      <!-- 状态1：待发货 -->
                                    <c:when test="${order.status == 2}">已发货</c:when>      <!-- 状态2：已发货 -->
                                    <c:when test="${order.status == 3}">已完成</c:when>      <!-- 状态3：已完成 -->
                                    <c:when test="${order.status == 4}">已取消</c:when>      <!-- 状态4：已取消 -->
                                    <c:otherwise>未知</c:otherwise>                     <!-- 其他状态：未知 -->
                                </c:choose>
                            </td>
                            <!-- 订单创建时间 -->
                            <td>${order.createTime}</td>
                            <!-- 订单操作按钮：根据订单状态和用户角色显示不同按钮 -->
                            <td>
                                <!-- 查看详情按钮：所有状态都显示 -->
                                <a class="btn" href="${pageContext.request.contextPath}/order/detail?id=${order.id}">查看详情</a>
                                
                                <!-- 根据订单状态显示不同操作按钮 -->
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
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>