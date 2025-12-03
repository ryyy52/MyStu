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
 * 购物车业务逻辑实现类 - 实现CartService接口的具体业务逻辑
 * 
 * 功能说明：
 * 1. 购物车查询：按购物车ID、用户ID查询购物车信息
 * 2. 购物车操作：添加商品、删除商品、清空购物车
 * 3. 商品管理：更新购物车中商品的数量
 * 4. 价格计算：计算购物车总价
 * 
 * 核心业务规则：
 * - 用户和购物车一一对应（一个用户只有一个购物车）
 * - 添加商品前必须验证商品存在且库存充足
 * - 购物车不存在时自动创建
 * - 同一商品重复添加时自动累加数量而不是创建新项
 * - 修改数量前必须验证库存是否充足
 * - 使用BigDecimal计算金额以避免浮点数精度问题
 * 
 * 依赖关系：
 * - CartDao：购物车数据访问
 * - ProductDao：商品数据访问（用于验证库存）
 */
public class CartServiceImpl implements CartService {
    // 注入CartDao依赖 - 用于访问购物车数据库
    private CartDao cartDao = new CartDaoImpl();
    // 注入ProductDao依赖 - 用于验证商品库存
    private ProductDao productDao = new ProductDaoImpl();

    /**
     * 根据购物车ID查询购物车信息
     * @param id 购物车ID
     * @return 购物车对象，不存在则返回null
     */
    @Override
    public Cart findById(Integer id) {
        // 直接调用DAO层查询
        return cartDao.findById(id);
    }

    /**
     * 根据用户ID查询该用户的购物车
     * @param userId 用户ID
     * @return 购物车对象，不存在则返回null
     */
    @Override
    public Cart findByUserId(Integer userId) {
        // 直接调用DAO层查询用户的购物车
        return cartDao.findByUserId(userId);
    }
    
    /**
     * 新建购物车
     * @param cart 购物车对象
     * @return 影响的行数（1表示创建成功）
     */
    @Override
    public int save(Cart cart) {
        // 调用DAO层保存购物车到数据库
        return cartDao.save(cart);
    }

    /**
     * 查询购物车中的所有商品项
     * @param cartId 购物车ID
     * @return 购物车商品列表
     */
    @Override
    public List<CartItem> getCartItems(Integer cartId) {
        // 调用DAO层查询购物车中的所有商品项
        return cartDao.findCartItemsByCartId(cartId);
    }

    /**
     * 添加商品到购物车业务逻辑
     * 
     * 实现流程：
     * 1. 验证商品是否存在
     * 2. 验证库存是否充足
     * 3. 检查用户购物车是否存在（不存在则创建）
     * 4. 检查购物车中是否已存在该商品（存在则累加数量，不存在则创建新项）
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 要添加的数量
     * @return true表示添加成功，false表示添加失败（商品不存在或库存不足）
     */
    @Override
    public boolean addToCart(Integer userId, Integer productId, Integer quantity) {
        // 步骤1：验证商品是否存在
        Product product = productDao.findById(productId);
        if (product == null) {
            // 商品不存在，无法添加
            return false;
        }

        // 步骤2：验证商品库存是否充足
        // 如果库存小于要添加的数量，则添加失败
        if (product.getStock() < quantity) {
            // 库存不足，无法添加
            return false;
        }

        // 步骤3：获取或创建用户的购物车
        // 首先尝试查询用户是否已有购物车
        Cart cart = cartDao.findByUserId(userId);
        if (cart == null) {
            // 用户还没有购物车，创建新购物车
            cart = new Cart();
            // 设置购物车所属用户
            cart.setUserId(userId);
            // 保存购物车到数据库
            cartDao.save(cart);
            // 重新查询购物车以获取数据库生成的ID（自增主键）
            cart = cartDao.findByUserId(userId);
        }

        // 步骤4：检查购物车中是否已存在该商品
        // 获取购物车中的所有商品项
        List<CartItem> cartItems = cartDao.findCartItemsByCartId(cart.getId());
        // 遍历购物车中的所有商品，查找是否已存在该商品
        for (CartItem item : cartItems) {
            // 如果找到相同的商品ID
            if (item.getProductId().equals(productId)) {
                // 将新添加的数量累加到现有数量上
                item.setQuantity(item.getQuantity() + quantity);
                // 更新购物车商品项数量到数据库
                return cartDao.updateCartItem(item) > 0;
            }
        }

        // 步骤5：购物车中不存在该商品，创建新的购物车商品项
        CartItem cartItem = new CartItem();
        // 设置购物车项所属的购物车ID
        cartItem.setCartId(cart.getId());
        // 设置商品ID
        cartItem.setProductId(productId);
        // 设置商品名称（从商品对象中获取）
        cartItem.setProductName(product.getName());
        // 设置商品单价（从商品对象中获取当前价格）
        cartItem.setPrice(product.getPrice());
        // 设置购买数量
        cartItem.setQuantity(quantity);
        
        // 步骤6：保存新的购物车商品项到数据库
        return cartDao.saveCartItem(cartItem) > 0;
    }

