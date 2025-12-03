package com.ecommerce.dao;

import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;

import java.util.List;

/**
 * 购物车数据访问接口 - 定义购物车数据的数据库操作规范
 * 
 * 职责：
 * 1. 定义购物车和购物车商品项的CRUD操作
 * 2. 定义购物车数据的查询方法
 * 3. 定义购物车商品项的管理方法
 * 
 * 主要功能：
 * 购物车操作:
 * - findById(Integer): 按ID查询购物车
 * - findByUserId(Integer): 按用户ID查询购物车
 * - save(Cart): 新增购物车
 * - update(Cart): 修改购物车
 * - delete(Integer): 删除购物车
 * 
 * 购物车商品项操作:
 * - findCartItemsByCartId(Integer): 查询购物车中的所有商品
 * - findCartItemByCartIdAndProductId(Integer, Integer): 查询购物车中的特定商品
 * - findCartItemById(Integer): 按ID查询购物车商品项
 * - saveCartItem(CartItem): 添加购物车商品项
 * - updateCartItem(CartItem): 修改购物车商品项
 * - deleteCartItem(Integer): 删除购物车商品项
 * - deleteCartItemsByCartId(Integer): 删除购物车的所有商品
 * 
 * 特点：
 * - 面向接口编程，便于测试和扩展
 * - 分离购物车和商品项的操作
 * - 支持完整的CRUD操作
 * - 支持批量和单个操作
 * 
 * 使用场景：
 * - 用户购物车数据的持久化
 * - 购物车商品的增删改查
 * - 订单创建前的购物车查询
 */
public interface CartDao {
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
     * @return 影响的行数
     */
    int save(Cart cart);

    /**
     * 更新购物车
     * @param cart 购物车对象
     * @return 影响的行数
     */
    int update(Cart cart);

    /**
     * 删除购物车
     * @param id 购物车ID
     * @return 影响的行数
     */
    int delete(Integer id);

    /**
     * 根据购物车ID查询购物车商品项
     * @param cartId 购物车ID
     * @return 购物车商品项列表
     */
    List<CartItem> findCartItemsByCartId(Integer cartId);

    /**
     * 根据购物车ID和商品ID查询购物车商品项
     * @param cartId 购物车ID
     * @param productId 商品ID
     * @return 购物车商品项
     */
    CartItem findCartItemByCartIdAndProductId(Integer cartId, Integer productId);

    /**
     * 保存购物车商品项
     * @param cartItem 购物车商品项
     * @return 影响的行数
     */
    int saveCartItem(CartItem cartItem);

    /**
     * 更新购物车商品项
     * @param cartItem 购物车商品项
     * @return 影响的行数
     */
    int updateCartItem(CartItem cartItem);

    /**
     * 删除购物车商品项
     * @param id 购物车商品项ID
     * @return 影响的行数
     */
    int deleteCartItem(Integer id);
    
    /**
     * 根据ID查询购物车商品项
     * @param id 购物车商品项ID
     * @return 购物车商品项
     */
    CartItem findCartItemById(Integer id);

    /**
     * 根据购物车ID删除所有购物车商品项
     * @param cartId 购物车ID
     * @return 影响的行数
     */
    int deleteCartItemsByCartId(Integer cartId);
}