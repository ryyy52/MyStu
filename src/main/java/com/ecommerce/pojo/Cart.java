package com.ecommerce.pojo;

// ==================== 导入序列化和集合类 ====================
// Serializable接口 - 使对象可以被序列化
import java.io.Serializable;
// Date类 - 用于存储时间戳
import java.util.Date;
// List接口 - 用于存储购物车商品项列表
import java.util.List;

/**
 * 购物车实体类 - 代表用户的购物车
 * 
 * 职责：
 * 1. 存储购物车的基本信息
 * 2. 关联用户和购物车商品项
 * 3. 记录购物车的创建和修改时间
 * 
 * 属性说明：
 * - id: 购物车唯一标识
 * - userId: 所属用户ID
 * - createTime: 购物车创建时间
 * - updateTime: 购物车最后修改时间
 * - cartItems: 购物车中的商品项列表
 * 
 * 特点：
 * - 实现Serializable接口支持序列化
 * - 关联多个CartItem对象构成购物车
 * - 支持时间戳记录操作历史
 * 
 * 使用场景：
 * - 登录用户的购物车数据存储在数据库
 * - 未登录用户的购物车存储在Session中
 * - 支持购物车的增删改查操作
 */
public class Cart implements Serializable {
    // ==================== 购物车基本属性 ====================
    /** 购物车唯一标识 - 数据库主键 */
    private Integer id;
    /** 所属用户ID - 关联的用户的唯一标识 */
    private Integer userId;
    /** 购物车创建时间 - 用户创建购物车时的时间戳 */
    private Date createTime;
    /** 购物车最后修改时间 - 购物车最后一次被修改的时间 */
    private Date updateTime;
    /** 购物车中的商品项列表 - 存储购物车包含的所有商品及其数量 */
    private List<CartItem> cartItems;

    // ==================== 构造方法 ====================
    /**
     * 无参构造方法 - 用于反射创建对象或ORM框架映射
     */
    public Cart() {
    }

    /**
     * 带参构造方法 - 初始化购物车的用户关联信息
     * @param userId 购物车所属用户的ID
     */
    public Cart(Integer userId) {
        this.userId = userId;
    }

    // ==================== Getter和Setter方法 ====================
    /**
     * 获取购物车ID
     * @return 购物车唯一标识
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置购物车ID
     * @param id 购物车唯一标识
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取购物车所属用户ID
     * @return 用户的唯一标识
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置购物车所属用户ID
     * @param userId 用户的唯一标识
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取购物车创建时间
     * @return 购物车创建的日期时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置购物车创建时间
     * @param createTime 购物车创建的日期时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取购物车最后修改时间
     * @return 购物车最后修改的日期时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置购物车最后修改时间
     * @param updateTime 购物车最后修改的日期时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取购物车商品项列表
     * @return 购物车中包含的所有商品项
     */
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /**
     * 设置购物车商品项列表
     * @param cartItems 购物车中包含的所有商品项
     */
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    /**
     * toString方法 - 返回购物车对象的字符串表示
     * @return 购物车对象的字符串表示，不包含商品项以避免过多信息
     */
    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}