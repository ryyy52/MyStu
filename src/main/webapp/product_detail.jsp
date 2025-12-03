<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品详情</title>
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
        .product-detail {
            display: flex;
            gap: 30px;
            margin-top: 20px;
        }
        .product-image {
            width: 400px;
            background-color: #f9f9f9;
            border-radius: 5px;
            display: block;
            text-align: center;
            padding: 10px;
            overflow: visible;
        }
        .product-image img {
            width: auto;
            height: auto;
            max-width: 100%;
            max-height: none;
            display: inline-block;
            vertical-align: middle;
        }
        .product-info {
            flex: 1;
        }
        .product-name {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .product-price {
            color: #e74c3c;
            font-size: 32px;
            margin-bottom: 20px;
        }
        .product-description {
            line-height: 1.6;
            margin-bottom: 20px;
        }
        .product-stock {
            margin-bottom: 20px;
            font-weight: bold;
        }
        .product-category {
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            padding: 12px 25px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            margin-right: 10px;
            font-size: 16px;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .btn-primary {
            background-color: #e74c3c;
        }
        .btn-primary:hover {
            background-color: #c0392b;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>商品详情</h1>
        
        <div class="product-detail">
            <div class="product-image" style="display:flex;justify-content:center;align-items:center;background-color:#f9f9f9;border-radius:5px;overflow:hidden;">
                <c:choose>
                    <c:when test="${not empty product.image}">
                        <c:choose>
                            <c:when test="${fn:startsWith(product.image,'http')}">
                                <img src="${product.image}" alt="图片" style="width:100%;height:100%;object-fit:cover;" onerror="this.src='https://via.placeholder.com/400x400?text=No+Image'" />
                            </c:when>
                            <c:when test="${fn:startsWith(product.image,'/')}">
                                <img src="${pageContext.request.contextPath}${product.image}" alt="图片" style="width:100%;height:100%;object-fit:cover;" onerror="this.src='https://via.placeholder.com/400x400?text=No+Image'" />
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/${product.image}" alt="图片" style="width:100%;height:100%;object-fit:cover;" onerror="this.src='https://via.placeholder.com/400x400?text=No+Image'" />
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <img src="https://via.placeholder.com/400x400?text=No+Image" alt="图片" style="width:100%;height:100%;object-fit:cover;" />
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="product-info">
                <div class="product-name">${product.name}</div>
                <div class="product-price">¥ ${product.price}</div>
                <div class="product-description">${product.description}</div>
                <div class="product-stock">库存：${product.stock}</div>
                <div class="product-category">分类ID：${product.categoryId}</div>
                <div>
                    <a href="${pageContext.request.contextPath}/product/list" class="btn">返回列表</a>
                    <a href="${pageContext.request.contextPath}/cart/add?productId=${product.id}&quantity=1" class="btn btn-primary">加入购物车</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>