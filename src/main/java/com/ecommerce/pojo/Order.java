package com.ecommerce.pojo;

// ==================== 导入序列化、数值处理和日期类 ====================
// Serializable接口 - 使对象可以被序列化
import java.io.Serializable;
// BigDecimal类 - 精确表示货币金额，避免浮点数精度问题
import java.math.BigDecimal;
// Date类 - 用于存储时间戳
import java.util.Date;
// List接口 - 用于存储订单商品项列表
import java.util.List;

/**
 * 订单实体类 - 代表用户的电商订单
 * 
 * 职责：
 * 1. 存储订单的基本信息
 * 2. 关联订单商品项列表
 * 3. 记录订单的收货地址
 * 4. 跟踪订单状态
 * 
 * 属性说明：
 * - id: 订单唯一标识
 * - orderNo: 订单编号（订单号）
 * - userId: 下单用户ID
 * - totalPrice: 订单总金额
 * - status: 订单状态
 *   * 0: 待付款
 *   * 1: 待发货
 *   * 2: 待收货
 *   * 3: 已完成
 *   * 4: 已取消
 * - receiverName: 收货人姓名
 * - receiverPhone: 收货人电话
 * - receiverAddress: 收货地址
 * - createTime: 订单创建时间
 * - updateTime: 订单修改时间
 * - orderItems: 订单包含的商品项列表
 * 
 * 特点：
 * - 实现Serializable接口支持序列化
 * - 包含完整的收货信息
 * - 支持订单状态跟踪
 * - 使用BigDecimal精确计算金额
 * 
 * 使用场景：
 * - 用户创建订单时生成
 * - 订单状态流转（从待付款到已完成）
 * - 订单列表查询和详情展示
 * - 销售数据统计
 */
public class Order implements Serializable {
    // ==================== 订单基本信息属性 ====================
    /** 订单唯一标识 - 数据库主键 */
    private Integer id;
    /** 订单编号 - 用于展示给用户的订单号，唯一标识符 */
    private String orderNo;
    /** 下单用户ID - 关联下单用户的唯一标识 */
    private Integer userId;
    /** 订单总金额 - 订单中所有商品的总价，使用BigDecimal确保精度 */
    private BigDecimal totalPrice;
    /** 订单状态 - 0=待付款，1=待发货，2=待收货，3=已完成，4=已取消 */
    private Integer status;
    /** 收货人姓名 - 订单的收货人姓名 */
    private String receiverName;
    /** 收货人电话 - 订单的收货人联系电话 */
    private String receiverPhone;
    /** 收货地址 - 订单的收货地址信息 */
    private String receiverAddress;
    /** 订单创建时间 - 订单生成时的时间戳 */
    private Date createTime;
    /** 订单修改时间 - 订单最后一次修改的时间 */
    private Date updateTime;
    /** 订单商品项列表 - 订单中包含的所有商品及其数量 */
    private List<OrderItem> orderItems;

    // ==================== 构造方法 ====================
    /**
     * 无参构造方法 - 用于反射创建对象或ORM框架映射
     */
    public Order() {
    }

    /**
     * 带参构造方法 - 初始化订单的基本信息
     * @param orderNo 订单编号
     * @param userId 用户ID
     * @param totalPrice 订单总金额
     */
    public Order(String orderNo, Integer userId, BigDecimal totalPrice) {
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalPrice = totalPrice;
    }

    // ==================== Getter和Setter方法 ====================
    /**
     * 获取订单ID
     * @return 订单唯一标识
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置订单ID
     * @param id 订单唯一标识
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单编号
     * @return 用户可见的订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单编号
     * @param orderNo 用户可见的订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取用户ID
     * @return 下单用户的唯一标识
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * @param userId 下单用户的唯一标识
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取订单总金额
     * @return 订单中所有商品的总价
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * 设置订单总金额
     * @param totalPrice 订单中所有商品的总价
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 获取订单状态
     * @return 订单的当前状态（0待付款，1待发货，2待收货，3已完成，4已取消）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置订单状态
     * @param status 订单的状态值
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取收货人姓名
     * @return 收货人的姓名
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * 设置收货人姓名
     * @param receiverName 收货人的姓名
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * 获取收货人电话
     * @return 收货人的联系电话
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * 设置收货人电话
     * @param receiverPhone 收货人的联系电话
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    /**
     * 获取收货地址
     * @return 订单的收货地址
     */
    public String getReceiverAddress() {
        return receiverAddress;
    }

    /**
     * 设置收货地址
     * @param receiverAddress 订单的收货地址
     */
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    /**
     * 获取订单创建时间
     * @return 订单创建的日期时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置订单创建时间
     * @param createTime 订单创建的日期时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取订单修改时间
     * @return 订单最后修改的日期时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置订单修改时间
     * @param updateTime 订单最后修改的日期时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取订单商品项列表
     * @return 订单中包含的所有商品项
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * 设置订单商品项列表
     * @param orderItems 订单中包含的所有商品项
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * toString方法 - 返回订单对象的字符串表示
     * @return 订单对象的字符串表示，不包含订单项以避免过多信息
     */
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", userId=" + userId +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}