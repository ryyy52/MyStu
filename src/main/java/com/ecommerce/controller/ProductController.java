package com.ecommerce.controller;

import com.ecommerce.pojo.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.impl.ProductServiceImpl;
import com.ecommerce.utils.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ProductController extends HttpServlet {
    private ProductService productService = new ProductServiceImpl();

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
        if ("product".equalsIgnoreCase(action)) {
            String a = request.getParameter("action");
            if (a != null && !a.isEmpty()) action = a;
        }

        try {
            switch (action) {
                case "list":
                    listProducts(request, response);
                    break;
                case "detail":
                    productDetail(request, response);
                    break;
                case "search":
                    searchProducts(request, response);
                    break;
                case "save":
                    saveProduct(request, response);
                    break;
                default:
                    out.println("无效的请求");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("系统错误：" + e.getMessage());
        }
    }

    /**
     * 商品列表
     */
    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");

        int page = 1;
        int pageSize = 12;
        Integer categoryId = null;

        if (pageStr != null && ValidationUtils.isValidPositiveInteger(pageStr)) {
            page = Integer.parseInt(pageStr);
            if (page < 1) page = 1;
        }

        if (pageSizeStr != null && ValidationUtils.isValidPositiveInteger(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
            if (pageSize < 1 || pageSize > 100) pageSize = 12;
        }

        if (categoryIdStr != null && ValidationUtils.isValidPositiveInteger(categoryIdStr)) {
            categoryId = Integer.parseInt(categoryIdStr);
        }

        List<Product> products;
        int totalCount;

        if (categoryId != null) {
            products = productService.findByCategoryIdAndPage(categoryId, page, pageSize);
            totalCount = productService.countByCategoryId(categoryId);
        } else {
            products = productService.findByPage(page, pageSize);
            totalCount = productService.countAll();
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        request.setAttribute("products", products);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("categoryId", categoryId);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    /**
     * 商品详情
     */
    private void productDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int productId = Integer.parseInt(idStr);
            Product product = productService.findById(productId);
            if (product != null) {
                request.setAttribute("product", product);
                request.getRequestDispatcher("/product_detail.jsp").forward(request, response);
            } else {
                response.getWriter().println("商品不存在！");
            }
        } else {
            response.getWriter().println("无效的商品ID！");
        }
    }

    /**
     * 商品搜索
     */
    private void searchProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String safeKeyword = ValidationUtils.sanitizeInput(keyword);
        if (safeKeyword == null) {
            safeKeyword = "";
        }

        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");

        int page = 1;
        int pageSize = 12;

        if (pageStr != null && ValidationUtils.isValidPositiveInteger(pageStr)) {
            page = Integer.parseInt(pageStr);
            if (page < 1) page = 1;
        }

        if (pageSizeStr != null && ValidationUtils.isValidPositiveInteger(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
            if (pageSize < 1 || pageSize > 100) pageSize = 12;
        }

        List<Product> products = productService.searchByPage(safeKeyword, page, pageSize);
        int totalCount = productService.countSearchResults(safeKeyword);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        request.setAttribute("products", products);
        request.setAttribute("keyword", safeKeyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    private void saveProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String categoryIdStr = request.getParameter("categoryId");
        String priceStr = request.getParameter("price");
        String stockStr = request.getParameter("stock");
        String image = request.getParameter("image");
        String description = request.getParameter("description");

        Integer categoryId = categoryIdStr != null && !categoryIdStr.isEmpty() ? Integer.parseInt(categoryIdStr) : null;
        java.math.BigDecimal price = priceStr != null && !priceStr.isEmpty() ? new java.math.BigDecimal(priceStr) : java.math.BigDecimal.ZERO;
        Integer stock = stockStr != null && !stockStr.isEmpty() ? Integer.parseInt(stockStr) : 0;

        Product p = new Product();
        p.setName(name);
        p.setCategoryId(categoryId);
        p.setPrice(price);
        p.setStock(stock);
        p.setImage(image);
        p.setDescription(description);
        boolean ok = productService.save(p);
        PrintWriter out = response.getWriter();
        if (ok) {
            out.println("商品保存成功！<a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
        } else {
            out.println("商品保存失败！<a href='" + request.getContextPath() + "/addProduct.jsp'>返回添加</a>");
        }
    }
}
