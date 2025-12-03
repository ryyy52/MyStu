package com.ecommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 错误处理控制器
 */
public class ErrorController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleError(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleError(request, response);
    }
    
    private void handleError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应编码
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        // 获取错误代码
        Integer errorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        
        // 获取URL参数中的message
        String urlMessage = request.getParameter("message");
        if (urlMessage != null && !urlMessage.isEmpty()) {
            errorMessage = urlMessage;
        }
        
        // 设置默认错误代码
        if (errorCode == null) {
            errorCode = 403; // 默认权限错误
        }
        
        // 设置错误信息
        request.setAttribute("errorCode", errorCode);
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("showDetails", false); // 生产环境中设置为false
        
        // 记录错误日志
        if (throwable != null) {
            StringBuilder errorDetails = new StringBuilder();
            errorDetails.append("错误URL: ").append(requestUri).append("\n");
            errorDetails.append("错误信息: ").append(throwable.getMessage()).append("\n");
            
            // 获取堆栈跟踪
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            for (int i = 0; i < Math.min(10, stackTrace.length); i++) {
                errorDetails.append(stackTrace[i].toString()).append("\n");
            }
            
            request.setAttribute("errorDetails", errorDetails.toString());
            
            // 记录到控制台（实际项目中应该使用日志框架）
            System.err.println("系统错误发生:");
            System.err.println("错误代码: " + errorCode);
            System.err.println("错误URL: " + requestUri);
            System.err.println("错误信息: " + errorMessage);
            throwable.printStackTrace();
        }
        
        // 转发到错误页面
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }
}