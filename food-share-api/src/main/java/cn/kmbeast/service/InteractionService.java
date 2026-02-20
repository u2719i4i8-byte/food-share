package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.vo.*;

import java.util.List;

/**
 * 内容互动业务逻辑接口
 * 提供浏览、点赞、收藏、评分等互动功能
 */
public interface InteractionService {

    /**
     * 查询互动记录
     * @param interactionQueryDto 查询参数
     * @return 互动记录列表
     */
    Result<List<InteractionVO>> query(InteractionQueryDto interactionQueryDto);

    /**
     * 浏览操作（增加浏览量）
     * @param contentId 内容ID
     * @return 操作结果
     */
    Result<Void> viewOperation(Integer contentId);

    /**
     * 点赞/取消点赞操作
     * @param contentId 内容ID
     * @return 操作结果（1=点赞成功，0=取消点赞）
     */
    Result<Integer> upvoteOperation(Integer contentId);

    /**
     * 查询点赞状态
     * @param contentId 内容ID
     * @return 点赞状态
     */
    Result<Integer> upvoteStatus(Integer contentId);

    /**
     * 收藏/取消收藏操作
     * @param contentId 内容ID
     * @return 操作结果（1=收藏成功，0=取消收藏）
     */
    Result<Integer> saveOperation(Integer contentId);

    /**
     * 查询收藏状态
     * @param contentId 内容ID
     * @return 收藏状态
     */
    Result<Integer> saveStatus(Integer contentId);

    /**
     * 查询用户收藏列表
     * @param interactionQueryDto 查询参数
     * @return 收藏列表
     */
    Result<List<GourmetListVO>> queryCollectionList(InteractionQueryDto interactionQueryDto);

    /**
     * 查询评分状态
     * @param contentId 内容ID
     * @return 评分状态
     */
    Result<Integer> ratingStatus(Integer contentId);

    /**
     * 评分操作
     * @param contentId 内容ID
     * @param score 评分
     * @return 操作结果
     */
    Result<List<GourmetVO>> ratingOperation(Integer contentId, Integer score);

    /**
     * 查询用户评分记录
     * @param interactionQueryDto 查询参数
     * @return 评分记录
     */
    Result<List<RatingVO>> queryUserRating(InteractionQueryDto interactionQueryDto);

    /**
     * 按时间统计互动数据
     * @param interactionQueryDto 查询参数
     * @return 统计结果
     */
    Result<List<ChartVO>> daysQuery(InteractionQueryDto interactionQueryDto);

    /**
     * 批量删除互动记录
     * @param ids ID列表
     * @return 操作结果
     */
    Result<String> batchDelete(List<Integer> ids);

    /**
     * 查询谁点赞了我
     * @param interactionQueryDto 查询参数
     * @return 点赞用户列表
     */
    Result<List<WhoLikedMeVO>> queryWhoLikedMe(InteractionQueryDto interactionQueryDto);

}
