package com.ecommerce.dao;

import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.OrderItem;

import java.util.List;

/**
 * 订单数据访问接口 - 定义订单数据的数据库操作规范
 * 
 * 职责：
 * 1. 定义订单和订单商品项的CRUD操作
 * 2. 定义订单查询的多种方式
 * 3. 定义订单统计方法
 * 
 * 主要功能：
 * 订单查询:
 * - findById(Integer): 按ID查询订单
 * - findByOrderNo(String): 按订单编号查询订单
 * - findByUserId(Integer): 按用户ID查询订单列表
 * - findAll(): 查询所有订单
 * 
 * 订单管理:
 * - save(Order): 新增订单
 * - update(Order): 修改订单
 * - delete(Integer): 删除订单
 * 
 * 订单商品:
 * - findOrderItemsByOrderId(Integer): 查询订单商品
 * - saveOrderItem(OrderItem): 保存订单商品
 * 
 * 订单统计:
 * - countAll(): 订单总数
 * - getTotalSales(): 总销售额
 * 
 * 特点：
 * - 支持多条件查询
 * - 支持订单详情查询
 * - 支持数据统计
 * - 完整的CRUD操作
 * 
 * 使用场景：
 * - 订单数据的持久化
 * - 用户订单查询
 * - 订单详情查询
 * - 销售数据统计
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