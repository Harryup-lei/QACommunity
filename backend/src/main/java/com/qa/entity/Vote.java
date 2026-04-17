package com.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 点赞实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("votes")
public class Vote {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    @TableField("target_type")
    private String targetType; // question 或 answer

    @TableField("target_id")
    private Long targetId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}