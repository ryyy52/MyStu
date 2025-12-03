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
