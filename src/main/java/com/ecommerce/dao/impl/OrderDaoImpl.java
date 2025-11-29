package com.ecommerce.dao.impl;

import com.ecommerce.dao.OrderDao;
import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.OrderItem;
import com.ecommerce.pojo.Product;
import com.ecommerce.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问实现类
 */
public class OrderDaoImpl implements OrderDao {
    private static final String FIND_BY_ID = "SELECT id, order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, create_time, update_time FROM `order` WHERE id = ?";
    private static final String FIND_BY_ORDER_NO = "SELECT id, order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, create_time, update_time FROM `order` WHERE order_no = ?";
    private static final String FIND_BY_USER_ID = "SELECT id, order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, create_time, update_time FROM `order` WHERE user_id = ? ORDER BY create_time DESC";
    private static final String FIND_ALL = "SELECT id, order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, create_time, update_time FROM `order` ORDER BY create_time DESC";
    private static final String SAVE = "INSERT INTO `order` (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE `order` SET status = ?, receiver_name = ?, receiver_phone = ?, receiver_address = ?, update_time = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM `order` WHERE id = ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM `order`";
    private static final String GET_TOTAL_SALES = "SELECT COALESCE(SUM(total_amount), 0) FROM `order` WHERE status = 2";
    
    private static final String FIND_ORDER_ITEMS_BY_ORDER_ID = "SELECT id, order_id, product_id, quantity, price FROM order_item WHERE order_id = ?";
    private static final String SAVE_ORDER_ITEM = "INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
    
    // 优化N+1查询：使用JOIN一次性查询订单和订单项
    private static final String FIND_ORDERS_WITH_ITEMS = "SELECT o.id as order_id, o.order_no, o.user_id, o.total_amount, o.status, o.receiver_name, o.receiver_phone, o.receiver_address, o.create_time, o.update_time, " +
            "oi.id as item_id, oi.product_id, oi.quantity, oi.price, " +
            "p.name as product_name, p.image as product_image " +
            "FROM `order` o " +
            "LEFT JOIN order_item oi ON o.id = oi.order_id " +
            "LEFT JOIN product p ON oi.product_id = p.id " +
            "WHERE o.user_id = ? " +
            "ORDER BY o.create_time DESC";
    
    private static final String FIND_ALL_ORDERS_WITH_ITEMS = "SELECT o.id as order_id, o.order_no, o.user_id, o.total_amount, o.status, o.receiver_name, o.receiver_phone, o.receiver_address, o.create_time, o.update_time, " +
            "oi.id as item_id, oi.product_id, oi.quantity, oi.price, " +
            "p.name as product_name, p.image as product_image " +
            "FROM `order` o " +
            "LEFT JOIN order_item oi ON o.id = oi.order_id " +
            "LEFT JOIN product p ON oi.product_id = p.id " +
            "ORDER BY o.create_time DESC";

