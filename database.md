# 问答社区数据库设计

## 数据库基础配置
- **数据库名**: qa_community
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **引擎**: InnoDB

## 用户表（users）
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码哈希',
    avatar VARCHAR(500) COMMENT '头像 URL',
    bio VARCHAR(500) COMMENT '个人签名',
    points INT DEFAULT 0 NOT NULL COMMENT '积分',
    status INT DEFAULT 1 COMMENT '账户状态 1-正常 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_points (points DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 问题表（questions）
```sql
CREATE TABLE questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '问题ID',
    title VARCHAR(255) NOT NULL COMMENT '问题标题',
    content LONGTEXT NOT NULL COMMENT '问题内容（Markdown）',
    tags JSON COMMENT '标签（JSON 数组）',
    author_id BIGINT NOT NULL COMMENT '提问者ID',
    views INT DEFAULT 0 NOT NULL COMMENT '浏览次数',
    answers_count INT DEFAULT 0 NOT NULL COMMENT '回答数',
    votes INT DEFAULT 0 NOT NULL COMMENT '点赞数',
    is_solved BOOLEAN DEFAULT FALSE COMMENT '是否已解决',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    CONSTRAINT fk_questions_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_author_id (author_id),
    INDEX idx_created_at (created_at DESC),
    INDEX idx_votes (votes DESC),
    INDEX idx_is_solved (is_solved),
    INDEX idx_views (views DESC),
    FULLTEXT idx_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 回答表（answers）
```sql
CREATE TABLE answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回答ID',
    content LONGTEXT NOT NULL COMMENT '回答内容（Markdown）',
    question_id BIGINT NOT NULL COMMENT '问题ID',
    author_id BIGINT NOT NULL COMMENT '回答者ID',
    votes INT DEFAULT 0 NOT NULL COMMENT '点赞数',
    is_accepted BOOLEAN DEFAULT FALSE COMMENT '是否被采纳',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id),
    INDEX idx_author_id (author_id),
    INDEX idx_is_accepted (is_accepted),
    INDEX idx_votes (votes DESC),
    INDEX idx_created_at (created_at DESC),
    CONSTRAINT UK_answers_question_accepted UNIQUE KEY (question_id, is_accepted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 点赞表（votes）
```sql
CREATE TABLE votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投票ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type ENUM('question', 'answer') NOT NULL COMMENT '目标类型',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    CONSTRAINT fk_votes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_target (user_id, target_type, target_id),
    UNIQUE KEY UK_votes_unique (user_id, target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 积分日志表（points_log）
```sql
CREATE TABLE points_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    points INT NOT NULL COMMENT '积分变化（正数增加，负数减少）',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型（publish_question, answer_question, answer_accepted 等）',
    related_id BIGINT COMMENT '关联的问题或回答ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    CONSTRAINT fk_points_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at DESC),
    INDEX idx_operation_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 标签表（tags）- 可选规范化
```sql
CREATE TABLE tags (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    count INT DEFAULT 0 COMMENT '使用次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_name (name),
    INDEX idx_count (count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 关键索引策略

| 表 | 索引 | 用途 |
| --- | --- | --- |
| users | username, email | 登录验证 |
| users | points | 积分排行 |
| questions | created_at | 最新问题 |
| questions | votes | 热门问题 |
| questions | is_solved | 未解决问题筛选 |
| questions | author_id | 用户问题列表 |
| answers | question_id | 问题的所有回答 |
| answers | is_accepted | 已采纳答案 |
| votes | user_target | 检查用户是否已点赞 |

## 数据库约束

### 主键约束
- 所有表均使用 BIGINT 自增主键

### 外键约束
- questions.author_id → users.id (ON DELETE CASCADE)
- answers.question_id → questions.id (ON DELETE CASCADE)
- answers.author_id → users.id (ON DELETE CASCADE)
- votes.user_id → users.id (ON DELETE CASCADE)
- points_log.user_id → users.id (ON DELETE CASCADE)

### 唯一约束
- users: username, email
- votes: (user_id, target_type, target_id) - 保证用户仅点赞一次
- tags: name
- answers: (question_id, is_accepted) - 同一问题只能有一个采纳答案

## 性能优化建议

1. **查询优化**
   - 常用查询应利用索引
   - 使用 EXPLAIN 分析查询计划
   - 避免 SELECT *，明确指定字段

2. **缓存层次**
   - 热点问题缓存到 Redis
   - 用户积分、排行数据缓存
   - 标签列表缓存

3. **分表分库考虑**（未来扩展）
   - 按 user_id 分片
   - 问题表按创建时间分区
   - 点赞、积分日志按时间分区

4. **查询优化示例**
   ```sql
   -- 获取热门未解决问题
   SELECT id, title, votes, answers_count FROM questions
   WHERE is_solved = 0
   ORDER BY votes DESC
   LIMIT 20;
   
   -- 获取用户点赞状态
   SELECT EXISTS(
       SELECT 1 FROM votes 
       WHERE user_id = ? AND target_type = 'question' AND target_id = ?
   ) AS user_voted;
   ```