package cn.kmbeast.utils;

import cn.kmbeast.pojo.dto.query.extend.RatingDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mahout工具类
 * 功能：基于协同过滤算法实现个性化推荐
 * 使用皮尔逊相关系数计算用户相似度，基于用户的协同过滤进行推荐
 */
@Slf4j
public class MahoutUtils {

    /**
     * 基于用户协同过滤的推荐算法
     * 根据用户评分数据，为目标用户推荐可能感兴趣的物品
     *
     * @param ratingDTOS   评分数据列表（用户ID、物品ID、评分）
     * @param targetUserId 目标用户ID（被推荐者）
     * @param targetItems  推荐物品数量
     * @return 推荐物品列表，推荐失败返回空列表
     */
    public static List<RecommendedItem> recommender(List<RatingDto> ratingDTOS,
                                                    Long targetUserId,
                                                    int targetItems) {
        List<Preference> preferences = new ArrayList<>();
        for (RatingDto ratingDTO : ratingDTOS) {
            long userId = ratingDTO.getUserId().longValue(); // 确保转换为Long类型
            long itemId = ratingDTO.getItemId().longValue(); // 确保转换为Long类型
            float score = ratingDTO.getScore();
            preferences.add(new GenericPreference(userId, itemId, score));
        }
        try {
            DataModel model = buildDataModel(preferences);
            // 使用皮尔逊相关系数计算用户相似度
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            // 找出最相似的2个用户作为邻居
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
            // 创建基于用户的推荐器
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            return recommender.recommend(targetUserId, targetItems);
        } catch (TasteException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 构建数据模型
     * 将评分数据转换为Mahout所需的DataModel格式
     *
     * @param preferences 评分数据列表
     * @return DataModel 数据模型
     * @throws TasteException 构建失败时抛出
     */
    private static DataModel buildDataModel(List<Preference> preferences) throws TasteException {
        // 按用户ID分组
        Map<Long, List<Preference>> groupedByUserID = preferences.stream()
                .collect(Collectors.groupingBy(Preference::getUserID));

        FastByIDMap<PreferenceArray> userPreferences = new FastByIDMap<>();
        for (Map.Entry<Long, List<Preference>> entry : groupedByUserID.entrySet()) {
            Long userID = entry.getKey();
            List<Preference> userPrefs = entry.getValue();
            PreferenceArray preferenceArray = new GenericUserPreferenceArray(userPrefs);
            userPreferences.put(userID, preferenceArray);
        }

        return new GenericDataModel(userPreferences);
    }

}
