package cn.kmbeast.pojo.dto.query.extend;

import lombok.Data;

import java.util.List;

/**
 * 临时饮食营养计算请求DTO
 */
@Data
public class TempDietQueryDto {
    /**
     * 临时饮食列表
     */
    private List<TempDietItem> dietHistories;

    @Data
    public static class TempDietItem {
        /**
         * 食谱ID
         */
        private Integer cookbookId;
        /**
         * 食用量(g)
         */
        private String value;
        /**
         * 备注
         */
        private String detail;
    }
}
