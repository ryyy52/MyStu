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

/**
 * 商品管理控制器
 * 负责处理商品相关的HTTP请求，包括商品列表查询、商品详情展示、商品搜索、商品新增、商品删除等功能
 */
public class ProductController extends HttpServlet {
    // 商品服务层实例，用于处理商品业务逻辑
    private ProductService productService = new ProductServiceImpl();
    // 分类服务层实例，用于处理分类相关业务逻辑
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
        // 如果action是"product"，则从请求参数中获取真正的action
        if ("product".equalsIgnoreCase(action)) {
            String a = request.getParameter("action");
            if (a != null && !a.isEmpty()) action = a;
        }

        // ==================== 3. 根据action分发请求 ====================
        try {
            // 使用switch语句根据action分发请求到不同的处理方法
            switch (action) {
                case "list":
                    // 商品列表查询
                    listProducts(request, response);
                    break;
                case "detail":
                    // 商品详情展示
                    productDetail(request, response);
                    break;
                case "search":
                    // 商品搜索
                    searchProducts(request, response);
                    break;
                case "save":
                    // 商品保存（新增或修改）
                    saveProduct(request, response);
                    break;
                case "add":
                    // 显示添加商品页面
                    showAddProductPage(request, response);
                    break;
                case "delete":
                    // 删除商品
                    deleteProduct(request, response);
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
        }
    }

