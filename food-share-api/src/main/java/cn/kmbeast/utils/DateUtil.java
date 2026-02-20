package cn.kmbeast.utils;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import cn.kmbeast.pojo.vo.ChartVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期工具类
 * 功能：日期范围计算、日期统计
 */
public class DateUtil {

    /**
     * 构造时间查询器
     * 根据指定天数生成开始时间和结束时间
     *
     * @param days 时间范围（天数，-1表示查询全部）
     * @return QueryDto 包含开始时间和结束时间的查询对象
     */
    public static QueryDto startAndEndTime(Integer days) {
        // 查全部
        if (days == -1) {
            return new QueryDto();
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDayStart = now.minusDays(days).plusDays(1).with(LocalTime.of(0, 0)); // 下一天的开始时间
        LocalDateTime daysAgoEnd = nextDayStart.minusSeconds(1);
        return QueryDto.builder().startTime(daysAgoEnd).endTime(now).build();
    }

    /**
     * 统计指定天数内的记录数
     * 按日期分组统计数据量
     *
     * @param dayRange 往前查多少天
     * @param dates    数据源（时间列表）
     * @return 图表数据列表（日期-数量）
     */
    public static List<ChartVO> countDatesWithinRange(Integer dayRange, List<LocalDateTime> dates) {
        LocalDate startDate = LocalDate.now().minusDays(dayRange);
        List<ChartVO> chartVOS = new ArrayList<>();
        for (int offset = 0; offset <= dayRange; offset++) {
            LocalDate currentDate = startDate.plusDays(offset);
            String dateKey = String.format("%02d-%02d", currentDate.getMonthValue(), currentDate.getDayOfMonth());
            int count = (int) dates.stream()
                    .filter(dateTime -> dateTime.toLocalDate().equals(currentDate))
                    .count();
            if (count != 0){
                chartVOS.add(new ChartVO(dateKey, count));
            }
        }
        return chartVOS;
    }
}
