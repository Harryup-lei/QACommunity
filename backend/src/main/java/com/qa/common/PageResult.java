package com.qa.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Long pages;
    private Long current;
    private Long pageSize;

    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long pageSize) {
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(records, total, pages, current, pageSize);
    }
}
