package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.database.domain.Api;
import com.autotest.LiuMa.dto.JsonSchemaItemDTO;
import com.autotest.LiuMa.database.mapper.ApiMapper;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ApiImportService {
    @Resource
    private ApiMapper apiMapper;

    private Components components;

    // api file is valid ?
    public boolean verifyApi(String data, String platformType) {
        JSONObject jsonObject = JSON.parseObject(data);
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
    public boolean saveImportApi(String data, String platformType, Map<String, String> apiMap){
        if (platformType.equals("postman")) {
            JSONObject requestJsonObject = JSON.parseObject(data);
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
            //todo 解析swagger3逻辑
            SwaggerParseResult result = new OpenAPIParser().readContents(data,null,null);
            //获取方法swagger对象
            OpenAPI openAPI = result.getOpenAPI();
            //获取方法对象集合
            Paths paths = openAPI.getPaths();
            // 获取所以方法路径
            Set<String> pathNames = paths.keySet();
            // 获取实体类对象集合
            this.components = openAPI.getComponents();
            for (String pathName : pathNames) {
                Api swaggerApi = new Api();
                PathItem pathItem = paths.get(pathName);
                swaggerApi.setId(UUID.randomUUID() + "");

                swaggerApi.setLevel("P0");
                swaggerApi.setModuleId(apiMap.get("moduleId"));
                swaggerApi.setProjectId(apiMap.get("projectId"));
                JSONObject bodySaveObject = new JSONObject();  //保存body参数
                JSONArray tmpDataArray = new JSONArray(); // /保存form参数
                Map<String,Object> tmpJsonDateObject = new HashMap<>();  //保存json参数
                JSONArray tmpRawDataArray = new JSONArray();  //保存raw参数

//              swaggerApi.setHeader("");   //一般不涉及header数据的提取
                if (pathItem.getPost() != null){
                    Operation operation = pathItem.getPost();
                    swaggerApi.setName(pathItem.getPost().getSummary() + "");
                    //解析api请求对象body
                    parseRequestBody(operation, tmpJsonDateObject,tmpDataArray,tmpRawDataArray);
                    JSONArray tmpEmptyArray = new JSONArray();
                    if (!tmpDataArray.isEmpty()){
                        bodySaveObject.put("raw", "");
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpDataArray);//todo
                        bodySaveObject.put("json", "");
                        bodySaveObject.put("type", "form-data");

                    } else if (!tmpJsonDateObject.isEmpty()) {
                        bodySaveObject.put("raw", "");
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpEmptyArray);
                        bodySaveObject.put("json", JSONObject.toJSONString(tmpJsonDateObject));
                        bodySaveObject.put("type", "json");

                    } else if (!tmpRawDataArray.isEmpty()) {
                        bodySaveObject.put("raw", tmpRawDataArray);
                        bodySaveObject.put("file", tmpEmptyArray);
                        bodySaveObject.put("form", tmpEmptyArray);
                        bodySaveObject.put("json", "");
                        bodySaveObject.put("type", "raw");

                    }
                    swaggerApi.setBody(bodySaveObject + "");
                    swaggerApi.setMethod("POST");
                }
                if (pathItem.getGet() != null){
                    Operation operation = pathItem.getGet();
                    swaggerApi.setName(pathItem.getGet().getSummary() + "");
                    swaggerApi.setMethod("GET");
                    //解析api请求对象参数
                    parseParameters(operation, swaggerApi);
                }
//                swaggerApi.setRest("tmp rest"); //swagger暂不涉及rest参数的存储
                swaggerApi.setPath(pathName+ "");  //   /api/add的接口path
                swaggerApi.setProtocol("HTTP");
                swaggerApi.setDomainSign("");
                swaggerApi.setDescription("swagger_api");

                swaggerApi.setCreateTime(System.currentTimeMillis());
                swaggerApi.setUpdateTime(System.currentTimeMillis());
                swaggerApi.setUpdateUser(apiMap.get("userId"));
                swaggerApi.setCreateUser(apiMap.get("userId"));
                swaggerApi.setStatus("Normal");

                apiMapper.addApi(swaggerApi);
                System.out.println("Swagger入库的api对象: " + swaggerApi);
                System.out.println("swagger api save succ");
            }
            return true;
        }else {
            System.out.println("不支持的文件导入格式!");
            return false;
        }
    }
    /**
     * 解析body参数
     * @param operation
     * @param tmpJsonDateObject
     * @param tmpDataArray
     * @param tmpRawDataArray
     */
    private void parseRequestBody(Operation operation,Map<String,Object> tmpJsonDateObject, JSONArray tmpDataArray,JSONArray tmpRawDataArray) {
        if (operation.getRequestBody() == null) {
            //解析表单格式参数
            parseFromBody(operation, tmpDataArray);
        }else {
            //解析json格式参数
            parseBody(operation,tmpJsonDateObject, tmpDataArray,tmpRawDataArray);
        }
    }

    /**
     * 解析 form表单参数
     * @param operation
     * @param tmpDataArray
     */
    private void parseFromBody(Operation operation, JSONArray tmpDataArray){
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        parameters.forEach(parameter -> {
            if (parameter != null) {
                //解析From参数
                parseFromParameters(parameter, tmpDataArray);
            }
        });
    }

    /**
     * 解析body参数
     * @param operation
     * @param tmpJsonDateObject
     * @param tmpDataArray
     * @param tmpRawDataArray
     */
    private void parseBody(Operation operation, Map<String,Object> tmpJsonDateObject, JSONArray tmpDataArray , JSONArray tmpRawDataArray) {
        Content content = operation.getRequestBody().getContent();
        if (content == null) {
            return;
        }
        // 多个contentType ，优先取json
        String contentType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
        MediaType mediaType = content.get(contentType);
        if (mediaType == null) {
            Set<String> contentTypes = content.keySet();
            if (contentTypes.size() == 0) {  //  防止空指针
                return;
            }
            contentType = contentTypes.iterator().next();
            if (StringUtils.isBlank(contentType)) {
                return;
            }
            mediaType = content.get(contentType);
            if (contentType.equals("*/*")) {
                contentType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
            }
        }

        Set<String> refSet = new HashSet<>();
        Map<String, Schema> infoMap = new HashMap();
        Schema schema = mediaType.getSchema();
        Object bodyData = null;
        if (!StringUtils.equals(contentType, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)) {
            //解析json结构
            bodyData = parseSchemaToJson(schema, refSet, infoMap);
            if (bodyData == null) return;
        }

        if (StringUtils.equals(contentType, org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            //解析key-value结构
            parseKvBody(schema, tmpDataArray, bodyData);
        } else if (StringUtils.equals(contentType, org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)) {
            //解析key-value结构
            parseKvBody(schema, tmpDataArray, bodyData);
        } else if (StringUtils.equals(contentType, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)) {
            //解析json结构
            Map<String, JsonSchemaItemDTO> jsonSchemaItemMap = parseSchema(schema, refSet).getProperties();
            Map<String, String> stringMap = new HashMap<>();
            jsonSchemaItemMap.forEach((key,value) -> {
                stringMap.put(key,value.getType());
            });
            parseSchemaToApi(stringMap,tmpJsonDateObject,jsonSchemaItemMap);
        }else {
            tmpRawDataArray.add(bodyData.toString());
        }

    }
    /**
     * 解析属性转api实体类，生成随机参数
     * @param infoMap
     * @param tmpJsonDateObject
     * @param jsonSchemaItemMap
     */
    private void parseSchemaToApi(Map<String, String> infoMap,Map<String,Object> tmpJsonDateObject,Map<String, JsonSchemaItemDTO> jsonSchemaItemMap) {
        //解析swagger对象成json对象，并且随机生成值
        try {
            infoMap.forEach((name,type) ->{
                if (StringUtils.contains(type, "string")){
                    tmpJsonDateObject.put(name, "");
                }else if (StringUtils.contains(type, "integer")){
                    tmpJsonDateObject.put(name, 0);

                }else if (StringUtils.contains(type, "number")){
                    tmpJsonDateObject.put(name,0);

                }else if (StringUtils.contains(type, "boolean")){
                    tmpJsonDateObject.put(name,false);

                }else if (StringUtils.contains(type, "object")){
                    /**
                     * 1.根据名字拿到实体类
                     * 2.getProperties()获取属性值
                     * 3.遍历属性设置名字和类型
                     * 3.调用解析方法设置类型对应的value
                     */
                    JsonSchemaItemDTO schemaItem = jsonSchemaItemMap.get(name);
                    Map<String, JsonSchemaItemDTO> properties = schemaItem.getProperties();
                    Map<String, String> stringMap = new HashMap<>();
                    properties.forEach((k,v) -> {
                        stringMap.put(k,v.getType());
                    });
                    JSONObject object = new JSONObject();
                    parseSchemaToApi(stringMap,object,properties);
                    tmpJsonDateObject.put(name,object);

                }else if (StringUtils.contains(type, "array")){
                    /**
                     * 1.parseArrayToDateObject()解析数组值
                     */
                    JSONArray jsonArray = new JSONArray();
                    tmpJsonDateObject.put(name,jsonArray);

                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    /**
     * 解析属性类型
     * @param schema
     * @param refSet
     * @return
     */
    private JsonSchemaItemDTO parseSchema(Schema schema, Set<String> refSet) {
        if (schema == null) return null;
        JsonSchemaItemDTO item = new JsonSchemaItemDTO();
        if(schema.getRequired()!=null){
            item.setRequired(schema.getRequired());
        }
        if (StringUtils.isNotBlank(schema.get$ref())) {
            if (refSet.contains(schema.get$ref())) return item;
            item.setType("object");
            refSet.add(schema.get$ref());
            Schema modelByRef = getModelByRef(schema.get$ref());
            if (modelByRef != null){
                item.setProperties(parseSchemaProperties(modelByRef, refSet));
                item.setRequired(modelByRef.getRequired());
            }
        } else if (schema instanceof ArraySchema) {
            Schema items = ((ArraySchema) schema).getItems();
            item.setType("array");
            item.setItems(new ArrayList<>());
            JsonSchemaItemDTO arrayItem = parseSchema(items, refSet);
            if (arrayItem != null) item.getItems().add(arrayItem);
        } else if (schema instanceof ObjectSchema) {
            item.setType("object");
            item.setProperties(parseSchemaProperties(schema, refSet));
        } else if (schema instanceof StringSchema) {
            item.setType("string");
        } else if (schema instanceof IntegerSchema) {
            item.setType("integer");
        } else if (schema instanceof NumberSchema) {
            item.setType("number");
        } else if (schema instanceof BooleanSchema) {
            item.setType("boolean");
        } else {
            return null;
        }

        item.setDescription(schema.getDescription());
        return item;
    }
    private Map<String, JsonSchemaItemDTO> parseSchemaProperties(Schema schema, Set<String> refSet) {
        Map<String, JsonSchemaItemDTO> JsonSchemaProperties = new LinkedHashMap<>();
        if (null == schema ) return JsonSchemaProperties;
        Map<String, Schema> properties = schema.getProperties();
        if (null == properties) return JsonSchemaProperties;
        properties.forEach((key, value) -> {
            JsonSchemaItemDTO item = new JsonSchemaItemDTO();
            item.setDescription(schema.getDescription());
            JsonSchemaItemDTO proItem = parseSchema(value, refSet);
            if (proItem != null) JsonSchemaProperties.put(key, proItem);
        });
        return JsonSchemaProperties;
    }
    /**
     * 解析属性转json类型
     * @param schema
     * @param refSet
     * @param infoMap
     * @return
     */
    private Object parseSchemaToJson(Schema schema, Set<String> refSet, Map<String, Schema> infoMap) {
        if (schema == null) {
            return new JSONObject(true);
        }
        infoMap.put(schema.getName(), schema);
        if (StringUtils.isNotBlank(schema.get$ref())) {
            //解析包装对象类型参数
            if (refSet.contains(schema.get$ref())) {
                return new JSONObject(true);
            }
            refSet.add(schema.get$ref());
            Schema modelByRef = getModelByRef(schema.get$ref());
            Object propertiesResult = null;
            if (modelByRef != null)
                propertiesResult = parseSchemaPropertiesToJson(modelByRef, refSet, infoMap);
            return propertiesResult == null ? getDefaultValueByPropertyType(schema) : propertiesResult;
        } else if (schema instanceof ArraySchema) {
            //解析数组类型参数
            JSONArray jsonArray = new JSONArray();
            Schema items = ((ArraySchema) schema).getItems();
            parseSchemaToJson(items, refSet, infoMap);
            jsonArray.add(parseSchemaToJson(items, refSet, infoMap));
            return jsonArray;
        } else if (schema instanceof ObjectSchema) {
            //解析对象类型参数
            Object propertiesResult = parseSchemaPropertiesToJson(schema, refSet, infoMap);
            return propertiesResult == null ? getDefaultValueByPropertyType(schema) : propertiesResult;
        } else {
            return schema;
        }
    }

    /**
     * 根据类型获取默认值
     * @param value
     * @return
     */
    private Object getDefaultValueByPropertyType(Schema value) {
        Object example = value.getExample();
        if (value instanceof IntegerSchema) {
            return example == null ? 0 : example;
        } else if (value instanceof NumberSchema) {
            return example == null ? 0.0 : example;
        } else if (value instanceof StringSchema) {
            return example == null ? "" : example;
        } else {// todo 其他类型?
            return StringUtils.isBlank(value.getDescription()) ? "" : value.getDescription();
        }
    }

    /**
     * 解析key-value类型body参数
     * @param schema
     * @param tmpDataArray
     * @param data
     */
    private void parseKvBody(Schema schema, JSONArray tmpDataArray, Object data) {
        if (data instanceof JSONObject) {
            ((JSONObject) data).forEach((k, v) -> {
                //解析包装对象类型参数
                parseKvBodyItem(schema, tmpDataArray, k);
            });
        } else {
            if(data instanceof Schema) {
                Schema dataSchema = (Schema) data;
                if (StringUtils.isNotBlank(dataSchema.getName())) {
                    parseKvBodyItem(schema, tmpDataArray, dataSchema.getName());
                } else if (dataSchema.getProperties() != null) {
                    dataSchema.getProperties().forEach((k, v) -> {
                        if (v instanceof Schema) {
                            parseKvBodyItem(v, tmpDataArray, k.toString());
                        }
                    });
                }
            }
        }
    }
    /**
     * 获取包装类型实体类
     * @param ref
     * @return
     */
    private Schema getModelByRef(String ref) {
        if (StringUtils.isBlank(ref)) {
            return null;
        }
        if (ref.split("/").length > 3) {
            ref = ref.replace("#/components/schemas/", "");
        }
        if (this.components.getSchemas() != null)
            return this.components.getSchemas().get(ref);
        return null;
    }
    /**
     * 解析key-value类型参数
     * @param schemaObject
     * @param tmpDataArray
     * @param name
     */
    private void parseKvBodyItem(Object schemaObject, JSONArray tmpDataArray, String name) {
        Schema schema = (Schema) schemaObject;
        if (schema == null) return;
        //添加key-value类型参数
        tmpDataArray.add(packageData(name, schema.getType(), String.valueOf(schema.getExample() == null ? "" : schema.getExample())));
    }
    /**
     * 解析属性转json类型
     * @param schema
     * @param refSet
     * @param infoMap
     * @return
     */
    private Object parseSchemaPropertiesToJson(Schema schema, Set<String> refSet, Map<String, Schema> infoMap) {
        if (schema == null) return null;
        Map<String, Schema> properties = schema.getProperties();
        if (properties.isEmpty()) return null;
        JSONObject jsonObject = new JSONObject(true);
        properties.forEach((key, value) -> {
            jsonObject.put(key, parseSchemaToJson(value, refSet, infoMap));
        });
        return jsonObject;
    }
    /**
     * 解析参数
     * @param operation
     * @param api
     */
    private void parseParameters(Operation operation, Api api) {
        List<Parameter> parameters = operation.getParameters();
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        JSONArray querySaveArray = new JSONArray(); //保存Query参数
        parameters.forEach(parameter -> {
            if (parameter instanceof QueryParameter) {
                //解析Query参数
                parseQueryParameters(parameter, querySaveArray);
            }
        });
        api.setQuery(querySaveArray + "");
    }

    /**
     * 解析From参数，返回json数组结果
     * @param fromParameter
     * @param fromSaveArray
     */
    private void parseFromParameters(Parameter fromParameter,JSONArray fromSaveArray) {
        //解析成{name：”123“，value：”123“，required：”true“}
        JSONObject tmpQueryObject = new JSONObject();
        tmpQueryObject.put("name", fromParameter.getName());
        tmpQueryObject.put("value", String.valueOf(fromParameter.getExample()));
        tmpQueryObject.put("required", fromParameter.getRequired());
        fromSaveArray.add(tmpQueryObject);
    }

    /**
     * 解析Query参数，返回json数组结果
     * @param parameter
     * @param querySaveArray
     */
    private void parseQueryParameters(Parameter parameter,JSONArray querySaveArray) {
        //解析成{name：”123“，value：”123“，required：”true“}
        QueryParameter queryParameter = (QueryParameter) parameter;
        JSONObject tmpQueryObject = new JSONObject();
        tmpQueryObject.put("name", queryParameter.getName());
        tmpQueryObject.put("value", String.valueOf(queryParameter.getExample()));
        tmpQueryObject.put("required", queryParameter.getRequired());
        querySaveArray.add(tmpQueryObject);
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
