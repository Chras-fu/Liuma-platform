package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.Api;
import com.autotest.LiuMa.database.mapper.ApiMapper;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.*;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@Service
public class ApiImportService {
    @Resource
    private ApiMapper apiMapper;

    private Components components;

    public void importApi(MultipartFile file,String sourceType, String projectId, String moduleId, String userId){
        try {
            StringBuilder stringBuilder ;
            InputStream bb = file.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(bb);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            bb.close();
            if (this.verifyApi(stringBuilder.toString(), sourceType)){
                if(sourceType.equals("postman")){
                    this.savePostmanApi(stringBuilder.toString(), projectId, moduleId, userId);
                }else if(sourceType.equals("swagger")){
                    this.saveSwaggerApi(stringBuilder.toString(), projectId, moduleId, userId);
                }
            }else{
                throw new LMException("格式校验错误 请检查文件");
            }
        } catch (Exception e) {
            throw new LMException("接口导入失败"+ e.getMessage());
        }
    }

    private boolean verifyApi(String data, String sourceType) {
        JSONObject json = JSONObject.parseObject(data);
        if (sourceType.equals("postman")) {
            JSONObject info = json.getJSONObject("info");
            if (info == null) {
                return false;
            } else {
                return info.getString("_postman_id") != null;
            }
        } else if(sourceType.equals("swagger")){
            //swagger3格式文件的基本校验
            return json.get("openapi") != null && json.get("servers") != null && json.get("paths") != null && json.get("components") != null;
        }
        return false;
    }

    private void getAllPostmanApi(LinkedList<JSONObject> apiInfoList, List<Object> apiList){
        for (Object anApiList : apiList) {  // 递归获取postman接口
            JSONObject json = JSONObject.parseObject(anApiList.toString());
            if (json.getJSONArray("item") != null){
                this.getAllPostmanApi(apiInfoList, json.getJSONArray("item"));
            }else {
                apiInfoList.add(json);
            }
        }
    }

