package com.ecommerce.service.impl;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.OrderDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.impl.CartDaoImpl;
import com.ecommerce.dao.impl.OrderDaoImpl;
import com.ecommerce.dao.impl.ProductDaoImpl;
import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;
import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.OrderItem;
import com.ecommerce.pojo.Product;
import com.ecommerce.service.OrderService;
import com.ecommerce.utils.JDBCUtils;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 订单业务逻辑实现类
 */
public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao = new OrderDaoImpl();
    private CartDao cartDao = new CartDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public Order findById(Integer id) {
        return orderDao.findById(id);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderDao.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        return orderDao.findByUserId(userId);
    }

    @Override
    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderDao.findOrderItemsByOrderId(orderId);
    }

    @Override
    public Order createOrder(Integer userId, String address, String phone, String receiver) {
        Connection conn = null;
        Order order = null;
        try {
            // 开启事务
            JDBCUtils.beginTransaction();
            
            // 获取用户购物车
            Cart cart = cartDao.findByUserId(userId);
            if (cart == null) {
                JDBCUtils.rollbackTransaction();
                return null;
            }

            // 获取购物车商品项
            List<CartItem> cartItems = cartDao.findCartItemsByCartId(cart.getId());
            if (cartItems.isEmpty()) {
                JDBCUtils.rollbackTransaction();
                return null;
            }

            // 计算订单总价
            java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                totalAmount = totalAmount.add(item.getTotalPrice());
            }

            // 生成订单号
            String orderNo = UUID.randomUUID().toString().replace("-", "");

            // 创建订单
            order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setTotalPrice(totalAmount);
            order.setReceiverAddress(address);
            order.setReceiverPhone(phone);
            order.setReceiverName(receiver);
            order.setStatus(0); // 0表示待付款
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());

            // 保存订单
            orderDao.save(order);
            
            // 重新获取订单以获取生成的ID
            order = orderDao.findByOrderNo(orderNo);
            if (order == null) {
                // 保存失败或查询失败，回滚事务
                JDBCUtils.rollbackTransaction();
                return null;
            }

            // 创建订单商品项并更新商品库存
            for (CartItem cartItem : cartItems) {
                // 更新商品库存
                Product product = productDao.findById(cartItem.getProductId());
                if (product != null) {
                    // 检查库存是否足够
                    if (product.getStock() < cartItem.getQuantity()) {
                        JDBCUtils.rollbackTransaction();
                        return null;
                    }
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    productDao.updateStock(product.getId(), product.getStock());
                }

                // 创建订单商品项
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setPrice(cartItem.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setProduct(cartItem.getProduct());
                
                // 保存订单商品项
                orderDao.saveOrderItem(orderItem);
            }

            // 清空购物车
            cartDao.deleteCartItemsByCartId(cart.getId());
            
            // 提交事务
            JDBCUtils.commitTransaction();
            
            return order;
        } catch (SQLException e) {
            // 回滚事务
            try {
                JDBCUtils.rollbackTransaction();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateOrderStatus(Integer orderId, Integer status) {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }

        order.setStatus(status);
        order.setUpdateTime(new Date());
        
        return orderDao.update(order) > 0;
    }

    @Override
    public boolean cancelOrder(Integer orderId) {
        Order order = orderDao.findById(orderId);
        if (order == null || order.getStatus() != 0) { // 只能取消待付款的订单（0表示待付款）
            return false;
        }

        // 更新订单状态为已取消
        order.setStatus(4); // 4表示已取消
        order.setUpdateTime(new Date());
        
        boolean result = orderDao.update(order) > 0;
        if (result) {
            // 恢复商品库存
            List<OrderItem> orderItems = orderDao.findOrderItemsByOrderId(orderId);
            for (OrderItem item : orderItems) {
                Product product = productDao.findById(item.getProductId());
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productDao.updateStock(product.getId(), product.getStock());
                }
            }
        }

        return result;
    }

    @Override
    public boolean deleteOrder(Integer orderId) {
        // 检查订单是否存在
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }

        // 删除订单（OrderDao的delete方法会自动删除订单商品项）
        return orderDao.delete(orderId) > 0;
    }

    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }
}