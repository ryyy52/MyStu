package com.ecommerce.service;

import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;

import java.util.List;

/**
 * 购物车业务逻辑接口 - 定义购物车管理的业务操作规范
 * 
 * 职责：
 * 1. 定义购物车的增删改查业务方法
 * 2. 定义购物车商品项的管理方法
 * 3. 定义购物车合计计算方法
 * 4. 定义购物车清空方法
 * 
 * 主要功能：
 * - 购物车查询: findById、findByUserId
 * - 购物车保存和更新: save、update
 * - 购物车删除: delete
 * - 购物车商品项管理: 获取、添加、更新、删除商品
 * - 购物车操作: addToCart、updateCartItemQuantity、removeFromCart、clearCart
 * - 合计计算: calculateTotalPrice
 * 
 * 特点：
 * - 面向接口编程，便于测试和扩展
 * - 定义清晰的业务操作规范
 * - 支持购物车的完整生命周期管理
 * - 包含库存检查和验证逻辑
 * 
 * 使用场景：
 * - 购物车的创建和初始化
 * - 添加、修改、删除商品
 * - 购物车金额计算
 * - 购物车转订单
 */
public interface CartService {
    /**
     * 根据ID查询购物车
     * @param id 购物车ID
     * @return 购物车对象
     */
    Cart findById(Integer id);

    /**
     * 根据用户ID查询购物车
     * @param userId 用户ID
     * @return 购物车对象
     */
    Cart findByUserId(Integer userId);
    
    /**
     * 保存购物车
     * @param cart 购物车对象
     * @return 保存结果
     */
    int save(Cart cart);

    /**
     * 获取购物车中的商品项
     * @param cartId 购物车ID
     * @return 购物车商品项列表
     */
    List<CartItem> getCartItems(Integer cartId);

    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 商品数量
     * @return 添加是否成功
     */
    boolean addToCart(Integer userId, Integer productId, Integer quantity);

    /**
     * 更新购物车商品数量
     * @param cartItemId 购物车商品项ID
     * @param quantity 商品数量
     * @return 更新是否成功
     */
    boolean updateCartItemQuantity(Integer cartItemId, Integer quantity);

    /**
     * 从购物车中删除商品
     * @param cartItemId 购物车商品项ID
     * @return 删除是否成功
     */
    boolean removeFromCart(Integer cartItemId);

    /**
     * 清空购物车
     * @param cartId 购物车ID
     * @return 清空是否成功
     */
    boolean clearCart(Integer cartId);

    /**
     * 计算购物车总价
     * @param cartId 购物车ID
     * @return 购物车总价
     */
    Double calculateTotalPrice(Integer cartId);
}