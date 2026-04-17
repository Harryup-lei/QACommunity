# QA社区系统 - 完整项目代码

## 项目概述

QA社区是一个功能完整的在线问答系统，支持问题发布、回答、点赞、积分管理等功能。采用 Spring Boot + Vue3 技术栈，提供现代化的用户界面和高性能的后端服务。

## 项目结构

```
QACommunity/
├── backend/                          # 后端项目
│   ├── pom.xml                       # Maven 配置
│   ├── src/main/java/com/qa/
│   │   ├── QaCommunityApplication.java     # 主应用类
│   │   ├── controller/               # API 控制器
│   │   │   ├── UserController.java
│   │   │   ├── QuestionController.java
│   │   │   ├── AnswerController.java
│   │   │   ├── VoteController.java
│   │   │   └── PointsController.java
│   │   ├── service/                  # 业务逻辑接口
│   │   ├── service/impl/             # 业务逻辑实现
│   │   ├── repository/               # 数据访问层
│   │   ├── entity/                   # 数据实体
│   │   ├── dto/                      # 数据传输对象
│   │   ├── common/                   # 通用类
│   │   └── utils/                    # 工具类
│   └── src/main/resources/
│       └── application.yml           # 应用配置
│
├── frontend/                         # 前端项目
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.js
│       ├── App.vue
│       ├── router/
│       │   └── index.js
│       ├── stores/
│       │   └── user.js
│       ├── services/                 # API 服务
│       │   ├── userService.js
│       │   ├── questionService.js
│       │   ├── answerService.js
│       │   ├── voteService.js
│       │   └── pointsService.js
│       ├── components/
│       │   ├── QuestionList.vue
│       │   └── AnswerList.vue
│       ├── pages/
│       │   ├── QuestionDetail.vue
│       │   └── PublishQuestion.vue
│       └── utils/
│           └── api.js
│
├── sql/
│   └── init.sql                      # 数据库初始化脚本
│
├── PRD.md                            # 产品需求文档
├── Tech.md                           # 技术选型文档
├── database.md                       # 数据库设计文档
└── README.md                         # 本文件
```

## 技术栈

### 后端
- **框架**: Spring Boot 3.0
- **ORM**: JPA + Hibernate
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **认证**: Spring Security + JWT
- **API 文档**: Knife4j/Swagger
- **构建**: Maven
- **Java 版本**: JDK 17

### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **HTTP 客户端**: Axios
- **Markdown 编辑器**: EasyMDE
- **Markdown 渲染**: marked

## 核心功能

### 1. 用户管理
- ✅ 用户注册和登录
- ✅ JWT Token 认证
- ✅ 用户信息管理
- ✅ 积分系统

### 2. 问题管理
- ✅ 发布问题（扣 5 积分）
- ✅ 编辑和删除问题
- ✅ 问题搜索
- ✅ 多种排序（最新、热门、未解决）
- ✅ 问题标签

### 3. 回答系统
- ✅ 发布回答（获得 10 积分）
- ✅ 编辑和删除回答
- ✅ 采纳答案（回答者获得 30 积分）
- ✅ 问题状态管理

### 4. 点赞功能
- ✅ 点赞问题和回答
- ✅ 取消点赞
- ✅ 点赞后目标获得积分
- ✅ 每用户每目标只能点赞一次

### 5. 积分系统
- ✅ 自动积分计算
- ✅ 积分日志追踪
- ✅ 积分排行榜
- ✅ 用户积分历史查询

## 快速开始

### 前置需求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Redis（可选）

### 后端启动

1. **配置数据库**
   ```sql
   mysql -h localhost -u root -p < sql/init.sql
   ```

2. **修改数据库配置** `backend/src/main/resources/application.yml`
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/qa_community
       username: root
       password: your_password
   ```

3. **启动后端**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

   后端将在 `http://localhost:8080` 启动

### 前端启动

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **启动开发服务器**
   ```bash
   npm run dev
   ```

   前端将在 `http://localhost:5173` 启动

