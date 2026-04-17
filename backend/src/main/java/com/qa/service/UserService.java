package com.qa.service;

import com.qa.dto.UserDTO;
import com.qa.entity.User;

/**
 * 用户业务服务
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User registerUser(String username, String email, String password);
    
    /**
     * 用户登录
     */
    String loginUser(String username, String password);
    
    /**
     * 获取用户信息
     */
    UserDTO getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    User updateUserInfo(Long userId, String avatar, String bio);
    
    /**
     * 获取用户积分
     */
    Integer getUserPoints(Long userId);
    
    /**
     * 更新用户积分
     */
    void updateUserPoints(Long userId, Integer points);
    
    /**
     * 获取积分排行榜
     */
    java.util.List<UserDTO> getPointsRanking(Integer limit);
    
    /**
     * 查询用户是否存在
     */
    boolean userExists(String username);
}
