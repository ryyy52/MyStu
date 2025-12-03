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
 * 负责处理订单相关的HTTP请求，包括订单创建、订单列表查询、订单详情展示、
 * 订单取消、订单支付、订单发货、订单收货等功能
 */
public class OrderController extends HttpServlet {
    // 订单服务层实例，用于处理订单业务逻辑
    private OrderService orderService = new OrderServiceImpl();

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
                case "create":
                    // 创建订单
                    createOrder(request, response);
                    break;
                case "list":
                    // 订单列表查询
                    listOrders(request, response);
                    break;
                case "detail":
                    // 订单详情展示
                    orderDetail(request, response);
                    break;
                case "cancel":
                    // 取消订单
                    cancelOrder(request, response);
                    break;
                case "pay":
                    // 支付订单
                    payOrder(request, response);
                    break;
                case "ship":
                    // 发货订单（管理员）
                    shipOrder(request, response);
                    break;
                case "receive":
                    // 确认收货
                    receiveOrder(request, response);
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
     * 创建订单方法
     * 根据用户购物车内容创建订单，需要填写收货信息
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void createOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        // 获取会话对象
        HttpSession session = request.getSession();
        
        // ==================== 1. 验证用户登录状态 ====================
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 如果用户未登录，提示用户登录
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // ==================== 2. 获取并验证收货信息 ====================
        // 获取收货人姓名
        String receiverName = request.getParameter("receiverName");
        // 获取收货人电话
        String receiverPhone = request.getParameter("receiverPhone");
        // 获取收货地址
        String receiverAddress = request.getParameter("receiverAddress");
        
        // 验证收货信息是否完整
        if (receiverName == null || receiverName.trim().isEmpty() || 
            receiverPhone == null || receiverPhone.trim().isEmpty() || 
            receiverAddress == null || receiverAddress.trim().isEmpty()) {
            out.println("请填写完整的收货信息！<a href='" + request.getContextPath() + "/cart/view'>返回购物车</a>");
            return;
        }
        
        // ==================== 3. 创建订单 ====================
        // 调用服务层创建订单
        Order order = orderService.createOrder(user.getId(), receiverAddress, receiverPhone, receiverName);
        
