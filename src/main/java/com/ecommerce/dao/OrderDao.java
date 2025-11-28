package com.ecommerce.dao;

import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.OrderItem;

import java.util.List;

/**
 * 订单数据访问接口
 */
public interface OrderDao {
    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    Order findById(Integer id);

    /**
     * 根据订单编号查询订单
     * @param orderNo 订单编号
     * @return 订单对象
     */
    Order findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Integer userId);

    /**
     * 查询所有订单
     * @return 订单列表
     */
    List<Order> findAll();

    /**
     * 保存订单
     * @param order 订单对象
     * @return 影响的行数
     */
    int save(Order order);

    /**
     * 更新订单
     * @param order 订单对象
     * @return 影响的行数
     */
    int update(Order order);

    /**
     * 删除订单
     * @param id 订单ID
     * @return 影响的行数
     */
    int delete(Integer id);

    /**
     * 根据订单ID查询订单商品项
     * @param orderId 订单ID
     * @return 订单商品项列表
     */
    List<OrderItem> findOrderItemsByOrderId(Integer orderId);

    /**
     * 保存订单商品项
     * @param orderItem 订单商品项
     * @return 影响的行数
     */
    int saveOrderItem(OrderItem orderItem);
    
    /**
     * 获取订单总数
     * @return 订单总数
     */
    int countAll();
    
    /**
     * 获取总销售额
     * @return 总销售额
     */
    double getTotalSales();
}