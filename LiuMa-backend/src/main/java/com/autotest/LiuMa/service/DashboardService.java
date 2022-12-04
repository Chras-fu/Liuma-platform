package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.ReportSourceType;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.common.constants.TaskType;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.StatisticsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Transactional(rollbackFor = Exception.class)
public class DashboardService {

    @Resource
    private StatisticsMapper statisticsMapper;


    public JSONObject getDashboardData(String projectId){
        JSONObject data = new JSONObject();
        List<DailyStatistics> dailyStatisticsList = statisticsMapper.getDailyDataByProject(projectId);
        JSONObject caseAddData = new JSONObject();
        caseAddData.put("xAxis", new JSONArray());
        caseAddData.put("yMaxLeft", 0);
        caseAddData.put("yMaxRight", 0);
        caseAddData.put("apiCaseNew", new JSONArray());
        caseAddData.put("webCaseNew", new JSONArray());
        caseAddData.put("appCaseNew", new JSONArray());
        caseAddData.put("apiCaseSum", new JSONArray());
        caseAddData.put("webCaseSum", new JSONArray());
        caseAddData.put("appCaseSum", new JSONArray());
        JSONObject caseRunData = new JSONObject();
        caseRunData.put("xAxis", new JSONArray());
        caseRunData.put("yMaxLeft", 0);
        caseRunData.put("apiCaseRun", new JSONArray());
        caseRunData.put("webCaseRun", new JSONArray());
        caseRunData.put("appCaseRun", new JSONArray());
        caseRunData.put("apiCasePassRate", new JSONArray());
        caseRunData.put("webCasePassRate", new JSONArray());
        caseRunData.put("appCasePassRate", new JSONArray());
        for (int i=dailyStatisticsList.size();i>0;i--){
            DailyStatistics dailyStatistics = dailyStatisticsList.get(i-1);
            caseAddData.getJSONArray("xAxis").add(dailyStatistics.getStatDate());
            caseAddData.getJSONArray("apiCaseNew").add(dailyStatistics.getApiCaseNew());
            caseAddData.getJSONArray("webCaseNew").add(dailyStatistics.getWebCaseNew());
            caseAddData.getJSONArray("appCaseNew").add(dailyStatistics.getAppCaseNew());
            caseAddData.getJSONArray("apiCaseSum").add(dailyStatistics.getApiCaseSum());
            caseAddData.getJSONArray("webCaseSum").add(dailyStatistics.getWebCaseSum());
            caseAddData.getJSONArray("appCaseSum").add(dailyStatistics.getAppCaseSum());
            caseAddData.put("yMaxLeft", this.getMax(dailyStatistics.getApiCaseNew(), dailyStatistics.getWebCaseNew(), dailyStatistics.getAppCaseNew()));
            caseAddData.put("yMaxRight", this.getMax(dailyStatistics.getApiCaseSum(), dailyStatistics.getWebCaseSum(), dailyStatistics.getAppCaseSum()));

            caseRunData.getJSONArray("xAxis").add(dailyStatistics.getStatDate());
            caseRunData.getJSONArray("apiCaseRun").add(dailyStatistics.getApiCaseRun());
            caseRunData.getJSONArray("webCaseRun").add(dailyStatistics.getWebCaseRun());
            caseRunData.getJSONArray("appCaseRun").add(dailyStatistics.getAppCaseRun());
            caseRunData.getJSONArray("apiCasePassRate").add(dailyStatistics.getApiCasePassRate());
            caseRunData.getJSONArray("webCasePassRate").add(dailyStatistics.getWebCasePassRate());
            caseRunData.getJSONArray("appCasePassRate").add(dailyStatistics.getAppCasePassRate());
            caseRunData.put("yMaxLeft", this.getMax(dailyStatistics.getApiCaseRun(), dailyStatistics.getWebCaseRun(), dailyStatistics.getAppCaseRun()));
        }
        data.put("caseAddData", caseAddData);
        data.put("caseRunData", caseRunData);
        SumStatistics sumStatistics = statisticsMapper.getSumDataByProject(projectId);
        if(sumStatistics != null){
            data.put("apiCaseTotal", sumStatistics.getApiCaseTotal());
            data.put("apiCaseNewWeek", sumStatistics.getApiCaseNewWeek());
            data.put("webCaseTotal", sumStatistics.getWebCaseTotal());
            data.put("webCaseNewWeek", sumStatistics.getWebCaseNewWeek());
            data.put("appCaseTotal", sumStatistics.getAppCaseTotal());
            data.put("appCaseNewWeek", sumStatistics.getAppCaseNewWeek());
            data.put("caseRunTotal", sumStatistics.getCaseRunTotal());
            data.put("caseRunToday", sumStatistics.getCaseRunToday());
            data.put("planRunWeekTop", JSONObject.parseObject(sumStatistics.getPlanRunWeekTop()));
            data.put("caseFailWeekTop", JSONObject.parseObject(sumStatistics.getCaseFailWeekTop()));
        }

        return data;
    }

    private Integer getMax(Integer a, Integer b, Integer c){
        Integer max = 0;
        if(a >= b && a >= c){
            max = a;
        }
        if(b >= a && b >= c){
            max = b;
        }
        if (c >= a && c >=b){
            max = c;
        }
        return max;
    }
}
