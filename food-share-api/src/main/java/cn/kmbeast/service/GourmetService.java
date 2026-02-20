package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.GourmetQueryDto;
import cn.kmbeast.pojo.entity.Gourmet;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.GourmetListVO;
import cn.kmbeast.pojo.vo.GourmetVO;

import java.util.List;

/**
 * 美食做法业务逻辑接口
 * 提供美食做法的增删改查、审核、统计等功能
 */
public interface GourmetService {

    /**
     * 保存美食做法
     * @param gourmet 美食实体
     * @return 操作结果
     */
    Result<String> save(Gourmet gourmet);

    /**
     * 更新美食做法
     * @param gourmet 美食实体
     * @return 操作结果
     */
    Result<String> update(Gourmet gourmet);

    /**
     * 批量删除美食做法
     * @param ids ID列表
     * @return 操作结果
     */
    Result<String> batchDelete(List<Integer> ids);

    /**
     * 查询美食做法（管理端）
     * @param gourmetQueryDto 查询参数
     * @return 查询结果
     */
    Result<List<GourmetVO>> query(GourmetQueryDto gourmetQueryDto);

    /**
     * 审核美食做法
     * @param id 美食ID
     * @return 操作结果
     */
    Result<String> audit(Integer id);

    /**
     * 查询美食列表（用户端）
     * @param gourmetQueryDto 查询参数
     * @return 查询结果
     */
    Result<List<GourmetListVO>> queryList(GourmetQueryDto gourmetQueryDto);

    /**
     * 根据ID查询美食详情
     * @param id 美食ID
     * @return 查询结果
     */
    Result<List<GourmetVO>> queryById(Integer id);

    /**
     * 查询当前用户的美食发布
     * @return 查询结果
     */
    Result<List<GourmetListVO>> queryUser();

    /**
     * 按天数统计美食增量
     * @param day 天数
     * @return 统计结果
     */
    Result<List<ChartVO>> daysQuery(Integer day);

    /**
     * 按浏览量查询美食
     * @param gourmetQueryDto 查询参数
     * @return 查询结果
     */
    Result<List<GourmetVO>> queryByView(GourmetQueryDto gourmetQueryDto);

}
