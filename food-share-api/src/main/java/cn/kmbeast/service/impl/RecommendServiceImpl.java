package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.GourmetMapper;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.GourmetQueryDto;
import cn.kmbeast.pojo.dto.query.extend.RatingDto;
import cn.kmbeast.pojo.dto.query.extend.UserBehaviorWeightDto;
import cn.kmbeast.pojo.vo.GourmetVO;
import cn.kmbeast.service.RecommendService;
import cn.kmbeast.utils.MahoutUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐业务逻辑实现
 * 基于用户行为的个性化推荐系统
 */
@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {

    @Resource
    private InteractionMapper interactionMapper;
    @Resource
    private GourmetMapper gourmetMapper;

    /**
     * 查询需要推荐给用户的美食做法帖子数据
     * 推荐策略：
     * 1. 基于用户行为的协同过滤推荐（综合浏览、点赞、收藏、评分）
     * 2. 如果协同过滤失败，基于用户偏好分类推荐
     * 3. 如果无偏好分类，返回热门推荐
     * 4. 兜底返回随机推荐
     *
     * @param item 需要的条数
     * @return Result<List < GourmetVO>>
     */
    @Override
    public Result<List<GourmetVO>> recommendGourmet(Integer item) {
        Integer currentUserId = LocalThreadHolder.getUserId();
        log.info("开始为用户 {} 生成推荐，推荐数量: {}", currentUserId, item);

        // 策略1: 基于用户行为的协同过滤推荐
        List<GourmetVO> cfRecommendations = getCollaborativeFilteringRecommendations(currentUserId, item);
        if (!CollectionUtils.isEmpty(cfRecommendations)) {
            log.info("协同过滤推荐成功，返回 {} 条推荐", cfRecommendations.size());
            return ApiResult.success(cfRecommendations);
        }

        // 策略2: 基于用户偏好分类推荐
        List<GourmetVO> categoryRecommendations = getCategoryBasedRecommendations(currentUserId, item);
        if (!CollectionUtils.isEmpty(categoryRecommendations)) {
            log.info("分类偏好推荐成功，返回 {} 条推荐", categoryRecommendations.size());
            return ApiResult.success(categoryRecommendations);
        }

        // 策略3: 热门推荐
        List<GourmetVO> hotRecommendations = getHotRecommendations(item);
        if (!CollectionUtils.isEmpty(hotRecommendations)) {
            log.info("热门推荐成功，返回 {} 条推荐", hotRecommendations.size());
            return ApiResult.success(hotRecommendations);
        }

        // 策略4: 随机推荐（兜底）
        List<GourmetVO> randomRecommendations = getRandomRecommendations(item);
        log.info("随机推荐，返回 {} 条推荐", randomRecommendations.size());
        return ApiResult.success(randomRecommendations);
    }

    /**
     * 基于用户行为的协同过滤推荐
     * 使用Mahout库实现基于用户的协同过滤算法
     *
     * @param userId 用户ID
     * @param item   推荐数量
     * @return 推荐美食列表
     */
    private List<GourmetVO> getCollaborativeFilteringRecommendations(Integer userId, int item) {
        try {
            // 获取所有用户的综合行为数据
            List<UserBehaviorWeightDto> behaviorWeights = interactionMapper.queryUserBehaviorWeights();
            if (CollectionUtils.isEmpty(behaviorWeights)) {
                log.info("没有用户行为数据，跳过协同过滤");
                return Collections.emptyList();
            }

            // 转换为评分数据格式
            List<RatingDto> ratingDtoList = behaviorWeights.stream()
                    .map(bw -> new RatingDto(
                            bw.getUserId(),
                            bw.getContentId(),
                            bw.getScore().floatValue()))
                    .collect(Collectors.toList());

            // 使用Mahout进行协同过滤推荐
            List<RecommendedItem> recommenderList = MahoutUtils.recommender(
                    ratingDtoList,
                    userId.longValue(),
                    item);

            if (CollectionUtils.isEmpty(recommenderList)) {
                log.info("协同过滤未生成推荐结果");
                return Collections.emptyList();
            }

            // 获取推荐的美食ID列表
            List<Integer> gourmetIds = recommenderList.stream()
                    .map(recommendedItem -> Integer.parseInt(String.valueOf(recommendedItem.getItemID())))
                    .collect(Collectors.toList());

            // 过滤掉用户已经交互过的内容
            List<Integer> filteredIds = filterUserInteracted(gourmetIds, userId);
            if (CollectionUtils.isEmpty(filteredIds)) {
                return Collections.emptyList();
            }

            return gourmetMapper.queryByIds(filteredIds);
        } catch (Exception e) {
            log.error("协同过滤推荐异常: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 基于用户偏好分类的推荐
     * 根据用户历史行为分析偏好分类，推荐该分类下的热门内容
     *
     * @param userId 用户ID
     * @param item   推荐数量
     * @return 推荐美食列表
     */
    private List<GourmetVO> getCategoryBasedRecommendations(Integer userId, int item) {
        // 获取当前用户的行为数据
        List<UserBehaviorWeightDto> userBehaviors = interactionMapper.queryUserBehaviorByUserId(userId);
        if (CollectionUtils.isEmpty(userBehaviors)) {
            log.info("用户 {} 没有行为数据", userId);
            return Collections.emptyList();
        }

        // 分析用户偏好的分类（按综合得分排序）
        Map<Integer, Double> categoryScores = new HashMap<>();
        for (UserBehaviorWeightDto behavior : userBehaviors) {
            if (behavior.getCategoryId() != null && behavior.getScore() != null) {
                categoryScores.merge(behavior.getCategoryId(), behavior.getScore(),
                        (v1, v2) -> Double.sum(v1 != null ? v1 : 0.0, v2 != null ? v2 : 0.0));
            }
        }

        if (categoryScores.isEmpty()) {
            log.info("用户 {} 没有偏好分类", userId);
            return Collections.emptyList();
        }

        // 按得分排序获取偏好分类
        List<Integer> preferredCategories = categoryScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        log.info("用户 {} 偏好分类: {}", userId, preferredCategories);

        // 获取用户已交互的内容ID
        Set<Integer> interactedIds = userBehaviors.stream()
                .map(UserBehaviorWeightDto::getContentId)
                .collect(Collectors.toSet());

        // 从偏好分类中推荐内容
        List<GourmetVO> recommendations = new ArrayList<>();
        for (Integer categoryId : preferredCategories) {
            if (recommendations.size() >= item) {
                break;
            }
            GourmetQueryDto queryDto = new GourmetQueryDto();
            queryDto.setCategoryId(categoryId);
            queryDto.setIsAudit(true);
            queryDto.setIsPublish(true);
            List<GourmetVO> categoryGourmets = gourmetMapper.query(queryDto);

            // 过滤已交互的内容并随机选择
            List<GourmetVO> newGourmets = categoryGourmets.stream()
                    .filter(g -> !interactedIds.contains(g.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(newGourmets);

            int needCount = item - recommendations.size();
            int addCount = Math.min(needCount, newGourmets.size());
            recommendations.addAll(newGourmets.subList(0, addCount));
        }

        return recommendations;
    }

    /**
     * 热门推荐
     * 基于浏览量、点赞数、收藏数计算热度进行推荐
     *
     * @param item 推荐数量
     * @return 推荐美食列表
     */
    private List<GourmetVO> getHotRecommendations(int item) {
        GourmetQueryDto queryDto = new GourmetQueryDto();
        queryDto.setIsAudit(true);
        queryDto.setIsPublish(true);
        List<GourmetVO> allGourmets = gourmetMapper.query(queryDto);

        if (CollectionUtils.isEmpty(allGourmets)) {
            return Collections.emptyList();
        }

        // 按热度排序（浏览量 + 点赞数×10 + 收藏数×15）
        List<GourmetVO> sortedGourmets = allGourmets.stream()
                .sorted((g1, g2) -> {
                    int hotScore1 = (g1.getViewCount() != null ? g1.getViewCount() : 0)
                            + (g1.getUpvoteCount() != null ? g1.getUpvoteCount() : 0) * 10
                            + (g1.getSaveCount() != null ? g1.getSaveCount() : 0) * 15;
                    int hotScore2 = (g2.getViewCount() != null ? g2.getViewCount() : 0)
                            + (g2.getUpvoteCount() != null ? g2.getUpvoteCount() : 0) * 10
                            + (g2.getSaveCount() != null ? g2.getSaveCount() : 0) * 15;
                    return Integer.compare(hotScore2, hotScore1);
                })
                .collect(Collectors.toList());

        int count = Math.min(item, sortedGourmets.size());
        return sortedGourmets.subList(0, count);
    }

    /**
     * 随机推荐（兜底策略）
     *
     * @param item 推荐数量
     * @return 推荐美食列表
     */
    private List<GourmetVO> getRandomRecommendations(int item) {
        GourmetQueryDto queryDto = new GourmetQueryDto();
        queryDto.setIsAudit(true);
        queryDto.setIsPublish(true);
        List<GourmetVO> allGourmets = gourmetMapper.query(queryDto);

        if (CollectionUtils.isEmpty(allGourmets)) {
            return Collections.emptyList();
        }

        Collections.shuffle(allGourmets);
        int count = Math.min(item, allGourmets.size());
        return new ArrayList<>(allGourmets.subList(0, count));
    }

    /**
     * 过滤用户已交互过的内容
     *
     * @param gourmetIds 美食ID列表
     * @param userId     用户ID
     * @return 过滤后的ID列表
     */
    private List<Integer> filterUserInteracted(List<Integer> gourmetIds, Integer userId) {
        List<UserBehaviorWeightDto> userBehaviors = interactionMapper.queryUserBehaviorByUserId(userId);
        if (CollectionUtils.isEmpty(userBehaviors)) {
            return gourmetIds;
        }
        Set<Integer> interactedIds = userBehaviors.stream()
                .map(UserBehaviorWeightDto::getContentId)
                .collect(Collectors.toSet());
        return gourmetIds.stream()
                .filter(id -> !interactedIds.contains(id))
                .collect(Collectors.toList());
    }

    /**
     * 获取分类推荐美食列表
     * 基于用户行为和分类的智能推荐
     *
     * @param categoryIds 分类ID列表（可选）
     * @param item        推荐数量
     * @return 推荐美食列表
     */
    @Override
    public Result<List<GourmetVO>> recommendByCategory(List<Integer> categoryIds, Integer item) {
        Integer currentUserId = LocalThreadHolder.getUserId();
        log.info("开始分类推荐，用户: {}, 分类: {}, 数量: {}", currentUserId, categoryIds, item);

        // 策略1: 如果用户已登录且有行为数据，尝试协同过滤
        if (currentUserId != null) {
            List<GourmetVO> cfResults = getCategoryCFRecommendations(currentUserId, categoryIds, item);
            if (!CollectionUtils.isEmpty(cfResults)) {
                log.info("分类协同过滤推荐成功，返回 {} 条", cfResults.size());
                return ApiResult.success(cfResults);
            }

            // 策略2: 基于用户偏好分类推荐
            List<GourmetVO> preferenceResults = getCategoryPreferenceRecommendations(currentUserId, categoryIds, item);
            if (!CollectionUtils.isEmpty(preferenceResults)) {
                log.info("分类偏好推荐成功，返回 {} 条", preferenceResults.size());
                return ApiResult.success(preferenceResults);
            }
        }

        // 策略3: 分类热门推荐
        List<GourmetVO> hotResults = getCategoryHotRecommendations(categoryIds, item);
        if (!CollectionUtils.isEmpty(hotResults)) {
            log.info("分类热门推荐成功，返回 {} 条", hotResults.size());
            return ApiResult.success(hotResults);
        }

        // 策略4: 随机推荐（兜底）
        List<GourmetVO> randomResults = getCategoryRandomRecommendations(categoryIds, item);
        log.info("分类随机推荐，返回 {} 条", randomResults.size());
        return ApiResult.success(randomResults);
    }

    /**
     * 分类协同过滤推荐
     */
    private List<GourmetVO> getCategoryCFRecommendations(Integer userId, List<Integer> categoryIds, int item) {
        try {
            List<UserBehaviorWeightDto> behaviorWeights = interactionMapper.queryUserBehaviorWeights();
            if (CollectionUtils.isEmpty(behaviorWeights)) {
                return Collections.emptyList();
            }

            List<RatingDto> ratingDtoList = behaviorWeights.stream()
                    .map(bw -> new RatingDto(bw.getUserId(), bw.getContentId(), bw.getScore().floatValue()))
                    .collect(Collectors.toList());

            List<RecommendedItem> recommenderList = MahoutUtils.recommender(ratingDtoList, userId.longValue(),
                    item * 2);
            if (CollectionUtils.isEmpty(recommenderList)) {
                return Collections.emptyList();
            }

            List<Integer> gourmetIds = recommenderList.stream()
                    .map(r -> Integer.parseInt(String.valueOf(r.getItemID())))
                    .collect(Collectors.toList());

            List<Integer> filteredIds = filterUserInteracted(gourmetIds, userId);
            if (CollectionUtils.isEmpty(filteredIds)) {
                return Collections.emptyList();
            }

            List<GourmetVO> gourmets = gourmetMapper.queryByIds(filteredIds);

            // 如果指定了分类，过滤分类
            if (!CollectionUtils.isEmpty(categoryIds)) {
                gourmets = gourmets.stream()
                        .filter(g -> categoryIds.contains(g.getCategoryId()))
                        .collect(Collectors.toList());
            }

            if (gourmets.size() > item) {
                return gourmets.subList(0, item);
            }
            return gourmets;
        } catch (Exception e) {
            log.error("分类协同过滤异常: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 分类偏好推荐
     */
    private List<GourmetVO> getCategoryPreferenceRecommendations(Integer userId, List<Integer> categoryIds, int item) {
        List<UserBehaviorWeightDto> userBehaviors = interactionMapper.queryUserBehaviorByUserId(userId);
        if (CollectionUtils.isEmpty(userBehaviors)) {
            return Collections.emptyList();
        }

        Set<Integer> interactedIds = userBehaviors.stream()
                .map(UserBehaviorWeightDto::getContentId)
                .collect(Collectors.toSet());

        GourmetQueryDto queryDto = new GourmetQueryDto();
        queryDto.setIsAudit(true);
        queryDto.setIsPublish(true);
        queryDto.setOrderBy("random");

        if (!CollectionUtils.isEmpty(categoryIds)) {
            queryDto.setCategoryIds(categoryIds);
        }

        List<GourmetVO> gourmets = gourmetMapper.query(queryDto);

        // 过滤已交互的内容
        List<GourmetVO> filtered = gourmets.stream()
                .filter(g -> !interactedIds.contains(g.getId()))
                .collect(Collectors.toList());

        Collections.shuffle(filtered);

        if (filtered.size() > item) {
            return filtered.subList(0, item);
        }
        return filtered;
    }

    /**
     * 分类热门推荐
     */
    private List<GourmetVO> getCategoryHotRecommendations(List<Integer> categoryIds, int item) {
        GourmetQueryDto queryDto = new GourmetQueryDto();
        queryDto.setIsAudit(true);
        queryDto.setIsPublish(true);

        if (!CollectionUtils.isEmpty(categoryIds)) {
            queryDto.setCategoryIds(categoryIds);
        }

        List<GourmetVO> gourmets = gourmetMapper.query(queryDto);
        if (CollectionUtils.isEmpty(gourmets)) {
            return Collections.emptyList();
        }

        // 按热度排序
        List<GourmetVO> sorted = gourmets.stream()
                .sorted((g1, g2) -> {
                    int score1 = (g1.getViewCount() != null ? g1.getViewCount() : 0)
                            + (g1.getUpvoteCount() != null ? g1.getUpvoteCount() : 0) * 10
                            + (g1.getSaveCount() != null ? g1.getSaveCount() : 0) * 15;
                    int score2 = (g2.getViewCount() != null ? g2.getViewCount() : 0)
                            + (g2.getUpvoteCount() != null ? g2.getUpvoteCount() : 0) * 10
                            + (g2.getSaveCount() != null ? g2.getSaveCount() : 0) * 15;
                    return Integer.compare(score2, score1);
                })
                .collect(Collectors.toList());

        if (sorted.size() > item) {
            return sorted.subList(0, item);
        }
        return sorted;
    }

    /**
     * 分类随机推荐
     */
    private List<GourmetVO> getCategoryRandomRecommendations(List<Integer> categoryIds, int item) {
        GourmetQueryDto queryDto = new GourmetQueryDto();
        queryDto.setIsAudit(true);
        queryDto.setIsPublish(true);
        queryDto.setOrderBy("random");

        if (!CollectionUtils.isEmpty(categoryIds)) {
            queryDto.setCategoryIds(categoryIds);
        }

        List<GourmetVO> gourmets = gourmetMapper.query(queryDto);
        if (CollectionUtils.isEmpty(gourmets)) {
            return Collections.emptyList();
        }

        Collections.shuffle(gourmets);
        if (gourmets.size() > item) {
            return gourmets.subList(0, item);
        }
        return gourmets;
    }

}
