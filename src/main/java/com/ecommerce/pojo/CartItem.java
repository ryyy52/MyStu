package com.ecommerce.pojo;

// ==================== 导入序列化和数值处理类 ====================
// Serializable接口 - 使对象可以被序列化
import java.io.Serializable;
// BigDecimal类 - 精确表示货币金额，避免浮点数精度问题
import java.math.BigDecimal;

/**
 * 购物车商品项实体类 - 代表购物车中的单个商品
 * 
 * 职责：
 * 1. 存储购物车中商品的信息
 * 2. 计算商品项的小计金额
 * 3. 关联具体的商品详情
 * 
 * 属性说明：
 * - id: 购物车商品项唯一标识
 * - cartId: 所属购物车ID
 * - productId: 关联的商品ID
 * - productName: 商品名称（冗余存储便于查询）
 * - quantity: 商品数量
 * - price: 商品单价（冗余存储保证历史数据不变）
 * - product: 关联的Product对象（包含详细信息）
 * 
 * 主要方法：
 * - getTotalPrice(): 计算商品项总价 = price × quantity
 * 
 * 特点：
 * - 实现Serializable接口支持序列化
 * - 冗余存储商品信息确保数据一致性
 * - 支持BigDecimal精确计算价格
 * - 提供完整的JavaBean标准setter/getter
 * 
 * 使用场景：
 * - 购物车中每个商品对应一个CartItem
 * - 可以单独修改数量
 * - 支持从购物车中移除
 */
public class CartItem implements Serializable {
    // ==================== 购物车商品项基本属性 ====================
    /** 购物车商品项唯一标识 - 数据库主键 */
    private Integer id;
    /** 所属购物车ID - 关联的购物车的唯一标识 */
    private Integer cartId;
    /** 商品ID - 关联的商品的唯一标识 */
    private Integer productId;
    /** 商品名称 - 冗余存储，便于显示而不需要关联查询 */
    private String productName;
    /** 商品数量 - 该商品在购物车中的购买数量 */
    private Integer quantity;
    /** 商品单价 - 冗余存储购物车创建时的商品价格，确保订单价格不随商品价格变化 */
    private BigDecimal price;
    /** 商品详情对象 - 关联的Product对象，包含商品的完整信息 */
    private Product product;

    // ==================== 构造方法 ====================
    /**
     * 无参构造方法 - 用于反射创建对象或ORM框架映射
     */
    public CartItem() {
    }

    /**
     * 带参构造方法 - 初始化购物车商品项的基本信息
     * @param cartId 所属购物车的ID
     * @param productId 商品的ID
     * @param productName 商品名称
     * @param quantity 商品数量
     * @param price 商品单价
     */
    public CartItem(Integer cartId, Integer productId, String productName, Integer quantity, BigDecimal price) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // ==================== 业务方法 ====================
    /**
     * 计算该购物车商品项的总价
     * 总价 = 单价 × 数量
     * @return 商品项的总价（单价 × 数量）
     */
    public BigDecimal getTotalPrice() {
        // 判断价格和数量是否都存在
        if (price != null && quantity != null) {
            // 使用BigDecimal的multiply方法精确计算乘积
            // 避免浮点数计算导致的精度问题
            return price.multiply(new BigDecimal(quantity));
        }
        // 如果价格或数量为null，返回0
        return BigDecimal.ZERO;
    }

    // ==================== Getter和Setter方法 ====================
    /**
     * 获取购物车商品项ID
     * @return 购物车商品项唯一标识
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置购物车商品项ID
     * @param id 购物车商品项唯一标识
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取购物车ID
     * @return 所属购物车的唯一标识
     */
    public Integer getCartId() {
        return cartId;
    }

    /**
     * 设置购物车ID
     * @param cartId 所属购物车的唯一标识
     */
    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    /**
     * 获取商品ID
     * @return 关联的商品的唯一标识
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * 设置商品ID
     * @param productId 关联的商品的唯一标识
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 获取商品数量
     * @return 该商品在购物车中的购买数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置商品数量
     * @param quantity 该商品在购物车中的购买数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取商品单价
     * @return 商品的单价（购物车创建时的价格）
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置商品单价
     * @param price 商品的单价
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取商品名称
     * @return 商品的名称（冗余存储）
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 设置商品名称
     * @param productName 商品的名称
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 获取商品详情对象
     * @return 关联的Product对象，包含商品的完整信息
     */
    public Product getProduct() {
        return product;
    }

    /**
     * 设置商品详情对象
     * @param product 关联的Product对象
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * toString方法 - 返回购物车商品项的字符串表示
     * @return 购物车商品项的字符串表示
     */
    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}