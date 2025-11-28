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

public class AuthFilter implements Filter {
    private UserService userService = new UserServiceImpl();

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        // 设置请求和响应编码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        // 获取当前会话
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // 自动登录处理
        if (user == null) {
            String username = RememberMeUtils.validateAndGetUsername(req);
            if (username != null) {
                User u = userService.findByUsername(username);
                if (u != null) {
                    session.setAttribute("user", u);
                    user = u;
                }
            }
        }
        
        // 角色权限控制
        String requestURI = req.getRequestURI();
        
        // 添加调试信息
        System.out.println("DEBUG AuthFilter.doFilter: Request URI: " + requestURI);
        System.out.println("DEBUG AuthFilter.doFilter: Session user: " + (user != null ? user.getId() + ", " + user.getUsername() + ", " + user.getRole() : "null"));
        
        // 需要管理员权限的路径
        boolean needAdmin = requestURI.contains("/category/") || requestURI.contains("/product/add") || 
                           requestURI.contains("/product/save") || requestURI.contains("/product/delete") ||
                           requestURI.contains("/product/update") || requestURI.contains("/product/edit");
        
        System.out.println("DEBUG AuthFilter.doFilter: Need admin: " + needAdmin);
        
        // 检查用户是否有权限
        if (needAdmin) {
            // 只有管理员可以访问管理功能
            if (user == null || !"admin".equals(user.getRole())) {
                // 非管理员用户尝试访问管理员功能，重定向到首页
                System.out.println("DEBUG AuthFilter.doFilter: Redirecting to index.jsp - Not admin");
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
                return;
            } else {
                System.out.println("DEBUG AuthFilter.doFilter: Allowing access - Admin user");
            }
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}