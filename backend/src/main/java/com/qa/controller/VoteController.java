package com.qa.controller;

import com.qa.common.Result;
import com.qa.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞 API 控制器
 */
@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<String> vote(@RequestParam String targetType,
                               @RequestParam Long targetId,
                               Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            voteService.vote(userId, targetType, targetId);
            return Result.success("Voted successfully");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public Result<String> unvote(@RequestParam String targetType,
                                 @RequestParam Long targetId,
                                 Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            voteService.unvote(userId, targetType, targetId);
            return Result.success("Unvoted successfully");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check")
    public Result<Boolean> checkVote(@RequestParam String targetType,
                                      @RequestParam Long targetId,
                                      Authentication authentication) {
        try {
            Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
            boolean voted = voteService.hasVoted(userId, targetType, targetId);
            return Result.success(voted);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/count")
    public Result<Long> getVoteCount(@RequestParam String targetType,
                                     @RequestParam Long targetId) {
        try {
            long count = voteService.getVoteCount(targetType, targetId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
