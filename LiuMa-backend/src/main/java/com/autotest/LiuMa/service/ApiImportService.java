package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.Api;
import com.autotest.LiuMa.database.mapper.ApiMapper;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ApiImportService {
    @Resource
    private ApiMapper apiMapper;

    // api file is valid ?
    public boolean verifyApi(JSONObject jsonObject, String platformType) {
        if (platformType.equals("postman")) {
            Object info = jsonObject.get("info");
            if (info == null) {
                System.out.println("postman_file missing info");
                return false;
            } else {
                JSONObject jsonObject1 = JSON.parseObject(info.toString());
                if (jsonObject1.get("_postman_id") == null) {
                    System.out.println("postman_file missing postman_id");
                    return false;
                }
            }
            return true;
        } else {
            //swagger3格式文件的基本校验
            return jsonObject.get("openapi") != null && jsonObject.get("servers") != null && jsonObject.get("paths") != null && jsonObject.get("components") != null;
        }
    }

    //处理api组成的List, 依次提取api的信息, 然后存数据库; 主方法是Map存储的信息, 这里提取出来具体的参数
    private void handleApiList(List<JSONObject> apiInfoList, String platformType, String projectId, String moduleId, String userId){
        if (platformType.equals("postman")){
            for (JSONObject jsonObject: apiInfoList){
                Api postmanApi = new Api();
                Map apiInfoMap = JSONObject.parseObject(jsonObject.toJSONString());
                Map requestMap = (Map) apiInfoMap.get("request");
                Map urlMap = (Map) requestMap.get("url");
                List apiPathList = (List) urlMap.get("path");
                String apiPath = String.format("/%s", String.join("/", apiPathList));
                List headerList = (List) requestMap.get("header");
                System.out.println("apiHeader: " + headerList);

                //组装header的存储数据
                ArrayList headerSaveList = new ArrayList();
                for (Object tmpHeaderList : headerList) {
                    HashMap headerMap = new HashMap(); //final save map
                    Map headerTmpMap = JSON.parseObject(tmpHeaderList + "", Map.class);
                    headerMap.put("name", headerTmpMap.get("key") + "");
                    headerMap.put("value", headerTmpMap.get("value") + "");
                    JSONObject headerJsonObject = new JSONObject(headerMap);

                    headerSaveList.add(JSON.toJSONString(headerJsonObject));
                }

                //query参数的提取 + 组装
                JSONArray querySaveArray = new JSONArray(); //final save Array
                List<Map<String, String>> queryList = (List) urlMap.get("query");  //postman api query param
                if (queryList != null) {
                    for (Map<String, String> queryJsonMap : queryList
                    ) {
                        JSONObject tmpQueryObject = new JSONObject();
                        tmpQueryObject.put("name", queryJsonMap.get("key"));
                        tmpQueryObject.put("value", queryJsonMap.get("value"));
                        tmpQueryObject.put("required", true);
                        querySaveArray.add(tmpQueryObject);
                    }
                }
                //body的提取 + 组装数据 逻辑
                Map bodyMap = (Map) requestMap.get("body");
                JSONObject bodySaveObject = new JSONObject();  //final save body data
                if (bodyMap != null) {
                    JSONArray tmpEmptyArray = new JSONArray();
                    if (bodyMap.get("options") != null && bodyMap.get("mode").equals("raw")) {  //json格式
                        bodySaveObject.put("raw", "");
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpEmptyArray);
                        bodySaveObject.put("json", bodyMap.get("raw"));
                        bodySaveObject.put("type", "json");
                    } else if (bodyMap.get("mode").equals("raw")) {    //text格式
                        bodySaveObject.put("raw", bodyMap.get("raw"));
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpEmptyArray);
                        bodySaveObject.put("json", "");
                        bodySaveObject.put("type", "raw");
                    } else if (bodyMap.get("mode").equals("formdata")) {  //formdata格式
                        JSONArray tmpDataArray = new JSONArray(); // use for form behind
                        List<Map<String, String>> postmanFormList = (List) bodyMap.get("formdata");
                        for (Map<String, String> tmpPostmanFormList : postmanFormList) {
                            tmpDataArray.add(packageData(tmpPostmanFormList.get("key"), tmpPostmanFormList.get("type"), tmpPostmanFormList.get("value")));
                        }

                        bodySaveObject.put("raw", "");
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpDataArray);//todo
                        bodySaveObject.put("json", "");
                        bodySaveObject.put("type", "form-data");
                    } else if (bodyMap.get("mode").equals("urlencoded")) {  //urlencoded格式
                        JSONArray tmpDataArray = new JSONArray(); // use for form behind
                        List<Map<String, String>> postmanFormList = (List) bodyMap.get("urlencoded");
                        for (Map<String, String> aPostmanFormList : postmanFormList) {
                            tmpDataArray.add(packageData(aPostmanFormList.get("key"), aPostmanFormList.get("type"), aPostmanFormList.get("value")));
                        }
                        bodySaveObject.put("raw", "");
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpDataArray);//todo
                        bodySaveObject.put("json", "");
                        bodySaveObject.put("type", "form-urlencoded");
                    } else {  //其它未知格式
                        System.out.println("import api mode: " + bodyMap.get("mode"));
                    }
                }

                postmanApi.setId(UUID.randomUUID() + "");
                postmanApi.setName(((JSONObject) apiInfoMap).getString("name"));
                postmanApi.setLevel("P0");
                postmanApi.setModuleId(moduleId);
                postmanApi.setProjectId(projectId);
                postmanApi.setHeader(headerSaveList + "");
                postmanApi.setBody(bodySaveObject + "");
                postmanApi.setQuery(querySaveArray + "");