    /**
     * 更新购物车商品项数量业务逻辑
     * 
     * 实现流程：
     * 1. 验证购物车商品项是否存在
     * 2. 验证该商品库存是否充足
     * 3. 更新数量
     * 
     * @param cartItemId 购物车商品项ID
     * @param quantity 新的数量
     * @return true表示更新成功，false表示更新失败
     */
    @Override
    public boolean updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        // 步骤1：验证购物车商品项是否存在
        CartItem cartItem = cartDao.findCartItemById(cartItemId);
        if (cartItem == null) {
            // 购物车商品项不存在，无法更新
            return false;
        }

        // 步骤2：验证该商品是否存在且库存是否充足
        // 查询商品信息
        Product product = productDao.findById(cartItem.getProductId());
        // 检查商品是否存在和库存是否充足
        if (product == null || product.getStock() < quantity) {
            // 商品不存在或库存不足，无法更新
            return false;
        }

        // 步骤3：设置新的数量
        cartItem.setQuantity(quantity);
        // 步骤4：更新购物车商品项到数据库
        return cartDao.updateCartItem(cartItem) > 0;
    }

    /**
     * 从购物车删除指定商品项业务逻辑
     * @param cartItemId 购物车商品项ID
     * @return true表示删除成功，false表示删除失败
     */
    @Override
    public boolean removeFromCart(Integer cartItemId) {
        // 调用DAO层删除购物车商品项
        return cartDao.deleteCartItem(cartItemId) > 0;
    }

    /**
     * 清空购物车（删除购物车中的所有商品）业务逻辑
     * @param cartId 购物车ID
     * @return true表示清空成功，false表示清空失败
     */
    @Override
    public boolean clearCart(Integer cartId) {
        // 调用DAO层删除购物车中的所有商品项
        return cartDao.deleteCartItemsByCartId(cartId) > 0;
    }

    /**
     * 计算购物车总价业务逻辑
     * 
     * 实现原理：
     * - 遍历购物车中的所有商品项
     * - 累加每个商品项的小计金额（单价 * 数量）
     * - 使用BigDecimal进行精确计算，避免浮点数精度问题
     * 
     * @param cartId 购物车ID
     * @return 购物车总价（保留两位小数）
     */
    @Override
    public Double calculateTotalPrice(Integer cartId) {
        // 步骤1：获取购物车中的所有商品项
        List<CartItem> cartItems = cartDao.findCartItemsByCartId(cartId);
        // 步骤2：初始化总价为0
        // 使用BigDecimal而不是double是为了避免浮点数运算精度问题
        java.math.BigDecimal totalPrice = java.math.BigDecimal.ZERO;
        // 步骤3：遍历购物车中的所有商品项
        for (CartItem item : cartItems) {
            // 步骤4：累加每个商品项的总价（item.getTotalPrice()为单价*数量）
            // add()方法返回新的BigDecimal对象，不会修改原对象
            totalPrice = totalPrice.add(item.getTotalPrice());
        }
        // 步骤5：将BigDecimal转换为Double返回
        return totalPrice.doubleValue();
    }
}