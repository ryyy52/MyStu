<%-- 
    管理员仪表盘页面
    功能：显示电商系统的统计数据和图表分析
    主要组件：
    1. 顶部导航栏 - 提供系统功能入口
    2. 统计卡片 - 显示关键业务指标
    3. 分类商品数量图表 - 使用ECharts展示各分类商品占比
    
    依赖：
    - ECharts 5.4.3 - 用于数据可视化
    - 后端提供的数据：
      - ${userCount} - 用户总数
      - ${orderCount} - 订单总数
      - ${totalSales} - 总销售额
      - ${lowStockCount} - 库存紧张商品数量
      - ${categoryNames} - 分类名称数组
      - ${productCounts} - 分类商品数量数组
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <!-- 页面元数据 -->
    <meta charset="UTF-8">
    <title>管理员仪表盘</title>
    <style>
        /* 全局样式重置和基础样式 */
        body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2;}
        
        /* 页面头部样式 */
        .header {background-color: #333; color: white; padding: 10px; text-align: center;}
        
        /* 导航栏样式 */
        .nav {background-color: #3498db; overflow: hidden;}
        .nav a {float: left; display: block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}
        .nav a:hover {background-color: #2980b9;}
        .nav-right {float: right;}
        
        /* 容器样式 */
        .container {padding: 20px;}
        
        /* 按钮样式 */
        .btn {display: inline-block; padding: 8px 15px; background-color: #3498db; color: white; text-decoration: none; border: none; border-radius: 3px; cursor: pointer;}
        .btn:hover {background-color: #2980b9;}
        
        /* 统计卡片网格布局 */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        /* 统计卡片样式 */
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.08);
            text-align: center;
        }
        
        /* 统计数值样式 */
        .stat-value {
            font-size: 2em;
            font-weight: bold;
            color: #3498db;
        }
        
        /* 统计标签样式 */
        .stat-label {
            color: #666;
            margin-top: 5px;
        }
        
        /* 图表容器样式 */
        .chart-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
        
        /* 图表标题样式 */
        .chart-title {
            font-size: 1.2em;
            font-weight: bold;
            margin-bottom: 20px;
            color: #333;
        }
        
        /* 图表画布样式 */
        #categoryChart {
            width: 100%;
            height: 400px;
        }
    </style>
    <!-- 引入 ECharts 5.4.3 库，用于数据可视化 -->
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
</head>
<body>
    <!-- 页面头部 -->
    <div class="header">
        <h1>电商网站 - 管理员仪表盘</h1>
    </div>

    <!-- 导航栏 -->
    <div class="nav">
        <!-- 左侧导航链接 -->
        <a href="${pageContext.request.contextPath}/index.jsp">首页</a>               <!-- 系统首页 -->
        <a href="${pageContext.request.contextPath}/product/list">商品列表</a>      <!-- 商品管理 -->
        <a href="${pageContext.request.contextPath}/category/list">分类管理</a>      <!-- 分类管理 -->
        
        <!-- 右侧导航链接 -->
        <div class="nav-right">
            <a href="${pageContext.request.contextPath}/cart/view">购物车</a>        <!-- 用户购物车 -->
            <a href="${pageContext.request.contextPath}/order/list">订单</a>         <!-- 订单管理 -->
            <a href="${pageContext.request.contextPath}/user/profile">个人中心</a>    <!-- 用户个人中心 -->
            <a href="${pageContext.request.contextPath}/user/logout">退出登录</a>    <!-- 退出登录 -->
        </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="container">
        <h2>统计仪表盘</h2>
        
        <!-- 统计卡片区域 -->
        <div class="stats-grid">
            <!-- 用户总数卡片 -->
            <div class="stat-card">
                <div class="stat-value">${userCount}</div>  <!-- 显示用户总数 -->
                <div class="stat-label">用户总数</div>
            </div>
            
            <!-- 订单总数卡片 -->
            <div class="stat-card">
                <div class="stat-value">${orderCount}</div> <!-- 显示订单总数 -->
                <div class="stat-label">订单总数</div>
            </div>
            
            <!-- 总销售额卡片 -->
            <div class="stat-card">
                <div class="stat-value">¥${totalSales}</div> <!-- 显示总销售额，带货币符号 -->
                <div class="stat-label">总销售额</div>
            </div>
            
            <!-- 库存紧张商品卡片 -->
            <div class="stat-card">
                <div class="stat-value">${lowStockCount}</div> <!-- 显示库存紧张的商品数量 -->
                <div class="stat-label">库存紧张商品</div>
            </div>
        </div>
        
        <!-- 图表容器区域 -->
        <div class="chart-container">
            <div class="chart-title">各分类商品数量占比</div>
            <!-- ECharts图表容器 -->
            <div id="categoryChart"></div>
        </div>
    </div>

    <!-- ECharts图表初始化和配置脚本 -->
    <script>
        // 1. 初始化 ECharts 实例，绑定到指定的DOM元素
        var myChart = echarts.init(document.getElementById('categoryChart'));
        
        // 2. 从后端获取数据
        var categoryNames = ${categoryNames};  // 分类名称数组
        var productCounts = ${productCounts};  // 各分类商品数量数组
        
        // 3. 配置ECharts图表选项
        var option = {
            // 图表标题
            title: {
                text: '各分类商品数量',   // 图表主标题
                left: 'center'           // 标题居中显示
            },
            // 提示框配置
            tooltip: {
                trigger: 'item',         // 触发方式：点击或悬停在数据项上
                formatter: '{a} <br/>{b}: {c} ({d}%)'  // 提示框格式：系列名称 <br/>分类名称: 数量 (百分比)
            },
            // 图例配置
            legend: {
                orient: 'vertical',      // 图例垂直排列
                left: 'left',            // 图例位于左侧
                data: categoryNames      // 图例数据为分类名称
            },
            // 系列配置
            series: [
                {
                    name: '商品数量',     // 系列名称
                    type: 'pie',          // 图表类型：饼图
                    radius: '50%',         // 饼图半径
                    center: ['50%', '60%'],  // 饼图中心位置
                    // 数据处理：将分类名称和数量转换为ECharts所需格式
                    data: categoryNames.map(function(name, index) {
                        return {
                            value: productCounts[index],  // 商品数量
                            name: name                    // 分类名称
                        };
                    }),
                    // 高亮配置：鼠标悬停时的样式
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,          // 阴影模糊度
                            shadowOffsetX: 0,         // 阴影X偏移
                            shadowColor: 'rgba(0, 0, 0, 0.5)'  // 阴影颜色
                        }
                    }
                }
            ]
        };
        
        // 4. 将配置应用到图表
        myChart.setOption(option);
        
        // 5. 添加窗口大小变化事件监听，实现图表响应式
        window.addEventListener('resize', function() {
            myChart.resize();  // 调整图表大小以适应容器
        });
    </script>
</body>
</html>