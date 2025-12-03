package com.ecommerce.controller;

import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;
import com.ecommerce.pojo.Product;
import com.ecommerce.pojo.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import com.ecommerce.service.impl.CartServiceImpl;
import com.ecommerce.service.impl.ProductServiceImpl;
import com.ecommerce.service.impl.UserServiceImpl;
import com.ecommerce.utils.CSRFTokenUtils;
import com.ecommerce.utils.ValidationUtils;
import com.ecommerce.utils.MD5Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 用户控制器
 */
public class UserController extends HttpServlet {
    private UserService userService = new UserServiceImpl();
    private CartService cartService = new CartServiceImpl();
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
        HttpSession session = request.getSession();

        // 获取请求路径
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf("/") + 1);

        try {
            switch (action) {
                case "register":
                    register(request, response);
                    break;
                case "login":
                    login(request, response);
                    break;
                case "logout":
                    logout(request, response);
                    break;
                case "profile":
                    profile(request, response);
                    break;
                case "update":
                    update(request, response);
                    break;
                default:
                    out.println("无效的请求");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "用户操作失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            out.close();
        }
    }

    /**
     * 用户注册
     */
    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 验证CSRF令牌
            String csrfToken = request.getParameter("csrfToken");
            if (!CSRFTokenUtils.validateCSRFToken(request.getSession(), csrfToken)) {
                request.setAttribute("errorMessage", "CSRF验证失败，请重新提交表单");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            String username = ValidationUtils.sanitizeInput(request.getParameter("username"));
            String password = request.getParameter("password");
            String email = ValidationUtils.sanitizeInput(request.getParameter("email"));
            String phone = ValidationUtils.sanitizeInput(request.getParameter("phone"));
            String address = ValidationUtils.sanitizeInput(request.getParameter("address"));

            // 输入验证
            if (!ValidationUtils.isValidUsername(username)) {
                request.setAttribute("errorMessage", "用户名必须是3-20位的字母、数字或下划线");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            if (!ValidationUtils.isValidPassword(password)) {
                request.setAttribute("errorMessage", "密码必须是6-20位的字母、数字或特殊字符");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            if (!ValidationUtils.isValidEmail(email)) {
                request.setAttribute("errorMessage", "邮箱格式不正确");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            if (phone != null && !phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
                request.setAttribute("errorMessage", "手机号格式不正确");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);

            boolean success = userService.register(user);

            if (success) {
                request.setAttribute("successMessage", "注册成功！请登录");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "用户名或邮箱已存在");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "注册失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    /**
     * 用户登录
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 验证CSRF令牌
            String csrfToken = request.getParameter("csrfToken");
            if (!CSRFTokenUtils.validateCSRFToken(request.getSession(), csrfToken)) {
                request.setAttribute("errorMessage", "CSRF验证失败，请重新提交表单");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            String username = ValidationUtils.sanitizeInput(request.getParameter("username"));
            String password = request.getParameter("password");
            String captcha = request.getParameter("captcha");

            HttpSession session = request.getSession();

            // 输入验证 - 临时添加调试信息
            if (!ValidationUtils.isValidUsername(username)) {
                System.out.println("DEBUG: Username validation failed for: " + username);
                request.setAttribute("errorMessage", "用户名格式不正确");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // 暂时注释验证码验证，用于测试登录功能
            /*
            String savedCaptcha = (String) session.getAttribute("captcha");
            if (savedCaptcha == null || captcha == null || !savedCaptcha.equalsIgnoreCase(captcha)) {
                request.setAttribute("errorMessage", "验证码错误");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            */

            // 使用原始密码交由业务层加密并校验 - 临时添加调试信息
            System.out.println("DEBUG: Attempting login for username: " + username);
            System.out.println("DEBUG: Raw password received, length: " + (password == null ? 0 : password.length()));
            User user = userService.login(username, password);
            System.out.println("DEBUG: Login result: " + (user != null ? "SUCCESS" : "FAILED"));

            if (user != null) {
                // 保存用户信息到会话
                session.setAttribute("user", user);
                // 添加调试信息
                System.out.println("DEBUG UserController.login: User saved to session - ID: " + user.getId() + ", Username: " + user.getUsername() + ", Role: " + user.getRole());
                // 记住我
                String remember = request.getParameter("rememberMe");
                if ("on".equalsIgnoreCase(remember)) {
                    com.ecommerce.utils.RememberMeUtils.setRememberMe(response, username, 7L*24*3600);
                } else {
                    com.ecommerce.utils.RememberMeUtils.clear(response);
                }
                
                // 移除CSRF令牌，防止重用
                CSRFTokenUtils.removeCSRFToken(session);
                
                // 合并Session购物车到数据库购物车
                mergeSessionCartToDatabase(session, user);
                
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            } else {
                request.setAttribute("errorMessage", "用户名或密码错误");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "登录失败：" + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * 合并Session购物车到数据库购物车
     */
    private void mergeSessionCartToDatabase(HttpSession session, User user) {
        // 获取Session中的购物车商品
        List<CartItem> sessionCartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (sessionCartItems == null || sessionCartItems.isEmpty()) {
            return;
        }
        
        // 获取或创建用户的数据库购物车
        Cart cart = cartService.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user.getId());
            cartService.save(cart);
            // 重新获取购物车以获取生成的ID
            cart = cartService.findByUserId(user.getId());
        }
        
        // 合并购物车商品
        for (CartItem sessionItem : sessionCartItems) {
            // 验证商品库存
            Product product = productService.findById(sessionItem.getProductId());
            if (product == null || product.getStock() < sessionItem.getQuantity()) {
                continue; // 跳过库存不足的商品
            }
            
            // 添加到数据库购物车
            cartService.addToCart(user.getId(), sessionItem.getProductId(), sessionItem.getQuantity());
        }
        
        // 清空Session购物车
        session.removeAttribute("cartItems");
    }

    /**
     * 用户注销
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate(); // 销毁会话
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    /**
     * 用户个人资料
     */
    private void profile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        if (user != null) {
            out.println("<h1>个人资料</h1>");
            out.println("用户名：" + user.getUsername() + "<br>");
            out.println("邮箱：" + user.getEmail() + "<br>");
            out.println("电话：" + user.getPhone() + "<br>");
            out.println("地址：" + user.getAddress() + "<br>");
            out.println("<a href='" + request.getContextPath() + "/updateProfile.jsp'>修改资料</a>");
        } else {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>点击登录</a>");
        }
    }

    /**
     * 更新用户信息
     */
    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter out = response.getWriter();

        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>点击登录</a>");
            return;
        }

        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        boolean success = userService.update(user);

        if (success) {
            // 更新会话中的用户信息
            session.setAttribute("user", user);
            out.println("信息更新成功！<a href='" + request.getContextPath() + "/user/profile'>返回个人资料</a>");
        } else {
            out.println("信息更新失败！<a href='" + request.getContextPath() + "/updateProfile.jsp'>返回修改</a>");
        }
    }
}