package com.qa.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 通用响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(1, message, null);
    }

    /**
     * 失败响应（带状态码）
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