//                postmanApi.setRest("tmp rest"); //postman暂不涉及rest参数的存储
                postmanApi.setMethod(requestMap.get("method") + "");
                postmanApi.setPath(apiPath);
                postmanApi.setProtocol((urlMap.get("protocol") + "").toUpperCase());
                postmanApi.setDomainSign("");
                postmanApi.setDescription("postman_api");

                postmanApi.setCreateTime(System.currentTimeMillis());
                postmanApi.setUpdateTime(System.currentTimeMillis());
                postmanApi.setUpdateUser(userId);
                postmanApi.setCreateUser(userId);
                postmanApi.setStatus("Normal");

                apiMapper.addApi(postmanApi);
                System.out.println("Postman入库的api对象: " + postmanApi);
                System.out.println("postman api save succ");
            }
        }else { //swagger

        }
    }

    //postman or swagger
    public boolean saveImportApi(JSONObject requestJsonObject, String platformType, Map<String, String> apiMap){
        if (platformType.equals("postman")) {
            LinkedList<JSONObject> fileApiList = new LinkedList<>();  //存放每个api信息的列表
            List apiList = requestJsonObject.getJSONArray("item");
            for (Object anApiList : apiList) {
                JSONObject jsonObject = JSONObject.parseObject(anApiList + "");

                //处理含有文件夹的情况(目前仅支持单层文件夹)
                if (jsonObject.getJSONArray("item") != null){
                    for (int k = 0; k < jsonObject.getJSONArray("item").size(); k++) {
                        fileApiList.add((JSONObject)jsonObject.getJSONArray("item").get(k));
                    }
                    continue;
                }

                fileApiList.add(jsonObject);
            }
            //调用处理apiInfo列表的方法
            handleApiList(fileApiList, platformType, apiMap.get("projectId"), apiMap.get("moduleId"), apiMap.get("userId"));
            return true;
        } else if (platformType.equals("swagger")){
            //todo 补充swagger逻辑
            JSONObject allPathObj = requestJsonObject.getJSONObject("paths");  //allPathObj   ep:  { "/api/add":{...}, "/api/delete":{...} }

//            Set<String> keySet = apiPathObj.keySet();
//            for (String key: keySet){
//                apiPathObj.get(key);
//            }

            Set<Map.Entry<String, Object>> entrySet = allPathObj.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                Api swaggerApi = new Api();
                // entry.getKey()  ep:  "/api/add"
                Map apiInfoObj = (Map)entry.getValue();  //apiInfoObj   ep: {"post": {...}}
                String tmpMethod = "";  //记录此api的请求方法
                for (Object apiMethod: apiInfoObj.keySet()){   //获取仅有1个键值对的key值
                    tmpMethod = apiMethod + "";
                    swaggerApi.setMethod(tmpMethod);
                }
                String apiName = apiInfoObj.get(tmpMethod) + "";


                swaggerApi.setId(UUID.randomUUID() + "");
                swaggerApi.setName(apiName);
                swaggerApi.setLevel("P0");
                swaggerApi.setModuleId(apiMap.get("module_id"));
                swaggerApi.setProjectId(apiMap.get("project_id"));
//            swaggerApi.setHeader("");   //一般不涉及header数据的提取

                swaggerApi.setBody( "");
                swaggerApi.setQuery("");
                
//                swaggerApi.setRest("tmp rest"); //swagger暂不涉及rest参数的存储
                swaggerApi.setPath(entry.getKey());  //   /api/add的接口path
                swaggerApi.setProtocol("HTTP");
                swaggerApi.setDomainSign("");
                swaggerApi.setDescription("swagger_api");

                swaggerApi.setCreateTime(System.currentTimeMillis());
                swaggerApi.setUpdateTime(System.currentTimeMillis());
                swaggerApi.setUpdateUser(apiMap.get("userId"));
                swaggerApi.setCreateUser(apiMap.get("userId"));
                swaggerApi.setStatus("Normal");

                apiMapper.addApi(swaggerApi);
                System.out.println("Postman入库的api对象: " + swaggerApi);
                System.out.println("postman api save succ");



    }
            return true;
        }else {
            System.out.println("不支持的文件导入格式!");
            return false;
        }
    }

    private JSONObject packageData(String key, String type, String value){
        JSONObject tmpObjectData = new JSONObject();
        tmpObjectData.put("name", key);
        tmpObjectData.put("type", type);
        tmpObjectData.put("value", value);
        tmpObjectData.put("required", true);
        return tmpObjectData;
    }
}
