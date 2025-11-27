package com.ecommerce.controller;

import com.ecommerce.pojo.Product;
import com.ecommerce.pojo.Category;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.impl.CategoryServiceImpl;
import com.ecommerce.service.impl.ProductServiceImpl;
import com.ecommerce.utils.ValidationUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class ProductController extends HttpServlet {
    private ProductService productService = new ProductServiceImpl();
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
        String normalizedUri = uri;
        int qMark = normalizedUri.indexOf('?');
        if (qMark > -1) normalizedUri = normalizedUri.substring(0, qMark);
        int scMark = normalizedUri.indexOf(';');
        if (scMark > -1) normalizedUri = normalizedUri.substring(0, scMark);
        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        String action = null;
        if (pathInfo != null && pathInfo.length() > 1) {
            action = pathInfo.substring(1);
        }
        if (action == null || action.isEmpty()) {
            action = uri.substring(uri.lastIndexOf('/') + 1);
        }
        String paramAction = request.getParameter("action");
        if (paramAction != null && !paramAction.isEmpty()) {
            action = paramAction;
        }
        if (action != null) {
            action = action.trim().toLowerCase();
        }
        System.out.println("Method: " + request.getMethod() + ", URI: " + uri + ", action: " + action);
        // 强制兜底：凡是命中 /product/save 均进入保存；GET直接跳转到添加页
        if ((normalizedUri != null && normalizedUri.endsWith("/product/save")) ||
            (pathInfo != null && "/save".equals(pathInfo)) ||
            (servletPath != null && servletPath.endsWith("/product/save")) ||
            ("save".equalsIgnoreCase(paramAction))) {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                request.getRequestDispatcher("/addProduct.jsp").forward(request, response);
            } else {
                saveProduct(request, response);
            }
            return;
        }

        // 命中 /product 根路径时显示列表
        if (normalizedUri != null && (normalizedUri.endsWith("/product") || normalizedUri.contains("/product;") || normalizedUri.contains("/product?"))) {
            listProducts(request, response);
            return;
        }
        int qidx = action.indexOf('?');
        if (qidx > -1) action = action.substring(0, qidx);
        int sidx = action.indexOf(';');
        if (sidx > -1) action = action.substring(0, sidx);
        action = action.trim().toLowerCase();
        System.out.println("DEBUG ProductController URI: " + uri + ", servletPath: " + servletPath + ", pathInfo: " + pathInfo + ", action: " + action);

        try {
            List<Category> preloadedTree = categoryService.getCategoryTree();
            request.setAttribute("categoryTree", preloadedTree);
            request.setAttribute("flatCategories", categoryService.findAll());
            boolean isAdd = "add".equals(action) || (pathInfo != null && "/add".equals(pathInfo)) || uri.endsWith("/product/add");
            if (isAdd) {
                if ("POST".equalsIgnoreCase(request.getMethod()) || "save".equalsIgnoreCase(paramAction)) {
                    saveProduct(request, response);
                } else {
                    addProductPage(request, response);
                }
                return;
            }
            switch (action) {
                case "product":
                    // 访问 /product 时显示列表
                    listProducts(request, response);
                    break;
                case "list":
                    listProducts(request, response);
                    break;
                case "list.json":
                    listProductsJson(request, response);
                    break;
                case "detail":
                    productDetail(request, response);
                    break;
                case "search":
                    searchProducts(request, response);
                    break;
                case "search.json":
                    searchProductsJson(request, response);
                    break;
                case "add":
                    addProductPage(request, response);
                    break;
                case "save":
                    saveProduct(request, response);
                    break;
                default:
                    // 默认回到列表，避免出现“无效的请求”
                    listProducts(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "系统错误：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
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
        // 加载分类树用于筛选下拉框
        List<Category> categoryTree = categoryService.getCategoryTree();
        request.setAttribute("categoryTree", categoryTree);

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

        try {
            List<Product> products = productService.searchByPage(safeKeyword, page, pageSize);
            int totalCount = productService.countSearchResults(safeKeyword);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages;

            // 获取分类树用于筛选下拉框
            List<Category> categoryTree = categoryService.getCategoryTree();

            request.setAttribute("products", products);
            request.setAttribute("keyword", safeKeyword);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("categoryTree", categoryTree);

            request.getRequestDispatcher("/product_list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "搜索商品时发生错误：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * 新增商品页面
     */
    private void addProductPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 加载分类树供选择
        List<Category> categoryTree = categoryService.getCategoryTree();
        request.setAttribute("categoryTree", categoryTree);
        request.getRequestDispatcher("/addProduct.jsp").forward(request, response);
    }

    /**
     * 保存商品
     */
    private void saveProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");
        String categoryIdStr = request.getParameter("categoryId");
        String priceStr = request.getParameter("price");
        String stockStr = request.getParameter("stock");
        String description = request.getParameter("description");
        String image = request.getParameter("image");

        if (name == null || name.trim().isEmpty() ||
            categoryIdStr == null || categoryIdStr.trim().isEmpty() ||
            priceStr == null || priceStr.trim().isEmpty() ||
            stockStr == null || stockStr.trim().isEmpty()) {
            out.println("参数不完整！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
            return;
        }

        try {
            Integer categoryId = Integer.parseInt(categoryIdStr);
            java.math.BigDecimal price = new java.math.BigDecimal(priceStr);
            Integer stock = Integer.parseInt(stockStr);

            com.ecommerce.pojo.Product product = new com.ecommerce.pojo.Product();
            product.setName(name);
            product.setCategoryId(categoryId);
            product.setPrice(price);
            product.setStock(stock);
            product.setDescription(description);
            Part imagePart = null;
            try { imagePart = request.getPart("imageFile"); } catch (Exception ignore) {}
            boolean hasFile = imagePart != null && imagePart.getSize() > 0;
            boolean hasUrl = image != null && image.trim().length() > 0;
            if (!hasFile && !hasUrl) {
                out.println("图片必填（上传文件或提供URL其一）！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                return;
            }
            if (hasFile) {
                String uploadRoot = request.getServletContext().getRealPath("/uploads");
                Path dir = Paths.get(uploadRoot);
                if (!Files.exists(dir)) { Files.createDirectories(dir); }
                String submitted = imagePart.getSubmittedFileName();
                String ext = "";
                if (submitted != null && submitted.contains(".")) {
                    ext = submitted.substring(submitted.lastIndexOf('.') + 1).toLowerCase();
                }
                if (!ext.matches("(png|jpg|jpeg|gif)")) {
                    out.println("图片格式不支持！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                    return;
                }
                String fname = UUID.randomUUID().toString().replace("-", "") + "." + ext;
                Path target = dir.resolve(fname);
                Files.copy(imagePart.getInputStream(), target);
                image = "/uploads/" + fname;
            }
            product.setImage(image);
            product.setStatus(1);

            boolean ok = productService.save(product);
            if (ok) {
                response.sendRedirect(request.getContextPath() + "/product/list");
            } else {
                out.println("保存失败！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
            }
        } catch (Exception e) {
            out.println("保存失败：" + e.getMessage() + "<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
        }
    }

    private void listProductsJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String categoryIdStr = request.getParameter("categoryId");
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 12;
        Integer categoryId = (categoryIdStr != null && ValidationUtils.isValidPositiveInteger(categoryIdStr)) ? Integer.parseInt(categoryIdStr) : null;
        List<Product> products;
        int totalCount;
        if (categoryId != null) {
            products = productService.findByCategoryIdAndPage(categoryId, page, pageSize);
            totalCount = productService.countByCategoryId(categoryId);
        } else {
            products = productService.findByPage(page, pageSize);
            totalCount = productService.countAll();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\"success\":true,\"totalCount\":").append(totalCount).append(",\"items\":[");
        for (int i=0;i<products.size();i++) {
            Product p = products.get(i);
            sb.append("{\"id\":").append(p.getId())
              .append(",\"name\":\"").append(p.getName()).append("\"")
              .append(",\"price\":").append(p.getPrice())
              .append(",\"stock\":").append(p.getStock())
              .append(",\"categoryId\":").append(p.getCategoryId())
              .append("}");
            if (i<products.size()-1) sb.append(",");
        }
        sb.append("]}");
        response.getWriter().write(sb.toString());
    }

    private void searchProductsJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String keyword = request.getParameter("keyword");
        String safeKeyword = ValidationUtils.sanitizeInput(keyword);
        if (safeKeyword == null) safeKeyword = "";
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 12;
        List<Product> products = productService.searchByPage(safeKeyword, page, pageSize);
        int totalCount = productService.countSearchResults(safeKeyword);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"success\":true,\"totalCount\":").append(totalCount).append(",\"items\":[");
        for (int i=0;i<products.size();i++) {
            Product p = products.get(i);
            sb.append("{\"id\":").append(p.getId())
              .append(",\"name\":\"").append(p.getName()).append("\"")
              .append(",\"price\":").append(p.getPrice())
              .append(",\"stock\":").append(p.getStock())
              .append(",\"categoryId\":").append(p.getCategoryId())
              .append("}");
            if (i<products.size()-1) sb.append(",");
        }
        sb.append("]}");
        response.getWriter().write(sb.toString());
    }
}
