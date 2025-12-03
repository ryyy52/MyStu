package com.ecommerce.dao.impl;

import com.ecommerce.dao.CartDao;
import com.ecommerce.pojo.Cart;
import com.ecommerce.pojo.CartItem;
import com.ecommerce.pojo.Product;
import com.ecommerce.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车数据访问实现类
 */
public class CartDaoImpl implements CartDao {
    private static final String FIND_BY_ID = "SELECT id, user_id, create_time, update_time FROM cart WHERE id = ?";
    private static final String FIND_BY_USER_ID = "SELECT id, user_id, create_time, update_time FROM cart WHERE user_id = ?";
    private static final String SAVE = "INSERT INTO cart (user_id, create_time, update_time) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE cart SET update_time = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM cart WHERE id = ?";
    
    private static final String FIND_CART_ITEMS_BY_CART_ID = "SELECT id, cart_id, product_id, quantity FROM cart_item WHERE cart_id = ?";
    private static final String FIND_CART_ITEM_BY_CART_ID_AND_PRODUCT_ID = "SELECT id, cart_id, product_id, quantity FROM cart_item WHERE cart_id = ? AND product_id = ?";
    private static final String FIND_CART_ITEM_BY_ID = "SELECT id, cart_id, product_id, quantity FROM cart_item WHERE id = ?";
    private static final String SAVE_CART_ITEM = "INSERT INTO cart_item (cart_id, product_id, quantity) VALUES (?, ?, ?)";
    private static final String UPDATE_CART_ITEM = "UPDATE cart_item SET quantity = ? WHERE id = ?";
    private static final String DELETE_CART_ITEM = "DELETE FROM cart_item WHERE id = ?";
    private static final String DELETE_CART_ITEMS_BY_CART_ID = "DELETE FROM cart_item WHERE cart_id = ?";

    @Override
    public Cart findById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cart cart = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cart = new Cart();
                cart.setId(rs.getInt("id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setCreateTime(rs.getTimestamp("create_time"));
                cart.setUpdateTime(rs.getTimestamp("update_time"));
                // 加载购物车商品项
                List<CartItem> cartItems = findCartItemsByCartId(cart.getId());
                cart.setCartItems(cartItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return cart;
    }

    @Override
    public Cart findByUserId(Integer userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cart cart = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_BY_USER_ID);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                cart = new Cart();
                cart.setId(rs.getInt("id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setCreateTime(rs.getTimestamp("create_time"));
                cart.setUpdateTime(rs.getTimestamp("update_time"));
                // 加载购物车商品项
                List<CartItem> cartItems = findCartItemsByCartId(cart.getId());
                cart.setCartItems(cartItems);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return cart;
    }

    @Override
    public int save(Cart cart) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE);
            ps.setInt(1, cart.getUserId());
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int update(Cart cart) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(2, cart.getId());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int delete(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            // 先删除购物车商品项
            deleteCartItemsByCartId(id);
            // 再删除购物车
            ps = conn.prepareStatement(DELETE);
            ps.setInt(1, id);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public List<CartItem> findCartItemsByCartId(Integer cartId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CartItem> cartItems = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_CART_ITEMS_BY_CART_ID);
            ps.setInt(1, cartId);
            rs = ps.executeQuery();
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getInt("id"));
                cartItem.setCartId(rs.getInt("cart_id"));
                cartItem.setProductId(rs.getInt("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                // 加载商品信息
                ProductDaoImpl productDao = new ProductDaoImpl();
                Product product = productDao.findById(cartItem.getProductId());
                cartItem.setProduct(product);
                if (product != null) {
                    cartItem.setPrice(product.getPrice());
                    cartItem.setProductName(product.getName());
                }
                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return cartItems;
    }

    @Override
    public CartItem findCartItemByCartIdAndProductId(Integer cartId, Integer productId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CartItem cartItem = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_CART_ITEM_BY_CART_ID_AND_PRODUCT_ID);
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            rs = ps.executeQuery();
            if (rs.next()) {
                cartItem = new CartItem();
                cartItem.setId(rs.getInt("id"));
                cartItem.setCartId(rs.getInt("cart_id"));
                cartItem.setProductId(rs.getInt("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                // 加载商品信息
                ProductDaoImpl productDao = new ProductDaoImpl();
                Product product = productDao.findById(cartItem.getProductId());
                cartItem.setProduct(product);
                if (product != null) {
                    cartItem.setPrice(product.getPrice());
                    cartItem.setProductName(product.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return cartItem;
    }

    @Override
    public int saveCartItem(CartItem cartItem) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(SAVE_CART_ITEM);
            ps.setInt(1, cartItem.getCartId());
            ps.setInt(2, cartItem.getProductId());
            ps.setInt(3, cartItem.getQuantity());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int updateCartItem(CartItem cartItem) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(UPDATE_CART_ITEM);
            ps.setInt(1, cartItem.getQuantity());
            ps.setInt(2, cartItem.getId());
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int deleteCartItem(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(DELETE_CART_ITEM);
            ps.setInt(1, id);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public int deleteCartItemsByCartId(Integer cartId) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(DELETE_CART_ITEMS_BY_CART_ID);
            ps.setInt(1, cartId);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, null);
        }
        return result;
    }

    @Override
    public CartItem findCartItemById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CartItem cartItem = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(FIND_CART_ITEM_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cartItem = new CartItem();
                cartItem.setId(rs.getInt("id"));
                cartItem.setCartId(rs.getInt("cart_id"));
                cartItem.setProductId(rs.getInt("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                // 加载商品信息
                ProductDaoImpl productDao = new ProductDaoImpl();
                Product product = productDao.findById(cartItem.getProductId());
                cartItem.setProduct(product);
                if (product != null) {
                    cartItem.setPrice(product.getPrice());
                    cartItem.setProductName(product.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(conn, ps, rs);
        }
        return cartItem;
    }
}