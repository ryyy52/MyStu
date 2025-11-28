package com.ecommerce.utils;

import java.util.regex.Pattern;

/**
 * 输入验证工具类
 */
public class ValidationUtils {
    
    // 正则表达式模式
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9_!@#$%^&*]{6,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_.]{1,100}$");
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+\\.?\\d{0,2}$");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^\\d+$");
    
    /**
     * 验证用户名
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证密码
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 验证邮箱
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证手机号
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证商品名称
     */
    public static boolean isValidProductName(String productName) {
        return productName != null && PRODUCT_NAME_PATTERN.matcher(productName).matches();
    }
    
    /**
     * 验证价格
     */
    public static boolean isValidPrice(String price) {
        return price != null && PRICE_PATTERN.matcher(price).matches();
    }
    
    /**
     * 验证正整数
     */
    public static boolean isValidPositiveInteger(String value) {
        return value != null && INTEGER_PATTERN.matcher(value).matches();
    }
    
    /**
     * 验证ID是否为正整数
     */
    public static boolean isValidId(String id) {
        if (id == null) return false;
        try {
            int value = Integer.parseInt(id);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 验证数量是否为正整数且在合理范围内
     */
    public static boolean isValidQuantity(String quantity) {
        if (!isValidPositiveInteger(quantity)) return false;
        try {
            int value = Integer.parseInt(quantity);
            return value > 0 && value <= 1000; // 最大数量限制为1000
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 转义HTML特殊字符，防止XSS攻击
     */
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * 清理用户输入，移除潜在的危险字符
     */
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        // 移除SQL注入相关的危险字符
        return input.replaceAll("[;'\"\\-\\*\\/\\\\]+", "").trim();
    }
}