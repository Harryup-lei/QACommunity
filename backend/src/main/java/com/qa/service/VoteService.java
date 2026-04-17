package com.qa.service;

/**
 * 点赞业务服务
 */
public interface VoteService {
    
    /**
     * 点赞问题或回答
     */
    void vote(Long userId, String targetType, Long targetId);
    
    /**
     * 取消点赞
     */
    void unvote(Long userId, String targetType, Long targetId);
    
    /**
     * 检查用户是否已点赞
     */
    boolean hasVoted(Long userId, String targetType, Long targetId);
    
    /**
     * 获取目标的点赞数
     */
    long getVoteCount(String targetType, Long targetId);
}
