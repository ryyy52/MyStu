package com.ecommerce.controller;

import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;
import com.ecommerce.pojo.Product;
import com.ecommerce.pojo.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.impl.CartServiceImpl;
import com.ecommerce.service.impl.ProductServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车控制器
 * 负责处理购物车相关的HTTP请求，包括查看购物车、添加商品到购物车、
 * 更新购物车商品数量、从购物车删除商品、清空购物车等功能
 * 支持JSON格式和HTML格式的响应，支持登录用户和未登录用户的购物车管理
 */
public class CartController extends HttpServlet {
    // 购物车服务层实例，用于处理购物车业务逻辑
    private CartService cartService = new CartServiceImpl();

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
        // 获取会话对象
        HttpSession session = request.getSession();

        // ==================== 2. 获取请求路径和action ====================
        // 获取请求URI
        String uri = request.getRequestURI();
        // 从URI中提取action参数
        String action = uri.substring(uri.lastIndexOf("/") + 1);

        // ==================== 3. 根据action分发请求 ====================
        try {
            switch (action) {
                case "view":
                    // 查看购物车（HTML格式）
                    viewCart(request, response);
                    break;
                case "add":
                    // 添加商品到购物车（HTML格式）
                    addToCart(request, response);
                    break;
                case "add.json":
                    // 添加商品到购物车（JSON格式）
                    addToCartJson(request, response);
                    break;
                case "update":
                    // 更新购物车商品数量（HTML格式）
                    updateCartItem(request, response);
                    break;
                case "update.json":
                    // 更新购物车商品数量（JSON格式）
                    updateCartItemJson(request, response);
                    break;
                case "remove":
                    // 从购物车删除商品（HTML格式）
                    removeFromCart(request, response);
                    break;
                case "remove.json":
                    // 从购物车删除商品（JSON格式）
                    removeFromCartJson(request, response);
                    break;
                case "view.json":
                    // 查看购物车（JSON格式）
                    viewCartJson(request, response);
                    break;
                case "clear":
                    // 清空购物车
                    clearCart(request, response);
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
     * 查看购物车方法（HTML格式）
     * 根据用户登录状态，从Session或数据库获取购物车数据
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void viewCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 初始化购物车商品列表
        List<CartItem> cartItems = new ArrayList<>();
        // 初始化购物车总价
        double totalPrice = 0;

        // ==================== 1. 根据用户登录状态获取购物车数据 ====================
        if (user == null) {
            // 未登录用户，从Session获取购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems != null) {
                cartItems = sessionCartItems;
                // 计算Session购物车总价
                totalPrice = calculateSessionCartTotalPrice(sessionCartItems);
            }
        } else {
            // 登录用户，从数据库获取购物车
            Cart cart = cartService.findByUserId(user.getId());
            if (cart != null) {
                // 获取购物车商品项列表
                cartItems = cartService.getCartItems(cart.getId());
                // 计算购物车总价
                totalPrice = cartService.calculateTotalPrice(cart.getId());
            }
        }

        // ==================== 2. 设置请求属性，转发到JSP ====================
        // 将购物车商品列表设置到请求属性中
        request.setAttribute("cartItems", cartItems);
        // 将购物车总价设置到请求属性中
        request.setAttribute("totalPrice", totalPrice);
        // 转发到购物车页面
        request.getRequestDispatcher("/cart_view.jsp").forward(request, response);
    }

