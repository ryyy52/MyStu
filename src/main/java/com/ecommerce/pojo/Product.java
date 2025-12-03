package com.ecommerce.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类 - 代表电商平台的商品
 * 
 * 职责：
 * 1. 存储商品的基本信息
 * 2. 关联商品的分类
 * 3. 记录商品的库存和上下架状态
 * 4. 提供商品的详细信息
 * 
 * 属性说明：
 * - id: 商品唯一标识
 * - name: 商品名称
 * - categoryId: 所属分类ID
 * - price: 商品销售价格
 * - stock: 商品库存数量
 * - description: 商品详细描述
 * - image: 商品主图URL
 * - status: 商品上下架状态
 *   * 0: 下架（不在前台显示）
 *   * 1: 上架（在前台显示）
 * - createTime: 商品创建时间
 * - updateTime: 商品最后修改时间
 * - category: 关联的Category对象
 * 
 * 主要特点：
 * - 实现Serializable接口支持序列化
 * - 使用BigDecimal精确存储价格
 * - 支持库存管理和预警
 * - 支持上下架控制商品展示
 * - 包含完整的商品描述和图片
 * 
 * 使用场景：
 * - 商品列表展示
 * - 商品详情页面
 * - 购物车和订单中的商品信息
 * - 库存查询和预警
 * - 商品搜索和筛选
 */
public class Product implements Serializable {
    private Integer id; // 商品ID
    private String name; // 商品名称
    private Integer categoryId; // 分类ID
    private BigDecimal price; // 商品价格
    private Integer stock; // 商品库存
    private String description; // 商品描述
    private String image; // 商品图片
    private Integer status; // 商品状态（0：下架，1：上架）
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

    // 构造方法
    public Product() {
    }

    public Product(String name, Integer categoryId, BigDecimal price, Integer stock) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stock = stock;
    }

    // getter和setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}