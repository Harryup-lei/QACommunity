package com.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 问题实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("questions")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("tags")
    private String tags; // 逗号分隔的标签

    @TableField("author_id")
    private Long authorId;

    @TableField(exist = false)
    @JsonIgnore
    private User author;

    @TableField("views")
    @Builder.Default
    private Integer views = 0;

    @TableField("answers_count")
    @Builder.Default
    private Integer answersCount = 0;

    @TableField("votes")
    @Builder.Default
    private Integer votes = 0;

    @TableField("is_solved")
    @Builder.Default
    private Boolean isSolved = false;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}