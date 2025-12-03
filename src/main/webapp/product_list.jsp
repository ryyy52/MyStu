<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品列表</title>
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
        .filter-section {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .filter-form {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }
        .filter-form select {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 30px;
            gap: 10px;
        }
        .pagination a, .pagination span {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 3px;
            text-decoration: none;
            color: #333;
        }
        .pagination a:hover {
            background-color: #3498db;
            color: white;
        }
        .pagination .current {
            background-color: #3498db;
            color: white;
        }
        .pagination .disabled {
            color: #ccc;
            cursor: not-allowed;
        }
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .product-card {
            border: 1px solid #ddd;
            padding: 15px;
            border-radius: 5px;
        }
        .product-image {
            width: 100%;
            height: 200px;
            background-color: #f9f9f9;
            border-radius: 5px;
            margin-bottom: 10px;
        }
        .product-name {
            font-weight: bold;
            margin-bottom: 5px;
        }
        .product-price {
            color: #e74c3c;
            font-size: 18px;
            margin-bottom: 10px;
        }
        .btn {
            display: inline-block;
            padding: 8px 15px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 3px;
            margin-right: 10px;
        }
        .btn:hover {
            background-color: #2980b9;
        }
        .search-form {
            margin-bottom: 20px;
        }
        .search-form input[type="text"] {
            padding: 8px;
            width: 300px;
        }
        .search-form button {
            padding: 8px 15px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>商品列表</h1>
        <div class="toolbar">
            <c:if test="${not empty user and user.role == 'admin'}">
                <a href="${pageContext.request.contextPath}/product/add" class="btn">添加商品</a>
            </c:if>
        </div>
        
        <!-- 搜索表单 -->
        <form class="search-form" action="${pageContext.request.contextPath}/product/search" method="post">
            <input type="text" name="keyword" placeholder="搜索商品..." value="${keyword}">
            <button type="submit">搜索</button>
        </form>

        <!-- 分类筛选 -->
        <div class="filter-section">
            <form class="filter-form" action="${pageContext.request.contextPath}/product/list" method="get">
                <label>商品分类：</label>
                <select name="categoryId" onchange="this.form.submit()" style="min-width:240px;">
                    <option value="">全部分类</option>
                    <c:choose>
                        <c:when test="${not empty categoryTree}">
                            <c:forEach var="top" items="${categoryTree}">
                                <optgroup label="${top.name}">
                                    <c:forEach var="sub" items="${top.children}">
                                        <option value="${sub.id}" ${sub.id == categoryId ? 'selected' : ''}>${sub.name}</option>
                                        <c:forEach var="sub2" items="${sub.children}">
                                            <option value="${sub2.id}" ${sub2.id == categoryId ? 'selected' : ''}>· ${sub2.name}</option>
                                        </c:forEach>
                                    </c:forEach>
                                </optgroup>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <script>
                                (function(){
                                    fetch('${pageContext.request.contextPath}/category/tree.json').then(r=>r.json()).then(data=>{
                                        if(!data||!data.items) return; const sel=document.querySelector('select[name=categoryId]');
                                        const add=(nodes,prefix)=>{nodes.forEach(n=>{const opt=document.createElement('option');opt.value=n.id;opt.textContent=prefix+n.name;sel.appendChild(opt);if(n.children&&n.children.length){add(n.children,prefix+'· ')};});};
                                        add(data.items,'');
                                        var currentId='${categoryId}'; if(currentId){ sel.value = currentId; }
                                    });
                                })();
                            </script>
                        </c:otherwise>
                    </c:choose>
                </select>
                <input type="hidden" name="page" value="1">
            </form>
        </div>

        <!-- 商品列表 -->
        <div class="product-grid">
            <c:forEach var="product" items="${products}">
                <div class="product-card">
                    <div class="product-image">
                        <c:choose>
                            <c:when test="${fn:startsWith(product.image,'http')}">
                                <img src="${product.image}" alt="图片" style="width:100%;height:100%;object-fit:cover;border-radius:5px;" onerror="this.src='https://via.placeholder.com/250x200?text=No+Image'" />
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}${product.image}" alt="图片" style="width:100%;height:100%;object-fit:cover;border-radius:5px;" onerror="this.src='https://via.placeholder.com/250x200?text=No+Image'" />
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="product-name">${product.name}</div>
                    <div class="product-price">¥ ${product.price}</div>
                    <div class="product-stock">库存：${product.stock}</div>
                    <a href="${pageContext.request.contextPath}/product/detail?id=${product.id}" class="btn">查看详情</a>
                    <button onclick="addToCart(${product.id})" class="btn">加入购物车</button>
                    <c:if test="${not empty user and user.role == 'admin'}">
                        <a href="javascript:void(0)" onclick="if(confirm('确定要删除该商品吗？')) window.location.href='${pageContext.request.contextPath}/product/delete?id=${product.id}'" class="btn" style="background-color: #e74c3c;">删除商品</a>
                    </c:if>
                </div>
            </c:forEach>
        </div>

        <!-- 分页控件 -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <!-- 上一页 -->
                <c:choose>
                    <c:when test="${currentPage > 1}">
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                <a href="${pageContext.request.contextPath}/product/search?keyword=${keyword}&page=${currentPage - 1}&pageSize=${pageSize}">上一页</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/product/list?page=${currentPage - 1}&pageSize=${pageSize}&categoryId=${categoryId}">上一页</a>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <span class="disabled">上一页</span>
                    </c:otherwise>
                </c:choose>

                <!-- 页码 -->
                <c:forEach begin="1" end="${totalPages}" var="pageNum">
                    <c:choose>
                        <c:when test="${pageNum == currentPage}">
                            <span class="current">${pageNum}</span>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${not empty keyword}">
                                    <a href="${pageContext.request.contextPath}/product/search?keyword=${keyword}&page=${pageNum}&pageSize=${pageSize}">${pageNum}</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/product/list?page=${pageNum}&pageSize=${pageSize}&categoryId=${categoryId}">${pageNum}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <!-- 下一页 -->
                <c:choose>
                    <c:when test="${currentPage < totalPages}">
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                <a href="${pageContext.request.contextPath}/product/search?keyword=${keyword}&page=${currentPage + 1}&pageSize=${pageSize}">下一页</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/product/list?page=${currentPage + 1}&pageSize=${pageSize}&categoryId=${categoryId}">下一页</a>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <span class="disabled">下一页</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- 分页信息 -->
        <div style="text-align: center; margin-top: 10px; color: #666;">
            共 ${totalCount} 件商品，第 ${currentPage}/${totalPages} 页
        </div>
    </div>

    <!-- AJAX 购物车功能 -->
    <script>
        function addToCart(productId) {
            fetch('${pageContext.request.contextPath}/cart/add.json?productId=' + productId + '&quantity=1', {
                method: 'POST'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("添加成功！");
                } else {
                    alert("添加失败：" + (data.message || "未知错误"));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("添加失败：网络错误");
            });
        }
    </script>
</body>
</html>