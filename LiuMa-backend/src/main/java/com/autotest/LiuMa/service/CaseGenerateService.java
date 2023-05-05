package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.utils.StringUtils;
import com.autotest.LiuMa.database.domain.Api;
import com.autotest.LiuMa.database.domain.Case;
import com.autotest.LiuMa.database.domain.CaseApi;
import com.autotest.LiuMa.database.mapper.ApiMapper;
import com.autotest.LiuMa.database.mapper.CaseApiMapper;
import com.autotest.LiuMa.database.mapper.CaseMapper;
import com.autotest.LiuMa.dto.ApiParamRuleDTO;
import com.autotest.LiuMa.dto.ApiParamVerifyDTO;
import com.autotest.LiuMa.request.ApiParamRuleRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CaseGenerateService {

    @Resource
    private ApiMapper apiMapper;

    @Resource
    private CaseMapper caseMapper;

    @Resource
    private CaseApiMapper caseApiMapper;

    private static final Map<String, Object> ParamTypeMap = new HashMap<String, Object>() {{
        put("Int", 100); put("Float", 0.1); put("Boolean", true); // 不同类型参数默认值
        put("String", "test"); put("SpecialStr", "&test@");
    }};

    private static final Map<String, Object[]> ParamRequiredMap = new HashMap<String, Object[]>() {{
        put("must", new Object[]{"逆向", true, "不能空传", null});    // 用例类型 是否删除该字段 规则描述 字段值
        put("empty", new Object[]{"正向", false, "可以传空", ""});
        put("null", new Object[]{"正向", false, "可以传null", null});
        put("lost", new Object[]{"正向", true, "可以空传", null});
    }};

    public void generateCase(ApiParamRuleRequest request) {
        Api api = apiMapper.getApiDetail(request.getApiId());
        // 先新增健壮性用例
        Case testCase = new Case();
        testCase.setId(UUID.randomUUID().toString());
        testCase.setName("【健壮性用例】接口 "+api.getName()+" 字段健壮性校验");
        testCase.setType("API");
        testCase.setLevel("P1");
        testCase.setDescription(api.getDescription());
        testCase.setEnvironmentIds("[]");
        testCase.setModuleId("");
        testCase.setThirdParty("");
        testCase.setProjectId(api.getProjectId());
        testCase.setCommonParam(JSONObject.parseObject(
                "{\"functions\": [], \"params\": [],\"header\": \"\", \"proxy\": \"\"}").toJSONString());
        testCase.setCreateTime(System.currentTimeMillis());
        testCase.setUpdateTime(System.currentTimeMillis());
        testCase.setCreateUser(request.getCreateUser());
        testCase.setUpdateUser(request.getCreateUser());
        testCase.setStatus("Normal");
        caseMapper.addCase(testCase);
        // 先生成完整的接口请求
        List<CaseApi> caseApis = new ArrayList<>();
        CaseApi caseApi = new CaseApi();
        caseApi.setId(UUID.randomUUID().toString());
        caseApi.setCaseId(testCase.getId());
        caseApi.setApiId(api.getId());
        caseApi.setDescription("【正向用例】所有字段正常输入");
        caseApi.setIndex(1L);
        caseApi.setHeader(this.replaceArray(api.getHeader(), request.getHeader()));
        caseApi.setBody(this.replaceObject(api.getBody(), request.getBody()));
        caseApi.setQuery(this.replaceArray(api.getQuery(), request.getQuery()));
        caseApi.setRest(this.replaceArray(api.getHeader(), request.getHeader()));
        caseApi.setRelation("[]");
        caseApi.setAssertion(JSONArray.toJSONString(request.getPositiveAssertion()));
        caseApi.setController(JSONArray.parseArray("[{\"name\": \"errorContinue\", \"value\": true}]").toJSONString());
        caseApis.add(caseApi);
        // 再按照规则替换用例接口
        Long index = 1L;    // 接口序号
        for(ApiParamRuleDTO ruleDTO: request.getHeader()){
            List<ApiParamVerifyDTO> verifyDTOS = this.analysisRule("请求头", ruleDTO);
            for(ApiParamVerifyDTO verifyDTO: verifyDTOS){
                index++;
                caseApis.add(this.getCaseApi(caseApi, index, "header", verifyDTO, request));
            }
        }
        for(ApiParamRuleDTO ruleDTO: request.getBody()){
            List<ApiParamVerifyDTO> verifyDTOS = this.analysisRule("请求体", ruleDTO);
            for(ApiParamVerifyDTO verifyDTO: verifyDTOS){
                index++;
                caseApis.add(this.getCaseApi(caseApi, index, "body", verifyDTO, request));
            }
        }
        for(ApiParamRuleDTO ruleDTO: request.getQuery()){
            List<ApiParamVerifyDTO> verifyDTOS = this.analysisRule("QUERY参数", ruleDTO);
            for(ApiParamVerifyDTO verifyDTO: verifyDTOS){
                index++;
                caseApis.add(this.getCaseApi(caseApi, index, "query", verifyDTO, request));
            }
        }
        for(ApiParamRuleDTO ruleDTO: request.getRest()){
            List<ApiParamVerifyDTO> verifyDTOS = this.analysisRule("REST参数", ruleDTO);
            for(ApiParamVerifyDTO verifyDTO: verifyDTOS){
                index++;
                caseApis.add(this.getCaseApi(caseApi, index, "rest", verifyDTO, request));
            }
        }
        caseApiMapper.addCaseApi(caseApis);
    }

    private CaseApi getCaseApi(CaseApi temp, Long index, String replaceType, ApiParamVerifyDTO verifyDTO, ApiParamRuleRequest request){
        CaseApi caseApi = new CaseApi();
        caseApi.setId(UUID.randomUUID().toString());
        caseApi.setCaseId(temp.getCaseId());
        caseApi.setApiId(temp.getApiId());
        caseApi.setDescription(verifyDTO.getDescription());
        caseApi.setIndex(index);
        caseApi.setHeader(replaceType.equals("header")?
                this.replaceArrayWithVerify(temp.getHeader(), verifyDTO):temp.getHeader());
        caseApi.setBody(replaceType.equals("body")?
                this.replaceObjectWithVerify(temp.getBody(), verifyDTO):temp.getBody());
        caseApi.setQuery(replaceType.equals("query")?
                this.replaceArrayWithVerify(temp.getQuery(), verifyDTO):temp.getQuery());
        caseApi.setRest(replaceType.equals("rest")?
                this.replaceArrayWithVerify(temp.getRest(), verifyDTO):temp.getRest());
        caseApi.setRelation("[]");
        caseApi.setAssertion(JSONArray.toJSONString(verifyDTO.getDirection().equals("正向") ?
                request.getPositiveAssertion(): request.getOppositeAssertion()));
        caseApi.setController(JSONArray.parseArray("[{\"name\": \"errorContinue\", \"value\": true}]").toJSONString());
        return caseApi;
    }

    private String replaceArrayWithVerify(String data, ApiParamVerifyDTO verifyDTO){
        // 替换请求头 查询参数 路径参数 单个参数
        JSONArray params = JSONArray.parseArray(data);
        for(int i=0;i<params.size();i++){
            JSONObject param = params.getJSONObject(i);
            if(verifyDTO.getName().equals(param.getString("name"))){
                if(param.containsKey("type")){
                    param.put("type", verifyDTO.getType());
                }
                if(verifyDTO.getDelete()){
                    params.remove(i);
                }else {
                    param.put("value", verifyDTO.getValue());
                }
                break;
            }
        }
        return params.toJSONString();
    }

    private String replaceObjectWithVerify(String data, ApiParamVerifyDTO verifyDTO){
        // 替换请求体单个参数
        JSONObject body = JSONObject.parseObject(data);
        String type = body.getString("type");
        if(type.equals("form-data") || type.equals("form-urlencoded")){
            String formData = this.replaceArrayWithVerify(body.getJSONArray("form").toJSONString(), verifyDTO);
            body.put("form", JSONArray.parseArray(formData));
        }else if(type.equals("json")){
            JSONObject json = JSONObject.parseObject(body.getString("json"));
            if(json==null){
                json = new JSONObject();
            }
            if(verifyDTO.getDelete()){
                json = this.deleteJsonWithExpression(json, verifyDTO.getName());
            }
            json = this.replaceJsonWithExpression(json, verifyDTO.getName(), verifyDTO.getValue());
            body.put("json", json.toJSONString());
        }
        return body.toJSONString();
    }

    private String replaceArray(String data, List<ApiParamRuleDTO> rules){
        // 替换请求头 查询参数 路径参数 所有参数
        JSONArray params = JSONArray.parseArray(data);
        Map<String, ApiParamRuleDTO> ruleMap = rules.stream().collect(
                Collectors.toMap(ApiParamRuleDTO::getName, apiParamRuleDTO -> apiParamRuleDTO));
        for(int i=0;i<params.size();i++){
            JSONObject param = params.getJSONObject(i);
            ApiParamRuleDTO ruleDTO = ruleMap.get(param.getString("name"));
            if(ruleDTO == null){
                continue;   // 字段没做配置则不更改默认值
            }
            param.put("value", ruleDTO.getValue());
        }
        return params.toJSONString();
    }

    private String replaceObject(String data, List<ApiParamRuleDTO> rules){
        // 替换请求体所有参数
        JSONObject body = JSONObject.parseObject(data);
        String type = body.getString("type");
        if(type.equals("form-data") || type.equals("form-urlencoded")){
            String formData = this.replaceArray(body.getJSONArray("form").toJSONString(), rules);
            body.put("form", JSONArray.parseArray(formData));
        }else if(type.equals("json")){
            JSONObject json = JSONObject.parseObject(body.getString("json"));
            if(json==null){
                json = new JSONObject();
            }
            for (ApiParamRuleDTO apiParamRuleDTO: rules){
                json = this.replaceJsonWithExpression(json, apiParamRuleDTO.getName(),
                        this.convertDataType(apiParamRuleDTO.getType(), apiParamRuleDTO.getValue()));
            }
            body.put("json", json.toJSONString());
        }
        return body.toJSONString();
    }

    private JSONObject replaceJsonWithExpression(JSONObject json, String expression, Object value){
        DocumentContext ext = JsonPath.parse(json);
        JsonPath p = JsonPath.compile(expression);   //根据表达式找到该节点
        ext.set(p, value);
        return ext.json();
    }

    private JSONObject deleteJsonWithExpression(JSONObject json, String expression){
        DocumentContext ext = JsonPath.parse(json);
        ext.delete(expression);
        return ext.json();
    }

    private Object convertDataType(String type, Object value){
        Object result;
        switch (type){
            case "Int":
                result = Integer.parseInt((String) value);
                break;
            case "Float":
                result = Double.parseDouble((String) value);
                break;
            default:
                result = value;
        }
        return result;
    }

    private List<ApiParamVerifyDTO> analysisRule(String replaceType, ApiParamRuleDTO rule){
        List<ApiParamVerifyDTO> verifyDTOS = new ArrayList<>();
        // 参数必填性校验
        if(!rule.getRequired().equals("None")) {    // 除选择不校验外
            verifyDTOS.addAll(this.getParamVerifyListWithRequired(replaceType, rule.getName(), rule.getType(), rule.getRequired()));
        }
        // 参数类型校验
        if(!rule.getType().equals("None")) {    // 除选择不校验外
            verifyDTOS.addAll(this.getParamVerifyListWithType(replaceType, rule.getName(), rule.getType()));
        }
        // 参数范围校验
        if(!(rule.getType().equals("Boolean") && rule.getType().equals("None"))){   // 布尔型或者不校验参数类型者不校验范围
            verifyDTOS.addAll(this.getParamVerifyListWithRandom(replaceType, rule.getName(), rule.getType(), rule.getRandom()));
        }
        return verifyDTOS;
    }

    private List<ApiParamVerifyDTO> getParamVerifyListWithRequired(String replaceType, String name, String type, String required){
        // 生成字段必填性校验
        List<ApiParamVerifyDTO> verifyDTOS = new ArrayList<>();
        if(required.equals("None")){
            return verifyDTOS;
        }
        Object[] rule = ParamRequiredMap.get(required);
        ApiParamVerifyDTO verifyDTO = new ApiParamVerifyDTO();
        verifyDTO.setName(name);
        verifyDTO.setDirection(rule[0].toString());
        verifyDTO.setType(type.equals("SpecialStr")? "String": type);
        verifyDTO.setDelete((Boolean) rule[1]);
        verifyDTO.setDescription(String.format("【%s用例】校验%s%s类型字段%s必填性校验为:%s",
                rule[0].toString(), replaceType, type, name, rule[2].toString()));
        verifyDTO.setValue(rule[3]);
        verifyDTOS.add(verifyDTO);
        return verifyDTOS;
    }

    private List<ApiParamVerifyDTO> getParamVerifyListWithType(String replaceType, String name, String type){
        // 生成字段类型校验
        List<ApiParamVerifyDTO> verifyDTOS = new ArrayList<>();
        for(String t: ParamTypeMap.keySet()){
            if (t.equals(type) || (type.equals("SpecialStr") && t.equals("String"))) {
                continue;
            }
            ApiParamVerifyDTO verifyDTO = new ApiParamVerifyDTO();
            verifyDTO.setName(name);
            verifyDTO.setDirection("逆向");
            verifyDTO.setType(t.equals("SpecialStr")? "String": t);
            verifyDTO.setDescription(String.format("【逆向用例】校验%s%s类型字段%s输入%s类型值:%s",
                    replaceType, type, name, t, ParamTypeMap.get(t)));
            verifyDTO.setValue(ParamTypeMap.get(t));
            verifyDTOS.add(verifyDTO);
        }
        return verifyDTOS;
    }

    private List<ApiParamVerifyDTO> getParamVerifyListWithRandom(String replaceType, String name, String type, String random){
        // 生成边界值校验
        List<Object[]> randomRules = this.analysisRandom(random);
        List<ApiParamVerifyDTO> verifyDTOS = new ArrayList<>();
        for(Object[] randomRule:randomRules){
            ApiParamVerifyDTO verifyDTO = new ApiParamVerifyDTO();
            verifyDTO.setName(name);
            verifyDTO.setDirection(randomRule[0].toString());
            verifyDTO.setType("String");
            verifyDTO.setDescription(String.format("【%s用例】校验%s%s类型字段%s%s:%s",
                    randomRule[0].toString(), replaceType, type, name, randomRule[1].toString(), randomRule[2].toString()));
            if(type.equals("String")){
                verifyDTO.setValue(StringUtils.randomSimpleString((Integer) randomRule[2]));
            }else if(type.equals("SpecialStr")){
                verifyDTO.setValue(StringUtils.randomSpecialString((Integer) randomRule[2]));
            }else {
                verifyDTO.setType(type);
                verifyDTO.setValue(randomRule[2]);
            }
            verifyDTOS.add(verifyDTO);
        }
        return verifyDTOS;
    }

    private List<Object[]> analysisRandom(String random){
        List<Object[]> result = new ArrayList<>();
        if(random == null || random.equals("")){
            return result;
        }
        if(!((random.startsWith("[") || random.startsWith("(")) &&
                (random.endsWith("]") || random.endsWith(")")) && random.contains(","))){
            return result;
        }
        String mix = random.substring(1).split(",")[0];
        String max = random.substring(0, random.length()-1).split(",")[1];
        // 判断最大最小可输入值
        Number[] mixList = this.getNumberList(mix, random.startsWith("["), true);
        Number[] maxList = this.getNumberList(max, random.startsWith("]"), false);
        result.add(new Object[]{"正向", "可用最小边界值(长度)", mixList[0]});
        result.add(new Object[]{"逆向", "最小边界值(长度)之外", mixList[1]});
        result.add(new Object[]{"正向", "可用最大边界值(长度)", maxList[0]});
        result.add(new Object[]{"逆向", "最大边界值(长度)之外", maxList[1]});
        return result;
    }

    public Number[] getNumberList(String num, Boolean isContain, Boolean isMix){
        // 返回列表内容 有效值 无效值
        int decimal = 0;
        if(num.contains(".")){
            decimal = num.split("\\.")[1].length();
        }
        BigDecimal step = BigDecimal.valueOf(1 / Math.pow(10, decimal));
        BigDecimal bdValue = new BigDecimal(num);
        BigDecimal[] result;
        if(isContain){
            if(isMix){
                result = new BigDecimal[]{bdValue, bdValue.subtract(step)};
            }else {
                result = new BigDecimal[]{bdValue, bdValue.add(step)};
            }
        }else {
            if(isMix){
                result = new BigDecimal[]{bdValue.subtract(step), bdValue};
            }else {
                result = new BigDecimal[]{bdValue.add(step), bdValue};
            }
        }
        if(num.contains(".")) {
            return new Number[]{result[0].doubleValue(), result[1].doubleValue()};
        }else {
            return new Number[]{result[0].longValue(), result[1].longValue()};
        }
    }
}
