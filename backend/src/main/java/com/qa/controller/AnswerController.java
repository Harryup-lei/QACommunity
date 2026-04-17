package com.qa.controller;

import com.qa.common.Result;
import com.qa.common.PageResult;
import com.qa.dto.AnswerDTO;
import com.qa.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 回答 API 控制器
 */
@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<Object> publishAnswer(@RequestParam Long questionId,
                                        @RequestParam String content,
                                        Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            var answer = answerService.publishAnswer(questionId, userId, content);
            return Result.success(answer);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<PageResult<AnswerDTO>> getAnswerList(
            @RequestParam Long questionId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            Authentication authentication) {
        try {
            Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
            var result = answerService.getAnswerList(questionId, pageNum, pageSize, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<AnswerDTO>> getUserAnswers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            Authentication authentication) {
        try {
            Long currentUserId = authentication != null ? (Long) authentication.getPrincipal() : null;
            var result = answerService.getUserAnswers(userId, pageNum, pageSize, currentUserId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Object> updateAnswer(@PathVariable Long id,
                                      @RequestParam String content,
                                      Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            var answer = answerService.updateAnswer(id, content, userId);
            return Result.success(answer);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<String> deleteAnswer(@PathVariable Long id,
                                      Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            answerService.deleteAnswer(id, userId);
            return Result.success("Answer deleted successfully");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
