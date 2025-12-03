package com.ecommerce.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类 - 用户密码加密和数据摘要计算
 * 
 * 职责：
 * 1. 提供MD5加密算法实现
 * 2. 用于用户密码的单向加密存储
 * 3. 计算数据的MD5摘要值
 * 
 * 主要方法：
 * - encrypt(String str): 对字符串进行MD5加密
 * 
 * 特点：
 * - 不可逆加密（MD5是单向哈希函数）
 * - 相同输入产生相同输出（可用于验证）
 * - 输出为32位十六进制字符串
 * - 应用于密码存储，保护用户隐私
 * 
 * 使用场景：
 * - 用户注册时密码加密
 * - 用户登录时密码验证
 * - 数据完整性校验
 * 
 * 安全说明：
 * - 不应该用于存储重要的密钥
 * - 应该配合盐值（salt）使用增强安全性
 * - 现代应用建议使用bcrypt等更安全的算法
 * 
 * 加密流程：
 * 1. 获取MD5 MessageDigest实例
 * 2. 将字符串转换为字节数组
 * 3. 计算MD5摘要
 * 4. 转换为16进制字符串返回
 */
public class MD5Utils {
    
    /**
     * MD5加密方法 - 对字符串进行单向MD5哈希加密
     * 
     * 实现过程：
     * 1. 获取MD5算法实例
     * 2. 将字符串转换为字节数组
     * 3. 计算MD5摘要
     * 4. 转换为16进制字符串
     * 
     * 算法说明：
     * - MD5是一种单向哈希算法，不可逆
     * - 相同的输入总是产生相同的输出
     * - 输出为32位十六进制字符串（128位二进制）
     * 
     * @param str 要加密的字符串（明文）
     * @return 加密后的32位十六进制字符串，异常情况返回null
     */
    public static String encrypt(String str) {
        try {
            // 步骤1：获取MD5 MessageDigest实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 步骤2：将字符串转换为字节数组并更新到摘要中
            md.update(str.getBytes());
            // 步骤3：完成哈希计算，返回摘要的字节数组（16字节）
            byte[] byteDigest = md.digest();
            
            // 步骤4：初始化字符串构建器，用于存储16进制字符
            int i;
            StringBuilder buf = new StringBuilder("");
            // 步骤5：遍历摘要的每一个字节，转换为16进制
            for (byte b : byteDigest) {
                // 将byte转换为int（处理符号问题）
                i = b;
                // 如果字节是负数，加上256转换为无符号值
                // 原因：Java中byte是有符号的，范围是-128到127
                // 而16进制表示需要0-255的范围
                if (i < 0) {
                    i += 256;
                }
                // 如果转换后的值小于16（即0x0到0xF），前面补0
                // 这样可以确保每个字节都占用两位16进制数字
                if (i < 16) {
                    buf.append("0");
                }
                // 将整数转换为16进制字符串并追加到结果中
                buf.append(Integer.toHexString(i));
            }
            // 步骤6：返回完整的32位（256位）16进制字符串
            // 32位 = 16字节 × 2位/字节
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果JVM不支持MD5算法（几乎不可能），打印异常栈并返回null
            e.printStackTrace();
            return null;
        }
    }
}