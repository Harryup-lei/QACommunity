package com.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qa.common.PageResult;
import com.qa.dto.UserDTO;
import com.qa.dto.PointsLogDTO;
import com.qa.entity.PointsLog;
import com.qa.entity.User;
import com.qa.mapper.PointsLogMapper;
import com.qa.mapper.UserMapper;
import com.qa.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分业务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PointsServiceImpl implements PointsService {

    private final PointsLogMapper pointsLogMapper;
    private final UserMapper userMapper;

    @Override
    public void addPointsLog(Long userId, Integer points, String operationType, Long relatedId) {
        // 先更新用户积分
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setPoints(user.getPoints() + points);
        userMapper.updateById(user);

        // 保存积分日志
        PointsLog log = PointsLog.builder()
                .userId(userId)
                .points(points)
                .operationType(operationType)
                .relatedId(relatedId)
                .build();

        pointsLogMapper.insert(log);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Object> getUserPointsHistory(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsLog::getUserId, userId)
                .orderByDesc(PointsLog::getCreatedAt);

        IPage<PointsLog> page = pointsLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<Object> logs = page.getRecords().stream()
                .map(log -> (Object) new PointsLogDTO(
                        log.getId(),
                        log.getPoints(),
                        log.getOperationType(),
                        log.getRelatedId(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return PageResult.of(logs, page.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserDTO> getPointsRanking(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1)
                .orderByDesc(User::getPoints);

        IPage<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<UserDTO> users = page.getRecords().stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .avatar(user.getAvatar())
                        .points(user.getPoints())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(users, page.getTotal(), (long) pageNum, (long) pageSize);
    }
}