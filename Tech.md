# 问答社区系统 - 技术选型文档

## 前端技术
- **框架**: Vue 3.x
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Redux / Pinia 
- **HTTP 客户端**: Axios
- **Markdown 编辑器**: EasyMDE / Vditor
- **富文本预览**: markdown-it

## 后端技术
- **框架**: Java Spring Boot 3.x
- **Web 框架**: Spring Web MVC
- **ORM**: MyBatis-Plus 
- **验证**: Spring Validation
- **API 文档**: Swagger/Springfox
- **Java 版本**: JDK 17

## 数据库
- **主数据库**: MySQL 8.0+
- **连接地址**: localhost:3306
- **字符集**: utf8mb4
- **连接池**:  HikariCP

## 中间件
### Redis 缓存
- **用途**: 缓存热点问题、用户信息、积分数据
- **地址**: localhost:6379
- **过期策略**: 针对不同数据设置合理的 TTL

### RocketMQ 消息队列（可选）
- **用途**: 积分系统异步处理、邮件通知、数据同步
- **地址**: localhost:9876（nameserver）

## 项目架构
```
backend/
├── src/main/java/com/qa/
│   ├── controller/      # API 控制器
│   ├── service/         # 业务逻辑
│   ├── repository/      # 数据访问层
│   ├── entity/          # 数据模型
│   ├── dto/             # 数据传输对象
│   └── utils/           # 工具类
└── resources/
    └── application.yml  # 配置文件

frontend/
├── src/
│   ├── components/      # React 组件
│   ├── pages/           # 页面
│   ├── services/        # API 调用
│   └── store/           # 状态管理
└── public/              # 静态资源
```

## API 设计基础
- **基础 URL**: /api/v1
- **认证方式**: JWT Token
- **响应格式**: JSON
- **分页**: 默认每页 20 条

## 部署环境
- **开发环境**: localhost
- **生产环境**: Docker + Docker Compose