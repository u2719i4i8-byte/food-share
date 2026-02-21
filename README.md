[项目文件结构说明.md](https://github.com/user-attachments/files/25433605/default.md)
# 橘子美食网 - 项目说明文档

## 项目概述

橘子美食网是一个美食分享平台，用户可以浏览、发布、收藏美食内容，管理饮食记录，进行营养分析。系统包含用户端和管理端两个部分。

---

## 项目结构

```
food-share/
├── food-share-view/          # 前端项目（Vue.js）
├── food-share-api/           # 后端项目（Spring Boot）
└── 项目文件结构说明.md        # 本说明文档
```

---

## 前端项目（food-share-view）

### 技术栈

- **框架**: Vue 2.6.x
- **路由**: Vue Router 3.x
- **UI组件**: Element UI 2.15.x
- **HTTP请求**: Axios 0.21.x
- **样式**: SCSS / Sass
- **弹窗**: SweetAlert2
- **富文本编辑器**: WangEditor
- **图表**: ECharts 4.x

### 目录结构

```
food-share-view/
├── public/                    # 静态资源
│   ├── index.html            # 入口HTML
│   ├── logo.png              # 网站Logo
│   └── bag.png               # Logo装饰
│
├── src/
│   ├── assets/               # 资源文件
│   │   ├── css/              # 样式文件
│   │   │   ├── theme.scss    # 全局主题变量
│   │   │   ├── admin-page.scss # 管理后台样式
│   │   │   ├── dialog.scss   # 弹窗样式
│   │   │   └── input.scss    # 输入框样式
│   │   └── img/              # 图片资源
│   │
│   ├── components/           # 公共组件
│   │   ├── Logo.vue          # Logo组件
│   │   └── Editor.vue        # 富文本编辑器
│   │
│   ├── router/               # 路由配置
│   │   └── index.js          # 路由定义
│   │
│   ├── utils/                # 工具函数
│   │   ├── request.js        # Axios封装
│   │   ├── storage.js        # Token管理
│   │   └── data.js           # 数据处理工具
│   │
│   ├── views/                # 页面组件
│   │   ├── login/            # 登录模块
│   │   │   └── Login.vue     # 登录页面
│   │   │
│   │   ├── register/         # 注册模块
│   │   │   └── Register.vue  # 注册页面
│   │   │
│   │   ├── user/             # 用户端页面
│   │   │   ├── Main.vue      # 用户主页布局
│   │   │   ├── Home.vue      # 首页（发现页）
│   │   │   ├── Gourmet.vue   # 美食集锦页
│   │   │   ├── GourmetDetail.vue # 美食详情页
│   │   │   ├── Save.vue      # 收藏夹页
│   │   │   ├── Mine.vue      # 个人中心页
│   │   │   ├── WhoLikedMe.vue # 谁点赞了我
│   │   │   ├── CreateGourmet.vue # 发布美食页
│   │   │   ├── EditGourmet.vue # 编辑美食页
│   │   │   ├── DietHistory.vue # 饮食管理页
│   │   │   ├── Nutriment.vue # 营养分析页
│   │   │   ├── Message.vue   # 消息通知页
│   │   │   ├── ResetPwd.vue  # 重置密码页
│   │   │   └── Person.vue    # 个人信息编辑页
│   │   │
│   │   └── admin/            # 管理端页面
│   │       ├── Main.vue      # 管理主页布局
│   │       ├── Home.vue      # 管理首页（仪表盘）
│   │       ├── UserManage.vue # 用户管理
│   │       ├── GourmetManage.vue # 美食管理
│   │       ├── CategoryManage.vue # 分类管理
│   │       ├── EvaluationsManage.vue # 评论管理
│   │       ├── CookbookManage.vue # 食谱管理
│   │       ├── NutrimentManage.vue # 营养素管理
│   │       ├── DietHistoryManage.vue # 饮食记录管理
│   │       └── ContentNetManage.vue # 内容分享管理
│   │
│   ├── App.vue               # 根组件
│   └── main.js               # 入口文件
│
├── vue.config.js             # Vue配置
└── package.json              # 依赖配置
```

### 主要页面功能

#### 用户端

| 页面 | 路由 | 功能说明 |
|------|------|----------|
| 首页 | `/home` | 分类导航、推荐美食、搜索功能 |
| 美食集锦 | `/gourmet` | 美食列表、分类筛选、搜索 |
| 美食详情 | `/gourmetDetail` | 美食详情、评论、收藏 |
| 收藏夹 | `/save` | 我的收藏列表 |
| 个人中心 | `/mine` | 个人信息、发布数量统计 |
| 发布美食 | `/createGourmet` | 发布新美食 |
| 饮食管理 | `/dietHistory` | 饮食记录管理 |
| 营养分析 | `/nutriment` | 营养数据分析 |
| 消息通知 | `/message` | 系统消息通知 |

#### 管理端

| 页面 | 路由 | 功能说明 |
|------|------|----------|
| 仪表盘 | `/admin/home` | 数据统计概览 |
| 用户管理 | `/admin/userManage` | 用户增删改查 |
| 美食管理 | `/admin/gourmetManage` | 美食内容管理 |
| 分类管理 | `/admin/categoryManage` | 分类增删改查 |
| 评论管理 | `/admin/evaluationsManage` | 评论管理 |

---

## 后端项目（food-share-api）

### 技术栈

- **框架**: Spring Boot 2.2.4
- **ORM**: MyBatis 2.1.2
- **数据库**: MySQL 8.0.x
- **认证**: JWT（java-jwt 3.4.0 / jjwt 0.9.0）
- **工具**: Lombok、EasyExcel、FastJSON2
- **推荐算法**: Mahout

### 目录结构

```
food-share-api/
├── src/main/java/cn/kmbeast/
│   │
│   ├── controller/           # 控制器层
│   │   ├── UserController.java      # 用户接口
│   │   ├── GourmetController.java   # 美食接口
│   │   ├── CategoryController.java  # 分类接口
│   │   ├── InteractionController.java # 互动接口
│   │   ├── EvaluationsController.java # 评论接口
│   │   ├── RecommendController.java # 推荐接口
│   │   ├── DietHistoryController.java # 饮食记录接口
│   │   ├── NutrimentController.java # 营养素接口
│   │   ├── CookbookController.java  # 食谱接口
│   │   ├── ContentNetController.java # 内容分享接口
│   │   └── FileController.java      # 文件上传接口
│   │
│   ├── service/              # 业务逻辑层
│   │   ├── impl/             # 实现类
│   │   └── *.java            # 接口定义
│   │
│   ├── mapper/               # 数据访问层
│   │   └── *.java            # Mapper接口
│   │
│   ├── pojo/                 # 实体类
│   │   ├── entity/           # 数据库实体
│   │   ├── vo/               # 视图对象
│   │   ├── dto/              # 数据传输对象
│   │   │   ├── query/        # 查询参数
│   │   │   └── update/       # 更新参数
│   │   ├── api/              # API响应封装
│   │   └── em/               # 枚举类
│   │
│   ├── config/               # 配置类
│   │   └── WebConfig.java    # Web配置
│   │
│   ├── Interceptor/          # 拦截器
│   │   └── JwtInterceptor.java # JWT认证
│   │
│   ├── aop/                  # 切面编程
│   │   ├── Protector.java    # 权限注解
│   │   ├── ProtectorAspect.java # 权限切面
│   │   ├── Pager.java        # 分页注解
│   │   └── PagerAspect.java  # 分页切面
│   │
│   ├── context/              # 上下文
│   │   └── LocalThreadHolder.java # 线程本地存储
│   │
│   ├── utils/                # 工具类
│   │   ├── JwtUtil.java      # JWT工具
│   │   ├── DateUtil.java     # 日期工具
│   │   └── IdFactoryUtil.java # ID生成
│   │
│   └── FoodShareApplication.java # 启动类
│
├── src/main/resources/
│   ├── application.yml       # 应用配置
│   ├── mapper/*.xml          # MyBatis映射文件
│   └── data/*.sql            # 数据初始化脚本
│
└── pom.xml                   # Maven配置
```

### 主要API接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | POST | 用户登录 |
| `/user/register` | POST | 用户注册 |
| `/user/queryList` | POST | 用户列表 |
| `/gourmet/queryList` | POST | 美食列表 |
| `/gourmet/save` | POST | 发布美食 |
| `/gourmet/update` | POST | 更新美食 |
| `/category/query` | POST | 分类列表 |
| `/interaction/upvote` | POST | 点赞 |
| `/interaction/save` | POST | 收藏 |
| `/evaluations/save` | POST | 发表评论 |
| `/recommend/{num}` | GET | 推荐美食 |

---

## 数据库设计

### 主要数据表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `category` | 美食分类表 |
| `gourmet` | 美食内容表 |
| `interaction` | 互动表（点赞、收藏、浏览、评分） |
| `evaluations` | 评论表 |
| `evaluations_upvote` | 评论点赞表 |
| `cookbook` | 食谱表 |
| `nutriment` | 营养素表 |
| `diet_history` | 饮食记录表 |
| `content_net` | 内容分享表 |

### 分类数据

系统预设了五大分类组：

| 分类组 | 包含分类 |
|--------|----------|
| 菜系 | 家常菜、川菜、粤菜、湘菜、鲁菜、江浙菜、西北菜、东北菜、闽南菜、云南菜、新疆菜、韩式、日式、泰式、意式、美式 |
| 场景 | 早餐、下午茶、夜宵、一人食、家庭聚餐、亲子友好、朋友小聚、节日家宴、户外野餐 |
| 食材 | 海鲜、肉食、素食、菌菇、豆制品、根茎类、叶菜类、禽蛋类、谷物、坚果、时令水果 |
| 做法 | 烧烤、火锅、煎炸、蒸煮、焖炖、凉拌、烘焙、爆炒、煲汤 |
| 特色 | 非遗、网红、新手友好、减脂轻食、下饭神器、下酒小菜、复刻经典 |

---

## 环境配置

### 环境要求

| 环境 | 版本要求 |
|------|----------|
| JDK | 1.8+ |
| Node.js | 14.x - 16.x |
| npm | 6.x+ |
| Maven | 3.6+ |
| MySQL | 5.7+ / 8.0+ |

### 配置步骤

#### 1. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE food_share DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 导入数据
USE food_share;
SOURCE food_share.sql;
```

#### 2. 后端配置

修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/food_share
    username: root
    password: your_password
server:
  port: 21090
```

#### 3. 前端配置

修改 `vue.config.js`：

```javascript
module.exports = {
  devServer: {
    port: 21092,
    proxy: {
      '/api': {
        target: 'http://localhost:21090',
        changeOrigin: true
      }
    }
  }
}
```

---

## 运行说明

### 启动后端

```bash
cd food-share-api
mvn spring-boot:run
# 或在IDE中运行 FoodShareApplication.java
# 服务端口: 21090
```

### 启动前端

```bash
cd food-share-view
npm install
npm run dev
# 访问: http://localhost:21092
```

### 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |
| 用户 | user | user123 |

---

## 端口说明

| 服务 | 端口 |
|------|------|
| 前端 | 21092 |
| 后端 | 21090 |
| MySQL | 3306 |

---

## 功能特性

### 用户端功能

- 用户注册、登录、个人信息管理
- 美食浏览、搜索、分类筛选
- 美食详情查看、收藏
- 评论发布、回复、删除
- 发布自己的美食内容
- 饮食记录管理
- 营养数据分析
- 消息通知

### 管理端功能

- 数据统计仪表盘
- 用户管理
- 美食内容管理
- 分类管理
- 评论管理
- 食谱管理
- 营养素管理
- 饮食记录管理

### 推荐系统

系统采用多策略推荐算法，按优先级依次执行：

1. **协同过滤推荐**（优先级最高）
   - 基于用户综合行为数据（浏览、点赞、收藏、评分）
   - 使用 Mahout 库实现基于用户的协同过滤
   - 皮尔逊相关系数计算用户相似度
   - 行为权重计算：浏览次数×0.1 + 点赞×2 + 收藏×3 + 评分×2

2. **分类偏好推荐**（次优先级）
   - 分析用户历史行为偏好分类
   - 从偏好分类中推荐未交互的内容
   - 按用户对各分类的综合得分排序

3. **热门推荐**（第三优先级）
   - 基于内容热度排序
   - 热度计算：浏览量 + 点赞数×10 + 收藏数×15

4. **随机推荐**（兜底策略）
   - 当以上策略都无法生成推荐时使用
   - 随机返回已审核通过的美食内容

---

## 常见问题

| 问题 | 解决方案 |
|------|----------|
| Node Sass 编译失败 | 使用 `npm install sass sass-loader --save-dev` |
| OpenSSL 错误 | 配置 `NODE_OPTIONS=--openssl-legacy-provider` |
| Maven 下载慢 | 配置阿里云镜像 |
| Lombok 不生效 | IDE 安装 Lombok 插件 |
| 数据库连接失败 | 检查 MySQL 服务和配置 |

---

## 开发团队

橘子美食网 - 毕业设计项目
