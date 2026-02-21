package cn.kmbeast.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息通知VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationVO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private String type;
    private String content;
    private Integer contentId;
    private String contentType;
    private String contentTitle;
    private String contentCover;
    private String createTime;
    private Boolean isRead;
}
