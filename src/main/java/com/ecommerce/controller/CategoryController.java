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
 */
public class CategoryController extends HttpServlet {
    private CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应编码
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        // 获取请求路径
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/") + 1);

        try {
            switch (action) {
                case "list":
                    listCategories(request, response);
                    break;
                case "tree":
                    getCategoryTree(request, response);
                    break;
                case "tree.json":
                    getCategoryTreeJson(request, response);
                    break;
                case "save":
                    saveCategory(request, response);
                    break;
                case "update":
                    updateCategory(request, response);
                    break;
                case "delete":
                    deleteCategory(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/product/list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("系统错误：" + e.getMessage());
        } finally {
            out.close();
        }
    }

    /**
     * 获取分类列表
     */
    private void listCategories(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        PrintWriter out = response.getWriter();

        out.println("<html lang='zh-CN'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>商品分类列表</title>");
        out.println("<style>body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f2f2f2}.container{width:80%;margin:20px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}h1{color:#333;margin:0 0 20px}.toolbar{margin-bottom:15px} .btn{display:inline-block;padding:8px 12px;background:#3498db;color:#fff;text-decoration:none;border-radius:3px} .btn:hover{background:#2980b9} table{width:100%;border-collapse:collapse} th,td{border:1px solid #ddd;padding:10px;text-align:left} th{background:#f8f9fa} tr:nth-child(even){background:#fafafa} .ops a{margin-right:10px;color:#3498db;text-decoration:none} .ops a:hover{color:#2980b9}</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>商品分类列表</h1>");
        out.println("<div class='toolbar'><a class='btn' href='" + request.getContextPath() + "/addCategory.jsp'>添加分类</a></div>");
        out.println("<table>");
        out.println("<tr><th>ID</th><th>分类名称</th><th>父分类ID</th><th>操作</th></tr>");

        for (Category category : categories) {
            out.println("<tr>");
            out.println("<td>" + category.getId() + "</td>");
            out.println("<td>" + category.getName() + "</td>");
            out.println("<td>" + category.getParentId() + "</td>");
            out.println("<td class='ops'>");
            out.println("<a href='" + request.getContextPath() + "/category/edit?id=" + category.getId() + "'>编辑</a>");
            out.println("<a href='" + request.getContextPath() + "/category/delete?id=" + category.getId() + "' onclick='return confirm(\"确定要删除吗？\")'>删除</a>");
            out.println("</td>");
            out.println("</tr>");
        }

        out.println("</table>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * 获取分类树结构
     */
    private void getCategoryTree(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categoryTree = categoryService.getCategoryTree();
        PrintWriter out = response.getWriter();

        out.println("<h1>商品分类树</h1>");
        printCategoryTree(categoryTree, out, 0);
    }

    private void getCategoryTreeJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Category> categoryTree = categoryService.getCategoryTree();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"success\":true,\"items\":[");
        writeTreeJson(categoryTree, sb);
        sb.append("]}");
        response.getWriter().write(sb.toString());
    }

    private void writeTreeJson(List<Category> cats, StringBuilder sb) {
        for (int i=0;i<cats.size();i++) {
            Category c = cats.get(i);
            sb.append("{\"id\":").append(c.getId())
              .append(",\"name\":\"").append(c.getName()).append("\"")
              .append(",\"parentId\":").append(c.getParentId()==null?0:c.getParentId())
              .append(",\"children\":[");
            if (c.getChildren()!=null && !c.getChildren().isEmpty()) {
                writeTreeJson(c.getChildren(), sb);
            }
            sb.append("]}");
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
        PrintWriter out = response.getWriter();

        if (success) {
            out.println("分类添加成功！<a href='" + request.getContextPath() + "/category/list'>返回列表</a>");
        } else {
            out.println("分类添加失败！<a href='" + request.getContextPath() + "/addCategory.jsp'>返回添加</a>");
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
            if (success) {
                out.println("分类更新成功！<a href='" + request.getContextPath() + "/category/list'>返回列表</a>");
            } else {
                out.println("分类更新失败！<a href='" + request.getContextPath() + "/category/edit?id=" + id + "'>返回编辑</a>");
            }
        } else {
            out.println("分类不存在！<a href='list'>返回列表</a>");
        }
    }

    /**
     * 删除分类
     */
    private void deleteCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        Integer id = Integer.parseInt(idStr);

        boolean success = categoryService.delete(id);
        PrintWriter out = response.getWriter();

        if (success) {
            out.println("分类删除成功！<a href='" + request.getContextPath() + "/category/list'>返回列表</a>");
        } else {
            out.println("分类删除失败！可能存在子分类或关联商品。<a href='" + request.getContextPath() + "/category/list'>返回列表</a>");
        }
    }
}