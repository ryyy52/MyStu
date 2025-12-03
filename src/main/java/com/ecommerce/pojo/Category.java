package com.ecommerce.pojo;

// ==================== 导入序列化相关类 ====================
// Serializable接口 - 使对象可以被序列化
import java.io.Serializable;
// Date类 - 用于存储时间戳
import java.util.Date;
// List接口 - 用于存储子分类列表
import java.util.List;

/**
 * 商品分类实体类 - 代表电商平台的商品分类
 * 
 * 职责：
 * 1. 存储商品分类的信息
 * 2. 支持多层级分类树形结构
 * 3. 关联父分类和子分类
 * 4. 提供分类的排序和分组功能
 * 
 * 属性说明：
 * - id: 分类唯一标识
 * - name: 分类名称
 * - parentId: 父分类ID（null表示顶级分类）
 * - level: 分类级别（1-3级）
 * - sort: 同级分类的排序权重
 * - icon: 分类图标URL
 * - description: 分类描述说明
 * - createTime: 创建时间
 * - updateTime: 修改时间
 * - children: 子分类列表
 * 
 * 分类结构：
 * 支持多层级树形结构，例如：
 * - 电子产品 (level=1)
 *   - 手机 (level=2, parentId=电子产品ID)
 *     - 安卓手机 (level=3, parentId=手机ID)
 * 
 * 特点：
 * - 实现Serializable接口支持序列化
 * - 支持树形结构展示
 * - 支持分类的增删改查操作
 * - 提供分类排序功能
 * 
 * 使用场景：
 * - 商品浏览时的分类导航
 * - 商品管理时的分类选择
 * - 数据统计时的分类维度
 */
public class Category implements Serializable {
    // ==================== 分类基本信息属性 ====================
    /** 分类唯一标识 - 数据库主键 */
    private Integer id;
    /** 分类名称 - 例如：电子产品、图书、服装 */
    private String name;
    /** 父分类ID - null或0表示顶级分类，否则表示该分类属于某个父分类 */
    private Integer parentId;
    /** 分类级别 - 1表示一级分类，2表示二级分类，3表示三级分类，以此类推 */
    private Integer level;
    /** 分类排序权重 - 用于同级分类的排序，值越小排序越靠前 */
    private Integer sort;
    /** 分类图标URL - 用于前端展示分类图标 */
    private String icon;
    /** 分类描述 - 分类的详细说明信息 */
    private String description;
    /** 分类创建时间 - 记录分类何时被创建 */
    private Date createTime;
    /** 分类修改时间 - 记录分类何时被最后修改 */
    private Date updateTime;
    /** 子分类列表 - 存储该分类下的所有子分类，用于构建树形结构 */
    private List<Category> children;

    // ==================== 构造方法 ====================
    /**
     * 无参构造方法 - 用于反射创建对象
     */
    public Category() {
    }

    /**
     * 带参构造方法 - 初始化分类的基本信息
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param level 分类级别
     */
    public Category(String name, Integer parentId, Integer level) {
        this.name = name;
        this.parentId = parentId;
        this.level = level;
    }

    // ==================== Getter和Setter方法 ====================
    /**
     * 获取分类ID
     * @return 分类唯一标识
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置分类ID
     * @param id 分类唯一标识
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取分类名称
     * @return 分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置分类名称
     * @param name 分类名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取父分类ID
     * @return 父分类ID（null表示顶级分类）
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父分类ID
     * @param parentId 父分类ID
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取分类级别
     * @return 分类级别
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * 设置分类级别
     * @param level 分类级别
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * 获取分类排序权重
     * @return 排序权重值（值越小越靠前）
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置分类排序权重
     * @param sort 排序权重值
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取分类图标URL
     * @return 分类图标的URL地址
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置分类图标URL
     * @param icon 分类图标的URL地址
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取分类描述
     * @return 分类的详细说明
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置分类描述
     * @param description 分类的详细说明
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取分类创建时间
     * @return 分类创建的日期时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置分类创建时间
     * @param createTime 分类创建的日期时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取分类修改时间
     * @return 分类最后修改的日期时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置分类修改时间
     * @param updateTime 分类最后修改的日期时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取子分类列表
     * @return 该分类下的所有子分类
     */
    public List<Category> getChildren() {
        return children;
    }

    /**
     * 设置子分类列表
     * @param children 该分类下的所有子分类
     */
    public void setChildren(List<Category> children) {
        this.children = children;
    }

    /**
     * toString方法 - 返回分类对象的字符串表示
     * @return 分类对象的JSON格式字符串
     */
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