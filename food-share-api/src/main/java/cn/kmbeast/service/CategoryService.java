package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.entity.Category;

import java.util.List;

/**
 * 美食类别业务逻辑接口
 * 提供美食分类的增删改查功能
 */
public interface CategoryService {

    /**
     * 保存分类
     * @param category 分类实体
     * @return 操作结果
     */
    Result<String> save(Category category);

    /**
     * 更新分类
     * @param category 分类实体
     * @return 操作结果
     */
    Result<String> update(Category category);

    /**
     * 批量删除分类
     * @param ids ID列表
     * @return 操作结果
     */
    Result<String> batchDelete(List<Integer> ids);

    /**
     * 查询分类列表
     * @param categoryQueryDto 查询参数
     * @return 分类列表
     */
    Result<List<Category>> query(CategoryQueryDto categoryQueryDto);

}
