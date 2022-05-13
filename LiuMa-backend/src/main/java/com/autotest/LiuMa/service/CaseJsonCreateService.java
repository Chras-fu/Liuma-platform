package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.autotest.LiuMa.common.constants.ReportSourceType;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.common.utils.FileUtils;
import com.autotest.LiuMa.common.utils.ZipUtils;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.*;
import com.autotest.LiuMa.request.CaseApiRequest;
import com.autotest.LiuMa.request.CaseRequest;
import com.autotest.LiuMa.request.CaseWebRequest;
import com.autotest.LiuMa.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaseJsonCreateService {

    @Value("${task.file.path}")
    public String TASK_FILE_PATH;

    private static final String DOWNLOAD_PATH = "/openapi/task/file/download";

    @Resource
    private EngineMapper engineMapper;

    @Resource
    private CaseMapper caseMapper;

    @Resource
    private ApiMapper apiMapper;

    @Resource
    private CaseApiMapper caseApiMapper;

    @Resource
    private CaseWebMapper caseWebMapper;

    @Resource
    private DebugDataMapper debugDataMapper;

    @Resource
    private PlanCollectionMapper planCollectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    private DomainMapper domainMapper;

    @Resource
    private CommonParamMapper commonParamMapper;

    @Resource
    private OperationMapper operationMapper;

    @Resource
    private FunctionMapper functionMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ElementMapper elementMapper;

    public String getDownloadUrl(TaskDTO task, List<TaskTestCollectionResponse> testCollectionList){
        String taskFilePath = TASK_FILE_PATH+"/"+task.getProjectId()+"/"+task.getId();
        String taskZipPath = TASK_FILE_PATH+"/"+task.getProjectId();
        String zipFileName = task.getId();
        for(TaskTestCollectionResponse taskTestCollection: testCollectionList){
            String collectionFilePath = taskFilePath +"/"+ taskTestCollection.getCollectionId();
            for(TaskTestCaseResponse taskTestCase:taskTestCollection.getTestCaseList()){
                if(taskTestCase.getCaseType().equals("API")) {
                    TestCaseApiResponse testCase = this.getApiTestCaseJson(task.getEnvironmentId(), task.getSourceType(), taskTestCase);
                    JSONObject taskCase = JSONObject.parseObject(JSONObject.toJSONString(testCase, SerializerFeature.WriteMapNullValue), Feature.OrderedField);
                    // 同一个集合下同一条用例文件重复就覆盖 因数据一样没必要生成两份
                    FileUtils.createJsonFile(taskCase, collectionFilePath + "/" + testCase.getCaseId() + ".json");
                }else { // WEB用例
                    TestCaseWebResponse testCase = this.getWebTestCaseJson(task.getEnvironmentId(),task.getSourceType(), taskTestCase);
                    JSONObject taskCase = JSONObject.parseObject(JSONObject.toJSONString(testCase, SerializerFeature.WriteMapNullValue), Feature.OrderedField);
                    // 同一个集合下同一条用例文件重复就覆盖 因数据一样没必要生成两份
                    FileUtils.createJsonFile(taskCase, collectionFilePath + "/" + testCase.getCaseId() + ".json");
                }
            }
        }
        try { // 打包文件
            ZipUtils.compress(taskFilePath, taskZipPath, zipFileName);
            // 打包完成删除源文件夹
            FileUtils.deleteDir(taskFilePath);
            return DOWNLOAD_PATH+"/"+task.getId();
        } catch (Exception exception) {
            throw new LMException("json文件压缩失败");
        }
    }

    public JSONObject getDebugData(TaskDTO task, List<TaskTestCollectionResponse> testCollectionList){
        TaskTestCaseResponse taskTestCase = testCollectionList.get(0).getTestCaseList().get(0);
        if(taskTestCase.getCaseType().equals("API")){
            TestCaseApiResponse testCase = this.getApiTestCaseJson(task.getEnvironmentId(),task.getSourceType(), taskTestCase);
            return JSONObject.parseObject(JSONObject.toJSONString(testCase, SerializerFeature.WriteMapNullValue), Feature.OrderedField);
        }else { // web用例
            TestCaseWebResponse testCase = this.getWebTestCaseJson(task.getEnvironmentId(),task.getSourceType(), taskTestCase);
            return JSONObject.parseObject(JSONObject.toJSONString(testCase, SerializerFeature.WriteMapNullValue), Feature.OrderedField);
        }
    }

    public TestCaseWebResponse getWebTestCaseJson(String environmentId, String SourceType, TaskTestCaseResponse taskTestCase){
        // 拼装Web用例
        TestCaseWebResponse testCaseWeb = new TestCaseWebResponse();
        if(SourceType.equals(ReportSourceType.TEMP.toString())) {
            DebugData debugData = debugDataMapper.getDebugData(taskTestCase.getCaseId());
            CaseRequest caseRequest = JSONObject.parseObject(debugData.getData(), CaseRequest.class);
            testCaseWeb.setComment(caseRequest.getDescription());
            testCaseWeb.setCaseId(taskTestCase.getCaseId());
            testCaseWeb.setCaseName(caseRequest.getName());
            // 组装自定义函数
            testCaseWeb.setFunctions(this.getCaseFunctions(caseRequest.getCommonParam().getJSONArray("functions")));
            // 组装用例公参
            testCaseWeb.setParams(this.getCaseParams(caseRequest.getCommonParam().getJSONArray("params")));
            // 组装浏览器开关配置
            testCaseWeb.setStartDriver(caseRequest.getCommonParam().getBoolean("startDriver"));
            testCaseWeb.setCloseDriver(caseRequest.getCommonParam().getBoolean("closeDriver"));
            // 组装操作
            List<CaseWebRequest> caseWebs = caseRequest.getCaseWebs();
            List<TestCaseWebDataResponse> optList = new ArrayList<>();
            for (CaseWebRequest caseWebRequest:caseWebs){
                TestCaseWebDataResponse optData = new TestCaseWebDataResponse();
                Operation operation = operationMapper.getOperationDetail(caseWebRequest.getOperationId());
                optData.setOperationType(operation.getType());
                optData.setOperationId(caseWebRequest.getOperationId());
                if(operation.getFrom().equals("custom")){
                    optData.setOperationName("自定义");
                    optData.setOperationCode(operation.getCode());
                }else {
                    optData.setOperationName(operation.getName());
                    optData.setOperationCode(null);
                }
                optData.setOperationTrans(operation.getName());
                optData.setOperationElement(this.getWebElement(caseWebRequest.getElement()));
                optData.setOperationData(this.getWebData(caseWebRequest.getData(), environmentId));
                optList.add(optData);
            }
            testCaseWeb.setOptList(optList);
        }else {
            CaseDTO caseDTO = caseMapper.getCaseDetail(taskTestCase.getCaseId());
            testCaseWeb.setComment(caseDTO.getDescription());
            testCaseWeb.setCaseId(taskTestCase.getCaseId());
            testCaseWeb.setCaseName(caseDTO.getName());
            JSONObject commonParam = JSONObject.parseObject(caseDTO.getCommonParam());
            // 组装自定义函数
            testCaseWeb.setFunctions(this.getCaseFunctions(commonParam.getJSONArray("functions")));
            // 组装用例公参
            testCaseWeb.setParams(this.getCaseParams(commonParam.getJSONArray("params")));
            // 组装浏览器开关配置
            testCaseWeb.setStartDriver(commonParam.getBoolean("startDriver"));
            testCaseWeb.setCloseDriver(commonParam.getBoolean("closeDriver"));
            // 组装操作
            List<CaseWebDTO> caseWebs = caseWebMapper.getCaseWebList(taskTestCase.getCaseId());
            List<TestCaseWebDataResponse> optList = new ArrayList<>();
            for (CaseWebDTO caseWebDTO:caseWebs){
                TestCaseWebDataResponse optData = new TestCaseWebDataResponse();
                Operation operation = operationMapper.getOperationDetail(caseWebDTO.getOperationId());
                optData.setOperationType(operation.getType());
                optData.setOperationId(caseWebDTO.getOperationId());
                if(operation.getFrom().equals("custom")){
                    optData.setOperationName("自定义");
                    optData.setOperationCode(operation.getCode());
                }else {
                    optData.setOperationName(operation.getName());
                    optData.setOperationCode(null);
                }
                optData.setOperationTrans(operation.getName());
                optData.setOperationElement(this.getWebElement(JSONArray.parseArray(caseWebDTO.getElement())));
                optData.setOperationData(this.getWebData(JSONArray.parseArray(caseWebDTO.getData()), environmentId));
                optList.add(optData);
            }
            testCaseWeb.setOptList(optList);
        }
        return testCaseWeb;
    }

    public TestCaseApiResponse getApiTestCaseJson(String environmentId, String SourceType, TaskTestCaseResponse taskTestCase){
        // 拼装Api用例
        TestCaseApiResponse testCaseApi = new TestCaseApiResponse();
        if(SourceType.equals(ReportSourceType.TEMP.toString())){
            DebugData debugData = debugDataMapper.getDebugData(taskTestCase.getCaseId());
            CaseRequest caseRequest = JSONObject.parseObject(debugData.getData(), CaseRequest.class);
            testCaseApi.setComment(caseRequest.getDescription());
            testCaseApi.setCaseId(taskTestCase.getCaseId());
            testCaseApi.setCaseName(caseRequest.getName());
            // 组装自定义函数
            testCaseApi.setFunctions(this.getCaseFunctions(caseRequest.getCommonParam().getJSONArray("functions")));
            // 组装用例公参
            testCaseApi.setParams(this.getCaseParams(caseRequest.getCommonParam().getJSONArray("params")));
            List<CaseApiRequest> caseApis = caseRequest.getCaseApis();
            List<TestCaseApiDataResponse> apiList = new ArrayList<>();
            for (CaseApiRequest caseApiRequest:caseApis){
                TestCaseApiDataResponse apiData = new TestCaseApiDataResponse();
                ApiDTO apiDTO = apiMapper.getApiDetail(caseApiRequest.getApiId());
                apiData.setApiId(caseApiRequest.getApiId());
                apiData.setApiName(apiDTO.getName());
                apiData.setUrl(this.getUrlBySign(environmentId, apiDTO.getDomainSign(), apiDTO.getPath()));
                apiData.setPath(apiDTO.getPath());
                apiData.setMethod(apiDTO.getMethod());
                apiData.setProtocol(apiDTO.getProtocol());
                // 拼接header
                apiData.setHeaders(this.getApiHeader(caseRequest.getCommonParam().getString("header"), caseApiRequest.getHeader()));
                // 拼接proxy
                apiData.setProxies(this.getApiProxy(caseRequest.getCommonParam().getString("proxy")));
                // 组装body
                apiData.setBody(caseApiRequest.getBody());
                // 组装query
                apiData.setQuery(this.getApiQuery(caseApiRequest.getQuery()));
                // 组装rest
                apiData.setRest(this.getApiRest(caseApiRequest.getRest()));
                // 组装relation assertion
                apiData.setRelations(caseApiRequest.getRelation());
                apiData.setAssertions(caseApiRequest.getAssertion());
                // 组装controller
                apiData.setController(this.getApiController(caseApiRequest.getController()));
                apiList.add(apiData);
            }
            testCaseApi.setApiList(apiList);
        }else {
            CaseDTO caseDTO = caseMapper.getCaseDetail(taskTestCase.getCaseId());
            testCaseApi.setComment(caseDTO.getDescription());
            testCaseApi.setCaseId(taskTestCase.getCaseId());
            testCaseApi.setCaseName(caseDTO.getName());
            JSONObject commonParam = JSONObject.parseObject(caseDTO.getCommonParam());
            // 组装自定义函数
            testCaseApi.setFunctions(this.getCaseFunctions(commonParam.getJSONArray("functions")));
            // 组装用例公参
            testCaseApi.setParams(this.getCaseParams(commonParam.getJSONArray("params")));
            List<CaseApiDTO> caseApiDTOS = caseApiMapper.getCaseApiList(taskTestCase.getCaseId());
            List<TestCaseApiDataResponse> apiList = new ArrayList<>();
            for(CaseApiDTO caseApiDTO: caseApiDTOS){
                TestCaseApiDataResponse apiData = new TestCaseApiDataResponse();
                apiData.setApiId(caseApiDTO.getApiId());
                apiData.setApiName(caseApiDTO.getApiName());
                apiData.setUrl(this.getUrlBySign(environmentId, caseApiDTO.getApiDomainSign(), caseApiDTO.getApiPath()));
                apiData.setPath(caseApiDTO.getApiPath());
                apiData.setMethod(caseApiDTO.getApiMethod());
                apiData.setProtocol(caseApiDTO.getApiProtocol());
                // 拼接header
                JSONArray headers = JSONArray.parseArray(caseApiDTO.getHeader());
                apiData.setHeaders(this.getApiHeader(commonParam.getString("header"), headers));
                // 拼接proxy
                apiData.setProxies(this.getApiProxy(commonParam.getString("proxy")));
                // 组装body
                apiData.setBody(JSONObject.parseObject(caseApiDTO.getBody()));
                // 组装query
                apiData.setQuery(this.getApiQuery(JSONArray.parseArray(caseApiDTO.getQuery())));
                // 组装rest
                apiData.setRest(this.getApiRest(JSONArray.parseArray(caseApiDTO.getRest())));
                // 组装relation assertion
                apiData.setRelations(JSONArray.parseArray(caseApiDTO.getRelation()));
                apiData.setAssertions(JSONArray.parseArray(caseApiDTO.getAssertion()));
                // 组装controller
                apiData.setController(this.getApiController(JSONArray.parseArray(caseApiDTO.getController())));
                apiList.add(apiData);
            }
            testCaseApi.setApiList(apiList);
        }

        return testCaseApi;
    }

    public JSONObject getWebElement(JSONArray elements){
        JSONObject elementObj = new JSONObject();
        if(elements == null){
            return elementObj;
        }
        for(int i=0;i<elements.size();i++){
            JSONObject element = elements.getJSONObject(i);
            JSONObject elementData = new JSONObject();
            // 获取最新元素
            ElementDTO elementDTO = elementMapper.getElementById(element.getString("id"));
            if(elementDTO != null) {
                elementData.put("by", elementDTO.getBy());
                elementData.put("expression", elementDTO.getExpression());
                elementData.put("target", elementDTO.getModuleName() + " / " + elementDTO.getName());
            }else {
                elementData.put("by", element.getString("by"));
                elementData.put("expression", element.getString("expression"));
                elementData.put("target", element.getString("moduleName") + " / " + element.getString("name"));
            }
            elementObj.put(element.getString("paramName"), elementData);
        }
        return elementObj;
    }

    public JSONObject getWebData(JSONArray dataList, String environmentId){
        JSONObject dataObj = new JSONObject();
        if(dataList == null){
            return dataObj;
        }
        for(int i=0;i<dataList.size();i++){
            JSONObject data = dataList.getJSONObject(i);
            JSONObject dataValue = new JSONObject();
            dataValue.put("type", data.getString("type"));
            dataValue.put("value", data.getString("value"));
            dataObj.put(data.getString("paramName"), dataValue);
        }
        // 对domain以及path字段处理
        if(dataObj.containsKey("domain")){
            String domainValue = dataObj.getJSONObject("domain").getString("value");
            if(domainValue != null && !domainValue.equals("")){
                // 根据域名标识来获取域名
                Domain domain = domainMapper.getDomainByName(environmentId, domainValue);
                if(domain != null) {
                    dataObj.getJSONObject("domain").put("value", domain.getDomainData());
                }
            }else {  // 根据path来获取域名
                if(dataObj.containsKey("path")){
                    String path = dataObj.getString("path");
                    List<Domain> domainList = domainMapper.getPathDomainList(environmentId);
                    for(Domain domain: domainList){
                        String domainKey = domain.getDomainKey();
                        if(path.length() < domainKey.length()){
                            continue;
                        }
                        if(path.substring(0, domainKey.length()).equals(domainKey)){
                            dataObj.getJSONObject("domain").put("value", domain.getDomainData());
                            break;
                        }
                    }
                }
            }
        }
        return dataObj;
    }

    public JSONObject getApiController(JSONArray controller){
        JSONObject controllerObj = new JSONObject();
        if(controller == null){
            return controllerObj;
        }
        for(int i =0; i<controller.size(); i++) {
            JSONObject controllerData = controller.getJSONObject(i);
            controllerObj.put(controllerData.getString("name"), controllerData.getString("value"));
        }
        return controllerObj;
    }

    public JSONObject getApiRest(JSONArray rest){
        JSONObject restObj = new JSONObject();
        if(rest == null){
            return restObj;
        }
        for(int i =0; i<rest.size(); i++) {
            JSONObject restData = rest.getJSONObject(i);
            restObj.put(restData.getString("name"), restData.getString("value"));
        }
        return restObj;
    }

    public JSONObject getApiQuery(JSONArray query){
        JSONObject queryObj = new JSONObject();
        if(query==null){
            return queryObj;
        }
        for(int i =0; i<query.size(); i++) {
            JSONObject queryData = query.getJSONObject(i);
            queryObj.put(queryData.getString("name"), queryData.getString("value"));
        }
        return queryObj;
    }

    public JSONObject getApiProxy(String proxyId){
        // 生成接口用例代理
        ParamData paramData = commonParamMapper.getParamById(proxyId);
        try{
            return JSONObject.parseObject(paramData.getParamData());
        }catch (Exception exception) {
            return new JSONObject();
        }
    }

    public JSONObject getApiHeader(String headerId, JSONArray headerList){
        // 合并接口用例请求头
        ParamData paramData = commonParamMapper.getParamById(headerId);
        try{
            JSONObject header = new JSONObject();
            if(paramData != null){
                header = JSONObject.parseObject(paramData.getParamData());
            }
            if(headerList == null){
                return header;
            }
            for(int i =0; i<headerList.size(); i++){
                JSONObject paramObj = headerList.getJSONObject(i);
                String headerKey = paramObj.getString("name");
                String headerValue = paramObj.getString("value");
                for (String item : header.keySet()) {
                    // 用例中的同名header key 替换公参中的 key不区分大小写
                    if (item.equalsIgnoreCase(headerKey)) {
                        header.remove(item);
                        break;
                    }
                }
                header.put(headerKey, headerValue);
            }
            return header;
        }catch (Exception exception) {
            return new JSONObject();
        }
    }

    public JSONArray getCaseFunctions(JSONArray functions){
        // 获取用例所需要的自定义函数
        JSONArray functionList = new JSONArray();
        for(int i=0; i<functions.size();i++){
            JSONObject functionObj = new JSONObject();
            String functionId = functions.getString(i);
            Function function = functionMapper.getFunctionDetail(functionId);
            functionObj.put("name", function.getName());
            functionObj.put("code", function.getCode());
            JSONArray params = JSONArray.parseArray(function.getParam());
            JSONObject paramObj = new JSONObject();
            for(int j=0; j<params.size(); j++){
                paramObj.put(params.getJSONObject(j).getString("paramName"), params.getJSONObject(j).getString("type"));
            }
            functionObj.put("params", paramObj);
            functionList.add(functionObj);
        }
        return functionList;
    }

    public JSONObject getCaseParams(JSONArray params){
        // 获取用例所需要的公参
        JSONObject paramObj = new JSONObject();
        for(int i=0; i<params.size();i++){
            String paramId = params.getString(i);
            ParamData paramData = commonParamMapper.getParamById(paramId);
            JSONObject param = new JSONObject();
            param.put("type", paramData.getDataType());
            param.put("value", paramData.getParamData());
            paramObj.put(paramData.getName(), param);
        }
        return paramObj;
    }

    public String getUrlBySign( String environmentId, String domainSign, String path){
        // 匹配环境下的域名 匹配不到则为null
        String url = null;
        if(domainSign != null && !domainSign.equals("")){
            Domain domain = domainMapper.getDomainByName(environmentId, domainSign);
            if(domain != null) {
                url = domain.getDomainData();
            }
        }else {
            List<Domain> domainList = domainMapper.getPathDomainList(environmentId);
            for(Domain domain: domainList){
                String domainKey = domain.getDomainKey();
                if(path.length() < domainKey.length()){
                    continue;
                }
                if(path.substring(0, domainKey.length()).equals(domainKey)){
                    url = domain.getDomainData();
                    break;
                }
            }
        }
        return url;
    }

    public List<TaskTestCollectionResponse> getTaskTestCollectionList(TaskDTO task) {
        // 获取每次测试所需的测试任务用例 按照测试集合列表-测试集合(集合下用例列表)-测试用例 维度给出
        // 用例调试以临时数据id为集合id-用例id
        // 用例执行以用例id为 集合id-用例id 都只有一个集合一个用例
        // 集合执行只有一个集合 多条用例 计划执行有多个集合 多条用例
        List<TaskTestCollectionResponse> taskTestCollectionList = new ArrayList<>();
        if(task.getSourceType().equals(ReportSourceType.PLAN.toString())){
            List<PlanCollectionDTO> planCollections = planCollectionMapper.getPlanCollectionList(task.getSourceId());
            for(PlanCollectionDTO planCollectionDTO:planCollections){
                TaskTestCollectionResponse taskTestCollection = new TaskTestCollectionResponse();
                taskTestCollection.setCollectionId(planCollectionDTO.getCollectionId());
                List<TaskTestCaseResponse> taskTestCaseList = this.getTaskTestCaseList(planCollectionDTO.getCollectionId());
                taskTestCollection.setTestCaseList(taskTestCaseList);
                taskTestCollectionList.add(taskTestCollection);
            }
        }else if(task.getSourceType().equals(ReportSourceType.COLLECTION.toString())){
            TaskTestCollectionResponse taskTestCollection = new TaskTestCollectionResponse();
            taskTestCollection.setCollectionId(task.getSourceId());
            List<TaskTestCaseResponse> taskTestCaseList = this.getTaskTestCaseList(task.getSourceId());
            taskTestCollection.setTestCaseList(taskTestCaseList);
            taskTestCollectionList.add(taskTestCollection);
        }else if(task.getSourceType().equals(ReportSourceType.CASE.toString())){
            TaskTestCollectionResponse taskTestCollection = new TaskTestCollectionResponse();
            taskTestCollection.setCollectionId(task.getSourceId());
            List<TaskTestCaseResponse> taskTestCaseList = new ArrayList<>();
            TaskTestCaseResponse taskTestCase = new TaskTestCaseResponse();
            taskTestCase.setIndex(1L);
            taskTestCase.setCaseId(task.getSourceId());
            CaseDTO caseDTO = caseMapper.getCaseDetail(task.getSourceId());
            taskTestCase.setCaseType(caseDTO.getType());
            taskTestCaseList.add(taskTestCase);
            taskTestCollection.setTestCaseList(taskTestCaseList);
            taskTestCollectionList.add(taskTestCollection);
        }else if(task.getSourceType().equals(ReportSourceType.TEMP.toString())){
            DebugData debugData = debugDataMapper.getDebugData(task.getSourceId());
            CaseRequest caseRequest = JSONObject.parseObject(debugData.getData(), CaseRequest.class);
            TaskTestCollectionResponse taskTestCollection = new TaskTestCollectionResponse();
            taskTestCollection.setCollectionId(task.getSourceId());
            List<TaskTestCaseResponse> taskTestCaseList = new ArrayList<>();
            TaskTestCaseResponse taskTestCase = new TaskTestCaseResponse();
            taskTestCase.setIndex(1L);
            taskTestCase.setCaseId(task.getSourceId());
            taskTestCase.setCaseType(caseRequest.getType());
            taskTestCaseList.add(taskTestCase);
            taskTestCollection.setTestCaseList(taskTestCaseList);
            taskTestCollectionList.add(taskTestCollection);
        }
        return taskTestCollectionList;
    }

    private List<TaskTestCaseResponse> getTaskTestCaseList(String collectionId){
        // 获取任务集合下的用例列表
        List<CollectionCaseDTO> collectionCases = collectionCaseMapper.getCollectionCaseList(collectionId);
        List<TaskTestCaseResponse> taskTestCaseList = new ArrayList<>();
        for(CollectionCaseDTO collectionCaseDTO:collectionCases){
            TaskTestCaseResponse taskTestCase = new TaskTestCaseResponse();
            taskTestCase.setIndex(collectionCaseDTO.getIndex());
            taskTestCase.setCaseId(collectionCaseDTO.getCaseId());
            taskTestCase.setCaseType(collectionCaseDTO.getCaseType());
            taskTestCaseList.add(taskTestCase);
        }
        return taskTestCaseList;
    }
}
