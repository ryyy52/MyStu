package com.ecommerce.filter;

import com.ecommerce.pojo.User;
import com.ecommerce.service.UserService;
import com.ecommerce.service.impl.UserServiceImpl;
import com.ecommerce.utils.RememberMeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {
    private UserService userService = new UserServiceImpl();

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            String username = RememberMeUtils.validateAndGetUsername(req);
            if (username != null) {
                User u = userService.findByUsername(username);
                if (u != null) {
                    req.getSession(true).setAttribute("user", u);
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}