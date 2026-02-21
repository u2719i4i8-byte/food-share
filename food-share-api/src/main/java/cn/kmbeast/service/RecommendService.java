package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.vo.GourmetVO;

import java.util.List;

/**
 * 推荐服务接口
 * 提供美食推荐功能
 */
public interface RecommendService {

    /**
     * 获取推荐美食列表（基于用户行为的智能推荐）
     * @param item 推荐数量
     * @return 推荐美食列表
     */
    Result<List<GourmetVO>> recommendGourmet(Integer item);

    /**
     * 获取分类推荐美食列表
     * @param categoryIds 分类ID列表（可选）
     * @param item 推荐数量
     * @return 推荐美食列表
     */
    Result<List<GourmetVO>> recommendByCategory(List<Integer> categoryIds, Integer item);
}
