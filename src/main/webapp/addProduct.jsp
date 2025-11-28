<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>添加商品</title>
    <style>
        body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}
        .container{width:70%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}
        h1{color:#333;margin:0 0 20px}
        .form-row{margin-bottom:12px}
        .form-row label{display:inline-block;width:100px;color:#555}
        .form-row input,.form-row select,.form-row textarea{padding:8px;border:1px solid #ddd;border-radius:3px;width:60%}
        .btn{display:inline-block;padding:8px 16px;background:#3498db;color:#fff;text-decoration:none;border:none;border-radius:3px;cursor:pointer}
        .btn:hover{background:#2980b9}
        .toolbar{margin-bottom:15px}
    </style>
    </head>
<body>
    <div class="container">
        <h1>添加商品</h1>
        <div class="toolbar">
            <a href="${pageContext.request.contextPath}/product/list" class="btn">返回商品列表</a>
        </div>
        <form id="productForm" action="${pageContext.request.contextPath}/product/save" method="post" enctype="multipart/form-data" onsubmit="return validateForm()">
            <div class="form-row">
                <label>商品名称</label>
                <input type="text" name="name" required maxlength="100" />
            </div>
            <div class="form-row">
                <label>所属分类</label>
                <select id="categorySelect" name="categoryId" required>
                    <option value="">请选择分类</option>
                    <c:forEach var="category" items="${categoryTree}">
                        <optgroup label="${category.name}">
                            <c:forEach var="sub" items="${category.children}">
                                <option value="${sub.id}">${sub.name}</option>
                                <c:forEach var="sub2" items="${sub.children}">
                                    <option value="${sub2.id}">· ${sub2.name}</option>
                                </c:forEach>
                            </c:forEach>
                        </optgroup>
                    </c:forEach>
                </select>
            </div>
            <div class="form-row">
                <label>价格</label>
                <input type="number" name="price" required min="0" step="0.01" inputmode="decimal" title="请输入合法的价格，如 199 或 199.99" />
            </div>
            <div class="form-row">
                <label>库存</label>
                <input type="number" name="stock" required min="0" />
            </div>
            <div class="form-row">
                <label>商品图片</label>
                <input type="file" name="imageFile" accept="image/*" onchange="preview(this)" />
            </div>
            <div class="form-row">
                <label>预览</label>
                <img id="imgPreview" style="max-width:200px;max-height:200px;border:1px solid #ddd" />
            </div>
            <div class="form-row">
                <label>图片URL</label>
                <input type="text" name="image" placeholder="例如 https://example.com/image.jpg 或 /images/xxx.jpg" />
            </div>
            <div class="form-row">
                <label>商品描述</label>
                <textarea name="description" rows="4"></textarea>
            </div>
            <div class="form-row">
                <button type="submit" class="btn">保 存</button>
            </div>
        </form>
        <script>
            function preview(input){
                var f=input.files&&input.files[0];
                if(!f)return;
                var r=new FileReader();
                r.onload=function(e){document.getElementById('imgPreview').src=e.target.result};
                r.readAsDataURL(f);
            }
            function validateForm(){
                var file = document.querySelector('input[name=imageFile]').files[0];
                var url = document.querySelector('input[name=image]').value.trim();
                var cat = document.getElementById('categorySelect').value;
                if(!file && !url){
                    alert('请上传图片或填写图片URL至少其一');
                    return false;
                }
                if(!cat){
                    alert('请选择所属分类');
                    return false;
                }
                return true;
            }
            // 当服务端未注入分类树时，使用异步加载构建分组下拉
            (function(){
                var sel = document.getElementById('categorySelect');
                if (sel.options.length <= 1) {
                    fetch('${pageContext.request.contextPath}/category/tree.json')
                        .then(function(r){return r.json()})
                        .then(function(data){
                            if(!data || !data.items) return;
                            data.items.forEach(function(top){
                                var group = document.createElement('optgroup');
                                group.label = top.name;
                                sel.appendChild(group);
                                (top.children||[]).forEach(function(sub){
                                    var opt = document.createElement('option');
                                    opt.value = sub.id;
                                    opt.textContent = sub.name;
                                    group.appendChild(opt);
                                    (sub.children||[]).forEach(function(sub2){
                                        var opt2 = document.createElement('option');
                                        opt2.value = sub2.id;
                                        opt2.textContent = '· ' + sub2.name;
                                        group.appendChild(opt2);
                                    });
                                });
                            });
                        })
                        .catch(function(){ /* 忽略错误 */ });
                }
            })();
        </script>
    </div>
</body>
</html>