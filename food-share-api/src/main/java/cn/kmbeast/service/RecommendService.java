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
     * 获取推荐美食列表（随机推荐）
     * @param item 推荐数量
     * @return 推荐美食列表
     */
    Result<List<GourmetVO>> recommendGourmet(Integer item);
}
