package com.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qa.entity.Answer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回答 Mapper
 */
@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {
}