package com.ecommerce.dao;

import com.ecommerce.pojo.Category;

import java.util.List;

/**
 * 商品分类数据访问接口
 */
public interface CategoryDao {
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
     * 保存分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int save(Category category);

    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响的行数
     */
    int update(Category category);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    int delete(Integer id);
}