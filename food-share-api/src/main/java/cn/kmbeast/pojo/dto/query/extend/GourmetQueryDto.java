package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 美食做法查询Dto类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GourmetQueryDto extends QueryDto {

    /**
     * 美食分类ID
     */
    private Integer categoryId;

    /**
     * 美食分类ID列表（用于多分类查询）
     */
    private List<Integer> categoryIds;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否已经审核
     */
    private Boolean isAudit;

    /**
     * 是否公开
     */
    private Boolean isPublish;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式 asc/desc
     */
    private String order;
}
