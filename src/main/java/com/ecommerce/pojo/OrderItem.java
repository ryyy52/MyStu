package com.ecommerce.pojo;

// ==================== 导入序列化和数值处理类 ====================
// Serializable接口 - 使对象可以被序列化
import java.io.Serializable;
// BigDecimal类 - 精确表示货币金额，避免浮点数精度问题
import java.math.BigDecimal;

/**
 * 订单商品项实体类 - 代表订单中的单个商品
 * 
 * 职责：
 * 1. 存储订单中商品的信息
 * 2. 记录订单时商品的价格和数量
 * 3. 关联具体的商品详情
 * 
 * 属性说明：
 * - id: 订单商品项唯一标识
 * - orderId: 所属订单ID
 * - productId: 商品ID
 * - quantity: 商品购买数量
 * - price: 商品单价（订单创建时的价格，不随后续商品价格变化）
 * - product: 关联的Product对象
 * 
 * 特点：
 * - 实现Serializable接口支持序列化
 * - 记录订单创建时的价格快照
 * - 一个订单可以包含多个OrderItem
 * - 使用BigDecimal精确存储价格
 * 
 * 使用场景：
 * - 订单创建时从CartItem转换而来
 * - 订单详情展示订单包含的商品
 * - 订单统计中计算订单商品数量和金额
 * - 防止订单数据随商品信息变化而变化
 */
public class OrderItem implements Serializable {
    // ==================== 订单商品项基本属性 ====================
    /** 订单商品项唯一标识 - 数据库主键 */
    private Integer id;
    /** 所属订单ID - 关联的订单的唯一标识 */
    private Integer orderId;
    /** 商品ID - 关联的商品的唯一标识 */
    private Integer productId;
    /** 商品购买数量 - 该商品在订单中的购买数量 */
    private Integer quantity;
    /** 商品单价 - 订单创建时的商品价格快照，确保订单数据的一致性 */
    private BigDecimal price;
    /** 商品详情对象 - 关联的Product对象，包含商品的完整信息 */
    private Product product;

    // ==================== 构造方法 ====================
    /**
     * 无参构造方法 - 用于反射创建对象或ORM框架映射
     */
    public OrderItem() {
    }

    /**
     * 带参构造方法 - 初始化订单商品项的基本信息
     * @param orderId 所属订单的ID
     * @param productId 商品的ID
     * @param quantity 商品数量
     * @param price 商品单价
     */
    public OrderItem(Integer orderId, Integer productId, Integer quantity, BigDecimal price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // ==================== 业务方法 ====================
    /**
     * 计算该订单商品项的总价
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
     * 获取订单商品项ID
     * @return 订单商品项唯一标识
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置订单商品项ID
     * @param id 订单商品项唯一标识
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单ID
     * @return 所属订单的唯一标识
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID
     * @param orderId 所属订单的唯一标识
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
     * @return 该商品在订单中的购买数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置商品数量
     * @param quantity 该商品在订单中的购买数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取商品单价
     * @return 商品的单价（订单创建时的价格）
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
     * toString方法 - 返回订单商品项的字符串表示
     * @return 订单商品项的字符串表示
     */
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}