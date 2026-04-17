package com.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 积分日志 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsLogDTO {
    private Long id;
    private Integer points;
    private String operationType;
    private Long relatedId;
    private LocalDateTime createdAt;
}
