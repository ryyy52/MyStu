package com.ecommerce.service.impl;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.impl.CartDaoImpl;
import com.ecommerce.dao.impl.ProductDaoImpl;
import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;
import com.ecommerce.pojo.Product;
import com.ecommerce.service.CartService;

import java.util.List;

/**
 * 购物车业务逻辑实现类
 */
public class CartServiceImpl implements CartService {
    private CartDao cartDao = new CartDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public Cart findById(Integer id) {
        return cartDao.findById(id);
    }

    @Override
    public Cart findByUserId(Integer userId) {
        return cartDao.findByUserId(userId);
    }
    
    @Override
    public int save(Cart cart) {
        return cartDao.save(cart);
    }

    @Override
    public List<CartItem> getCartItems(Integer cartId) {
        return cartDao.findCartItemsByCartId(cartId);
    }

    @Override
    public boolean addToCart(Integer userId, Integer productId, Integer quantity) {
        // 验证商品是否存在
        Product product = productDao.findById(productId);
        if (product == null) {
            return false;
        }

        // 验证商品库存
        if (product.getStock() < quantity) {
            return false;
        }

        // 获取或创建购物车
        Cart cart = cartDao.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cartDao.save(cart);
            // 重新获取购物车以获取生成的ID
            cart = cartDao.findByUserId(userId);
        }

        // 检查购物车中是否已存在该商品
        List<CartItem> cartItems = cartDao.findCartItemsByCartId(cart.getId());
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                // 更新商品数量
                item.setQuantity(item.getQuantity() + quantity);
                return cartDao.updateCartItem(item) > 0;
            }
        }

        // 添加新的购物车商品项
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setProductId(productId);
        cartItem.setProductName(product.getName());
        cartItem.setPrice(product.getPrice());
        cartItem.setQuantity(quantity);
        
        return cartDao.saveCartItem(cartItem) > 0;
    }

    @Override
    public boolean updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        // 验证购物车商品项是否存在
        CartItem cartItem = cartDao.findCartItemById(cartItemId);
        if (cartItem == null) {
            return false;
        }

        // 验证商品库存
        Product product = productDao.findById(cartItem.getProductId());
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        // 更新商品数量
        cartItem.setQuantity(quantity);
        return cartDao.updateCartItem(cartItem) > 0;
    }

    @Override
    public boolean removeFromCart(Integer cartItemId) {
        return cartDao.deleteCartItem(cartItemId) > 0;
    }

    @Override
    public boolean clearCart(Integer cartId) {
        return cartDao.deleteCartItemsByCartId(cartId) > 0;
    }

    @Override
    public Double calculateTotalPrice(Integer cartId) {
        List<CartItem> cartItems = cartDao.findCartItemsByCartId(cartId);
        java.math.BigDecimal totalPrice = java.math.BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getTotalPrice());
        }
        return totalPrice.doubleValue();
    }
}