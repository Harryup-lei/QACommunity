package com.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private Integer points;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