    private void savePostmanApi(String data, String projectId, String moduleId, String userId){
        JSONObject requestJsonObject = JSONObject.parseObject(data);
        LinkedList<JSONObject> apiInfoList = new LinkedList<>();  //存放每个api信息的列表
        this.getAllPostmanApi(apiInfoList, requestJsonObject.getJSONArray("item"));
        for (JSONObject apiInfo: apiInfoList){
            JSONObject request =  apiInfo.getJSONObject("request");
            if(!request.containsKey("url")) continue;
            JSONObject url =  new JSONObject();
            String path;
            if(request.get("url") instanceof String){
                try {
                    URL u = new URL(request.getString("url"));
                    path = u.getPath();
                }catch (Exception e){
                    continue;
                }
            }else {
                url = request.getJSONObject("url");
                List<String> apiPathList = url.getJSONArray("path").toJavaList(String.class);
                path = String.format("/%s", String.join("/", apiPathList));
            }
            //组装header
            JSONArray header = new JSONArray();
            if(request.containsKey("header")) {
                List<JSONObject> headerData = request.getJSONArray("header").toJavaList(JSONObject.class);
                for (JSONObject headerParam: headerData) {
                    JSONObject tmpHeaderParam = this.getHeaderParam(headerParam.getString("key"), headerParam.getString("value"));
                    header.add(tmpHeaderParam);
                }
            }
            //query参数
            JSONArray query = new JSONArray();
            if(url.containsKey("query")) {
                List<JSONObject> queryData = url.getJSONArray("query").toJavaList(JSONObject.class);
                for (JSONObject queryParam : queryData) {
                    JSONObject tmpQueryParam = this.getQueryParam(queryParam.getString("key"), queryParam.getString("value"), true);
                    query.add(tmpQueryParam);
                }
            }
            //rest参数
            JSONArray rest = new JSONArray();   // 目前postman没有rest参数 todo
            //body参数
            JSONObject body = this.getRequestBody("json", "", new JSONArray(), new JSONArray(), new JSONObject());
            if(request.containsKey("body")){
                JSONObject bodyData = request.getJSONObject("body");
                JSONArray tmpParams = new JSONArray();
                if (bodyData.getString("mode").equals("raw") && bodyData.get("options") != null &&
                        bodyData.getJSONObject("options").getJSONObject("raw").getString("language").equals("json")) {  //json格式
                    body = this.getRequestBody("json", "", new JSONArray(), new JSONArray(), JSONObject.parseObject(body.getString("raw")));
                } else if (bodyData.getString("mode").equals("raw")) { // raw格式
                    if(bodyData.get("options") == null){
                        body = this.getRequestBody("text", bodyData.getString("raw"), new JSONArray(), new JSONArray(), new JSONObject());
                    }else {
                        if(bodyData.getJSONObject("options").getJSONObject("raw").getString("language").equals("javascript")) continue;
                        body = this.getRequestBody(bodyData.getJSONObject("options").getJSONObject("raw").getString("language"),
                                bodyData.getString("raw"), new JSONArray(), new JSONArray(), new JSONObject());
                    }
                } else if (bodyData.getString("mode").equals("formdata") || bodyData.getString("mode").equals("urlencoded")) {  //formdata格式
                    JSONArray form = new JSONArray();
                    List<JSONObject> formData = bodyData.getJSONArray(bodyData.getString("mode")).toJavaList(JSONObject.class);
                    for (JSONObject formParam : formData) {
                        form.add(this.getFormParam(formParam.getString("key"), formParam.getString("type"), formParam.getString("value")));
                    }
                    if(bodyData.getString("mode").equals("formdata")){
                        body = this.getRequestBody("form-data", "", new JSONArray(), form, new JSONObject());
                    }else {
                        body = this.getRequestBody("form-urlencoded", "", new JSONArray(), form, new JSONObject());
                    }
                } else if(bodyData.getString("mode").equals("file")){
                    JSONArray file = this.getFileParam();
                    body = this.getRequestBody("file", "", file, new JSONArray(), new JSONObject());
                } else{
                    continue;
                }
            }
            Api postmanApi = new Api();
            postmanApi.setId(UUID.randomUUID() + "");
            postmanApi.setName(apiInfo.getString("name"));
            postmanApi.setLevel("P0");
            postmanApi.setModuleId(moduleId);
            postmanApi.setProjectId(projectId);
            postmanApi.setHeader(header.toJSONString());
            postmanApi.setBody(body.toJSONString());
            postmanApi.setQuery(query.toJSONString());
            postmanApi.setRest(rest.toJSONString());
            postmanApi.setMethod(request.getString("method"));
            postmanApi.setPath(path);
            postmanApi.setProtocol("HTTP");
            postmanApi.setDomainSign("");
            postmanApi.setDescription("");
            postmanApi.setCreateTime(System.currentTimeMillis());
            postmanApi.setUpdateTime(System.currentTimeMillis());
            postmanApi.setUpdateUser(userId);
            postmanApi.setCreateUser(userId);
            postmanApi.setStatus("Normal");
            apiMapper.addApi(postmanApi);
        }
    }

