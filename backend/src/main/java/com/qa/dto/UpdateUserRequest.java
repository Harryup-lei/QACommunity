package com.qa.dto;

import lombok.Data;

/**
 * 更新用户信息请求
 */
@Data
public class UpdateUserRequest {

    private String avatar;

    private String bio;
}