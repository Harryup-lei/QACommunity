package com.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 回答实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("answers")
public class Answer {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("content")
    private String content;

    @TableField("question_id")
    private Long questionId;

    @TableField(exist = false)
    @JsonIgnore
    private Question question;

    @TableField("author_id")
    private Long authorId;

    @TableField(exist = false)
    @JsonIgnore
    private User author;

    @TableField("votes")
    @Builder.Default
    private Integer votes = 0;

    @TableField("is_accepted")
    @Builder.Default
    private Boolean isAccepted = false;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}