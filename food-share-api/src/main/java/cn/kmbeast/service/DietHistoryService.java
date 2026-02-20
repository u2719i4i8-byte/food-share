package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.DietHistoryQueryDto;
import cn.kmbeast.pojo.dto.query.extend.DietQueryDto;
import cn.kmbeast.pojo.dto.query.extend.TempDietQueryDto;
import cn.kmbeast.pojo.entity.DietHistory;
import cn.kmbeast.pojo.vo.DietHistoryVO;
import cn.kmbeast.pojo.vo.DietNutrimentVO;
import cn.kmbeast.pojo.vo.NutritionSummaryVO;

import java.util.List;

/**
 * 用户饮食业务逻辑接口
 * 提供饮食记录的增删改查、营养统计等功能
 */
public interface DietHistoryService {

    /**
     * 保存饮食记录
     * @param dietHistories 饮食记录列表
     * @return 操作结果
     */
    Result<String> save(List<DietHistory> dietHistories);

    /**
     * 更新饮食记录
     * @param dietHistory 饮食记录实体
     * @return 操作结果
     */
    Result<String> update(DietHistory dietHistory);

    /**
     * 批量删除饮食记录
     * @param ids ID列表
     * @return 操作结果
     */
    Result<String> batchDelete(List<Integer> ids);

    /**
     * 查询饮食记录（管理端）
     * @param dietHistoryQueryDto 查询参数
     * @return 饮食记录列表
     */
    Result<List<DietHistoryVO>> query(DietHistoryQueryDto dietHistoryQueryDto);

    /**
     * 查询用户自己的饮食记录
     * @param dietHistoryQueryDto 查询参数
     * @return 饮食记录列表
     */
    Result<List<DietHistoryVO>> queryUser(DietHistoryQueryDto dietHistoryQueryDto);

    /**
     * 查询饮食营养信息
     * @param dietQueryDto 查询参数
     * @return 营养信息列表
     */
    Result<List<DietNutrimentVO>> queryDietNutrimentInfo(DietQueryDto dietQueryDto);

    /**
     * 计算临时饮食的营养成分
     * @param tempDietQueryDto 临时饮食数据
     * @return 营养摘要列表
     */
    Result<List<NutritionSummaryVO>> calculateNutrition(TempDietQueryDto tempDietQueryDto);

}
