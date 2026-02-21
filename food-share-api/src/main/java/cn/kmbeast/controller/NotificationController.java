package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.vo.NotificationVO;
import cn.kmbeast.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息通知控制器
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    /**
     * 获取评论消息列表
     *
     * @param offset 偏移量（默认0）
     * @param size   数量（默认20）
     * @return 消息列表
     */
    @Protector
    @GetMapping("/comments")
    @ResponseBody
    public Result<List<NotificationVO>> getCommentNotifications(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer size) {
        return notificationService.getCommentNotifications(offset, size);
    }

    /**
     * 获取点赞消息列表
     *
     * @param offset 偏移量（默认0）
     * @param size   数量（默认20）
     * @return 消息列表
     */
    @Protector
    @GetMapping("/likes")
    @ResponseBody
    public Result<List<NotificationVO>> getLikeNotifications(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer size) {
        return notificationService.getLikeNotifications(offset, size);
    }

}
