package com.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qa.dto.UserDTO;
import com.qa.entity.User;
import com.qa.mapper.UserMapper;
import com.qa.service.UserService;
import com.qa.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户业务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public User registerUser(String username, String email, String password) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, username);
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new RuntimeException("Username already exists");
        }

        // 检查邮箱是否存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, email);
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .points(0)
                .status(1)
                .build();

        userMapper.insert(user);
        return user;
    }

    @Override
    public String loginUser(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.isDisabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // 生成 JWT Token
        return jwtUtils.generateToken(user.getId(), user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .points(user.getPoints())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public User updateUserInfo(Long userId, String avatar, String bio) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (avatar != null) {
            user.setAvatar(avatar);
        }
        if (bio != null) {
            user.setBio(bio);
        }

        userMapper.updateById(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserPoints(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getPoints();
    }

    @Override
    public void updateUserPoints(Long userId, Integer points) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setPoints(user.getPoints() + points);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getPointsRanking(Integer limit) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1)
                .orderByDesc(User::getPoints)
                .last("LIMIT " + Math.min(limit, 100));

        List<User> users = userMapper.selectList(wrapper);

        return users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .avatar(user.getAvatar())
                        .points(user.getPoints())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
}