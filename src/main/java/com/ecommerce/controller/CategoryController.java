package com.ecommerce.controller;

import com.ecommerce.pojo.Category;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.impl.CategoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 商品分类控制器
 * 负责处理商品分类相关的HTTP请求，包括分类列表查询、分类树获取、
 * 分类添加、分类修改、分类删除等功能
 */
public class CategoryController extends HttpServlet {
    // 分类服务层实例，用于处理分类业务逻辑
    private CategoryService categoryService = new CategoryServiceImpl();

    /**
     * 处理GET请求
     * 所有GET请求都会转发到doPost方法处理
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 处理POST请求
     * 根据请求路径中的action参数，分发到对应的处理方法
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ==================== 1. 设置响应编码 ====================
        // 设置响应内容类型和编码
        response.setContentType("text/html;charset=UTF-8");
        // 设置请求编码，避免中文乱码
        request.setCharacterEncoding("UTF-8");

        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // ==================== 2. 获取请求路径和action ====================
        // 获取请求URI
        String uri = request.getRequestURI();
        // 从URI中提取action参数
        String action = uri.substring(uri.lastIndexOf("/") + 1);

        // ==================== 3. 根据action分发请求 ====================
        try {
            switch (action) {
                case "list":
                    // 获取分类列表
                    listCategories(request, response);
                    break;
                case "tree":
                    // 获取分类树结构（HTML格式）
                    getCategoryTree(request, response);
                    break;
                case "tree.json":
                    // 获取分类树结构（JSON格式）
                    getCategoryTreeJson(request, response);
                    break;
                case "save":
                    // 保存分类
                    saveCategory(request, response);
                    break;
                case "update":
                    // 更新分类
                    updateCategory(request, response);
                    break;
                case "delete":
                    // 删除分类
                    deleteCategory(request, response);
                    break;
                case "edit":
                    // 编辑分类
                    editCategory(request, response);
                    break;
                default:
                    // 无效的请求
                    out.println("无效的请求");
            }
        } catch (Exception e) {
            // 捕获所有异常，打印堆栈信息
            e.printStackTrace();
            // 向客户端输出错误信息
            out.println("系统错误：" + e.getMessage());
        } finally {
            // 关闭响应输出流
            out.close();
        }
    }

    /**
     * 获取分类列表方法
     * 以树形结构展示商品分类列表，支持编辑和删除操作
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void listCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取分类树数据
        List<Category> categoryTree = categoryService.getCategoryTree();
        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // ==================== 1. 生成HTML页面 ====================
        // 生成HTML页面头部
        out.println("<html lang='zh-CN'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>商品分类列表</title>");
        // 生成CSS样式
        out.println("<style>body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}.container{width:80%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}h1{color:#333;margin:0 0 20px}.toolbar{margin-bottom:15px} .btn{display:inline-block;padding:8px 12px;background:#3498db;color:#fff;text-decoration:none;border-radius:3px} .btn:hover{background:#2980b9} table{width:100%;border-collapse:collapse} th,td{border:1px solid #ddd;padding:10px;text-align:left} th{background:#f8f9fa} tr:nth-child(even){background:#fafafa} .ops a{margin-right:10px;color:#3498db;text-decoration:none} .ops a:hover{color:#2980b9}</style>");
        out.println("</head>");
        // 生成HTML页面主体
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>商品分类列表</h1>");
        // 生成添加分类按钮
        out.println("<div class='toolbar'><a class='btn' href='" + request.getContextPath() + "/addCategory.jsp'>添加分类</a></div>");
        // 生成分类列表表格
        out.println("<table>");
        out.println("<tr><th>ID</th><th>分类名称</th><th>父分类ID</th><th>操作</th></tr>");

        // ==================== 2. 递归打印分类树 ====================
        printCategoryList(categoryTree, out, 0, request);

        // 关闭HTML标签
        out.println("</table>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * 递归打印分类列表，显示层级结构
     * @param categories 分类列表
     * @param out        响应输出流
     * @param level      分类层级
     * @param request    HTTP请求对象
     */
    private void printCategoryList(List<Category> categories, PrintWriter out, int level, HttpServletRequest request) {
        // 遍历分类列表
        for (Category category : categories) {
            // ==================== 1. 生成缩进字符串 ====================
            // 根据层级添加缩进，每级4个空格
            String indent = "";
            for (int i = 0; i < level; i++) {
                indent += "&nbsp;&nbsp;&nbsp;&nbsp;";
            }
            
            // ==================== 2. 打印分类行 ====================
            // 打印分类行开始标签
            out.println("<tr>");
            // 打印分类ID
            out.println("<td>" + category.getId() + "</td>");
            // 打印分类名称，带层级缩进
            out.println("<td>" + indent + category.getName() + "</td>");
            // 打印父分类ID
            out.println("<td>" + category.getParentId() + "</td>");
            // 打印操作按钮
            out.println("<td class='ops'>");
            // 编辑按钮
            out.println("<a href='" + request.getContextPath() + "/category/edit?id=" + category.getId() + "'>编辑</a>");
            // 删除按钮，带确认提示
            out.println("<a href='" + request.getContextPath() + "/category/delete?id=" + category.getId() + "' onclick='return confirm(\"确定要删除吗？\")'>删除</a>");
            out.println("</td>");
            // 打印分类行结束标签
            out.println("</tr>");
            
            // ==================== 3. 递归打印子分类 ====================
            // 如果当前分类有子分类，递归打印
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                printCategoryList(category.getChildren(), out, level + 1, request);
            }
        }
    }

    /**
     * 获取分类树结构方法（HTML格式）
     * 以树形结构展示商品分类
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void getCategoryTree(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取分类树数据
        List<Category> categoryTree = categoryService.getCategoryTree();
        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // 生成页面标题
        out.println("<h1>商品分类树</h1>");
        // 调用printCategoryTree方法打印分类树
        printCategoryTree(categoryTree, out, 0);
    }

    /**
     * 获取分类树结构方法（JSON格式）
     * 返回JSON格式的分类树数据，用于AJAX请求
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws IOException IO异常
     */
    private void getCategoryTreeJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        // 获取分类树数据
        List<Category> categoryTree = categoryService.getCategoryTree();
        // 创建StringBuilder用于构建JSON响应
        StringBuilder sb = new StringBuilder();
        // 构建JSON响应的基本结构
        sb.append("{\"success\":true,\"items\":[");
        // 调用writeTreeJson方法生成JSON格式的分类树数据
        writeTreeJson(categoryTree, sb);
        // 闭合JSON响应
        sb.append("]}");
        // 发送JSON响应
        response.getWriter().write(sb.toString());
    }

    /**
     * 递归生成JSON格式的分类树数据
     * @param cats 分类列表
     * @param sb   StringBuilder对象，用于构建JSON响应
     */
    private void writeTreeJson(List<Category> cats, StringBuilder sb) {
        // 遍历分类列表
        for (int i=0;i<cats.size();i++) {
            Category c = cats.get(i);
            // ==================== 1. 生成分类基本信息 ====================
            // 生成分类ID
            sb.append("{\"id\":").append(c.getId())
              .append(",\"name\":\"").append(c.getName()).append("\"")
              .append(",\"parentId\":").append(c.getParentId()==null?0:c.getParentId())
              .append(",\"children\":[");
            // ==================== 2. 递归生成子分类JSON ====================
            if (c.getChildren()!=null && !c.getChildren().isEmpty()) {
                writeTreeJson(c.getChildren(), sb);
            }
            // ==================== 3. 闭合JSON结构 ====================
            sb.append("]}");
            // 添加逗号分隔符（除了最后一个元素）
            if (i<cats.size()-1) sb.append(",");
        }
    }

    /**
     * 递归打印分类树
     */
    private void printCategoryTree(List<Category> categories, PrintWriter out, int level) {
        for (Category category : categories) {
            // 打印缩进
            for (int i = 0; i < level; i++) {
                out.print("&nbsp;&nbsp;");
            }
            out.println("- " + category.getName() + "<br>");
            // 递归打印子分类
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                printCategoryTree(category.getChildren(), out, level + 1);
            }
        }
    }

    /**
     * 保存分类
     */
    private void saveCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String parentIdStr = request.getParameter("parentId");
        String sortStr = request.getParameter("sort");
        String icon = request.getParameter("icon");
        String description = request.getParameter("description");
        
        Integer parentId = parentIdStr != null && !parentIdStr.isEmpty() ? Integer.parseInt(parentIdStr) : 0;
        Integer sort = sortStr != null && !sortStr.isEmpty() ? Integer.parseInt(sortStr) : 0;

        Category category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setSort(sort);
        category.setIcon(icon);
        category.setDescription(description);
        
        // 计算分类级别
        if (parentId == 0) {
            // 顶级分类
            category.setLevel(1);
        } else {
            // 子分类，级别为父分类级别+1
            Category parentCategory = categoryService.findById(parentId);
            if (parentCategory != null) {
                category.setLevel(parentCategory.getLevel() + 1);
            } else {
                // 如果父分类不存在，默认设为顶级分类
                category.setLevel(1);
            }
        }

        boolean success = categoryService.save(category);
        
        // 使用重定向代替直接输出HTML，解决浏览器返回键问题
        if (success) {
            response.sendRedirect(request.getContextPath() + "/category/list");
        } else {
            response.sendRedirect(request.getContextPath() + "/category/add?error=save_failed");
        }
    }

    /**
     * 更新分类
     */
    private void updateCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String parentIdStr = request.getParameter("parentId");
        String sortStr = request.getParameter("sort");

        Integer id = Integer.parseInt(idStr);
        Integer parentId = parentIdStr != null && !parentIdStr.isEmpty() ? Integer.parseInt(parentIdStr) : 0;
        Integer sort = sortStr != null && !sortStr.isEmpty() ? Integer.parseInt(sortStr) : 0;

        Category category = categoryService.findById(id);
        PrintWriter out = response.getWriter();

        if (category != null) {
            category.setName(name);
            category.setParentId(parentId);
            category.setSort(sort);
            
            // 计算分类级别
            if (parentId == 0) {
                // 顶级分类
                category.setLevel(1);
            } else {
                // 子分类，级别为父分类级别+1
                Category parentCategory = categoryService.findById(parentId);
                if (parentCategory != null) {
                    category.setLevel(parentCategory.getLevel() + 1);
                } else {
                    // 如果父分类不存在，默认设为顶级分类
                    category.setLevel(1);
                }
            }

            boolean success = categoryService.update(category);
            
            // 使用重定向代替直接输出HTML，解决浏览器返回键问题
            if (success) {
                response.sendRedirect(request.getContextPath() + "/category/list");
            } else {
                response.sendRedirect(request.getContextPath() + "/category/edit?id=" + id + "&error=update_failed");
            }
        } else {
            out.println("分类不存在！<a href='list'>返回列表</a>");
        }
    }

    /**
     * 编辑分类
     */
    private void editCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int categoryId = Integer.parseInt(idStr);
            Category category = categoryService.findById(categoryId);
            PrintWriter out = response.getWriter();

            if (category != null) {
                // 渲染编辑页面
                out.println("<html lang='zh-CN'>");
                out.println("<head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<title>编辑分类</title>");
                out.println("<style>body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}.container{width:60%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}h1{color:#333;margin:0 0 20px}.form-group{margin-bottom:15px}label{display:block;margin-bottom:5px}input,select,textarea{width:100%;padding:8px;box-sizing:border-box;border:1px solid #ddd;border-radius:3px}.btn{padding:8px 12px;background:#3498db;color:#fff;border:none;border-radius:3px;cursor:pointer}.btn:hover{background:#2980b9}</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<div class='container'>");
                out.println("<h1>编辑分类</h1>");
                out.println("<form action='" + request.getContextPath() + "/category/update' method='post'>");
                out.println("<input type='hidden' name='id' value='" + category.getId() + "'>");
                out.println("<div class='form-group'><label for='name'>分类名称：</label><input type='text' id='name' name='name' value='" + category.getName() + "' required></div>");
                out.println("<div class='form-group'><label for='parentId'>父分类：</label><select id='parentId' name='parentId'><option value='0'>顶级分类</option>");
                // 获取所有分类，用于父分类选择
                List<Category> allCategories = categoryService.findAll();
                for (Category cat : allCategories) {
                    if (cat.getId() != category.getId()) { // 排除自身
                        String selected = cat.getId().equals(category.getParentId()) ? "selected" : "";
                        out.println("<option value='" + cat.getId() + "' " + selected + ">" + cat.getName() + "</option>");
                    }
                }
                out.println("</select></div>");
                out.println("<div class='form-group'><label for='sort'>排序：</label><input type='number' id='sort' name='sort' value='" + (category.getSort() != null ? category.getSort() : 0) + "'></div>");
                out.println("<div class='form-group'><label for='icon'>图标：</label><input type='text' id='icon' name='icon' value='" + (category.getIcon() != null ? category.getIcon() : "") + "'></div>");
                out.println("<div class='form-group'><label for='description'>描述：</label><textarea id='description' name='description' rows='3'>" + (category.getDescription() != null ? category.getDescription() : "") + "</textarea></div>");
                out.println("<div class='form-group'><input type='submit' class='btn' value='保存修改'> <a href='" + request.getContextPath() + "/category/list' class='btn' style='background:#95a5a6'>返回列表</a></div>");
                out.println("</form>");
                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
            } else {
                // 使用重定向代替直接输出HTML，解决浏览器返回键问题
            response.sendRedirect(request.getContextPath() + "/category/list?error=category_not_found");
            }
        } else {
            response.getWriter().println("无效的分类ID！<a href='" + request.getContextPath() + "/category/list'>返回列表</a>");
        }
    }

    /**
     * 删除分类
     */
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        Integer id = Integer.parseInt(idStr);

        boolean success = categoryService.delete(id);
        
        // 使用重定向代替直接输出HTML，解决浏览器返回键问题
        if (success) {
            response.sendRedirect(request.getContextPath() + "/category/list");
        } else {
            response.sendRedirect(request.getContextPath() + "/category/list?error=delete_failed");
        }
    }
}