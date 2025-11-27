<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>我的购物车</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
        }
        .container {
            width: 80%;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .total {
            text-align: right;
            font-size: 20px;
            font-weight: bold;
            margin-top: 20px;
            color: #e74c3c;
        }
        .btn {
            display: inline-block;
            padding: 8px 15px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            margin-right: 10px;
            border: none;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .btn-danger {
            background-color: #e74c3c;
        }
        .btn-danger:hover {
            background-color: #c0392b;
        }
        .btn-primary {
            background-color: #3498db;
        }
        .btn-primary:hover {
            background-color: #2980b9;
        }
        .empty-cart {
            text-align: center;
            padding: 50px;
            font-size: 18px;
            color: #7f8c8d;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>我的购物车</h1>
        
        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="empty-cart">
                    <p>购物车为空！</p>
                    <a href="${pageContext.request.contextPath}/product/list" class="btn btn-primary">去购物</a>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>商品名称</th>
                        <th>价格</th>
                        <th>数量</th>
                        <th>小计</th>
                        <th>操作</th>
                    </tr>
                    <c:forEach var="item" items="${cartItems}">
                        <tr>
                            <td>${item.productName}</td>
                            <td>¥ ${item.price}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: inline;">
                                    <input type="hidden" name="cartItemId" value="${item.id}">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" style="width: 50px;">
                                    <button type="submit" class="btn" style="padding: 4px 8px;">更新</button>
                                </form>
                            </td>
                            <td>¥ ${item.price * item.quantity}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/cart/remove?cartItemId=${item.id}&productId=${item.productId}" class="btn btn-danger">删除</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                
                <div class="total">
                    总计：¥ ${totalPrice}
                </div>
                
                <div style="margin-top: 20px;">
                    <a href="${pageContext.request.contextPath}/cart/clear" class="btn btn-danger">清空购物车</a>
                    <a href="${pageContext.request.contextPath}/product/list" class="btn">继续购物</a>
                    <form action="${pageContext.request.contextPath}/order/create" method="post" style="display:inline-block; margin-left: 10px;">
                        <input type="text" name="receiverName" placeholder="收货人" required style="padding:6px;">
                        <input type="text" name="receiverPhone" placeholder="联系电话" required style="padding:6px;">
                        <input type="text" name="receiverAddress" placeholder="收货地址" required style="padding:6px; width:280px;">
                        <button type="submit" class="btn btn-primary">结算</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>