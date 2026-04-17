package com.qa.controller;

import com.qa.common.Result;
import com.qa.common.PageResult;
import com.qa.dto.UserDTO;
import com.qa.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 积分 API 控制器
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    @GetMapping("/ranking")
    public Result<PageResult<UserDTO>> getPointsRanking(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            var result = pointsService.getPointsRanking(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{userId}/history")
    public Result<PageResult<Object>> getUserPointsHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            var result = pointsService.getUserPointsHistory(userId, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
