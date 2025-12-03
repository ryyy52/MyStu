<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员仪表盘</title>
    <style>
        body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2;}
        .header {background-color: #333; color: white; padding: 10px; text-align: center;}
        .nav {background-color: #3498db; overflow: hidden;}
        .nav a {float: left; display: block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}
        .nav a:hover {background-color: #2980b9;}
        .nav-right {float: right;}
        .container {padding: 20px;}
        .btn {display: inline-block; padding: 8px 15px; background-color: #3498db; color: white; text-decoration: none; border: none; border-radius: 3px; cursor: pointer;}
        .btn:hover {background-color: #2980b9;}
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.08);
            text-align: center;
        }
        .stat-value {
            font-size: 2em;
            font-weight: bold;
            color: #3498db;
        }
        .stat-label {
            color: #666;
            margin-top: 5px;
        }
        .chart-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.08);
            margin-bottom: 30px;
        }
        .chart-title {
            font-size: 1.2em;
            font-weight: bold;
            margin-bottom: 20px;
            color: #333;
        }
        #categoryChart {
            width: 100%;
            height: 400px;
        }
    </style>
    <!-- 引入 ECharts -->
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
</head>
<body>
    <div class="header">
        <h1>电商网站 - 管理员仪表盘</h1>
    </div>

    <div class="nav">
        <a href="${pageContext.request.contextPath}/index.jsp">首页</a>
        <a href="${pageContext.request.contextPath}/product/list">商品列表</a>
        <a href="${pageContext.request.contextPath}/category/list">分类管理</a>
        <div class="nav-right">
            <a href="${pageContext.request.contextPath}/cart/view">购物车</a>
            <a href="${pageContext.request.contextPath}/order/list">订单</a>
            <a href="${pageContext.request.contextPath}/user/profile">个人中心</a>
            <a href="${pageContext.request.contextPath}/user/logout">退出登录</a>
        </div>
    </div>

    <div class="container">
        <h2>统计仪表盘</h2>
        
        <!-- 统计卡片 -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-value">${userCount}</div>
                <div class="stat-label">用户总数</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${orderCount}</div>
                <div class="stat-label">订单总数</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">¥${totalSales}</div>
                <div class="stat-label">总销售额</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${lowStockCount}</div>
                <div class="stat-label">库存紧张商品</div>
            </div>
        </div>
        
        <!-- 图表容器 -->
        <div class="chart-container">
            <div class="chart-title">各分类商品数量占比</div>
            <div id="categoryChart"></div>
        </div>
    </div>

    <script>
        // 初始化 ECharts 实例
        var myChart = echarts.init(document.getElementById('categoryChart'));
        
        // 准备数据
        var categoryNames = ${categoryNames};
        var productCounts = ${productCounts};
        
        // 配置项
        var option = {
            title: {
                text: '各分类商品数量',
                left: 'center'
            },
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: categoryNames
            },
            series: [
                {
                    name: '商品数量',
                    type: 'pie',
                    radius: '50%',
                    center: ['50%', '60%'],
                    data: categoryNames.map(function(name, index) {
                        return {
                            value: productCounts[index],
                            name: name
                        };
                    }),
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };
        
        // 使用配置项显示图表
        myChart.setOption(option);
        
        // 响应式调整
        window.addEventListener('resize', function() {
            myChart.resize();
        });
    </script>
</body>
</html>