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
        System.out.println("=== ProductServiceImpl.save() 开始执行 ===");
        System.out.println("传入的商品对象：");
        System.out.println("商品名称: " + product.getName());
        System.out.println("分类ID: " + product.getCategoryId());
        System.out.println("价格: " + product.getPrice());
        System.out.println("库存: " + product.getStock());
        System.out.println("图片URL: " + product.getImage());
        System.out.println("描述: " + product.getDescription());
        System.out.println("状态: " + product.getStatus());
        
        // 设置默认状态为1（上架）
        product.setStatus(1);
        System.out.println("设置默认状态为1（上架）");
        
        // 设置默认库存
        if (product.getStock() == null) {
            product.setStock(0);
            System.out.println("设置默认库存为0");
        }
        
        System.out.println("调用productDao.save()");
        int result = productDao.save(product);
        System.out.println("productDao.save()返回结果: " + result);
        
        boolean success = result > 0;
        System.out.println("ProductServiceImpl.save()返回结果: " + success);
        System.out.println("=== ProductServiceImpl.save() 执行结束 ===");
        return success;
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
     * 根据分类ID列表查询商品
     */
    @Override
    public List<Product> findByCategoryIds(List<Integer> categoryIds) {
        return productDao.findByCategoryIds(categoryIds);
    }

    /**
     * 根据分类ID列表分页查询商品
     */
    @Override
    public List<Product> findByCategoryIdsAndPage(List<Integer> categoryIds, int page, int pageSize) {
        int offset = Math.max(0, (page - 1) * pageSize);
        return productDao.findByCategoryIdsAndPage(categoryIds, offset, pageSize);
    }

    /**
     * 根据分类ID列表获取商品总数
     */
    @Override
    public int countByCategoryIds(List<Integer> categoryIds) {
        return productDao.countByCategoryIds(categoryIds);
    }

    /**
     * 搜索商品总数
     */
    @Override
    public int countSearchResults(String keyword) {
        return productDao.countSearchResults(keyword);
    }
}