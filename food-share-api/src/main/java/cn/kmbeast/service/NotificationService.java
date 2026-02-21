package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.vo.NotificationVO;

import java.util.List;

/**
 * 消息通知服务接口
 */
public interface NotificationService {

    /**
     * 获取评论消息列表
     * @param offset 偏移量
     * @param size   数量
     * @return 消息列表
     */
    Result<List<NotificationVO>> getCommentNotifications(Integer offset, Integer size);

    /**
     * 获取点赞消息列表
     * @param offset 偏移量
     * @param size   数量
     * @return 消息列表
     */
    Result<List<NotificationVO>> getLikeNotifications(Integer offset, Integer size);

}
