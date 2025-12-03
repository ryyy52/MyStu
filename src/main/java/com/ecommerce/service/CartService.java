package com.ecommerce.service;

import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;

import java.util.List;

/**
 * 购物车业务逻辑接口
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