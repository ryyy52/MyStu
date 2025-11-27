package com.ecommerce.dao;

import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;

import java.util.List;

/**
 * 购物车数据访问接口
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