    /**
     * 商品列表查询方法
     * 显示分类商品的分页列表，支持分类筛选和分页查询
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ==================== 1. 获取请求参数 ====================
        // 获取分类ID参数 - 用于过滤指定分类的商品
        String categoryIdStr = request.getParameter("categoryId");
        // 获取当前页码参数 - 用于分页
        String pageStr = request.getParameter("page");
        // 获取每页显示数量参数 - 用于分页
        String pageSizeStr = request.getParameter("pageSize");

        // ==================== 2. 参数验证和初始化 ====================
        // 初始化当前页码，默认为第1页
        int page = 1;
        // 初始化每页显示数量，默认为12条
        int pageSize = 12;
        // 初始化分类ID，默认为null（显示所有分类）
        Integer categoryId = null;

        // 验证并设置当前页码
        if (pageStr != null && ValidationUtils.isValidPositiveInteger(pageStr)) {
            page = Integer.parseInt(pageStr);
            // 页码不能小于1
            if (page < 1) page = 1;
        }

        // 验证并设置每页显示数量
        if (pageSizeStr != null && ValidationUtils.isValidPositiveInteger(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
            // 每页显示数量必须在1-100之间
            if (pageSize < 1 || pageSize > 100) pageSize = 12;
        }

        // 验证并设置分类ID
        if (categoryIdStr != null && ValidationUtils.isValidPositiveInteger(categoryIdStr)) {
            categoryId = Integer.parseInt(categoryIdStr);
        }

        // ==================== 3. 查询商品数据 ====================
        List<Product> products;
        // 商品总数量
        int totalCount;

        if (categoryId != null) {
            // 如果指定了分类ID，则查询该分类及其所有子分类的商品
            // 获取分类及其所有子分类的ID列表
            List<Integer> categoryIds = categoryService.getCategoryIdsWithChildren(categoryId);
            System.out.println("分类ID列表：" + categoryIds);
            
            // 根据分类ID列表查询商品，并进行分页
            products = productService.findByCategoryIdsAndPage(categoryIds, page, pageSize);
            // 获取该分类及其所有子分类的商品总数量
            totalCount = productService.countByCategoryIds(categoryIds);
        } else {
            // 如果没有指定分类ID，则查询所有商品
            products = productService.findByPage(page, pageSize);
            // 获取所有商品的总数量
            totalCount = productService.countAll();
        }

        // ==================== 4. 计算分页信息 ====================
        // 计算总页数，向上取整
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        // 如果总页数为0，则设置为1（避免除以0错误）
        if (totalPages == 0) totalPages = 1;
        // 如果当前页码大于总页数，则设置为总页数
        if (page > totalPages) page = totalPages;

        // ==================== 5. 获取分类树数据 ====================
        // 获取分类树数据，所有用户都可以访问
        List<com.ecommerce.pojo.Category> categoryTree = categoryService.getCategoryTree();
        // 将分类树数据设置到请求属性中
        request.setAttribute("categoryTree", categoryTree);
        
        // ==================== 6. 设置请求属性，转发到JSP ====================
        // 设置商品列表到请求属性
        request.setAttribute("products", products);
        // 设置当前页码到请求属性
        request.setAttribute("currentPage", page);
        // 设置总页数到请求属性
        request.setAttribute("totalPages", totalPages);
        // 设置商品总数量到请求属性
        request.setAttribute("totalCount", totalCount);
        // 设置每页显示数量到请求属性
        request.setAttribute("pageSize", pageSize);
        // 设置当前分类ID到请求属性
        request.setAttribute("categoryId", categoryId);

        // 转发到商品列表页面
        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    /**
     * 商品详情展示方法
     * 根据商品ID查询商品详情并展示
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void productDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ==================== 1. 获取请求参数 ====================
        // 获取商品ID参数
        String idStr = request.getParameter("id");
        
        // ==================== 2. 验证商品ID ====================
        if (idStr != null && !idStr.isEmpty()) {
            // 将商品ID转换为整数
            int productId = Integer.parseInt(idStr);
            // ==================== 3. 查询商品详情 ====================
            // 根据商品ID查询商品详情
            Product product = productService.findById(productId);
            
            // ==================== 4. 处理查询结果 ====================
            if (product != null) {
                // 如果商品存在，将商品详情设置到请求属性中
                request.setAttribute("product", product);
                // 转发到商品详情页面
                request.getRequestDispatcher("/product_detail.jsp").forward(request, response);
            } else {
                // 如果商品不存在，向客户端输出错误信息
                response.getWriter().println("商品不存在！");
            }
        } else {
            // 如果商品ID无效，向客户端输出错误信息
            response.getWriter().println("无效的商品ID！");
        }
    }

    /**
     * 商品搜索方法
     * 根据关键词搜索商品，并分页展示搜索结果
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void searchProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ==================== 1. 获取请求参数 ====================
        // 获取搜索关键词参数
        String keyword = request.getParameter("keyword");
        // 对搜索关键词进行安全处理，防止XSS攻击
        String safeKeyword = ValidationUtils.sanitizeInput(keyword);
        // 如果关键词为空，设置为空字符串
        if (safeKeyword == null) {
            safeKeyword = "";
        }

        // 获取当前页码参数 - 用于分页
        String pageStr = request.getParameter("page");
        // 获取每页显示数量参数 - 用于分页
        String pageSizeStr = request.getParameter("pageSize");

        // ==================== 2. 参数验证和初始化 ====================
        // 初始化当前页码，默认为第1页
        int page = 1;
        // 初始化每页显示数量，默认为12条
        int pageSize = 12;

        // 验证并设置当前页码
        if (pageStr != null && ValidationUtils.isValidPositiveInteger(pageStr)) {
            page = Integer.parseInt(pageStr);
            // 页码不能小于1
            if (page < 1) page = 1;
        }

        // 验证并设置每页显示数量
        if (pageSizeStr != null && ValidationUtils.isValidPositiveInteger(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
            // 每页显示数量必须在1-100之间
            if (pageSize < 1 || pageSize > 100) pageSize = 12;
        }

        // ==================== 3. 查询搜索结果 ====================
        // 根据关键词搜索商品，并进行分页
        List<Product> products = productService.searchByPage(safeKeyword, page, pageSize);
        // 获取搜索结果总数量
        int totalCount = productService.countSearchResults(safeKeyword);
        // 计算总页数，向上取整
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        // 如果总页数为0，则设置为1（避免除以0错误）
        if (totalPages == 0) totalPages = 1;
        // 如果当前页码大于总页数，则设置为总页数
        if (page > totalPages) page = totalPages;

        // ==================== 4. 获取分类树数据 ====================
        // 获取分类树数据，所有用户都可以访问
        List<com.ecommerce.pojo.Category> categoryTree = categoryService.getCategoryTree();
        // 将分类树数据设置到请求属性中
        request.setAttribute("categoryTree", categoryTree);
        
        // ==================== 5. 设置请求属性，转发到JSP ====================
        // 设置商品列表到请求属性
        request.setAttribute("products", products);
        // 设置搜索关键词到请求属性
        request.setAttribute("keyword", safeKeyword);
        // 设置当前页码到请求属性
        request.setAttribute("currentPage", page);
        // 设置总页数到请求属性
        request.setAttribute("totalPages", totalPages);
        // 设置商品总数量到请求属性
        request.setAttribute("totalCount", totalCount);
        // 设置每页显示数量到请求属性
        request.setAttribute("pageSize", pageSize);

        // 转发到商品列表页面（复用列表页面展示搜索结果）
        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }

    /**
     * 商品保存方法
     * 处理商品新增或修改请求，支持文件上传
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void saveProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        
        System.out.println("=== 开始处理商品保存请求 ===");
        
        try {
            // ==================== 1. 获取表单参数 ====================
            // 获取商品名称参数
            String name = request.getParameter("name");
            // 获取分类ID参数
            String categoryIdStr = request.getParameter("categoryId");
            // 获取商品价格参数
            String priceStr = request.getParameter("price");
            // 获取商品库存参数
            String stockStr = request.getParameter("stock");
            // 获取商品描述参数
            String description = request.getParameter("description");
            // 获取商品图片URL参数（用于修改商品时）
            String image = request.getParameter("image");
            
            System.out.println("表单参数：");
            System.out.println("name: " + name);
            System.out.println("categoryIdStr: " + categoryIdStr);
            System.out.println("priceStr: " + priceStr);
            System.out.println("stockStr: " + stockStr);
            System.out.println("description: " + description);
            System.out.println("image: " + image);
            
            // ==================== 2. 表单验证 ====================
            // 验证商品名称不能为空
            if (name == null || name.trim().isEmpty()) {
                out.println("商品名称不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品名称不能为空");
                return;
            }
            
            // 验证商品分类不能为空
            if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
                out.println("商品分类不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品分类不能为空");
                return;
            }
            
            // 验证商品价格不能为空
            if (priceStr == null || priceStr.trim().isEmpty()) {
                out.println("商品价格不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品价格不能为空");
                return;
            }
            
            // 验证商品库存不能为空
            if (stockStr == null || stockStr.trim().isEmpty()) {
                out.println("商品库存不能为空！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品库存不能为空");
                return;
            }
            
            // ==================== 3. 处理文件上传 ====================
            // 获取图片文件上传部分
            Part imagePart = request.getPart("imageFile");
            // 初始化图片URL，默认为原图片URL（用于修改商品时）
            String imageUrl = image;
            
            System.out.println("文件上传信息：");
            System.out.println("imagePart: " + imagePart);
            System.out.println("imagePart.size: " + (imagePart != null ? imagePart.getSize() : 0));
            
            // 如果上传了文件，处理文件上传
            if (imagePart != null && imagePart.getSize() > 0) {
                // 获取上传的文件名
                String submittedFileName = imagePart.getSubmittedFileName();
                System.out.println("上传的文件名：" + submittedFileName);
                
                // 生成唯一文件名，避免文件名冲突
                String fileName = System.currentTimeMillis() + "_" + submittedFileName;
                // 获取文件保存路径
                String uploadPath = request.getServletContext().getRealPath("/images");
                java.io.File uploadDir = new java.io.File(uploadPath);
                System.out.println("文件保存路径：" + uploadPath);
                
                // 如果保存目录不存在，创建目录
                if (!uploadDir.exists()) {
                    boolean mkdirResult = uploadDir.mkdirs();
                    System.out.println("创建目录结果：" + mkdirResult);
                }
                // 构建完整文件路径
                String filePath = uploadPath + java.io.File.separator + fileName;
                System.out.println("完整文件路径：" + filePath);
                
                // 保存文件到服务器
                imagePart.write(filePath);
                System.out.println("文件保存成功");
                
                // 设置图片URL，用于前端展示
                imageUrl = "/images/" + fileName;
                System.out.println("生成的图片URL：" + imageUrl);
            }
            
            // 验证图片URL长度，防止过长URL导致数据库存储问题
            if (imageUrl != null && imageUrl.length() > 1024) {
                out.println("图片URL长度不能超过1024个字符！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：图片URL长度超过1024个字符，当前长度：" + imageUrl.length());
                return;
            }
            
            // ==================== 4. 参数转换 ====================
            // 初始化分类ID
            Integer categoryId = null;
            // 初始化商品价格，使用BigDecimal确保精度
            java.math.BigDecimal price = java.math.BigDecimal.ZERO;
            // 初始化商品库存
            Integer stock = 0;
            
            try {
                // 将分类ID字符串转换为整数
                categoryId = Integer.parseInt(categoryIdStr);
                // 将价格字符串转换为BigDecimal
                price = new java.math.BigDecimal(priceStr);
                // 将库存字符串转换为整数
                stock = Integer.parseInt(stockStr);
                System.out.println("参数转换成功：");
                System.out.println("categoryId: " + categoryId);
                System.out.println("price: " + price);
                System.out.println("stock: " + stock);
            } catch (NumberFormatException e) {
                // 参数格式错误，向客户端输出错误信息
                out.println("参数格式错误！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("参数转换失败：" + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // ==================== 5. 验证价格和库存的合理性 ====================
            // 验证商品价格不能为负数
            if (price.compareTo(java.math.BigDecimal.ZERO) < 0) {
                out.println("商品价格不能为负数！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品价格不能为负数");
                return;
            }
            
            // 验证商品库存不能为负数
            if (stock < 0) {
                out.println("商品库存不能为负数！<a href='" + request.getContextPath() + "/product/add'>返回添加</a>");
                System.out.println("验证失败：商品库存不能为负数");
                return;
            }

            // ==================== 6. 创建商品对象 ====================
            // 创建商品对象
            Product p = new Product();
            // 设置商品名称，去除首尾空格
            p.setName(name.trim());
            // 设置商品分类ID
            p.setCategoryId(categoryId);
            // 设置商品价格
            p.setPrice(price);
            // 设置商品库存
            p.setStock(stock);
            // 设置商品图片URL
            p.setImage(imageUrl);
            // 设置商品描述，去除首尾空格，为空时设置为空字符串
            p.setDescription(description != null ? description.trim() : "");
            
            System.out.println("创建商品对象成功：");
            System.out.println("商品名称: " + p.getName());
            System.out.println("分类ID: " + p.getCategoryId());
            System.out.println("价格: " + p.getPrice());
            System.out.println("库存: " + p.getStock());
            System.out.println("图片URL: " + p.getImage());
            System.out.println("描述: " + p.getDescription());
            
            // ==================== 7. 保存商品 ====================
            System.out.println("调用productService.save()");
            // 调用服务层保存商品
            boolean ok = productService.save(p);
            System.out.println("productService.save()返回结果: " + ok);
            
            // ==================== 8. 处理保存结果 ====================
            if (ok) {
                System.out.println("商品保存成功");
                // 保存成功，重定向到商品列表页面
                // 使用重定向代替直接输出HTML，解决浏览器返回键问题
                response.sendRedirect(request.getContextPath() + "/product/list");
            } else {
                System.out.println("商品保存失败");
                // 保存失败，重定向到添加商品页面，并附带错误信息
                response.sendRedirect(request.getContextPath() + "/product/add?error=save_failed");
            }
        } catch (Exception e) {
            // 捕获所有异常
            e.printStackTrace();
            System.out.println("商品保存过程中发生异常：" + e.getMessage());
            // 发生异常，重定向到添加商品页面，并附带异常信息
            response.sendRedirect(request.getContextPath() + "/product/add?error=exception&message=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } finally {
            System.out.println("=== 商品保存请求处理结束 ===");
        }
    }
    
    /**
     * 显示添加商品页面
     * 获取分类树数据并转发到添加商品页面
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void showAddProductPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ==================== 1. 获取分类树数据 ====================
        // 获取分类树数据，用于在添加商品页面选择分类
        List<com.ecommerce.pojo.Category> categoryTree = categoryService.getCategoryTree();
        
        // ==================== 2. 设置请求属性，转发到JSP ====================
        // 设置分类树为请求属性
        request.setAttribute("categoryTree", categoryTree);
        // 转发到添加商品页面
        request.getRequestDispatcher("/addProduct.jsp").forward(request, response);
    }
    
    /**
     * 删除商品方法
     * 根据商品ID删除商品
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        
        System.out.println("=== 开始处理商品删除请求 ===");
        
        try {
            // ==================== 1. 获取商品ID ====================
            // 获取商品ID参数
            String idStr = request.getParameter("id");
            System.out.println("商品ID: " + idStr);
            
            // ==================== 2. 验证商品ID ====================
            // 验证商品ID是否有效
            if (idStr == null || idStr.trim().isEmpty() || !ValidationUtils.isValidPositiveInteger(idStr)) {
                out.println("无效的商品ID！<a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
                System.out.println("验证失败：无效的商品ID");
                return;
            }
            
            // 将商品ID转换为整数
            int productId = Integer.parseInt(idStr);
            
            // ==================== 3. 调用服务层删除商品 ====================
            System.out.println("调用productService.delete()");
            // 调用服务层删除商品
            boolean ok = productService.delete(productId);
            System.out.println("productService.delete()返回结果: " + ok);
            
            // ==================== 4. 处理删除结果 ====================
            if (ok) {
                // 删除成功，重定向到商品列表页面
                System.out.println("商品删除成功");
                response.sendRedirect(request.getContextPath() + "/product/list");
            } else {
                // 删除失败，向客户端输出错误信息
                out.println("商品删除失败！<a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
                System.out.println("商品删除失败");
            }
        } catch (Exception e) {
            // 捕获所有异常
            e.printStackTrace();
            // 向客户端输出错误信息
            out.println("商品删除失败！错误信息：" + e.getMessage() + "<br><a href='" + request.getContextPath() + "/product/list'>返回商品列表</a>");
            System.out.println("商品删除过程中发生异常：" + e.getMessage());
        } finally {
            System.out.println("=== 商品删除请求处理结束 ===");
        }
    }
}
