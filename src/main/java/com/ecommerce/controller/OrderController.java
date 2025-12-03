package com.ecommerce.controller;

import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.impl.OrderServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 订单控制器
 */
public class OrderController extends HttpServlet {
    private OrderService orderService = new OrderServiceImpl();

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
                case "create":
                    createOrder(request, response);
                    break;
                case "list":
                    listOrders(request, response);
                    break;
                case "detail":
                    orderDetail(request, response);
                    break;
                case "cancel":
                    cancelOrder(request, response);
                    break;
                case "pay":
                    payOrder(request, response);
                    break;
                case "ship":
                    shipOrder(request, response);
                    break;
                case "receive":
                    receiveOrder(request, response);
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
     * 创建订单
     */
    private void createOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // 获取收货信息
        String receiverName = request.getParameter("receiverName");
        String receiverPhone = request.getParameter("receiverPhone");
        String receiverAddress = request.getParameter("receiverAddress");
        
        if (receiverName == null || receiverName.trim().isEmpty() || 
            receiverPhone == null || receiverPhone.trim().isEmpty() || 
            receiverAddress == null || receiverAddress.trim().isEmpty()) {
            out.println("请填写完整的收货信息！<a href='" + request.getContextPath() + "/cart/view'>返回购物车</a>");
            return;
        }
        
        // 创建订单
        Order order = orderService.createOrder(user.getId(), receiverAddress, receiverPhone, receiverName);
        
        if (order != null) {
            // 订单创建成功，跳转到订单详情页
            out.println("订单创建成功！<a href='" + request.getContextPath() + "/order/detail?id=" + order.getId() + "'>查看订单详情</a>");
        } else {
            out.println("订单创建失败！购物车可能为空。<a href='" + request.getContextPath() + "/cart/view'>返回购物车</a>");
        }
    }

    /**
     * 订单列表
     */
    private void listOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        List<Order> orders;
        // 管理员查看所有订单，普通用户查看自己的订单
        if ("admin".equals(user.getRole())) {
            orders = orderService.findAll();
        } else {
            orders = orderService.findByUserId(user.getId());
        }
        request.setAttribute("orders", orders);
        
        // 转发到订单列表页面
        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }

    /**
     * 订单详情
     */
    private void orderDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        // 获取订单ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            Integer orderId = Integer.parseInt(idStr);
            Order order = orderService.findById(orderId);
            
            if (order != null) {
                // 转发到订单详情页面
                request.setAttribute("order", order);
                request.getRequestDispatcher("/order_detail.jsp").forward(request, response);
            } else {
                out.println("订单不存在！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 取消订单
     */
    private void cancelOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // 获取订单ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            Integer orderId = Integer.parseInt(idStr);
            boolean success = orderService.cancelOrder(orderId);
            
            if (success) {
                out.println("订单取消成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                out.println("订单取消失败！只能取消待付款的订单。<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 支付订单
     */
    private void payOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // 获取订单ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            Integer orderId = Integer.parseInt(idStr);
            boolean success = orderService.updateOrderStatus(orderId, 1); // 1表示待发货
            
            if (success) {
                out.println("订单支付成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                out.println("订单支付失败！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 发货订单（管理员）
     */
    private void shipOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            out.println("您没有权限执行此操作！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        // 获取订单ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            Integer orderId = Integer.parseInt(idStr);
            boolean success = orderService.updateOrderStatus(orderId, 2); // 2表示待收货
            
            if (success) {
                out.println("订单发货成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                out.println("订单发货失败！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 确认收货
     */
    private void receiveOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // 获取订单ID
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            Integer orderId = Integer.parseInt(idStr);
            boolean success = orderService.updateOrderStatus(orderId, 3); // 3表示已完成
            
            if (success) {
                out.println("订单确认收货成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                out.println("订单确认收货失败！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
}