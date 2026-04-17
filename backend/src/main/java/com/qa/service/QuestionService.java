package com.qa.service;

import com.qa.common.PageResult;
import com.qa.dto.QuestionDTO;
import com.qa.entity.Question;

/**
 * 问题业务服务
 */
public interface QuestionService {
    
    /**
     * 发布问题
     */
    Question publishQuestion(Long authorId, String title, String content, String tags);
    
    /**
     * 获取问题详情
     */
    QuestionDTO getQuestionDetail(Long questionId, Long userId);
    
    /**
     * 获取问题列表
     */
    PageResult<QuestionDTO> getQuestionList(String sortBy, String tags, Boolean solved, 
                                             Integer pageNum, Integer pageSize, Long userId);
    
    /**
     * 搜索问题
     */
    PageResult<QuestionDTO> searchQuestions(String keyword, Integer pageNum, Integer pageSize, Long userId);
    
    /**
     * 获取用户的问题列表
     */
    PageResult<QuestionDTO> getUserQuestions(Long authorId, Integer pageNum, Integer pageSize, Long userId);
    
    /**
     * 采纳答案
     */
    void acceptAnswer(Long questionId, Long answerId, Long userId);
    
    /**
     * 更新问题
     */
    Question updateQuestion(Long questionId, String title, String content, String tags, Long userId);
    
    /**
     * 删除问题
     */
    void deleteQuestion(Long questionId, Long userId);
}
