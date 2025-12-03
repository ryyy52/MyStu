package com.ecommerce.filter;

import com.ecommerce.pojo.User;
import com.ecommerce.service.UserService;
import com.ecommerce.service.impl.UserServiceImpl;
import com.ecommerce.utils.RememberMeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 认证授权过滤器 - 处理用户身份验证和权限控制
 * 
 * 职责：
 * 1. 设置HTTP请求和响应的字符编码
 * 2. 实现用户"记住我"自动登录
 * 3. 进行用户身份和权限检查
 * 4. 拦截未授权的请求
 * 5. 实现基于角色的访问控制（RBAC）
 * 
 * 工作流程：
 * 1. 拦截所有HTTP请求
 * 2. 设置编码为UTF-8
 * 3. 尝试从Session获取用户信息
 * 4. 如果用户不存在，检查"记住我"Cookie
 * 5. 验证请求URI是否需要权限
 * 6. 根据用户角色判断是否有访问权限
 * 7. 如无权限，重定向到错误页面
 * 8. 有权限则继续处理请求
 * 
 * 主要功能：
 * - 字符编码设置
 * - 自动登录（"记住我"功能）
 * - 权限验证
 * - 日志记录
 * 
 * 权限控制规则：
 * 管理员权限 (role = "admin"):
 * - /category/* - 分类管理
 * - /product/* - 商品管理
 * - /dashboard - 仪表盘
 * 
 * 普通用户权限 (role = "user"):
 * - /product/list - 商品列表
 * - /product/detail - 商品详情
 * - /cart/* - 购物车操作
 * - /order/* - 订单操作
 * - /user/profile - 个人资料
 * 
 * 匿名用户权限：
 * - /product/list - 商品列表
 * - /product/detail - 商品详情
 * - /user/register - 用户注册
 * - /user/login - 用户登录
 * - / - 首页
 * 
 * 特点：
 * - 支持"记住我"自动登录
 * - 支持基于角色的权限控制
 * - 统一设置字符编码
 * - 提供调试日志
 * 
 * 过滤器链执行：
 * 按配置顺序执行多个过滤器
 * 所有请求都必须经过此过滤器
 * 
 * 配置方式：
 * 在web.xml中配置：
 * <filter>
 *     <filter-name>AuthFilter</filter-name>
 *     <filter-class>com.ecommerce.filter.AuthFilter</filter-class>
 * </filter>
 * <filter-mapping>
 *     <filter-name>AuthFilter</filter-name>
 *     <url-pattern>/*</url-pattern>
 * </filter-mapping>
 */
public class AuthFilter implements Filter {
    private UserService userService = new UserServiceImpl();

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        // 步骤1：设置请求和响应的字符编码为UTF-8
        // 确保所有请求参数和响应内容都使用UTF-8编码
        // 这样可以正确处理中文等多字节字符
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        // 步骤2：获取当前会话
        // true表示如果会话不存在则创建新会话
        HttpSession session = req.getSession(true);
        // 从会话中获取登录用户信息
        User user = (User) session.getAttribute("user");
        
        // 步骤3：处理"记住我"自动登录
        // 如果会话中没有用户信息，则尝试从Cookie恢复
        if (user == null) {
            // 验证并获取"记住我" Cookie中的用户名
            String username = RememberMeUtils.validateAndGetUsername(req);
            if (username != null) {
                // Cookie有效，根据用户名查询用户信息
                User u = userService.findByUsername(username);
                if (u != null) {
                    // 将用户信息保存到会话中
                    session.setAttribute("user", u);
                    user = u;
                    System.out.println("DEBUG AuthFilter: Auto-login successful for user: " + username);
                }
            }
        }
        
        // 步骤4：获取请求的URI用于权限检查
        String requestURI = req.getRequestURI();
        
        // 调试输出：请求URI和当前用户信息
        System.out.println("DEBUG AuthFilter.doFilter: Request URI: " + requestURI);
        System.out.println("DEBUG AuthFilter.doFilter: Session user: " + (user != null ? user.getId() + ", " + user.getUsername() + ", " + user.getRole() : "null"));
        
        // 步骤5：检查请求是否需要管理员权限
        // 以下操作需要管理员权限：
        // - 分类管理：/category/*
        // - 商品添加：/product/add
        // - 商品保存：/product/save
        // - 商品删除：/product/delete
        // - 商品编辑：/product/update 或 /product/edit
        boolean needAdmin = requestURI.contains("/category/") || requestURI.contains("/product/add") || 
                           requestURI.contains("/product/save") || requestURI.contains("/product/delete") ||
                           requestURI.contains("/product/update") || requestURI.contains("/product/edit");
        
        System.out.println("DEBUG AuthFilter.doFilter: Need admin: " + needAdmin);
        
        // 步骤6：执行权限检查
        if (needAdmin) {
            // 检查用户是否登录且拥有管理员角色
            if (user == null || !"admin".equals(user.getRole())) {
                // 非管理员用户或未登录用户尝试访问管理员功能
                // 重定向到首页，不允许访问
                System.out.println("DEBUG AuthFilter.doFilter: Redirecting to index.jsp - Not admin");
                // resp.sendRedirect重定向到指定URL
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
                // return确保不继续处理该请求
                return;
            } else {
                // 用户是管理员，允许访问
                System.out.println("DEBUG AuthFilter.doFilter: Allowing access - Admin user");
            }
        }
        
        // 步骤7：权限检查通过，继续处理请求
        // chain.doFilter()将请求传递给过滤器链中的下一个过滤器或目标资源
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}