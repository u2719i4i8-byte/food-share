package cn.kmbeast.pojo.dto.query.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户行为权重DTO
 * 用于记录用户对某个内容的综合兴趣度
 * 基于浏览、点赞、收藏、评分等行为计算综合得分
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehaviorWeightDto {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 内容ID（美食ID）
     */
    private Integer contentId;

    /**
     * 综合兴趣得分
     * 计算公式：浏览次数*0.1 + 点赞*2 + 收藏*3 + 评分*2
     */
    private Double score;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否点赞（0=未点赞，1=已点赞）
     */
    private Integer upvoteFlag;

    /**
     * 是否收藏（0=未收藏，1=已收藏）
     */
    private Integer collectFlag;

    /**
     * 评分（1-5分，未评分为null）
     */
    private Integer ratingScore;

    /**
     * 内容的分类ID
     */
    private Integer categoryId;

}
