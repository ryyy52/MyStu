package com.ecommerce.controller;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.dao.OrderDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.UserDao;
import com.ecommerce.dao.impl.CategoryDaoImpl;
import com.ecommerce.dao.impl.OrderDaoImpl;
import com.ecommerce.dao.impl.ProductDaoImpl;
import com.ecommerce.dao.impl.UserDaoImpl;
import com.ecommerce.pojo.Category;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 仪表盘控制器
 */
/**
 * 仪表盘/管理后台控制器 - 显示系统统计信息和概览
 * 
 * 职责：
 * 1. 统计用户、订单、商品等核心数据
 * 2. 计算销售额、库存预警等关键指标
 * 3. 提供数据可视化（图表数据）
 * 4. 为管理员提供系统概览
 * 
 * 主要功能：
 * - 统计用户总数
 * - 统计订单总数和总销售额
 * - 查询低库存商品数量
 * - 生成分类商品统计数据（用于图表展示）
 * - 准备数据转发到仪表盘JSP页面
 * 
 * 特点：
 * - 提供系统关键指标快速了解
 * - 支持数据可视化（ECharts等图表库）
 * - 实时计算各类统计数据
 * - 支持管理员快速定位问题
 * 
 * 工作流程：
 * 1. 接收GET请求到/dashboard
 * 2. 从各DAO查询统计数据
 * 3. 计算分类商品数量（用于柱状图）
 * 4. 设置请求属性（数据传递到JSP）
 * 5. 转发到dashboard.jsp展示
 */
@WebServlet(name = "DashboardController", urlPatterns = "/dashboard")
public class DashboardController extends HttpServlet {
    private UserDao userDao = new UserDaoImpl();
    private OrderDao orderDao = new OrderDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();
    private CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 统计数据
        int userCount = userDao.countAll();
        int orderCount = orderDao.countAll();
        double totalSales = orderDao.getTotalSales();
        int lowStockCount = productDao.countLowStockProducts();

        // 各分类商品数量
        List<Category> categories = categoryDao.findAll();
        StringBuilder categoryNames = new StringBuilder("[");
        StringBuilder productCounts = new StringBuilder("[");
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            int productCount = productDao.countByCategoryId(category.getId());
            
            categoryNames.append("'").append(category.getName()).append("'");
            productCounts.append(productCount);
            
            if (i < categories.size() - 1) {
                categoryNames.append(",");
                productCounts.append(",");
            }
        }
        categoryNames.append("]");
        productCounts.append("]");

        // 设置请求属性
        request.setAttribute("userCount", userCount);
        request.setAttribute("orderCount", orderCount);
        request.setAttribute("totalSales", String.format("%.2f", totalSales));
        request.setAttribute("lowStockCount", lowStockCount);
        request.setAttribute("categoryNames", categoryNames.toString());
        request.setAttribute("productCounts", productCounts.toString());

        // 转发到仪表盘页面
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}
