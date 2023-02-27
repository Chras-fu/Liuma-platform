package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.DailyStatistics;
import com.autotest.LiuMa.database.domain.SumStatistics;
import com.autotest.LiuMa.dto.StatisticsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticsMapper {
    void updateDailyData(List<DailyStatistics> dailyStatisticsList);

    void updateSumData(List<SumStatistics> sumStatisticsList);

    List<DailyStatistics> getDailyDataByProject(String projectId);

    SumStatistics getSumDataByProject(String projectId);

    List<StatisticsDTO> getCaseCountByProject();

    List<StatisticsDTO> getCaseTodayNewCountByProject();

    List<StatisticsDTO> getCaseWeekNewCountByProject();

    List<StatisticsDTO> getCaseTodayRunCountByProject();

    List<StatisticsDTO> getCaseTotalRunCountByProject();

    List<StatisticsDTO> getCaseTotalTodayRunCountByProject();

    List<StatisticsDTO> getPlanRunTopByProject();

    List<StatisticsDTO> getCaseFailTopByProject();
}