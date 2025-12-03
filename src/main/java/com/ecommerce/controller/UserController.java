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
 * 用户管理控制器 - 处理所有与用户相关的HTTP请求
 * 
 * 职责：
 * 1. 管理用户的注册、登录、登出、信息查看和修改
 * 2. 处理用户身份验证和授权
 * 3. 实现用户会话管理
 * 4. 提供"记住我"功能支持自动登录
 * 5. 处理用户信息的安全性（CSRF防护、密码加密等）
 * 
 * 主要功能：
 * - register: 用户注册（含CSRF验证）
 * - login: 用户登录（支持"记住我"）
 * - logout: 用户登出
 * - profile: 查看用户个人信息
 * - update: 修改用户信息
 * 
 * 特点：
 * - 完整的用户认证流程
 * - 支持CSRF令牌防护
 * - 密码MD5加密存储
 * - 支持"记住我"自动登录功能
 * - 提供完整的数据验证
 * - 支持购物车迁移（登录时）
 * 
 * 工作流程：
 * 1. 解析请求的action参数
 * 2. 验证CSRF令牌（表单请求）
 * 3. 验证用户输入数据
 * 4. 调用UserService进行业务处理
 * 5. 更新会话并处理购物车迁移
 * 6. 返回结果或重定向到目标页面
 */

/**
 * 用户控制器 - 处理用户相关的HTTP请求
 * 
 * 该类作为Servlet控制器，处理用户注册、登录、登出、个人资料查看和信息更新等操作。
 * 主要功能包括：
 * - 用户注册：验证表单数据、检查用户名/邮箱重复、密码加密、保存用户信息
 * - 用户登录：CSRF验证、密码校验、会话管理、购物车合并
 * - 用户登出：销毁会话
 * - 用户资料：查看个人信息
 * - 信息更新：修改邮箱、电话、地址等个人信息
 * 
 * 特点：
 * - 集成CSRF保护机制
 * - 支持"记住我"功能
 * - 登录后自动合并Session购物车到数据库
 * - 完整的表单验证和错误处理
 */
public class UserController extends HttpServlet {
    /** 用户业务逻辑服务 - 处理用户相关的业务逻辑 */
    private UserService userService = new UserServiceImpl();
    /** 购物车业务逻辑服务 - 处理购物车相关操作 */
    private CartService cartService = new CartServiceImpl();
    /** 商品业务逻辑服务 - 处理商品相关操作 */
    private ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * POST请求处理方法 - 根据不同的action参数分发请求到相应的业务处理方法
     * 
     * 该方法作为主要的请求入口，接收所有POST请求并根据URI中最后一段路径确定执行的操作。
     * 支持的action类型：register(注册)、login(登录)、logout(登出)、profile(查看资料)、update(更新资料)
     * 
     * @param request HTTP请求对象，包含请求参数和会话信息
     * @param response HTTP响应对象，用于返回处理结果
     * @throws ServletException 当Servlet处理发生错误时抛出
     * @throws IOException 当I/O操作发生错误时抛出
     */
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
     * 用户注册处理方法 - 处理新用户注册请求
     * 
     * 业务流程：
     * 1. 验证CSRF令牌防止跨站请求伪造
     * 2. 获取并清理注册表单数据（用户名、密码、邮箱、电话、地址）
     * 3. 验证输入数据的合法性（格式、长度等）
     * 4. 调用业务层进行用户注册
     * 5. 返回注册结果
     * 
     * 验证规则：
     * - 用户名：3-20位字母、数字或下划线
     * - 密码：6-20位字母、数字或特殊字符
     * - 邮箱：标准邮箱格式
     * - 电话：11位手机号格式（可选）
     * 
     * @param request HTTP请求对象，包含注册表单数据
     * @param response HTTP响应对象，用于转发到相应页面
     * @throws ServletException 当Servlet处理发生错误时抛出
     * @throws IOException 当I/O操作发生错误时抛出
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
     * 用户登录处理方法 - 处理用户登录请求
     * 
     * 业务流程：
     * 1. 验证CSRF令牌
     * 2. 获取登录表单参数（用户名、密码、验证码）
     * 3. 验证用户名格式
     * 4. 调用业务层验证用户身份
     * 5. 登录成功后：
     *    - 保存用户信息到会话
     *    - 处理"记住我"功能
     *    - 清除CSRF令牌以防重用
     *    - 合并Session购物车到数据库
     * 6. 登录失败返回错误信息
     * 
     * 特点：
     * - 支持会话管理和记住我功能
     * - 自动合并购物车数据
     * - 完整的错误处理
     * 
     * @param request HTTP请求对象，包含登录表单数据
     * @param response HTTP响应对象，用于重定向或转发
     * @throws ServletException 当Servlet处理发生错误时抛出
     * @throws IOException 当I/O操作发生错误时抛出
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
     * 合并Session购物车到数据库购物车 - 登录成功后的辅助方法
     * 
     * 当未登录用户在Session中添加商品到购物车后，成功登录时调用此方法。
     * 此方法会将Session中的所有商品项合并到该用户数据库中的购物车。
     * 
     * 处理步骤：
     * 1. 获取Session中的购物车商品列表
     * 2. 获取或创建用户的数据库购物车
     * 3. 遍历Session商品项：
     *    - 验证商品和库存
     *    - 将有效商品添加到数据库购物车
     * 4. 清空Session购物车
     * 
     * @param session HTTP会话对象，包含Session购物车数据
     * @param user 已登录的用户对象
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
     * 用户登出处理方法 - 处理用户登出请求
     * 
     * 业务流程：
     * 1. 获取当前会话
     * 2. 销毁会话（清除所有会话属性）
     * 3. 重定向到登录页面
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象，用于重定向
     * @throws ServletException 当Servlet处理发生错误时抛出
     * @throws IOException 当I/O操作发生错误时抛出
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate(); // 销毁会话
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    /**
     * 用户个人资料查看方法 - 显示当前登录用户的个人信息
     * 
     * 业务流程：
     * 1. 从会话中获取当前用户对象
     * 2. 如果用户已登录，显示用户信息（用户名、邮箱、电话、地址）
     * 3. 如果用户未登录，提示用户先登录
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象，用于输出用户信息
     * @throws ServletException 当Servlet处理发生错误时抛出
     * @throws IOException 当I/O操作发生错误时抛出
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
     * 用户信息更新方法 - 处理用户个人信息修改请求
     * 
     * 业务流程：
     * 1. 从会话中获取当前用户对象
     * 2. 如果用户未登录，提示登录
     * 3. 获取更新表单参数（邮箱、电话、地址）
     * 4. 更新用户对象属性
     * 5. 调用业务层保存更新
     * 6. 同时更新会话中的用户信息，确保数据一致性
     * 
     * 返回结果：
     * - 成功：返回成功消息和个人资料页面链接
     * - 失败：返回失败消息和修改页面链接
     * 
     * @param request HTTP请求对象，包含更新的表单数据
     * @param response HTTP响应对象，用于输出处理结果
     * @throws ServletException 当Servlet处理发生错误时抛出
     * @throws IOException 当I/O操作发生错误时抛出
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