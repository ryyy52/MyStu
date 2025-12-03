<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>添加商品分类</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 500px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            color: #333;
        }
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            margin: 10px 0 5px;
            font-weight: bold;
        }
        input[type="text"] {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        input[type="submit"] {
            margin-top: 20px;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        input[type="submit"]:hover {
            background-color: #45a049;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #0066cc;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>添加商品分类</h1>
        <form action="${pageContext.request.contextPath}/category/save" method="post">
            <label for="name">分类名称：</label>
            <input type="text" id="name" name="name" required>
            
            <label for="parentId">父分类ID（0表示顶级分类）：</label>
            <input type="text" id="parentId" name="parentId" placeholder="0">
            
            <label for="sort">排序：</label>
            <input type="text" id="sort" name="sort" placeholder="0">
            
            <label for="icon">分类图标：</label>
            <input type="text" id="icon" name="icon" placeholder="可选">
            
            <label for="description">分类描述：</label>
            <input type="text" id="description" name="description" placeholder="可选">
            
            <input type="submit" value="添加分类">
        </form>
        <a href="${pageContext.request.contextPath}/index.jsp" class="back-link">返回首页</a>
    </div>
</body>
</html>