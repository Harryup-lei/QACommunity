package com.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qa.entity.Vote;
import com.qa.entity.Question;
import com.qa.entity.Answer;
import com.qa.entity.User;
import com.qa.mapper.VoteMapper;
import com.qa.mapper.QuestionMapper;
import com.qa.mapper.AnswerMapper;
import com.qa.service.VoteService;
import com.qa.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞业务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final PointsService pointsService;

    @Override
    public void vote(Long userId, String targetType, Long targetId) {
        // 检查是否已点赞
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vote::getUserId, userId)
                .eq(Vote::getTargetType, targetType)
                .eq(Vote::getTargetId, targetId);
        if (voteMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("Already voted");
        }

        Vote vote = Vote.builder()
                .userId(userId)
                .targetType(targetType)
                .targetId(targetId)
                .build();

        voteMapper.insert(vote);

        // 更新目标的投票数
        if ("question".equals(targetType)) {
            Question question = questionMapper.selectById(targetId);
            if (question == null) {
                throw new RuntimeException("Question not found");
            }
            question.setVotes(question.getVotes() + 1);
            questionMapper.updateById(question);

            // 问题被点赞，提问者获得5积分
            pointsService.addPointsLog(question.getAuthorId(), 5, "vote_question", targetId);

        } else if ("answer".equals(targetType)) {
            Answer answer = answerMapper.selectById(targetId);
            if (answer == null) {
                throw new RuntimeException("Answer not found");
            }
            answer.setVotes(answer.getVotes() + 1);
            answerMapper.updateById(answer);

            // 回答被点赞，回答者获得10积分
            pointsService.addPointsLog(answer.getAuthorId(), 10, "vote_answer", targetId);
        } else {
            throw new RuntimeException("Unsupported target type: " + targetType);
        }
    }

    @Override
    public void unvote(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vote::getUserId, userId)
                .eq(Vote::getTargetType, targetType)
                .eq(Vote::getTargetId, targetId);
        Vote vote = voteMapper.selectOne(wrapper);

        if (vote == null) {
            throw new RuntimeException("Vote not found");
        }

        voteMapper.deleteById(vote.getId());

        // 更新目标的投票数
        if ("question".equals(targetType)) {
            Question question = questionMapper.selectById(targetId);
            if (question != null) {
                question.setVotes(Math.max(0, question.getVotes() - 1));
                questionMapper.updateById(question);

                // 扣除积分
                pointsService.addPointsLog(question.getAuthorId(), -5, "unvote_question", targetId);
            }

        } else if ("answer".equals(targetType)) {
            Answer answer = answerMapper.selectById(targetId);
            if (answer != null) {
                answer.setVotes(Math.max(0, answer.getVotes() - 1));
                answerMapper.updateById(answer);

                // 扣除积分
                pointsService.addPointsLog(answer.getAuthorId(), -10, "unvote_answer", targetId);
            }
        } else {
            throw new RuntimeException("Unsupported target type: " + targetType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVoted(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vote::getUserId, userId)
                .eq(Vote::getTargetType, targetType)
                .eq(Vote::getTargetId, targetId);
        return voteMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long getVoteCount(String targetType, Long targetId) {
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vote::getTargetType, targetType)
                .eq(Vote::getTargetId, targetId);
        return voteMapper.selectCount(wrapper);
    }
}