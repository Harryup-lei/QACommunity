package com.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 积分日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("points_log")
public class PointsLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @JsonIgnore
    private User user;

    @TableField("points")
    private Integer points; // 正数增加，负数减少

    @TableField("operation_type")
    private String operationType; // publish_question, answer_question, answer_accepted, vote_question, vote_answer

    @TableField("related_id")
    private Long relatedId; // 关联的问题或回答ID

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}