-- QA 社区数据库初始化脚本
-- 适用于 MySQL 8.x
-- 包含表结构建表语句和初始数据

-- 创建数据库
CREATE DATABASE IF NOT EXISTS qa_community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qa_community;

-- ===========================
-- 表结构
-- ===========================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(500),
    bio VARCHAR(500),
    points INT NOT NULL DEFAULT 0 COMMENT '用户积分',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账户状态: 1-正常, 0-禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_points (points DESC),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 问题表
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '问题标题',
    content LONGTEXT NOT NULL COMMENT '问题内容(Markdown格式)',
    tags JSON COMMENT '标签(JSON数组)',
    author_id BIGINT NOT NULL COMMENT '提问者ID',
    views INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    answers_count INT NOT NULL DEFAULT 0 COMMENT '回答数',
    votes INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    is_solved TINYINT NOT NULL DEFAULT 0 COMMENT '是否已解决: 0-否, 1-是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_author_id (author_id),
    INDEX idx_created_at (created_at DESC),
    INDEX idx_votes (votes DESC),
    INDEX idx_is_solved (is_solved),
    INDEX idx_views (views DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';

-- 回答表
CREATE TABLE IF NOT EXISTS answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content LONGTEXT NOT NULL COMMENT '回答内容(Markdown格式)',
    question_id BIGINT NOT NULL COMMENT '所属问题ID',
    author_id BIGINT NOT NULL COMMENT '回答者ID',
    votes INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    is_accepted TINYINT NOT NULL DEFAULT 0 COMMENT '是否被采纳: 0-否, 1-是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id),
    INDEX idx_author_id (author_id),
    INDEX idx_is_accepted (is_accepted),
    INDEX idx_votes (votes DESC),
    INDEX idx_created_at (created_at DESC),
    UNIQUE KEY UK_answers_question_accepted (question_id, is_accepted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回答表';

-- 点赞表
CREATE TABLE IF NOT EXISTS votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型: question/answer',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_target (user_id, target_type, target_id),
    UNIQUE KEY UK_votes_unique (user_id, target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- 积分日志表
CREATE TABLE IF NOT EXISTS points_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    points INT NOT NULL COMMENT '积分变化(正数增加,负数减少)',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    related_id BIGINT COMMENT '关联的问题或回答ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at DESC),
    INDEX idx_operation_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分日志表';

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    count INT DEFAULT 0 COMMENT '使用次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_name (name),
    INDEX idx_count (count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ===========================
-- 初始数据
-- ===========================

-- 初始用户 (密码: password)
-- bcrypt hash for "password"
INSERT INTO users (username, email, password, avatar, bio, points, status) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW3WzD8z2Byh1S9u', NULL, 'Admin user', 1000, 1),
('test_user', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW3WzD8z2Byh1S9u', NULL, 'Test user', 0, 1);

-- 初始标签
INSERT INTO tags (name, count) VALUES
('Java', 0),
('Spring', 0),
('MySQL', 0),
('Vue', 0),
('API', 0),
('Database', 0),
('Frontend', 0),
('Backend', 0);