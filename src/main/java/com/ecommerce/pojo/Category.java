package com.ecommerce.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品分类实体类
 */
public class Category implements Serializable {
    private Integer id; // 分类ID
    private String name; // 分类名称
    private Integer parentId; // 父分类ID
    private Integer level; // 分类级别（1-3）
    private Integer sort; // 排序
    private String icon; // 分类图标
    private String description; // 分类描述
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private List<Category> children; // 子分类列表

    // 构造方法
    public Category() {
    }

    public Category(String name, Integer parentId, Integer level) {
        this.name = name;
        this.parentId = parentId;
        this.level = level;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", level=" + level +
                ", sort=" + sort +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}