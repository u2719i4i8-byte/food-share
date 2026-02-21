package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.EvaluationsMapper;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.vo.NotificationVO;
import cn.kmbeast.service.NotificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 消息通知服务实现
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private EvaluationsMapper evaluationsMapper;

    @Resource
    private InteractionMapper interactionMapper;

    private static final int UPVOTE_TYPE = 1;

    @Override
    public Result<List<NotificationVO>> getCommentNotifications(Integer offset, Integer size) {
        Integer userId = LocalThreadHolder.getUserId();
        if (userId == null) {
            return ApiResult.success(Collections.emptyList());
        }
        List<NotificationVO> notifications = evaluationsMapper.getCommentNotifications(userId, offset, size);
        return ApiResult.success(notifications);
    }

    @Override
    public Result<List<NotificationVO>> getLikeNotifications(Integer offset, Integer size) {
        Integer userId = LocalThreadHolder.getUserId();
        if (userId == null) {
            return ApiResult.success(Collections.emptyList());
        }
        List<NotificationVO> notifications = interactionMapper.getLikeNotifications(userId, UPVOTE_TYPE, offset, size);
        return ApiResult.success(notifications);
    }

}
