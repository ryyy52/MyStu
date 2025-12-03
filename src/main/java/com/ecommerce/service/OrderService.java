package com.ecommerce.service;

import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.OrderItem;

import java.util.List;

/**
 * 订单业务逻辑接口
 */
public interface OrderService {
    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    Order findById(Integer id);

    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
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
     * 获取订单中的商品项
     * @param orderId 订单ID
     * @return 订单商品项列表
     */
    List<OrderItem> getOrderItems(Integer orderId);

    /**
     * 创建订单
     * @param userId 用户ID
     * @param address 收货地址
     * @param phone 联系电话
     * @param receiver 收货人
     * @return 生成的订单对象
     */
    Order createOrder(Integer userId, String address, String phone, String receiver);

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 订单状态
     * @return 更新是否成功
     */
    boolean updateOrderStatus(Integer orderId, Integer status);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消是否成功
     */
    boolean cancelOrder(Integer orderId);

    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 删除是否成功
     */
    boolean deleteOrder(Integer orderId);
    
    /**
     * 查询所有订单
     * @return 订单列表
     */
    List<Order> findAll();
}