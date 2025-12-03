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
 */
public class CartController extends HttpServlet {
    private CartService cartService = new CartServiceImpl();

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
        HttpSession session = request.getSession();

        // 获取请求路径
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/") + 1);

        try {
            switch (action) {
                case "view":
                    viewCart(request, response);
                    break;
                case "add":
                    addToCart(request, response);
                    break;
                case "add.json":
                    addToCartJson(request, response);
                    break;
                case "update":
                    updateCartItem(request, response);
                    break;
                case "update.json":
                    updateCartItemJson(request, response);
                    break;
                case "remove":
                    removeFromCart(request, response);
                    break;
                case "remove.json":
                    removeFromCartJson(request, response);
                    break;
                case "view.json":
                    viewCartJson(request, response);
                    break;
                case "clear":
                    clearCart(request, response);
                    break;
                default:
                    out.println("无效的请求");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("系统错误：" + e.getMessage());
        } finally {
            out.close();
        }
    }

    /**
     * 查看购物车
     */
    private void viewCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<CartItem> cartItems = new ArrayList<>();
        double totalPrice = 0;

        if (user == null) {
            // 未登录用户，从Session获取购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems != null) {
                cartItems = sessionCartItems;
                totalPrice = calculateSessionCartTotalPrice(sessionCartItems);
            }
        } else {
            // 登录用户，从数据库获取购物车
            Cart cart = cartService.findByUserId(user.getId());
            if (cart != null) {
                cartItems = cartService.getCartItems(cart.getId());
                totalPrice = cartService.calculateTotalPrice(cart.getId());
            }
        }

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("totalPrice", totalPrice);
        request.getRequestDispatcher("/cart_view.jsp").forward(request, response);
    }

    /**
     * 计算Session购物车总价
     */
    private double calculateSessionCartTotalPrice(List<CartItem> cartItems) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getTotalPrice());
        }
        return totalPrice.doubleValue();
    }

    /**
     * 添加商品到购物车
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");

        Integer productId = Integer.parseInt(productIdStr);
        Integer quantity = Integer.parseInt(quantityStr);

        // 验证商品是否存在
        ProductService productService = new ProductServiceImpl();
        Product product = productService.findById(productId);
        if (product == null) {
            out.println("商品不存在！<a href='../product/list'>继续购物</a>");
            return;
        }

        // 验证商品库存
        if (product.getStock() < quantity) {
            out.println("商品添加失败！库存不足。<a href='../product/list'>继续购物</a>");
            return;
        }

        if (user == null) {
            // 未登录用户，使用Session存储购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                sessionCartItems = new ArrayList<>();
            }

            // 检查购物车中是否已存在该商品
            boolean itemExists = false;
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(productId)) {
                    // 更新商品数量
                    item.setQuantity(item.getQuantity() + quantity);
                    itemExists = true;
                    break;
                }
            }

            // 添加新的购物车商品项
            if (!itemExists) {
                CartItem cartItem = new CartItem();
                cartItem.setProductId(productId);
                cartItem.setProductName(product.getName());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                sessionCartItems.add(cartItem);
            }

            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            response.sendRedirect("view");
        } else {
            // 登录用户，使用数据库存储购物车
            boolean success = cartService.addToCart(user.getId(), productId, quantity);

            if (success) {
                response.sendRedirect("view");
            } else {
                out.println("商品添加失败！库存不足。<a href='../product/list'>继续购物</a>");
            }
        }
    }

    private void addToCartJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");
        if (productIdStr == null || quantityStr == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"参数不完整\"}");
            return;
        }
        Integer productId = Integer.parseInt(productIdStr);
        Integer quantity = Integer.parseInt(quantityStr);
        ProductService productService = new ProductServiceImpl();
        Product product = productService.findById(productId);
        if (product == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"商品不存在\"}");
            return;
        }
        if (user == null) {
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) sessionCartItems = new ArrayList<>();
            boolean exists = false;
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(productId)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                CartItem cartItem = new CartItem();
                cartItem.setProductId(productId);
                cartItem.setProductName(product.getName());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                sessionCartItems.add(cartItem);
            }
            session.setAttribute("cartItems", sessionCartItems);
            response.getWriter().write("{\"success\":true,\"message\":\"已加入购物车\"}");
        } else {
            boolean ok = cartService.addToCart(user.getId(), productId, quantity);
            response.getWriter().write(ok ? "{\"success\":true,\"message\":\"已加入购物车\"}" : "{\"success\":false,\"message\":\"库存不足\"}");
        }
    }

    private void updateCartItemJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String cartItemIdStr = request.getParameter("cartItemId");
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");
        if (quantityStr == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"数量缺失\"}");
            return;
        }
        Integer quantity = Integer.parseInt(quantityStr);
        if (user == null) {
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"购物车为空\"}");
                return;
            }
            if (productIdStr == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"缺少商品信息\"}");
                return;
            }
            ProductService productService = new ProductServiceImpl();
            Product product = productService.findById(Integer.parseInt(productIdStr));
            if (product == null || product.getStock() < quantity) {
                response.getWriter().write("{\"success\":false,\"message\":\"库存不足\"}");
                return;
            }
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(Integer.parseInt(productIdStr))) {
                    item.setQuantity(quantity);
                    break;
                }
            }
            session.setAttribute("cartItems", sessionCartItems);
            response.getWriter().write("{\"success\":true}");
        } else {
            if (cartItemIdStr == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"缺少购物车项\"}");
                return;
            }
            boolean ok = cartService.updateCartItemQuantity(Integer.parseInt(cartItemIdStr), quantity);
            response.getWriter().write(ok ? "{\"success\":true}" : "{\"success\":false,\"message\":\"库存不足\"}");
        }
    }

    private void removeFromCartJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String cartItemIdStr = request.getParameter("cartItemId");
        String productIdStr = request.getParameter("productId");
        if (user == null) {
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"购物车为空\"}");
                return;
            }
            if (productIdStr == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"缺少商品信息\"}");
                return;
            }
            sessionCartItems.removeIf(item -> item.getProductId().equals(Integer.parseInt(productIdStr)));
            session.setAttribute("cartItems", sessionCartItems);
            response.getWriter().write("{\"success\":true}");
        } else {
            if (cartItemIdStr == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"缺少购物车项\"}");
                return;
            }
            boolean ok = cartService.removeFromCart(Integer.parseInt(cartItemIdStr));
            response.getWriter().write(ok ? "{\"success\":true}" : "{\"success\":false}");
        }
    }

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
     * 更新购物车商品数量
     */
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        String cartItemIdStr = request.getParameter("cartItemId");
        if (cartItemIdStr == null || cartItemIdStr.isEmpty()) {
            cartItemIdStr = request.getParameter("itemId");
        }
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");

        Integer quantity = Integer.parseInt(quantityStr);

        if (user == null) {
            // 未登录用户，更新Session购物车
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                out.println("购物车为空！<a href='../product/list'>去购物</a>");
                return;
            }

            // 验证商品库存
            if (productIdStr == null || productIdStr.isEmpty()) {
                out.println("更新失败！缺少商品信息。<a href='view'>返回购物车</a>");
                return;
            }
            ProductService productService = new ProductServiceImpl();
            Product product = productService.findById(Integer.parseInt(productIdStr));
            if (product == null || product.getStock() < quantity) {
                out.println("更新失败！库存不足。<a href='view'>返回购物车</a>");
                return;
            }

            // 更新商品数量
            for (CartItem item : sessionCartItems) {
                if (item.getProductId().equals(Integer.parseInt(productIdStr))) {
                    item.setQuantity(quantity);
                    break;
                }
            }

            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            response.sendRedirect("view");
        } else {
            // 登录用户，更新数据库购物车
            Integer cartItemId = Integer.parseInt(cartItemIdStr);
            boolean success = cartService.updateCartItemQuantity(cartItemId, quantity);

            if (success) {
                response.sendRedirect("view");
            } else {
                out.println("更新失败！库存不足。<a href='view'>返回购物车</a>");
            }
        }
    }

    /**
     * 从购物车删除商品
     */
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        String cartItemIdStr = request.getParameter("cartItemId");
        if (cartItemIdStr == null || cartItemIdStr.isEmpty()) {
            cartItemIdStr = request.getParameter("itemId");
        }
        String productIdStr = request.getParameter("productId");

        if (user == null) {
            // 未登录用户，删除Session购物车中的商品
            List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (sessionCartItems == null) {
                out.println("购物车为空！<a href='../product/list'>去购物</a>");
                return;
            }

            // 删除商品
            if (productIdStr == null || productIdStr.isEmpty()) {
                out.println("删除失败！缺少商品信息。<a href='view'>返回购物车</a>");
                return;
            }
            sessionCartItems.removeIf(item -> item.getProductId().equals(Integer.parseInt(productIdStr)));

            // 更新Session中的购物车
            session.setAttribute("cartItems", sessionCartItems);
            response.sendRedirect("view");
        } else {
            // 登录用户，删除数据库购物车中的商品
            Integer cartItemId = Integer.parseInt(cartItemIdStr);
            boolean success = cartService.removeFromCart(cartItemId);

            if (success) {
                response.sendRedirect("view");
            } else {
                out.println("删除失败！<a href='view'>返回购物车</a>");
            }
        }
    }

    /**
     * 清空购物车
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        if (user == null) {
            // 未登录用户，清空Session购物车
            session.removeAttribute("cartItems");
            response.sendRedirect("view");
        } else {
            // 登录用户，清空数据库购物车
            Cart cart = cartService.findByUserId(user.getId());
            if (cart == null) {
                out.println("购物车为空！<a href='../product/list'>去购物</a>");
                return;
            }

            boolean success = cartService.clearCart(cart.getId());

            if (success) {
                response.sendRedirect("view");
            } else {
                out.println("清空失败！<a href='view'>返回购物车</a>");
            }
        }
    }
}