    @Override
    public Order findById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Order order = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getInt("status"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setCreateTime(rs.getTimestamp("create_time"));
                order.setUpdateTime(rs.getTimestamp("update_time"));
                // 加载订单商品项
                List<OrderItem> orderItems = findOrderItemsByOrderId(order.getId());
                order.setOrderItems(orderItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return order;
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Order order = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_ORDER_NO);
            ps.setString(1, orderNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getInt("status"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setReceiverAddress(rs.getString("receiver_address"));
                order.setCreateTime(rs.getTimestamp("create_time"));
                order.setUpdateTime(rs.getTimestamp("update_time"));
                // 加载订单商品项
                List<OrderItem> orderItems = findOrderItemsByOrderId(order.getId());
                order.setOrderItems(orderItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return order;
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_ORDERS_WITH_ITEMS);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            // 使用Map缓存订单对象，避免重复创建
            java.util.Map<Integer, Order> orderMap = new java.util.HashMap<>();
            
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Order order = orderMap.get(orderId);
                
                // 如果订单不存在，创建新订单对象
                if (order == null) {
                    order = new Order();
                    order.setId(orderId);
                    order.setOrderNo(rs.getString("order_no"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTotalPrice(rs.getBigDecimal("total_amount"));
                    order.setStatus(rs.getInt("status"));
                    order.setReceiverName(rs.getString("receiver_name"));
                    order.setReceiverPhone(rs.getString("receiver_phone"));
                    order.setReceiverAddress(rs.getString("receiver_address"));
                    order.setCreateTime(rs.getTimestamp("create_time"));
                    order.setUpdateTime(rs.getTimestamp("update_time"));
                    order.setOrderItems(new ArrayList<>());
                    orderMap.put(orderId, order);
                    orders.add(order);
                }
                
                // 获取订单项数据
                int itemId = rs.getInt("item_id");
                // 如果有订单项数据（LEFT JOIN可能返回null）
                if (!rs.wasNull()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(itemId);
                    orderItem.setOrderId(orderId);
                    orderItem.setProductId(rs.getInt("product_id"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setPrice(rs.getBigDecimal("price"));
                    
                    // 创建商品对象
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("product_name"));
                    product.setImage(rs.getString("product_image"));
                    orderItem.setProduct(product);
                    
                    // 添加到订单的订单项列表
                    order.getOrderItems().add(orderItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return orders;
    }

    @Override
    public List<Order> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_ALL_ORDERS_WITH_ITEMS);
            rs = ps.executeQuery();
            
            // 使用Map缓存订单对象，避免重复创建
            java.util.Map<Integer, Order> orderMap = new java.util.HashMap<>();
            
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Order order = orderMap.get(orderId);
                
                // 如果订单不存在，创建新订单对象
                if (order == null) {
                    order = new Order();
                    order.setId(orderId);
                    order.setOrderNo(rs.getString("order_no"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTotalPrice(rs.getBigDecimal("total_amount"));
                    order.setStatus(rs.getInt("status"));
                    order.setReceiverName(rs.getString("receiver_name"));
                    order.setReceiverPhone(rs.getString("receiver_phone"));
                    order.setReceiverAddress(rs.getString("receiver_address"));
                    order.setCreateTime(rs.getTimestamp("create_time"));
                    order.setUpdateTime(rs.getTimestamp("update_time"));
                    order.setOrderItems(new ArrayList<>());
                    orderMap.put(orderId, order);
                    orders.add(order);
                }
                
                // 获取订单项数据
                int itemId = rs.getInt("item_id");
                // 如果有订单项数据（LEFT JOIN可能返回null）
                if (!rs.wasNull()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(itemId);
                    orderItem.setOrderId(orderId);
                    orderItem.setProductId(rs.getInt("product_id"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setPrice(rs.getBigDecimal("price"));
                    
                    // 创建商品对象
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("product_name"));
                    product.setImage(rs.getString("product_image"));
                    orderItem.setProduct(product);
                    
                    // 添加到订单的订单项列表
                    order.getOrderItems().add(orderItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return orders;
    }

    @Override
    public int save(Order order) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE);
            ps.setString(1, order.getOrderNo());
            ps.setInt(2, order.getUserId());
            ps.setBigDecimal(3, order.getTotalPrice());
            ps.setInt(4, order.getStatus());
            ps.setString(5, order.getReceiverName());
            ps.setString(6, order.getReceiverPhone());
            ps.setString(7, order.getReceiverAddress());
            ps.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int update(Order order) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setInt(1, order.getStatus());
            ps.setString(2, order.getReceiverName());
            ps.setString(3, order.getReceiverPhone());
            ps.setString(4, order.getReceiverAddress());
            ps.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(6, order.getId());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int delete(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            // 先删除订单商品项
            ps = conn.prepareStatement("DELETE FROM order_item WHERE order_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            // 再删除订单
            ps = conn.prepareStatement(DELETE);
            ps.setInt(1, id);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public List<OrderItem> findOrderItemsByOrderId(Integer orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_ORDER_ITEMS_BY_ORDER_ID);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(rs.getInt("id"));
                orderItem.setOrderId(rs.getInt("order_id"));
                orderItem.setProductId(rs.getInt("product_id"));
                orderItem.setQuantity(rs.getInt("quantity"));
                orderItem.setPrice(rs.getBigDecimal("price"));
                // 加载商品信息
                ProductDaoImpl productDao = new ProductDaoImpl();
                Product product = productDao.findById(orderItem.getProductId());
                orderItem.setProduct(product);
                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return orderItems;
    }

    @Override
    public int saveOrderItem(OrderItem orderItem) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE_ORDER_ITEM);
            ps.setInt(1, orderItem.getOrderId());
            ps.setInt(2, orderItem.getProductId());
            ps.setInt(3, orderItem.getQuantity());
            ps.setBigDecimal(4, orderItem.getPrice());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int countAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(COUNT_ALL);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return count;
    }

    @Override
    public double getTotalSales() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double totalSales = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(GET_TOTAL_SALES);
            rs = ps.executeQuery();
            if (rs.next()) {
                totalSales = rs.getBigDecimal(1).doubleValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return totalSales;
    }
}