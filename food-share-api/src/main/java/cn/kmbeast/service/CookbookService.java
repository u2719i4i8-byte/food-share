package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CookbookQueryDto;
import cn.kmbeast.pojo.entity.Cookbook;
import cn.kmbeast.pojo.vo.CookbookVO;
import cn.kmbeast.pojo.vo.SelectedVO;

import java.util.List;

/**
 * 食谱业务逻辑接口
 * 提供食谱的增删改查、下拉选择等功能
 */
public interface CookbookService {

    /**
     * 保存食谱
     * @param cookbook 食谱实体
     * @return 操作结果
     */
    Result<String> save(Cookbook cookbook);

    /**
     * 更新食谱
     * @param cookbook 食谱实体
     * @return 操作结果
     */
    Result<String> update(Cookbook cookbook);

    /**
     * 批量删除食谱
     * @param ids ID列表
     * @return 操作结果
     */
    Result<String> batchDelete(List<Integer> ids);

    /**
     * 查询食谱列表
     * @param cookbookQueryDto 查询参数
     * @return 食谱列表
     */
    Result<List<CookbookVO>> query(CookbookQueryDto cookbookQueryDto);

    /**
     * 查询食谱下拉选择项（全部公开）
     * @return 下拉选择列表
     */
    Result<List<SelectedVO>> querySelectedItems();

    /**
     * 查询用户自己的食谱下拉选择项
     * @return 下拉选择列表
     */
    Result<List<SelectedVO>> querySelectedItemsUser();

    /**
     * 查询食谱下拉选择项（用户自己的+公开的）
     * @return 下拉选择列表
     */
    Result<List<SelectedVO>> querySelectedItemsAll();

}
