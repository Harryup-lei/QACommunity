package com.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qa.common.PageResult;
import com.qa.dto.QuestionDTO;
import com.qa.dto.UserDTO;
import com.qa.entity.Question;
import com.qa.entity.Answer;
import com.qa.entity.User;
import com.qa.mapper.QuestionMapper;
import com.qa.mapper.AnswerMapper;
import com.qa.mapper.UserMapper;
import com.qa.mapper.VoteMapper;
import com.qa.service.QuestionService;
import com.qa.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问题业务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final VoteMapper voteMapper;
    private final AnswerMapper answerMapper;
    private final PointsService pointsService;

    @Override
    public Question publishQuestion(Long authorId, String title, String content, String tags) {
        if (authorId == null) {
            throw new RuntimeException("User must be logged in to publish a question");
        }
        User user = userMapper.selectById(authorId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Question question = Question.builder()
                .title(title)
                .content(content)
                .tags(tags)
                .authorId(authorId)
                .views(0)
                .answersCount(0)
                .votes(0)
                .isSolved(false)
                .build();

        questionMapper.insert(question);

        // 发布问题扣5积分
        pointsService.addPointsLog(authorId, -5, "publish_question", question.getId());

        return question;
    }

    @Override
    public QuestionDTO getQuestionDetail(Long questionId, Long userId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("Question not found");
        }

        // 增加浏览次数
        question.setViews(question.getViews() + 1);
        questionMapper.updateById(question);

        return buildQuestionDTO(question, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<QuestionDTO> getQuestionList(String sortBy, String tags, Boolean solved,
                                                    Integer pageNum, Integer pageSize, Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 标签过滤
        if (tags != null && !tags.isBlank()) {
            wrapper.like(Question::getTags, tags);
        }
        // 状态过滤
        if (solved != null) {
            wrapper.eq(Question::getIsSolved, solved);
        }

        // 排序
        if ("hot".equals(sortBy)) {
            wrapper.orderByDesc(Question::getVotes);
        } else if ("unsolved".equals(sortBy)) {
            wrapper.eq(Question::getIsSolved, false).orderByDesc(Question::getCreatedAt);
        } else {
            wrapper.orderByDesc(Question::getCreatedAt);
        }

        IPage<Question> page = questionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<QuestionDTO> dtos = page.getRecords().stream()
                .map(q -> buildQuestionDTO(q, userId))
                .collect(Collectors.toList());

        return PageResult.of(dtos, page.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<QuestionDTO> searchQuestions(String keyword, Integer pageNum, Integer pageSize, Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Question::getTitle, keyword)
                .or()
                .like(Question::getContent, keyword)
                .orderByDesc(Question::getCreatedAt);

        IPage<Question> page = questionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<QuestionDTO> dtos = page.getRecords().stream()
                .map(q -> buildQuestionDTO(q, userId))
                .collect(Collectors.toList());

        return PageResult.of(dtos, page.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<QuestionDTO> getUserQuestions(Long authorId, Integer pageNum, Integer pageSize, Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getAuthorId, authorId)
                .orderByDesc(Question::getCreatedAt);

        IPage<Question> page = questionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<QuestionDTO> dtos = page.getRecords().stream()
                .map(q -> buildQuestionDTO(q, userId))
                .collect(Collectors.toList());

        return PageResult.of(dtos, page.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    public void acceptAnswer(Long questionId, Long answerId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("User must be logged in to accept an answer");
        }
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("Question not found");
        }

        if (!question.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only question author can accept answer");
        }

        if (Boolean.TRUE.equals(question.getIsSolved())) {
            throw new RuntimeException("Question already has accepted answer");
        }

        Answer answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new RuntimeException("Answer not found");
        }
        if (!answer.getQuestionId().equals(questionId)) {
            throw new RuntimeException("Answer does not belong to this question");
        }

        // 取消之前的采纳
        LambdaQueryWrapper<Answer> oldWrapper = new LambdaQueryWrapper<>();
        oldWrapper.eq(Answer::getQuestionId, questionId).eq(Answer::getIsAccepted, true);
        Answer oldAnswer = answerMapper.selectOne(oldWrapper);
        if (oldAnswer != null) {
            oldAnswer.setIsAccepted(false);
            answerMapper.updateById(oldAnswer);
        }

        // 设置新采纳
        answer.setIsAccepted(true);
        answerMapper.updateById(answer);

        question.setIsSolved(true);
        questionMapper.updateById(question);

        // 采纳回答获得20积分
        pointsService.addPointsLog(answer.getAuthorId(), 20, "answer_accepted", answerId);
    }

    @Override
    public Question updateQuestion(Long questionId, String title, String content, String tags, Long userId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("Question not found");
        }

        if (!question.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can update question");
        }

        question.setTitle(title);
        question.setContent(content);
        question.setTags(tags);
        question.setUpdatedAt(LocalDateTime.now());

        questionMapper.updateById(question);
        return question;
    }

    @Override
    public void deleteQuestion(Long questionId, Long userId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("Question not found");
        }

        if (!question.getAuthorId().equals(userId)) {
            throw new RuntimeException("Only author can delete question");
        }

        questionMapper.deleteById(questionId);
    }

    private QuestionDTO buildQuestionDTO(Question question, Long userId) {
        boolean userVoted = false;
        if (userId != null) {
            LambdaQueryWrapper<com.qa.entity.Vote> voteWrapper = new LambdaQueryWrapper<>();
            voteWrapper.eq(com.qa.entity.Vote::getUserId, userId)
                    .eq(com.qa.entity.Vote::getTargetType, "question")
                    .eq(com.qa.entity.Vote::getTargetId, question.getId());
            userVoted = voteMapper.selectCount(voteWrapper) > 0;
        }

        // 获取作者信息
        User authorUser = userMapper.selectById(question.getAuthorId());
        UserDTO author = UserDTO.builder()
                .id(question.getAuthorId())
                .username(authorUser != null ? authorUser.getUsername() : "Unknown")
                .build();

        return QuestionDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .tags(question.getTags())
                .authorId(question.getAuthorId())
                .author(author)
                .views(question.getViews())
                .answersCount(question.getAnswersCount())
                .votes(question.getVotes())
                .isSolved(question.getIsSolved())
                .userVoted(userVoted)
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}