package com.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qa.common.PageResult;
import com.qa.dto.AnswerDTO;
import com.qa.dto.UserDTO;
import com.qa.entity.Answer;
import com.qa.entity.Question;
import com.qa.entity.User;
import com.qa.mapper.AnswerMapper;
import com.qa.mapper.QuestionMapper;
import com.qa.mapper.UserMapper;
import com.qa.mapper.VoteMapper;
import com.qa.service.AnswerService;
import com.qa.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回答业务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final VoteMapper voteMapper;
    private final PointsService pointsService;

    @Override
    public Answer publishAnswer(Long questionId, Long authorId, String content) {
        if (authorId == null) {
            throw new RuntimeException("User must be logged in to publish an answer");
        }
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("Question not found");
        }

        User user = userMapper.selectById(authorId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Answer answer = Answer.builder()
                .content(content)
                .questionId(questionId)
                .authorId(authorId)
                .votes(0)
                .isAccepted(false)
                .build();

        answerMapper.insert(answer);

        // 更新问题回答数
        question.setAnswersCount(question.getAnswersCount() + 1);
        questionMapper.updateById(question);

        // 回答问题获得10积分
        pointsService.addPointsLog(authorId, 10, "answer_question", questionId);

        return answer;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AnswerDTO> getAnswerList(Long questionId, Integer pageNum, Integer pageSize, Long userId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getQuestionId, questionId)
                .orderByDesc(Answer::getCreatedAt);

        IPage<Answer> page = answerMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<AnswerDTO> dtos = page.getRecords().stream()
                .map(a -> buildAnswerDTO(a, userId))
                .collect(Collectors.toList());

        return PageResult.of(dtos, page.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    public Answer updateAnswer(Long answerId, String content, Long userId) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new RuntimeException("Answer not found");
        }

        if (!answer.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can update answer");
        }

        answer.setContent(content);
        answer.setUpdatedAt(LocalDateTime.now());

        answerMapper.updateById(answer);
        return answer;
    }

    @Override
    public void deleteAnswer(Long answerId, Long userId) {
        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new RuntimeException("Answer not found");
        }

        if (!answer.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can delete answer");
        }

        // 更新问题回答数
        Question question = questionMapper.selectById(answer.getQuestionId());
        if (question != null) {
            question.setAnswersCount(Math.max(0, question.getAnswersCount() - 1));
            questionMapper.updateById(question);
        }

        answerMapper.deleteById(answerId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AnswerDTO> getUserAnswers(Long authorId, Integer pageNum, Integer pageSize, Long userId) {
        LambdaQueryWrapper<Answer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Answer::getAuthorId, authorId)
                .orderByDesc(Answer::getCreatedAt);

        IPage<Answer> page = answerMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<AnswerDTO> dtos = page.getRecords().stream()
                .map(a -> buildAnswerDTO(a, userId))
                .collect(Collectors.toList());

        return PageResult.of(dtos, page.getTotal(), (long) pageNum, (long) pageSize);
    }

    private AnswerDTO buildAnswerDTO(Answer answer, Long userId) {
        boolean userVoted = false;
        if (userId != null) {
            LambdaQueryWrapper<com.qa.entity.Vote> voteWrapper = new LambdaQueryWrapper<>();
            voteWrapper.eq(com.qa.entity.Vote::getUserId, userId)
                    .eq(com.qa.entity.Vote::getTargetType, "answer")
                    .eq(com.qa.entity.Vote::getTargetId, answer.getId());
            userVoted = voteMapper.selectCount(voteWrapper) > 0;
        }

        // 获取作者信息
        User authorUser = userMapper.selectById(answer.getAuthorId());
        UserDTO author = UserDTO.builder()
                .id(answer.getAuthorId())
                .username(authorUser != null ? authorUser.getUsername() : "Unknown")
                .build();

        return AnswerDTO.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .questionId(answer.getQuestionId())
                .authorId(answer.getAuthorId())
                .author(author)
                .votes(answer.getVotes())
                .isAccepted(answer.getIsAccepted())
                .userVoted(userVoted)
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}