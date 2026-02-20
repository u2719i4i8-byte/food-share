package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.HomeStatsVO;

import java.util.List;

public interface ViewsService {

    Result<List<ChartVO>> staticControls();

    Result<HomeStatsVO> homeStats();

}
