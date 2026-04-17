package com.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 问题 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private String tags;
    private Long authorId;
    private UserDTO author;
    private Integer views;
    private Integer answersCount;
    private Integer votes;
    private Boolean isSolved;
    private Boolean userVoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

/**
 * 创建问题请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CreateQuestionRequest {
    private String title;
    private String content;
    private String tags; // JSON 数组字符串或逗号分隔
}

/**
 * 问题列表查询 DTO
 */
@Data
class QuestionQueryRequest {
    private String sortBy; // latest, hot, unsolved, views, answers
    private String tags; // 多个标签用逗号分隔
    private Boolean solved; // 筛选是否已解决
    private Integer pageNum;
    private Integer pageSize;
    private String keyword; // 搜索关键词
}
