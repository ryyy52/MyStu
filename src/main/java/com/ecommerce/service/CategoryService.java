package com.ecommerce.service;

import com.ecommerce.pojo.Category;

import java.util.List;

/**
 * 商品分类业务逻辑接口
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