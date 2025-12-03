package com.ecommerce.controller;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.util.Random;

/**
 * 验证码生成Servlet - 动态生成图形化验证码防止自动化攻击
 * 
 * 职责：
 * 1. 生成随机的图形化验证码
 * 2. 验证码包含字母和数字混合
 * 3. 添加干扰线和干扰点提高破解难度
 * 4. 将验证码存储到会话中供后续验证
 * 5. 返回JPEG格式的验证码图片
 * 
 * 主要特性：
 * - 验证码字符数: 4个
 * - 验证码图片: 120x40像素
 * - 字符集: 大小写字母+数字
 * - 干扰: 5条干扰线+50个干扰点
 * - 输出格式: JPEG
 * 
 * 安全特点：
 * - 禁用浏览器缓存（设置Cache-Control等响应头）
 * - 内存缓冲输出，避免文件系统缓存
 * - 支持自定义临时目录防止ImageIO异常
 * - 字符随机颜色提高识别难度
 * - 每次请求生成新的验证码
 * 
 * 使用流程：
 * 1. 前端页面通过IMG标签加载: /captcha
 * 2. 服务端生成验证码图片
 * 3. 验证码存储到Session: session.getAttribute(\"captcha\")
 * 4. 用户提交表单时比对输入值
 * 
 * 工作流程：
 * 1. 初始化临时目录和缓存策略
 * 2. 创建BufferedImage画布
 * 3. 绘制背景和边框
 * 4. 随机生成验证码字符串
 * 5. 绘制验证码到图片
 * 6. 添加干扰线和干扰点
 * 7. 将验证码存储到Session
 * 8. 设置HTTP响应头禁用缓存
 * 9. 输出JPEG图片到客户端
 */
public class CaptchaServlet extends HttpServlet {
    // 验证码字符集
    /** 验证码可用字符集 - 包含26个大写字母、26个小写字母、10个数字 */
    private static final String CAPTCHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 验证码长度
    /** 验证码生成的字符数量 */
    private static final int CAPTCHA_LENGTH = 4;
    // 图片宽度
    /** 验证码图片宽度（像素） */
    private static final int WIDTH = 120;
    // 图片高度
    /** 验证码图片高度（像素） */
    private static final int HEIGHT = 40;
    // 字体大小
    /** 验证码文字显示的字体大小 */
    private static final int FONT_SIZE = 20;
    // 随机数生成器
    /** 全局随机数生成器 - 用于生成随机验证码和干扰元素 */
    private static final Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 保障临时目录与缓存策略，避免 ImageIO 在不存在的临时目录下创建缓存文件失败
        ServletContext ctx = request.getServletContext();
        File sysTmp = new File(System.getProperty("java.io.tmpdir", ""));
        if (!sysTmp.exists()) {
            File webTmp = new File(ctx.getRealPath("/WEB-INF/temp"));
            if (!webTmp.exists()) {
                webTmp.mkdirs();
            }
            System.setProperty("java.io.tmpdir", webTmp.getAbsolutePath());
        }
        javax.imageio.ImageIO.setUseCache(false);
        // 创建图像
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 设置背景颜色
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制边框
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // 生成随机验证码
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            char c = CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length()));
            captcha.append(c);
            // 绘制单个字符
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
            g.drawString(String.valueOf(c), 25 * i + 10, 25);
        }

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2, 2);
        }

        // 关闭图形上下文
        g.dispose();

        // 将验证码存储到会话中
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha.toString());

        // 设置响应头
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        // 输出图像（内存缓冲，避免文件缓存）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", baos);
        byte[] data = baos.toByteArray();
        response.setContentLength(data.length);
        OutputStream os = response.getOutputStream();
        os.write(data);
        os.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}