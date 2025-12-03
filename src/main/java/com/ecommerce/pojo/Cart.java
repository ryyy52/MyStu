package com.ecommerce.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 购物车实体类
 */
public class Cart implements Serializable {
    private Integer id; // 购物车ID
    private Integer userId; // 用户ID
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private List<CartItem> cartItems; // 购物车商品项列表

    // 构造方法
    public Cart() {
    }

    public Cart(Integer userId) {
        this.userId = userId;
    }

    // getter和setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

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