    /**
     * 计算Session购物车总价
     * @param cartItems 购物车商品项列表
     * @return 购物车总价
     */
    private double calculateSessionCartTotalPrice(List<CartItem> cartItems) {
        // 使用BigDecimal确保价格计算精度
        BigDecimal totalPrice = BigDecimal.ZERO;
        // 遍历购物车商品项，累加每个商品项的总价
        for (CartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getTotalPrice());
        }
        // 将BigDecimal转换为double返回
        return totalPrice.doubleValue();
    }

    /**
     * 添加商品到购物车方法（HTML格式）
     * 根据用户登录状态，将商品添加到Session购物车或数据库购物车
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // ==================== 1. 获取请求参数 ====================
        // 获取商品ID参数
        String productIdStr = request.getParameter("productId");
        // 获取商品数量参数
        String quantityStr = request.getParameter("quantity");

        // 将参数转换为整数
        Integer productId = Integer.parseInt(productIdStr);
        Integer quantity = Integer.parseInt(quantityStr);

        // ==================== 2. 验证商品是否存在 ====================
        ProductService productService = new ProductServiceImpl();
        Product product = productService.findById(productId);
        if (product == null) {
            // 商品不存在，提示用户
            out.println("商品不存在！<a href='../product/list'>继续购物</a>");
            return;
        }

        // ==================== 3. 验证商品库存 ====================
        if (product.getStock() < quantity) {
            // 库存不足，提示用户
            out.println("商品添加失败！库存不足。<a href='../product/list'>继续购物</a>");
            return;
        }

        // ==================== 4. 根据用户登录状态添加商品到购物车 ====================
        if (user == null) {
            // 未登录用户，使用Session存储购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                // 如果Session中没有购物车，创建新的购物车列表
                sessionCartItems = new ArrayList<>();
            }

            // 检查购物车中是否已存在该商品
            boolean itemExists = false;
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(productId)) {
                    // 如果商品已存在，更新商品数量
                    item.setQuantity(item.getQuantity() + quantity);
                    itemExists = true;
                    break;
                }
            }

            // 添加新的购物车商品项
            if (!itemExists) {
                // 创建购物车商品项
                CartItem cartItem = new CartItem();
                cartItem.setProductId(productId);
                cartItem.setProductName(product.getName());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                // 将商品项添加到Session购物车
                sessionCartItems.add(cartItem);
            }

            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            // 重定向到购物车页面
            response.sendRedirect("view");
        } else {
            // 登录用户，使用数据库存储购物车
            boolean success = cartService.addToCart(user.getId(), productId, quantity);

            if (success) {
                // 添加成功，重定向到购物车页面
                response.sendRedirect("view");
            } else {
                // 添加失败，提示用户
                out.println("商品添加失败！库存不足。<a href='../product/list'>继续购物</a>");
            }
        }
    }

    /**
     * 添加商品到购物车方法（JSON格式）
     * 返回JSON格式的响应，用于AJAX请求
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws IOException IO异常
     */
    private void addToCartJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取商品ID参数
        String productIdStr = request.getParameter("productId");
        // 获取商品数量参数
        String quantityStr = request.getParameter("quantity");
        
        // ==================== 1. 验证参数完整性 ====================
        if (productIdStr == null || quantityStr == null) {
            // 参数不完整，返回错误响应
            response.getWriter().write("{\"success\":false,\"message\":\"参数不完整\"}");
            return;
        }
        
        // 将参数转换为整数
        Integer productId = Integer.parseInt(productIdStr);
        Integer quantity = Integer.parseInt(quantityStr);
        
        // ==================== 2. 验证商品是否存在 ====================
        ProductService productService = new ProductServiceImpl();
        Product product = productService.findById(productId);
        if (product == null) {
            // 商品不存在，返回错误响应
            response.getWriter().write("{\"success\":false,\"message\":\"商品不存在\"}");
            return;
        }
        
        // ==================== 3. 根据用户登录状态添加商品到购物车 ====================
        if (user == null) {
            // 未登录用户，使用Session存储购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                // 如果Session中没有购物车，创建新的购物车列表
                sessionCartItems = new ArrayList<>();
            }
            
            // 检查购物车中是否已存在该商品
            boolean exists = false;
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(productId)) {
                    // 如果商品已存在，更新商品数量
                    item.setQuantity(item.getQuantity() + quantity);
                    exists = true;
                    break;
                }
            }
            
            // 添加新的购物车商品项
            if (!exists) {
                // 创建购物车商品项
                CartItem cartItem = new CartItem();
                cartItem.setProductId(productId);
                cartItem.setProductName(product.getName());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                // 将商品项添加到Session购物车
                sessionCartItems.add(cartItem);
            }
            
            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            // 返回成功响应
            response.getWriter().write("{\"success\":true,\"message\":\"已加入购物车\"}");
        } else {
            // 登录用户，使用数据库存储购物车
            boolean ok = cartService.addToCart(user.getId(), productId, quantity);
            // 返回成功或失败响应
            response.getWriter().write(ok ? "{\"success\":true,\"message\":\"已加入购物车\"}" : "{\"success\":false,\"message\":\"库存不足\"}");
        }
    }

    /**
     * 更新购物车商品数量方法（JSON格式）
     * 返回JSON格式的响应，用于AJAX请求
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws IOException IO异常
     */
    private void updateCartItemJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取购物车商品项ID参数
        String cartItemIdStr = request.getParameter("cartItemId");
        // 获取商品ID参数
        String productIdStr = request.getParameter("productId");
        // 获取商品数量参数
        String quantityStr = request.getParameter("quantity");
        
        // ==================== 1. 验证数量参数是否存在 ====================
        if (quantityStr == null) {
            // 数量参数缺失，返回错误响应
            response.getWriter().write("{\"success\":false,\"message\":\"数量缺失\"}");
            return;
        }
        
        // 将数量参数转换为整数
        Integer quantity = Integer.parseInt(quantityStr);
        
        // ==================== 2. 根据用户登录状态更新购物车 ====================
        if (user == null) {
            // 未登录用户，更新Session购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                // 购物车为空，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"购物车为空\"}");
                return;
            }
            
            // 验证商品ID参数是否存在
            if (productIdStr == null) {
                // 缺少商品信息，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"缺少商品信息\"}");
                return;
            }
            
            // 验证商品是否存在和库存是否充足
            ProductService productService = new ProductServiceImpl();
            Product product = productService.findById(Integer.parseInt(productIdStr));
            if (product == null || product.getStock() < quantity) {
                // 商品不存在或库存不足，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"库存不足\"}");
                return;
            }
            
            // 更新Session购物车中的商品数量
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(Integer.parseInt(productIdStr))) {
                    // 更新商品数量
                    item.setQuantity(quantity);
                    break;
                }
            }
            
            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            // 返回成功响应
            response.getWriter().write("{\"success\":true}");
        } else {
            // 登录用户，更新数据库购物车
            // 验证购物车商品项ID参数是否存在
            if (cartItemIdStr == null) {
                // 缺少购物车项，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"缺少购物车项\"}");
                return;
            }
            
            // 更新数据库购物车中的商品数量
            boolean ok = cartService.updateCartItemQuantity(Integer.parseInt(cartItemIdStr), quantity);
            // 返回成功或失败响应
            response.getWriter().write(ok ? "{\"success\":true}" : "{\"success\":false,\"message\":\"库存不足\"}");
        }
    }

    /**
     * 从购物车删除商品方法（JSON格式）
     * 返回JSON格式的响应，用于AJAX请求
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws IOException IO异常
     */
    private void removeFromCartJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取购物车商品项ID参数
        String cartItemIdStr = request.getParameter("cartItemId");
        // 获取商品ID参数
        String productIdStr = request.getParameter("productId");
        
        // ==================== 1. 根据用户登录状态删除购物车商品 ====================
        if (user == null) {
            // 未登录用户，删除Session购物车中的商品
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                // 购物车为空，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"购物车为空\"}");
                return;
            }
            
            // 验证商品ID参数是否存在
            if (productIdStr == null) {
                // 缺少商品信息，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"缺少商品信息\"}");
                return;
            }
            
            // 从Session购物车中删除商品
            sessionCartItems.removeIf(item -> item.getProductId().equals(Integer.parseInt(productIdStr)));
            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            // 返回成功响应
            response.getWriter().write("{\"success\":true}");
        } else {
            // 登录用户，删除数据库购物车中的商品
            // 验证购物车商品项ID参数是否存在
            if (cartItemIdStr == null) {
                // 缺少购物车项，返回错误响应
                response.getWriter().write("{\"success\":false,\"message\":\"缺少购物车项\"}");
                return;
            }
            
            // 从数据库购物车中删除商品
            boolean ok = cartService.removeFromCart(Integer.parseInt(cartItemIdStr));
            // 返回成功或失败响应
            response.getWriter().write(ok ? "{\"success\":true}" : "{\"success\":false}");
        }
    }

    /**
     * 查看购物车方法（JSON格式）
     * 返回JSON格式的购物车数据，用于AJAX请求
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws IOException IO异常
     */
    private void viewCartJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<CartItem> cartItems = new ArrayList<>();
        double totalPrice = 0;
        if (user == null) {
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems != null) {
                cartItems = sessionCartItems;
                totalPrice = calculateSessionCartTotalPrice(sessionCartItems);
            }
        } else {
            Cart cart = cartService.findByUserId(user.getId());
            if (cart != null) {
                cartItems = cartService.getCartItems(cart.getId());
                totalPrice = cartService.calculateTotalPrice(cart.getId());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\"success\":true,\"total\":").append(totalPrice).append(",\"items\":[");
        for (int i=0;i<cartItems.size();i++) {
            CartItem it = cartItems.get(i);
            sb.append("{\"id\":").append(it.getId()==null?"null":it.getId())
              .append(",\"productId\":").append(it.getProductId())
              .append(",\"name\":\"").append(it.getProductName()).append("\"")
              .append(",\"price\":").append(it.getPrice())
              .append(",\"quantity\":").append(it.getQuantity())
              .append("}");
            if (i<cartItems.size()-1) sb.append(",");
        }
        sb.append("]}");
        response.getWriter().write(sb.toString());
    }

    /**
     * 更新购物车商品数量方法（HTML格式）
     * 根据用户登录状态，更新Session购物车或数据库购物车中的商品数量
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // 获取购物车商品项ID参数
        String cartItemIdStr = request.getParameter("cartItemId");
        // 如果cartItemId为空，尝试从itemId参数获取
        if (cartItemIdStr == null || cartItemIdStr.isEmpty()) {
            cartItemIdStr = request.getParameter("itemId");
        }
        // 获取商品ID参数
        String productIdStr = request.getParameter("productId");
        // 获取商品数量参数
        String quantityStr = request.getParameter("quantity");

        // 将数量参数转换为整数
        Integer quantity = Integer.parseInt(quantityStr);

        // ==================== 1. 根据用户登录状态更新购物车 ====================
        if (user == null) {
            // 未登录用户，更新Session购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                // 购物车为空，提示用户
                out.println("购物车为空！<a href='../product/list'>去购物</a>");
                return;
            }

            // 验证商品ID参数是否存在
            if (productIdStr == null || productIdStr.isEmpty()) {
                // 缺少商品信息，提示用户
                out.println("更新失败！缺少商品信息。<a href='view'>返回购物车</a>");
                return;
            }
            
            // 验证商品是否存在和库存是否充足
            ProductService productService = new ProductServiceImpl();
            Product product = productService.findById(Integer.parseInt(productIdStr));
            if (product == null || product.getStock() < quantity) {
                // 商品不存在或库存不足，提示用户
                out.println("更新失败！库存不足。<a href='view'>返回购物车</a>");
                return;
            }

            // 更新Session购物车中的商品数量
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(Integer.parseInt(productIdStr))) {
                    // 更新商品数量
                    item.setQuantity(quantity);
                    break;
                }
            }

            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            // 重定向到购物车页面
            response.sendRedirect("view");
        } else {
            // 登录用户，更新数据库购物车
            Integer cartItemId = Integer.parseInt(cartItemIdStr);
            // 更新数据库购物车中的商品数量
            boolean success = cartService.updateCartItemQuantity(cartItemId, quantity);

            if (success) {
                // 更新成功，重定向到购物车页面
                response.sendRedirect("view");
            } else {
                // 更新失败，提示用户
                out.println("更新失败！库存不足。<a href='view'>返回购物车</a>");
            }
        }
    }

    /**
     * 从购物车删除商品方法（HTML格式）
     * 根据用户登录状态，删除Session购物车或数据库购物车中的商品
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // 获取购物车商品项ID参数
        String cartItemIdStr = request.getParameter("cartItemId");
        // 如果cartItemId为空，尝试从itemId参数获取
        if (cartItemIdStr == null || cartItemIdStr.isEmpty()) {
            cartItemIdStr = request.getParameter("itemId");
        }
        // 获取商品ID参数
        String productIdStr = request.getParameter("productId");

        // ==================== 1. 根据用户登录状态删除购物车商品 ====================
        if (user == null) {
            // 未登录用户，删除Session购物车中的商品
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                // 购物车为空，提示用户
                out.println("购物车为空！<a href='../product/list'>去购物</a>");
                return;
            }

            // 验证商品ID参数是否存在
            if (productIdStr == null || productIdStr.isEmpty()) {
                // 缺少商品信息，提示用户
                out.println("删除失败！缺少商品信息。<a href='view'>返回购物车</a>");
                return;
            }
            // 从Session购物车中删除商品
            sessionCartItems.removeIf(item -> item.getProductId().equals(Integer.parseInt(productIdStr)));

            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            // 重定向到购物车页面
            response.sendRedirect("view");
        } else {
            // 登录用户，删除数据库购物车中的商品
            Integer cartItemId = Integer.parseInt(cartItemIdStr);
            // 从数据库购物车中删除商品
            boolean success = cartService.removeFromCart(cartItemId);

            if (success) {
                // 删除成功，重定向到购物车页面
                response.sendRedirect("view");
            } else {
                // 删除失败，提示用户
                out.println("删除失败！<a href='view'>返回购物车</a>");
            }
        }
    }

    /**
     * 清空购物车方法
     * 根据用户登录状态，清空Session购物车或数据库购物车
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取会话对象
        HttpSession session = request.getSession();
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 获取响应输出流
        PrintWriter out = response.getWriter();

        // ==================== 1. 根据用户登录状态清空购物车 ====================
        if (user == null) {
            // 未登录用户，清空Session购物车
            session.removeAttribute("cartItems");
            // 重定向到购物车页面
            response.sendRedirect("view");
        } else {
            // 登录用户，清空数据库购物车
            Cart cart = cartService.findByUserId(user.getId());
            if (cart == null) {
                // 购物车为空，提示用户
                out.println("购物车为空！<a href='../product/list'>去购物</a>");
                return;
            }

            // 调用服务层清空购物车
            boolean success = cartService.clearCart(cart.getId());

            if (success) {
                // 清空成功，重定向到购物车页面
                response.sendRedirect("view");
            } else {
                // 清空失败，提示用户
                out.println("清空失败！<a href='view'>返回购物车</a>");
            }
        }
    }
}