        // ==================== 4. 处理创建结果 ====================
        if (order != null) {
            // 订单创建成功，跳转到订单详情页
            out.println("订单创建成功！<a href='" + request.getContextPath() + "/order/detail?id=" + order.getId() + "'>查看订单详情</a>");
        } else {
            // 订单创建失败，提示用户
            out.println("订单创建失败！购物车可能为空。<a href='" + request.getContextPath() + "/cart/view'>返回购物车</a>");
        }
    }

    /**
     * 订单列表查询方法
     * 根据用户角色显示不同的订单列表，管理员查看所有订单，普通用户查看自己的订单
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void listOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取会话对象
        HttpSession session = request.getSession();
        
        // ==================== 1. 验证用户登录状态 ====================
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 如果用户未登录，重定向到登录页面
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // ==================== 2. 根据用户角色查询订单 ====================
        List<Order> orders;
        // 管理员查看所有订单，普通用户查看自己的订单
        if ("admin".equals(user.getRole())) {
            // 管理员查询所有订单
            orders = orderService.findAll();
        } else {
            // 普通用户查询自己的订单
            orders = orderService.findByUserId(user.getId());
        }
        
        // ==================== 3. 设置请求属性，转发到JSP ====================
        // 将订单列表设置到请求属性中
        request.setAttribute("orders", orders);
        
        // 转发到订单列表页面
        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }

    /**
     * 订单详情展示方法
     * 根据订单ID查询订单详情并展示
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void orderDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        
        // ==================== 1. 获取订单ID ====================
        // 获取订单ID参数
        String idStr = request.getParameter("id");
        
        // ==================== 2. 验证订单ID ====================
        if (idStr == null || idStr.trim().isEmpty()) {
            // 如果订单ID无效，提示用户
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            // ==================== 3. 查询订单详情 ====================
            // 将订单ID转换为整数
            Integer orderId = Integer.parseInt(idStr);
            // 根据订单ID查询订单详情
            Order order = orderService.findById(orderId);
            
            // ==================== 4. 处理查询结果 ====================
            if (order != null) {
                // 如果订单存在，将订单详情设置到请求属性中
                request.setAttribute("order", order);
                // 转发到订单详情页面
                request.getRequestDispatcher("/order_detail.jsp").forward(request, response);
            } else {
                // 如果订单不存在，提示用户
                out.println("订单不存在！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            // 如果订单ID格式错误，提示用户
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 取消订单方法
     * 根据订单ID取消订单，只有待付款的订单可以取消
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void cancelOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        // 获取会话对象
        HttpSession session = request.getSession();
        
        // ==================== 1. 验证用户登录状态 ====================
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 如果用户未登录，提示用户登录
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // ==================== 2. 获取并验证订单ID ====================
        // 获取订单ID参数
        String idStr = request.getParameter("id");
        // 验证订单ID是否有效
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            // ==================== 3. 取消订单 ====================
            // 将订单ID转换为整数
            Integer orderId = Integer.parseInt(idStr);
            // 调用服务层取消订单
            boolean success = orderService.cancelOrder(orderId);
            
            // ==================== 4. 处理取消结果 ====================
            if (success) {
                // 订单取消成功，提示用户
                out.println("订单取消成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                // 订单取消失败，提示用户
                out.println("订单取消失败！只能取消待付款的订单。<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            // 如果订单ID格式错误，提示用户
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 支付订单方法
     * 根据订单ID更新订单状态为待发货
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void payOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        // 获取会话对象
        HttpSession session = request.getSession();
        
        // ==================== 1. 验证用户登录状态 ====================
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 如果用户未登录，提示用户登录
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // ==================== 2. 获取并验证订单ID ====================
        // 获取订单ID参数
        String idStr = request.getParameter("id");
        // 验证订单ID是否有效
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            // ==================== 3. 支付订单 ====================
            // 将订单ID转换为整数
            Integer orderId = Integer.parseInt(idStr);
            // 调用服务层更新订单状态为待发货（1表示待发货）
            boolean success = orderService.updateOrderStatus(orderId, 1);
            
            // ==================== 4. 处理支付结果 ====================
            if (success) {
                // 订单支付成功，提示用户
                out.println("订单支付成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                // 订单支付失败，提示用户
                out.println("订单支付失败！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            // 如果订单ID格式错误，提示用户
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 发货订单方法（管理员）
     * 根据订单ID更新订单状态为待收货，只有管理员可以执行此操作
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void shipOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        // 获取会话对象
        HttpSession session = request.getSession();
        
        // ==================== 1. 验证用户权限 ====================
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 验证用户是否登录且为管理员
        if (user == null || !"admin".equals(user.getRole())) {
            out.println("您没有权限执行此操作！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        // ==================== 2. 获取并验证订单ID ====================
        // 获取订单ID参数
        String idStr = request.getParameter("id");
        // 验证订单ID是否有效
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            // ==================== 3. 发货订单 ====================
            // 将订单ID转换为整数
            Integer orderId = Integer.parseInt(idStr);
            // 调用服务层更新订单状态为待收货（2表示待收货）
            boolean success = orderService.updateOrderStatus(orderId, 2);
            
            // ==================== 4. 处理发货结果 ====================
            if (success) {
                // 订单发货成功，提示管理员
                out.println("订单发货成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                // 订单发货失败，提示管理员
                out.println("订单发货失败！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            // 如果订单ID格式错误，提示管理员
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
    
    /**
     * 确认收货方法
     * 根据订单ID更新订单状态为已完成
     * @param request  HTTP请求对象，包含客户端请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    private void receiveOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取响应输出流
        PrintWriter out = response.getWriter();
        // 获取会话对象
        HttpSession session = request.getSession();
        
        // ==================== 1. 验证用户登录状态 ====================
        // 获取当前用户
        User user = (User) session.getAttribute("user");
        // 如果用户未登录，提示用户登录
        if (user == null) {
            out.println("请先登录！<a href='" + request.getContextPath() + "/login.jsp'>去登录</a>");
            return;
        }
        
        // ==================== 2. 获取并验证订单ID ====================
        // 获取订单ID参数
        String idStr = request.getParameter("id");
        // 验证订单ID是否有效
        if (idStr == null || idStr.trim().isEmpty()) {
            out.println("无效的订单ID！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            return;
        }
        
        try {
            // ==================== 3. 确认收货 ====================
            // 将订单ID转换为整数
            Integer orderId = Integer.parseInt(idStr);
            // 调用服务层更新订单状态为已完成（3表示已完成）
            boolean success = orderService.updateOrderStatus(orderId, 3);
            
            // ==================== 4. 处理收货结果 ====================
            if (success) {
                // 订单确认收货成功，提示用户
                out.println("订单确认收货成功！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            } else {
                // 订单确认收货失败，提示用户
                out.println("订单确认收货失败！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
            }
        } catch (NumberFormatException e) {
            // 如果订单ID格式错误，提示用户
            out.println("无效的订单ID格式！<a href='" + request.getContextPath() + "/order/list'>返回订单列表</a>");
        }
    }
}