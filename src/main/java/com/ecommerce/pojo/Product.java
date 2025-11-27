package com.ecommerce.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类
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