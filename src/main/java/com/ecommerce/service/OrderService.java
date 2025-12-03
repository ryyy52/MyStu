package com.ecommerce.service;

import com.ecommerce.pojo.Order;
import com.ecommerce.pojo.OrderItem;

import java.util.List;

/**
 * 订单业务逻辑接口 - 定义订单业务的核心操作规范
 * 
 * 职责：
 * 1. 定义订单的CRUD操作
 * 2. 定义订单状态管理
 * 3. 定义订单与订单商品项的关系操作
 * 4. 定义订单查询方法
 * 
 * 主要功能：
 * 1. 订单创建与生成
 * 2. 订单状态更新
 * 3. 订单查询与检索
 * 4. 订单商品项管理
 * 5. 订单取消操作
 * 
 * 特点：
 * - 面向接口编程，便于测试和扩展
 * - 封装订单业务逻辑，实现业务与数据访问分离
 * - 支持多种订单查询方式
 * - 提供完整的订单生命周期管理
 * 
 * 使用场景：
 * - 用户下单流程
 * - 订单状态更新
 * - 订单历史记录查询
 * - 订单详情查看
 * - 订单取消操作
 */
public interface OrderService {
    /**
     * 根据ID查询订单
     * <p>根据订单ID获取完整的订单信息，包括订单基本信息和订单状态</p>
     * @param id 订单ID，唯一标识订单的主键
     * @return 订单对象，包含完整的订单信息
     */
    Order findById(Integer id);

    /**
     * 根据订单号查询订单
     * <p>根据系统生成的唯一订单号获取订单信息，订单号通常用于前端展示和用户查询</p>
     * @param orderNo 订单号，系统生成的唯一字符串
     * @return 订单对象，包含完整的订单信息
     */
    Order findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     * <p>获取指定用户的所有订单，按创建时间倒序排列</p>
     * @param userId 用户ID，关联到订单的用户
     * @return 订单列表，包含该用户的所有订单
     */
    List<Order> findByUserId(Integer userId);

    /**
     * 获取订单中的商品项
     * <p>根据订单ID查询该订单包含的所有商品项，包括商品信息、数量和单价</p>
     * @param orderId 订单ID，关联到订单商品项
     * @return 订单商品项列表，包含订单中的所有商品
     */
    List<OrderItem> getOrderItems(Integer orderId);

    /**
     * 创建订单
     * <p>根据用户购物车内容创建新订单，包括以下步骤：
     * 1. 从购物车获取商品列表
     * 2. 验证商品库存
     * 3. 生成唯一订单号
     * 4. 计算订单总价
     * 5. 保存订单信息
     * 6. 保存订单商品项
     * 7. 扣减商品库存
     * 8. 清空购物车</p>
     * @param userId 用户ID，创建订单的用户
     * @param address 收货地址，用户填写的详细收货地址
     * @param phone 联系电话，用户的联系方式
     * @param receiver 收货人，接收商品的人员姓名
     * @return 生成的订单对象，包含完整的订单信息
     */
    Order createOrder(Integer userId, String address, String phone, String receiver);

    /**
     * 更新订单状态
     * <p>根据订单ID更新订单状态，订单状态说明：
     * 0 - 待付款
     * 1 - 待发货
     * 2 - 待收货
     * 3 - 已完成
     * 4 - 已取消</p>
     * @param orderId 订单ID，需要更新状态的订单
     * @param status 订单状态，新的状态值
     * @return 更新是否成功，true表示更新成功，false表示更新失败
     */
    boolean updateOrderStatus(Integer orderId, Integer status);

    /**
     * 取消订单
     * <p>取消指定订单，只有待付款的订单可以取消。取消订单会：
     * 1. 更新订单状态为已取消
     * 2. 恢复商品库存
     * 3. 更新相关统计信息</p>
     * @param orderId 订单ID，需要取消的订单
     * @return 取消是否成功，true表示取消成功，false表示取消失败
     */
    boolean cancelOrder(Integer orderId);

    /**
     * 删除订单
     * <p>物理删除订单记录，通常用于后台管理操作</p>
     * @param orderId 订单ID，需要删除的订单
     * @return 删除是否成功，true表示删除成功，false表示删除失败
     */
    boolean deleteOrder(Integer orderId);
    
    /**
     * 查询所有订单
     * <p>获取系统中所有订单，按创建时间倒序排列，主要用于后台管理</p>
     * @return 订单列表，包含系统中所有订单
     */
    List<Order> findAll();
}