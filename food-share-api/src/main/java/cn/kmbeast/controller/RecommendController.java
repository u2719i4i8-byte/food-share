package cn.kmbeast.controller;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.vo.GourmetVO;
import cn.kmbeast.service.RecommendService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 内容推荐的控制器
 */
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    /**
     * 查询推荐的美食做法数据
     * 基于用户行为的智能推荐
     *
     * @param item 需要的推荐的条数
     * @return Result<List < GourmetVO>> 响应结果
     */
    @GetMapping(value = "/{item}")
    @ResponseBody
    public Result<List<GourmetVO>> recommendGourmet(@PathVariable Integer item) {
        return recommendService.recommendGourmet(item);
    }

    /**
     * 查询分类推荐美食数据
     * 基于用户行为和分类的智能推荐
     *
     * @param categoryIds 分类ID列表（逗号分隔）
     * @param item        推荐数量
     * @return Result<List < GourmetVO>> 响应结果
     */
    @GetMapping(value = "/category/{item}")
    @ResponseBody
    public Result<List<GourmetVO>> recommendByCategory(
            @RequestParam(required = false) String categoryIds,
            @PathVariable Integer item) {
        List<Integer> categoryIdList = null;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            categoryIdList = java.util.Arrays.stream(categoryIds.split(","))
                    .map(Integer::parseInt)
                    .collect(java.util.stream.Collectors.toList());
        }
        return recommendService.recommendByCategory(categoryIdList, item);
    }

}

