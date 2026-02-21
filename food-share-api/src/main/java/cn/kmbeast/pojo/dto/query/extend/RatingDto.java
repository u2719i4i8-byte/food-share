package cn.kmbeast.pojo.dto.query.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评分的Dto类
 * 用于推荐算法中的用户-物品评分数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 物品ID
     */
    private Integer itemId;
    /**
     * 评分（支持浮点数，用于综合行为权重）
     */
    private Float score;
}
