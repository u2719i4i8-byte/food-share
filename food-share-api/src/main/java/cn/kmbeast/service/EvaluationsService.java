package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.kmbeast.pojo.entity.Evaluations;

import java.util.List;

/**
 * 评论服务接口
 * 提供评论的增删改查、点赞等功能
 */
public interface EvaluationsService {

    /**
     * 发表评论
     * @param evaluations 评论实体
     * @return 操作结果
     */
    Result<Object> insert(Evaluations evaluations);

    /**
     * 获取内容下的评论列表
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 评论列表
     */
    Result<Object> list(Integer contentId, String contentType);

    /**
     * 分页查询评论（管理端）
     * @param evaluationsQueryDto 查询参数
     * @return 评论列表
     */
    Result<Object> query(EvaluationsQueryDto evaluationsQueryDto);

    /**
     * 批量删除评论
     * @param ids ID列表
     * @return 操作结果
     */
    Result<Object> batchDelete(List<Integer> ids);

    /**
     * 删除单条评论
     * @param id 评论ID
     * @return 操作结果
     */
    Result<String> delete(Integer id);

    /**
     * 更新评论
     * @param evaluations 评论实体
     * @return 操作结果
     */
    Result<Void> update(Evaluations evaluations);

    /**
     * 评论点赞/取消点赞
     * @param evaluations 评论实体
     * @return 操作结果
     */
    Result<Object> upvoteOperation(Evaluations evaluations);

}
