package com.qa.controller;

import com.qa.common.Result;
import com.qa.common.PageResult;
import com.qa.dto.QuestionDTO;
import com.qa.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 问题 API 控制器
 */
@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<Object> publishQuestion(@RequestParam String title,
                                          @RequestParam String content,
                                          @RequestParam(required = false) String tags,
                                          Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            var question = questionService.publishQuestion(userId, title, content, tags);
            return Result.success(question);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<QuestionDTO> getQuestionDetail(@PathVariable Long id,
                                                  Authentication authentication) {
        try {
            Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
            return Result.success(questionService.getQuestionDetail(id, userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<PageResult<QuestionDTO>> getQuestionList(
            @RequestParam(required = false, defaultValue = "latest") String sortBy,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Boolean solved,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            Authentication authentication) {
        try {
            Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
            var result = questionService.getQuestionList(sortBy, tags, solved, pageNum, pageSize, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result<PageResult<QuestionDTO>> searchQuestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            Authentication authentication) {
        try {
            Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
            var result = questionService.searchQuestions(keyword, pageNum, pageSize, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<QuestionDTO>> getUserQuestions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            Authentication authentication) {
        try {
            Long currentUserId = authentication != null ? (Long) authentication.getPrincipal() : null;
            var result = questionService.getUserQuestions(userId, pageNum, pageSize, currentUserId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Object> updateQuestion(@PathVariable Long id,
                                        @RequestParam String title,
                                        @RequestParam String content,
                                        @RequestParam(required = false) String tags,
                                        Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            var question = questionService.updateQuestion(id, title, content, tags, userId);
            return Result.success(question);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<String> deleteQuestion(@PathVariable Long id,
                                        Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            questionService.deleteQuestion(id, userId);
            return Result.success("Question deleted successfully");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{questionId}/accept/{answerId}")
    @PreAuthorize("isAuthenticated()")
    public Result<String> acceptAnswer(@PathVariable Long questionId,
                                      @PathVariable Long answerId,
                                      Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            questionService.acceptAnswer(questionId, answerId, userId);
            return Result.success("Answer accepted successfully");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
