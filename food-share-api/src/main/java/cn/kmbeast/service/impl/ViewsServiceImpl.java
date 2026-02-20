package cn.kmbeast.service.impl;

import cn.kmbeast.mapper.*;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.*;
import cn.kmbeast.pojo.em.InteractionTypeEnum;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.HomeStatsVO;
import cn.kmbeast.service.ViewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ViewsServiceImpl implements ViewsService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private GourmetMapper gourmetMapper;
    @Resource
    private CookbookMapper cookbookMapper;
    @Resource
    private NutrimentMapper nutrimentMapper;
    @Resource
    private ContentNetMapper contentNetMapper;
    @Resource
    private InteractionMapper interactionMapper;

    @Override
    public Result<List<ChartVO>> staticControls() {
        List<ChartVO> chartVOS = new ArrayList<>();
        int userCount = userMapper.queryCount(new UserQueryDto());
        change(userCount, "存量用户", chartVOS);
        int gourmetCount = gourmetMapper.queryCount(new GourmetQueryDto());
        change(gourmetCount, "美食做法", chartVOS);
        int cookbookCount = cookbookMapper.queryCount(new CookbookQueryDto());
        change(cookbookCount, "收录食谱", chartVOS);
        int nutrimentCount = nutrimentMapper.queryCount(new NutrimentQueryDto());
        change(nutrimentCount, "营养模型", chartVOS);
        int contentNetCount = contentNetMapper.queryCount(new ContentNetQueryDto());
        change(contentNetCount, "内容分享", chartVOS);
        int interactionCount = interactionMapper.queryCount(new InteractionQueryDto());
        change(interactionCount, "互动量", chartVOS);
        return ApiResult.success(chartVOS);
    }

    @Override
    public Result<HomeStatsVO> homeStats() {
        InteractionQueryDto viewQuery = new InteractionQueryDto();
        viewQuery.setType(InteractionTypeEnum.VIEW.getType());
        int viewCount = interactionMapper.queryCount(viewQuery);

        InteractionQueryDto shareQuery = new InteractionQueryDto();
        shareQuery.setType(InteractionTypeEnum.COLLECTION.getType());
        int shareCount = interactionMapper.queryCount(shareQuery);

        HomeStatsVO statsVO = HomeStatsVO.builder()
                .gourmets(gourmetMapper.queryCount(new GourmetQueryDto()))
                .users(userMapper.queryCount(new UserQueryDto()))
                .views(viewCount)
                .shares(shareCount)
                .build();
        return ApiResult.success(statsVO);
    }

    private void change(Integer count, String name, List<ChartVO> chartVOS) {
        ChartVO chartVO = new ChartVO(name, count);
        chartVOS.add(chartVO);
    }


}
