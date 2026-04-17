package com.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qa.entity.Vote;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞 Mapper
 */
@Mapper
public interface VoteMapper extends BaseMapper<Vote> {
}