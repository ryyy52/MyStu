package com.ecommerce.controller;

import com.ecommerce.pojo.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.impl.ProductServiceImpl;
import com.ecommerce.utils.ValidationUtils;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.impl.CategoryServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
                case "add":
                    showAddProductPage(request, response);
                    break;
                case "delete":
                    deleteProduct(request, response);
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
            // 获取分类及其所有子分类的ID列表
            List<Integer> categoryIds = categoryService.getCategoryIdsWithChildren(categoryId);
            System.out.println("分类ID列表：" + categoryIds);
            
            // 根据分类ID列表查询商品
            products = productService.findByCategoryIdsAndPage(categoryIds, page, pageSize);
            totalCount = productService.countByCategoryIds(categoryIds);
        } else {
            products = productService.findByPage(page, pageSize);
            totalCount = productService.countAll();
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // 获取分类树数据，所有用户都可以访问
        List<com.ecommerce.pojo.Category> categoryTree = categoryService.getCategoryTree();
        request.setAttribute("categoryTree", categoryTree);
        
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

        // 获取分类树数据，所有用户都可以访问
        List<com.ecommerce.pojo.Category> categoryTree = categoryService.getCategoryTree();
        request.setAttribute("categoryTree", categoryTree);
        
        request.setAttribute("products", products);
        request.setAttribute("keyword", safeKeyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    private void saveProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        System.out.println("=== 开始处理商品保存请求 ===");
        
        try {
            // 获取表单参数
            String name = request.getParameter("name");
            String categoryIdStr = request.getParameter("categoryId");
            String priceStr = request.getParameter("price");
            String stockStr = request.getParameter("stock");
            String description = request.getParameter("description");
            String image = request.getParameter("image");
            
            System.out.println("表单参数：");
            System.out.println("name: " + name);
            System.out.println("categoryIdStr: " + categoryIdStr);
            System.out.println("priceStr: " + priceStr);
            System.out.println("stockStr: " + stockStr);
            System.out.println("description: " + description);
            System.out.println("image: " + image);
            
            // 表单验证
            if (name == null || name.trim().isEmpty()) {
                out.println("商品名称不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品名称不能为空");
                return;
            }
            
            if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                out.println("商品分类不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品分类不能为空");
                return;
            }
            
            if (priceStr == null || priceStr.trim().isEmpty()) {
                out.println("商品价格不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品价格不能为空");
                return;
            }
            
            if (stockStr == null || stockStr.trim().isEmpty()) {
                out.println("商品库存不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品库存不能为空");
                return;
            }
            
            // 处理文件上传
            Part imagePart = request.getPart("imageFile");
            String imageUrl = image;
            
            System.out.println("文件上传信息：");
            System.out.println("imagePart: " + imagePart);
            System.out.println("imagePart.size: " + (imagePart != null ? imagePart.getSize() : 0));
            
            // 如果上传了文件，处理文件上传
            if (imagePart != null && imagePart.getSize() > 0) {
                // 获取文件名
                String submittedFileName = imagePart.getSubmittedFileName();
                System.out.println("上传的文件名：" + submittedFileName);
                
                // 生成唯一文件名
                String fileName = System.currentTimeMillis() + "_" + submittedFileName;
                // 设置文件保存路径
                String uploadPath = request.getServletContext().getRealPath("/images");
                java.io.File uploadDir = new java.io.File(uploadPath);
                System.out.println("文件保存路径：" + uploadPath);
                
                if (!uploadDir.exists()) {
                    boolean mkdirResult = uploadDir.mkdirs();
                    System.out.println("创建目录结果：" + mkdirResult);
                }
                // 保存文件
                String filePath = uploadPath + java.io.File.separator + fileName;
                System.out.println("完整文件路径：" + filePath);
                
                imagePart.write(filePath);
                System.out.println("文件保存成功");
                
                // 设置图片URL
                imageUrl = "/images/" + fileName;
                System.out.println("生成的图片URL：" + imageUrl);
            }
            
            // 验证图片URL长度
            if (imageUrl != null && imageUrl.length() > 1024) {
                out.println("图片URL长度不能超过1024个字符！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：图片URL长度超过1024个字符，当前长度：" + imageUrl.length());
                return;
            }
            
            // 参数转换
            Integer categoryId = null;
            java.math.BigDecimal price = java.math.BigDecimal.ZERO;
            Integer stock = 0;
            
            try {
                categoryId = Integer.parseInt(categoryIdStr);
                price = new java.math.BigDecimal(priceStr);
                stock = Integer.parseInt(stockStr);
                System.out.println("参数转换成功：");
                System.out.println("categoryId: " + categoryId);
                System.out.println("price: " + price);
                System.out.println("stock: " + stock);
            } catch (NumberFormatException e) {
                out.println("参数格式错误！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("参数转换失败：" + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // 验证价格和库存的合理性
            if (price.compareTo(java.math.BigDecimal.ZERO) < 0) {
                out.println("商品价格不能为负数！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品价格不能为负数");
                return;
            }
            
            if (stock < 0) {
                out.println("商品库存不能为负数！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品库存不能为负数");
                return;
            }

            // 创建商品对象
            Product p = new Product();
            p.setName(name.trim());
            p.setCategoryId(categoryId);
            p.setPrice(price);
            p.setStock(stock);
            p.setImage(imageUrl);
            p.setDescription(description != null ? description.trim() : "");
            
            System.out.println("创建商品对象成功：");
            System.out.println("商品名称: " + p.getName());
            System.out.println("分类ID: " + p.getCategoryId());
            System.out.println("价格: " + p.getPrice());
            System.out.println("库存: " + p.getStock());
            System.out.println("图片URL: " + p.getImage());
            System.out.println("描述: " + p.getDescription());
            
            // 保存商品
            System.out.println("调用productService.save()");
            boolean ok = productService.save(p);
            System.out.println("productService.save()返回结果: " + ok);
            
            if (ok) {
                System.out.println("商品保存成功");
                // 使用重定向代替直接输出HTML，解决浏览器返回键问题
                response.sendRedirect(request.getContextPath() + "/product/list");
            } else {
                System.out.println("商品保存失败");
                // 使用重定向代替直接输出HTML，解决浏览器返回键问题
                response.sendRedirect(request.getContextPath() + "/product/add?error=save_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("商品保存过程中发生异常：" + e.getMessage());
            // 使用重定向代替直接输出HTML，解决浏览器返回键问题
            response.sendRedirect(request.getContextPath() + "/product/add?error=exception&message=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } finally {
            System.out.println("=== 商品保存请求处理结束 ===");
        }
    }
    
    /**
     * 显示添加商品页面
     */
    private void showAddProductPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取分类树数据
        List<com.ecommerce.pojo.Category> categoryTree = categoryService.getCategoryTree();
        // 设置分类树为请求属性
        request.setAttribute("categoryTree", categoryTree);
        // 转发到添加商品页面
        request.getRequestDispatcher("/addProduct.jsp").forward(request, response);
    }
    
    /**
     * 删除商品
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        System.out.println("=== 开始处理商品删除请求 ===");
        
        try {
            // 获取商品ID
            String idStr = request.getParameter("id");
            System.out.println("商品ID: " + idStr);
            
            // 验证商品ID
            if (idStr == null || idStr.trim().isEmpty() || !ValidationUtils.isValidPositiveInteger(idStr)) {
                out.println("无效的商品ID！<a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
                System.out.println("验证失败：无效的商品ID");
                return;
            }
            
            int productId = Integer.parseInt(idStr);
            
            // 调用服务层删除商品
            System.out.println("调用productService.delete()");
            boolean ok = productService.delete(productId);
            System.out.println("productService.delete()返回结果: " + ok);
            
            if (ok) {
                // 删除成功，重定向到商品列表页面
                System.out.println("商品删除成功");
                response.sendRedirect(request.getContextPath() + "/product/list");
            } else {
                // 删除失败
                out.println("商品删除失败！<a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
                System.out.println("商品删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("商品删除失败！错误信息：" + e.getMessage() + "<br><a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
            System.out.println("商品删除过程中发生异常：" + e.getMessage());
        } finally {
            System.out.println("=== 商品删除请求处理结束 ===");
        }
    }
}