## API 文档

### 用户 API
- `POST /api/v1/users/register` - 用户注册
- `POST /api/v1/users/login` - 用户登录
- `GET /api/v1/users/:id` - 获取用户信息
- `PUT /api/v1/users/:id` - 更新用户信息
- `GET /api/v1/users/ranking` - 积分排行榜

### 问题 API
- `POST /api/v1/questions` - 发布问题
- `GET /api/v1/questions` - 获取问题列表
- `GET /api/v1/questions/:id` - 获取问题详情
- `PUT /api/v1/questions/:id` - 编辑问题
- `DELETE /api/v1/questions/:id` - 删除问题
- `GET /api/v1/questions/search` - 搜索问题

### 回答 API
- `POST /api/v1/answers` - 发布回答
- `GET /api/v1/answers` - 获取回答列表
- `PUT /api/v1/answers/:id` - 编辑回答
- `DELETE /api/v1/answers/:id` - 删除回答

### 点赞 API
- `POST /api/v1/votes` - 点赞
- `DELETE /api/v1/votes` - 取消点赞
- `GET /api/v1/votes/check` - 检查是否已点赞

### 积分 API
- `GET /api/v1/points/ranking` - 积分排行榜
- `GET /api/v1/points/:userId/history` - 用户积分历史

## 数据库设计

### 主要表结构

**users 表**
- id: 用户 ID
- username: 用户名
- email: 邮箱
- password: 密码哈希
- points: 积分
- status: 账户状态

**questions 表**
- id: 问题 ID
- title: 标题
- content: 内容（Markdown）
- tags: 标签（JSON）
- author_id: 提问者 ID
- votes: 点赞数
- is_solved: 是否已解决

**answers 表**
- id: 回答 ID
- content: 内容（Markdown）
- question_id: 问题 ID
- author_id: 回答者 ID
- is_accepted: 是否被采纳

**votes 表**
- user_id: 用户 ID
- target_type: 目标类型（question/answer）
- target_id: 目标 ID

**points_log 表**
- user_id: 用户 ID
- points: 积分变化
- operation_type: 操作类型
- related_id: 关联 ID

## 积分规则

| 操作 | 积分 | 说明 |
|------|------|------|
| 发布问题 | -5 | 提问者扣分 |
| 发布回答 | +10 | 回答者加分 |
| 回答被采纳 | +30 | 额外奖励 |
| 问题被点赞 | +5/次 | 提问者加分 |
| 回答被点赞 | +10/次 | 回答者加分 |

## 部署指南

### Docker 部署

1. **构建后端镜像**
   ```bash
   cd backend
   docker build -t qa-community-backend .
   ```

2. **构建前端镜像**
   ```bash
   cd frontend
   docker build -t qa-community-frontend .
   ```

3. **使用 Docker Compose**
   ```bash
   docker-compose up -d
   ```

## 常见问题

### Q: 如何重置密码？
A: 目前系统没有重置密码功能，需要手工在数据库中更新密码哈希。

### Q: 如何导出数据？
A: 可以使用 MySQL 导出命令或数据库管理工具导出数据。

### Q: 如何扩展功能？
A: 
1. 在 `service` 中定义新的接口
2. 在 `service/impl` 中实现业务逻辑
3. 在 `controller` 中添加 REST API 端点
4. 前端创建相应的页面和服务

## 性能优化

- ✅ 使用数据库索引优化查询
- ✅ Redis 缓存热点数据
- ✅ 分页加载，避免一次加载大量数据
- ✅ 前端懒加载和代码分割
- ✅ API 响应速度 < 500ms

## 安全考虑

- ✅ 密码使用 bcrypt 加密
- ✅ JWT Token 认证
- ✅ SQL 注入防护
- ✅ CSRF 保护
- ✅ XSS 防护（Markdown 内容过滤）
- ✅ 速率限制

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或联系开发团队。

---

**项目创建时间**: 2026年
**最后更新**: 2026年
**版本**: 1.0.0
