# 开发指南

## 项目开发流程

### 1. 添加新的 API 端点

**步骤 1**: 在 `service` 接口中定义方法
```java
public interface YourService {
    void yourMethod(Long id);
}
```

**步骤 2**: 在 `service/impl` 中实现
```java
@Service
@RequiredArgsConstructor
public class YourServiceImpl implements YourService {
    // 实现
}
```

**步骤 3**: 在 `controller` 中添加端点
```java
@RestController
@RequestMapping("/your")
public class YourController {
    @GetMapping
    public Result<Object> yourMethod() {
        // 实现
    }
}
```

### 2. 前端调用新 API

**步骤 1**: 在 `services` 中创建服务
```javascript
const service = {
    callYourMethod() {
        return api.get('/your')
    }
}
```

**步骤 2**: 在组件中导入并使用
```javascript
import service from '@/services/yourService'

const result = await service.callYourMethod()
```

## 常用命令

### 后端
```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 启动
mvn spring-boot:run
```

### 前端
```bash
# 安装依赖
npm install

# 开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 代码规范

### Java
- 使用 lombok 减少样板代码
- 使用 @RequiredArgsConstructor 进行依赖注入
- 在 service 层添加事务管理
- 使用 Optional 处理可能的 null 值

### JavaScript/Vue
- 使用 Composition API 编写组件
- 组件名使用 PascalCase
- 使用 scoped 样式避免样式冲突
- 使用 ref 管理响应式状态

## 数据库迁移

如需修改数据库结构，使用 Liquibase 或 Flyway 进行版本管理：

1. 在 `src/main/resources/db/migration/` 创建迁移脚本
2. 命名规约：`V1__Initial_schema.sql`, `V2__Add_new_table.sql`
3. 应用启动时自动执行

## 测试

### 单元测试
```java
@SpringBootTest
public class YourServiceTest {
    @Test
    public void testYourMethod() {
        // 测试代码
    }
}
```

### 集成测试
```bash
mvn verify
```

## 调试

### 后端调试
1. 在 IDE 中设置断点
2. 以 Debug 模式运行
3. 前端调用相关 API 时会在断点处停止

### 前端调试
1. 打开浏览器开发者工具（F12）
2. 在 Console 或 Sources 标签设置断点
3. 刷新页面进行调试

## 性能优化

### 数据库
- 为常用的查询字段添加索引
- 使用分页避免一次加载过多数据
- 合理使用 JOIN 和 LEFT JOIN

### 缓存
- 将热数据存入 Redis
- 设置合理的过期时间
- 考虑使用缓存预热

### API
- 使用 gzip 压缩响应
- 合理设计 API 返回的数据量
- 考虑实现 GraphQL 查询优化

## 常见问题解决

### 连接超时
- 检查数据库是否启动
- 检查防火墙设置
- 增加连接超时时间

### 前端跨域错误
- 确保后端配置了 CORS
- 检查代理配置是否正确

### 密码编码错误
- 更新 BCrypt 密钥盐值
- 重新编码所有密码

