package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.database.domain.Api;
import com.autotest.LiuMa.database.mapper.ApiMapper;
import com.autotest.LiuMa.dto.ApiDTO;
import com.autotest.LiuMa.request.ApiRequest;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApiService {

    @Resource
    private ApiMapper apiMapper;

    public String saveApi(ApiRequest apiRequest) {
        JSONObject apiObject = (JSONObject) JSON.toJSON(apiRequest);
        Api api = apiObject.toJavaObject(Api.class);
        if(api.getId().equals("") || api.getId() == null){ // 新增接口
            api.setId(UUID.randomUUID().toString());
            api.setCreateTime(System.currentTimeMillis());
            api.setUpdateTime(System.currentTimeMillis());
            api.setCreateUser(api.getUpdateUser());
            api.setStatus("Normal");
            apiMapper.addApi(api);
        }else{ // 修改接口
            api.setUpdateTime(System.currentTimeMillis());
            apiMapper.updateApi(api);
        }
        return api.getId();
    }

    public void deleteApi(ApiRequest apiRequest) {
        apiMapper.deleteApi(apiRequest.getId());
    }

    public ApiDTO getApiDetail(String apiId) {
        return apiMapper.getApiDetail(apiId);
    }

    public List<ApiDTO> getApiList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return apiMapper.getApiList(request);
    }

}