    private void saveSwaggerApi(String data, String projectId, String moduleId, String userId){
        SwaggerParseResult result = new OpenAPIParser().readContents(data,null,null);
        OpenAPI openAPI = result.getOpenAPI(); //获取方法swagger对象
        Paths paths = openAPI.getPaths(); //获取方法对象集合
        Set<String> pathNames = paths.keySet(); // 获取所有方法路径
        Components components = openAPI.getComponents(); // 获取实体类对象集合
        for (String pathName : pathNames) {
            PathItem pathItem = paths.get(pathName);
            // 每个请求方式单独生成一个接口
            Map<PathItem.HttpMethod, Operation> operations = pathItem.readOperationsMap();
            for(PathItem.HttpMethod method: operations.keySet()){
                Operation operation = operations.get(method);
                // 先解析参数
                List<Parameter> parameters = operation.getParameters();
                JSONArray header = new JSONArray();
                JSONArray query = new JSONArray();
                JSONArray rest = new JSONArray();
                if(parameters != null) {
                    parameters.forEach(param -> {
                        if (param instanceof QueryParameter) {
                            JSONObject queryParam = this.getQueryParam(param.getName(), String.valueOf(param.getExample()), param.getRequired());
                            query.add(queryParam);
                        }else if(param instanceof PathParameter){
                            JSONObject pathParam = this.getRestParam(param.getName(), String.valueOf(param.getExample()));
                            rest.add(pathParam);
                        }else if(param instanceof HeaderParameter) {
                            JSONObject headerParam = this.getHeaderParam(param.getName(), String.valueOf(param.getExample()));
                            header.add(headerParam);
                        } // 其他参数暂不支持
                    });
                }
                // 解析body
                RequestBody requestBody = operation.getRequestBody();
                Content content = null;
                if(requestBody != null){
                    content = operation.getRequestBody().getContent();
                }
                JSONObject body = this.getRequestBody("json", "", new JSONArray(), new JSONArray(), new JSONObject());
                if (content != null) {
                    Set<String> contentTypes = content.keySet();
                    if(contentTypes.contains(org.springframework.http.MediaType.APPLICATION_JSON_VALUE)){
                        // 优先 json 请求体
                        MediaType mediaType = content.get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
                        Schema schema = mediaType.getSchema();
                        Object json = this.getRequestBodyData(schema, components);
                        body = this.getRequestBody("json", "", new JSONArray(), new JSONArray(), json);
                    }else if(contentTypes.contains(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE)){
                        JSONArray file = this.getFileParam();
                        body = this.getRequestBody("file", "", file, new JSONArray(), new JSONObject());
                    }else if(contentTypes.contains(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)){
                        JSONArray form = new JSONArray();
                        MediaType mediaType = content.get(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                        Schema schema = mediaType.getSchema();
                        JSONObject json = (JSONObject) this.getRequestBodyData(schema, components);
                        for(String name: json.keySet()){
                            JSONObject formParam = this.getFormParam(name, "String", json.getString(name));
                            form.add(formParam);
                        }
                        body = this.getRequestBody("file", "", new JSONArray(), form, new JSONObject());
                    }else if(contentTypes.contains(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)){
                        JSONArray form = new JSONArray();
                        MediaType mediaType = content.get(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE);
                        Schema schema = mediaType.getSchema();
                        JSONObject json = (JSONObject) this.getRequestBodyData(schema, components);
                        for(String name: json.keySet()){
                            JSONObject formParam = this.getFormParam(name, "String", json.getString(name));
                            form.add(formParam);
                        }
                        body = this.getRequestBody("file", "", new JSONArray(), form, new JSONObject());
                    }else if(contentTypes.contains(org.springframework.http.MediaType.TEXT_PLAIN_VALUE)){
                        MediaType mediaType = content.get(org.springframework.http.MediaType.TEXT_PLAIN_VALUE);
                        Schema schema = mediaType.getSchema();
                        String text = (String) this.getRequestBodyData(schema, components);
                        body = this.getRequestBody("text", text, new JSONArray(), new JSONArray(), new JSONObject());
                    }else if(contentTypes.contains(org.springframework.http.MediaType.TEXT_XML_VALUE)){
                        MediaType mediaType = content.get(org.springframework.http.MediaType.TEXT_XML_VALUE);
                        Schema schema = mediaType.getSchema();
                        String xml = (String) this.getRequestBodyData(schema, components);
                        body = this.getRequestBody("xml", xml, new JSONArray(), new JSONArray(), new JSONObject());
                    }else if(contentTypes.contains(org.springframework.http.MediaType.TEXT_HTML_VALUE)){
                        MediaType mediaType = content.get(org.springframework.http.MediaType.TEXT_HTML_VALUE);
                        Schema schema = mediaType.getSchema();
                        String html = (String) this.getRequestBodyData(schema, components);
                        body = this.getRequestBody("html", html, new JSONArray(), new JSONArray(), new JSONObject());
                    }else {
                        continue;   // 其余类型不支持
                    }
                }
                Api swaggerApi = new Api();
                swaggerApi.setId(UUID.randomUUID().toString());
                swaggerApi.setName(operation.getSummary());
                swaggerApi.setLevel("P0");
                swaggerApi.setModuleId(moduleId);
                swaggerApi.setProjectId(projectId);
                swaggerApi.setHeader(header.toJSONString());
                swaggerApi.setBody(body.toJSONString());
                swaggerApi.setQuery(query.toJSONString());
                swaggerApi.setRest(rest.toJSONString());
                swaggerApi.setMethod(method.toString().toUpperCase(Locale.ROOT));
                swaggerApi.setPath(pathName);
                swaggerApi.setProtocol("HTTP");
                swaggerApi.setDomainSign("");
                swaggerApi.setDescription(pathItem.getDescription());
                swaggerApi.setCreateTime(System.currentTimeMillis());
                swaggerApi.setUpdateTime(System.currentTimeMillis());
                swaggerApi.setUpdateUser(userId);
                swaggerApi.setCreateUser(userId);
                swaggerApi.setStatus("Normal");
                apiMapper.addApi(swaggerApi);
            }
        }
    }
    /**
     * 获取请求体数据
     */
    private Object getRequestBodyData(Schema schema, Components components){
        if (schema == null) return null;
        if (StringUtils.isNotBlank(schema.get$ref())) {
            JSONObject data;
            Schema sonNode = this.getModelByRef(schema.get$ref(), components);
            data = (JSONObject) this.getRequestBodyData(sonNode, components);
            return data;
        }else if(schema instanceof ObjectSchema){
            Map<String, Schema> schemaMap = schema.getProperties();
            JSONObject data = new JSONObject();
            for(String key: schemaMap.keySet()){
                data.put(key, this.getRequestBodyData(schemaMap.get(key), components));
            }
            return data;
        }else if(schema instanceof ArraySchema){
            JSONArray data = new JSONArray();
            data.add(this.getRequestBodyData(schema.getItems(), components));
            return data;
        }else if (schema instanceof StringSchema) {
            if(schema.getExample()!=null){
                return schema.getExample();
            }else {
                return "";
            }
        } else if (schema instanceof IntegerSchema) {
            if(schema.getExample()!=null){
                return schema.getExample();
            }else {
                return 0;
            }
        } else if (schema instanceof NumberSchema) {
            if(schema.getExample()!=null){
                return schema.getExample();
            }else {
                return 0.0;
            }
        } else if (schema instanceof BooleanSchema) {
            if(schema.getExample()!=null){
                return schema.getExample();
            }else {
                return true;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取包装类型实体类
     */
    private Schema getModelByRef(String ref, Components components) {
        if (StringUtils.isBlank(ref)) {
            return null;
        }
        if (ref.split("/").length > 3) {
            ref = ref.replace("#/components/schemas/", "");
        }
        if (components.getSchemas() != null)
            return components.getSchemas().get(ref);
        return null;
    }

    /**
     * 生成body参数
     */
    private JSONObject getRequestBody(String type, String raw, JSONArray file, JSONArray form, Object json) {
        JSONObject body = new JSONObject();
        body.put("type", type);
        body.put("raw", raw);
        body.put("file", file);
        body.put("form", form);
        body.put("json", json.toString());
        return body;
    }

    /**
     * 生成header参数
     */
    private JSONObject getHeaderParam(String name, String value) {
        JSONObject headerParam = new JSONObject();
        headerParam.put("name", name);
        headerParam.put("value", value);
        return headerParam;
    }

    /**
     * 生成rest参数
     */
    private JSONObject getRestParam(String name, String value) {
        JSONObject restParam = new JSONObject();
        restParam.put("name", name);
        restParam.put("value", value);
        return restParam;
    }

    /**
     * 生成query参数
     */
    private JSONObject getQueryParam(String name, String value, Boolean required) {
        JSONObject queryParam = new JSONObject();
        queryParam.put("name", name);
        queryParam.put("value", value);
        queryParam.put("required", required);
        return queryParam;
    }

    /**
     * 生成form表单格式
     */
    private JSONObject getFormParam(String name, String type, String value){
        JSONObject formParam = new JSONObject();
        formParam.put("name", name);
        if(type.toLowerCase(Locale.ROOT).equals("file")){
            formParam.put("type", "File");
            formParam.put("value", "");
        }else {
            formParam.put("type", "String");
            formParam.put("value", value);
        }
        formParam.put("required", true);
        return formParam;
    }

    /**
     * 生成文件格式
     */
    private JSONArray getFileParam(){
        JSONArray file = new JSONArray();
        JSONObject fileParam = new JSONObject();
        fileParam.put("file", "");
        file.add(fileParam);
        return file;
    }
}
