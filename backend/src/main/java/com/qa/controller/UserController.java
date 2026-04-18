package com.qa.controller;

import com.qa.common.Result;
import com.qa.dto.LoginRequest;
import com.qa.dto.RegisterRequest;
import com.qa.dto.UpdateUserRequest;
import com.qa.dto.UserDTO;
import com.qa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 用户 API 控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            var user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
            );
            var dto = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(
                request.getUsername(),
                request.getPassword()
            );
            return Result.success(token);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<UserDTO> getUserInfo(@PathVariable Long id) {
        try {
            return Result.success(userService.getUserInfo(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<UserDTO> updateUserInfo(@PathVariable Long id,
                                          @RequestBody UpdateUserRequest request) {
        try {
            String avatar = request.getAvatar();
            String bio = request.getBio();
            var user = userService.updateUserInfo(id, avatar, bio);
            var dto = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .avatar(user.getAvatar())
                    .bio(user.getBio())
                    .points(user.getPoints())
                    .build();
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/points")
    public Result<Integer> getUserPoints(@PathVariable Long id) {
        try {
            return Result.success(userService.getUserPoints(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/ranking")
    public Result<List<UserDTO>> getPointsRanking(@RequestParam(defaultValue = "100") Integer limit) {
        try {
            return Result.success(userService.getPointsRanking(limit));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传头像文件
     */
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            return userService.uploadAvatar(file);
        } catch (Exception e) {
            return Result.error(1, "文件上传失败: " + e.getMessage());
        }
    }
}