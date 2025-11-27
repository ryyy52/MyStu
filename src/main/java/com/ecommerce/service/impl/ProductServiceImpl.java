package com.ecommerce.service.impl;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.impl.ProductDaoImpl;
import com.ecommerce.pojo.Product;
import com.ecommerce.service.ProductService;

import java.util.List;

/**
 * 商品业务逻辑实现类
 */
public class ProductServiceImpl implements ProductService {
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public Product findById(Integer id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        return productDao.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> search(String keyword) {
        return productDao.search(keyword);
    }

    @Override
    public boolean save(Product product) {
        // 设置默认状态为1（上架）
        product.setStatus(1);
        // 设置默认库存
        if (product.getStock() == null) {
            product.setStock(0);
        }
        int result = productDao.save(product);
        return result > 0;
    }

    @Override
    public boolean update(Product product) {
        int result = productDao.update(product);
        return result > 0;
    }

    @Override
    public boolean updateStock(Integer productId, Integer quantity) {
        int result = productDao.updateStock(productId, quantity);
        return result > 0;
    }

    @Override
    public boolean delete(Integer id) {
        int result = productDao.delete(id);
        return result > 0;
    }

    /**
     * 分页查询商品
     */
    @Override
    public List<Product> findByPage(int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return productDao.findByPage(offset, pageSize);
    }

    /**
     * 根据分类ID分页查询商品
     */
    @Override
    public List<Product> findByCategoryIdAndPage(Integer categoryId, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return productDao.findByCategoryIdAndPage(categoryId, offset, pageSize);
    }

    /**
     * 搜索商品（分页）
     */
    @Override
    public List<Product> searchByPage(String keyword, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return productDao.searchByPage(keyword, offset, pageSize);
    }

    /**
     * 获取商品总数
     */
    @Override
    public int countAll() {
        return productDao.countAll();
    }

    /**
     * 根据分类ID获取商品总数
     */
    @Override
    public int countByCategoryId(Integer categoryId) {
        return productDao.countByCategoryId(categoryId);
    }

    /**
     * 搜索商品总数
     */
    @Override
    public int countSearchResults(String keyword) {
        return productDao.countSearchResults(keyword);
    }
}