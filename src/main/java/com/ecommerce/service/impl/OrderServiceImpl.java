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
 * 订单业务逻辑实现类 - 实现订单业务的核心操作
 * 
 * 职责：
 * 1. 实现OrderService接口定义的所有方法
 * 2. 处理订单的完整业务逻辑
 * 3. 管理订单与购物车的交互
 * 4. 处理订单状态流转
 * 5. 管理订单商品项
 * 
 * 主要功能：
 * - 订单创建（包含购物车转换、库存验证、事务管理）
 * - 订单查询（多种条件）
 * - 订单状态更新
 * - 订单取消（包含库存恢复）
 * - 订单删除
 * - 订单商品项管理
 * 
 * 特点：
 * - 使用JDBC事务确保数据一致性
 * - 实现完整的订单生命周期管理
 * - 与购物车、商品等模块集成
 * - 遵循分层架构设计
 * 
 * 使用场景：
 * - 用户下单流程
 * - 订单管理功能
 * - 订单状态更新
 * - 订单历史记录查询
 */
public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao = new OrderDaoImpl();
    private CartDao cartDao = new CartDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();

    /**
     * 根据ID查询订单
     * <p>调用OrderDao的findById方法获取订单信息</p>
     * @param id 订单ID，唯一标识订单的主键
     * @return 订单对象，包含完整的订单信息
     */
    @Override
    public Order findById(Integer id) {
        return orderDao.findById(id);
    }

    /**
     * 根据订单号查询订单
     * <p>调用OrderDao的findByOrderNo方法获取订单信息</p>
     * @param orderNo 订单号，系统生成的唯一字符串
     * @return 订单对象，包含完整的订单信息
     */
    @Override
    public Order findByOrderNo(String orderNo) {
        return orderDao.findByOrderNo(orderNo);
    }

    /**
     * 根据用户ID查询订单列表
     * <p>调用OrderDao的findByUserId方法获取用户订单列表</p>
     * @param userId 用户ID，关联到订单的用户
     * @return 订单列表，包含该用户的所有订单
     */
    @Override
    public List<Order> findByUserId(Integer userId) {
        return orderDao.findByUserId(userId);
    }

    /**
     * 获取订单中的商品项
     * <p>调用OrderDao的findOrderItemsByOrderId方法获取订单商品项</p>
     * @param orderId 订单ID，关联到订单商品项
     * @return 订单商品项列表，包含订单中的所有商品
     */
    @Override
    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderDao.findOrderItemsByOrderId(orderId);
    }

    /**
     * 创建订单
     * <p>根据用户购物车内容创建新订单，使用事务确保数据一致性</p>
     * @param userId 用户ID，创建订单的用户
     * @param address 收货地址，用户填写的详细收货地址
     * @param phone 联系电话，用户的联系方式
     * @param receiver 收货人，接收商品的人员姓名
     * @return 生成的订单对象，创建失败返回null
     */
    @Override
    public Order createOrder(Integer userId, String address, String phone, String receiver) {
        Connection conn = null;
        Order order = null;
        try {
            // 开启事务，确保所有操作要么全部成功，要么全部失败
            JDBCUtils.beginTransaction();
            
            // 获取用户购物车，检查购物车是否存在
            Cart cart = cartDao.findByUserId(userId);
            if (cart == null) {
                JDBCUtils.rollbackTransaction();
                return null;
            }

            // 获取购物车商品项，检查购物车是否为空
            List<CartItem> cartItems = cartDao.findCartItemsByCartId(cart.getId());
            if (cartItems.isEmpty()) {
                JDBCUtils.rollbackTransaction();
                return null;
            }

            // 计算订单总价，使用BigDecimal确保精度
            java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                totalAmount = totalAmount.add(item.getTotalPrice());
            }

            // 生成唯一订单号，使用UUID确保唯一性
            String orderNo = UUID.randomUUID().toString().replace("-", "");

            // 创建订单对象，设置基本信息
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

            // 保存订单到数据库
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
                // 获取商品信息，检查库存
                Product product = productDao.findById(cartItem.getProductId());
                if (product != null) {
                    // 检查库存是否足够
                    if (product.getStock() < cartItem.getQuantity()) {
                        JDBCUtils.rollbackTransaction();
                        return null;
                    }
                    // 更新商品库存
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
                
                // 保存订单商品项到数据库
                orderDao.saveOrderItem(orderItem);
            }

            // 清空购物车，创建订单后购物车为空
            cartDao.deleteCartItemsByCartId(cart.getId());
            
            // 提交事务，所有操作成功完成
            JDBCUtils.commitTransaction();
            
            return order;
        } catch (SQLException e) {
            // 发生异常，回滚事务
            try {
                JDBCUtils.rollbackTransaction();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新订单状态
     * <p>根据订单ID更新订单状态，同时更新订单修改时间</p>
     * @param orderId 订单ID，需要更新状态的订单
     * @param status 订单状态，新的状态值
     * @return 更新是否成功，true表示更新成功，false表示更新失败
     */
    @Override
    public boolean updateOrderStatus(Integer orderId, Integer status) {
        // 查询订单是否存在
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }

        // 更新订单状态和修改时间
        order.setStatus(status);
        order.setUpdateTime(new Date());
        
        // 调用OrderDao的update方法更新订单
        return orderDao.update(order) > 0;
    }

    /**
     * 取消订单
     * <p>取消指定订单，只有待付款的订单可以取消，取消后恢复商品库存</p>
     * @param orderId 订单ID，需要取消的订单
     * @return 取消是否成功，true表示取消成功，false表示取消失败
     */
    @Override
    public boolean cancelOrder(Integer orderId) {
        // 查询订单是否存在，并且状态为待付款（0表示待付款）
        Order order = orderDao.findById(orderId);
        if (order == null || order.getStatus() != 0) {
            return false;
        }

        // 更新订单状态为已取消（4表示已取消）
        order.setStatus(4);
        order.setUpdateTime(new Date());
        
        // 更新订单状态
        boolean result = orderDao.update(order) > 0;
        
        if (result) {
            // 恢复商品库存，将取消的订单商品数量加回库存
            List<OrderItem> orderItems = orderDao.findOrderItemsByOrderId(orderId);
            for (OrderItem item : orderItems) {
                Product product = productDao.findById(item.getProductId());
                if (product != null) {
                    // 恢复库存
                    product.setStock(product.getStock() + item.getQuantity());
                    productDao.updateStock(product.getId(), product.getStock());
                }
            }
        }

        return result;
    }

    /**
     * 删除订单
     * <p>删除指定订单，同时会自动删除关联的订单商品项</p>
     * @param orderId 订单ID，需要删除的订单
     * @return 删除是否成功，true表示删除成功，false表示删除失败
     */
    @Override
    public boolean deleteOrder(Integer orderId) {
        // 检查订单是否存在
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }

        // 删除订单，OrderDao的delete方法会自动删除关联的订单商品项
        return orderDao.delete(orderId) > 0;
    }

    /**
     * 查询所有订单
     * <p>获取系统中所有订单，按创建时间倒序排列</p>
     * @return 订单列表，包含系统中所有订单
     */
    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }
}