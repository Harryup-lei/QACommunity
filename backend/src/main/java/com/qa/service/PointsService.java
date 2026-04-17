package com.qa.service;

import com.qa.common.PageResult;
import com.qa.dto.UserDTO;

/**
 * 积分业务服务
 */
public interface PointsService {
    
    /**
     * 添加积分日志
     */
    void addPointsLog(Long userId, Integer points, String operationType, Long relatedId);
    
    /**
     * 获取用户积分历史
     */
    PageResult<Object> getUserPointsHistory(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取积分排行榜
     */
    PageResult<UserDTO> getPointsRanking(Integer pageNum, Integer pageSize);
}
