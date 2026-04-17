package com.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    @TableField("password")
    @JsonIgnore
    private String password;

    @TableField("avatar")
    private String avatar;

    @TableField("bio")
    private String bio;

    @TableField("points")
    @Builder.Default
    private Integer points = 0;

    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 检查用户是否被禁用
     */
    public boolean isDisabled() {
        return status != null && status == 0;
    }

    /**
     * 检查用户是否活跃
     */
    public boolean isActive() {
        return status != null && status == 1;
    }
}