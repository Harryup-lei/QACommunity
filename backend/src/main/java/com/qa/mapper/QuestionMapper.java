package com.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qa.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问题 Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}