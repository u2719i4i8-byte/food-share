package cn.kmbeast.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 营养成分汇总VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NutritionSummaryVO {
    /**
     * 营养素ID
     */
    private Integer nutrimentId;
    /**
     * 营养素名称
     */
    private String nutrimentName;
    /**
     * 营养素单位
     */
    private String nutrimentUnit;
    /**
     * 总含量值
     */
    private Double value;
}
