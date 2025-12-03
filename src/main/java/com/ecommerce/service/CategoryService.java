package com.ecommerce.service;

import com.ecommerce.pojo.Category;

import java.util.List;

/**
 * 商品分类业务逻辑接口 - 定义分类管理的业务操作规范
 * 
 * 职责：
 * 1. 定义分类的查询业务方法
 * 2. 定义分类树的构建方法
 * 3. 定义分类的增删改业务方法
 * 4. 定义分类与商品关联的查询方法
 * 
 * 主要功能：
 * - 分类查询: findById、findAll、findByParentId
 * - 分类树构建: getCategoryTree、getCategoryIdsWithChildren
 * - 分类管理: save、update、delete
 * - 分类统计: 获取分类下的商品数量
 * 
 * 分类结构：
 * 支持多层级树形结构（最多3级）
 * - 一级分类（顶级）
 *   - 二级分类
 *     - 三级分类
 * 
 * 特点：
 * - 支持分类树形展示
 * - 支持递归查询子分类
 * - 支持分类的权限控制
 * - 支持分类排序
 * 
 * 使用场景：
 * - 商品浏览时的分类导航
 * - 分类管理后台
 * - 商品添加时的分类选择
 * - 数据统计的分类维度
 */
public interface CategoryService {
    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类对象
     */
    Category findById(Integer id);

    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentId(Integer parentId);

    /**
     * 获取分类树结构
     * @return 分类树列表
     */
    List<Category> getCategoryTree();

    /**
     * 保存分类
     * @param category 分类对象
     * @return 保存是否成功
     */
    boolean save(Category category);

    /**
     * 更新分类
     * @param category 分类对象
     * @return 更新是否成功
     */
    boolean update(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 删除是否成功
     */
    boolean delete(Integer id);
    
    /**
     * 获取分类及其所有子分类的ID列表
     * @param categoryId 分类ID
     * @return 分类ID列表
     */
    List<Integer> getCategoryIdsWithChildren(Integer categoryId);
}