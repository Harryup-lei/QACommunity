package com.qa.service;

import com.qa.common.PageResult;
import com.qa.dto.AnswerDTO;
import com.qa.entity.Answer;

/**
 * 回答业务服务
 */
public interface AnswerService {
    
    /**
     * 发布回答
     */
    Answer publishAnswer(Long questionId, Long authorId, String content);
    
    /**
     * 获取回答列表
     */
    PageResult<AnswerDTO> getAnswerList(Long questionId, Integer pageNum, Integer pageSize, Long userId);
    
    /**
     * 更新回答
     */
    Answer updateAnswer(Long answerId, String content, Long userId);
    
    /**
     * 删除回答
     */
    void deleteAnswer(Long answerId, Long userId);
    
    /**
     * 获取用户的回答列表
     */
    PageResult<AnswerDTO> getUserAnswers(Long authorId, Integer pageNum, Integer pageSize, Long userId);
}
