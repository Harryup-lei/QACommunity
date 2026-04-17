package com.qa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 回答 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private Long id;
    private String content;
    private Long questionId;
    private Long authorId;
    private UserDTO author;
    private Integer votes;
    private Boolean isAccepted;
    private Boolean userVoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

/**
 * 创建回答请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CreateAnswerRequest {
    private String content;
    private Long questionId;
}
