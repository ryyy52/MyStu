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
 * 验证码生成Servlet
 */
public class CaptchaServlet extends HttpServlet {
    // 验证码字符集
    private static final String CAPTCHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 验证码长度
    private static final int CAPTCHA_LENGTH = 4;
    // 图片宽度
    private static final int WIDTH = 120;
    // 图片高度
    private static final int HEIGHT = 40;
    // 字体大小
    private static final int FONT_SIZE = 20;
    // 随机数生成器
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