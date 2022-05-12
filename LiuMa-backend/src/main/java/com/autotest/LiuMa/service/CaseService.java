package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.database.domain.Case;
import com.autotest.LiuMa.database.domain.CaseApi;
import com.autotest.LiuMa.database.domain.CaseWeb;
import com.autotest.LiuMa.database.domain.Element;
import com.autotest.LiuMa.database.mapper.CaseApiMapper;
import com.autotest.LiuMa.database.mapper.CaseMapper;
import com.autotest.LiuMa.database.mapper.CaseWebMapper;
import com.autotest.LiuMa.database.mapper.ElementMapper;
import com.autotest.LiuMa.dto.CaseApiDTO;
import com.autotest.LiuMa.dto.CaseDTO;
import com.autotest.LiuMa.dto.CaseWebDTO;
import com.autotest.LiuMa.dto.ElementDTO;
import com.autotest.LiuMa.request.CaseApiRequest;
import com.autotest.LiuMa.request.CaseRequest;
import com.autotest.LiuMa.request.CaseWebRequest;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class CaseService {

    @Resource
    private CaseMapper caseMapper;

    @Resource
    private CaseApiMapper caseApiMapper;

    @Resource
    private CaseWebMapper caseWebMapper;

    @Resource
    private ElementMapper elementMapper;

    public void saveCase(CaseRequest caseRequest) {
        JSONObject caseObject = (JSONObject) JSON.toJSON(caseRequest);
        Case testCase = caseObject.toJavaObject(Case.class);
        if(testCase.getId().equals("") || testCase.getId() == null){ // 新增用例
            testCase.setId(UUID.randomUUID().toString());
            testCase.setCreateTime(System.currentTimeMillis());
            testCase.setUpdateTime(System.currentTimeMillis());
            testCase.setCreateUser(testCase.getUpdateUser());
            testCase.setStatus("Normal");
            caseMapper.addCase(testCase);
        }else{ // 修改用例
            testCase.setUpdateTime(System.currentTimeMillis());
            caseMapper.updateCase(testCase);
        }
        if(caseRequest.getType().equals("API")){    // 新增接口用例事务
            caseApiMapper.deleteCaseApi(testCase.getId());  //先删除全部用例接口
            List<CaseApiRequest> caseApiArray = caseRequest.getCaseApis();
            List<CaseApi> caseApis = new ArrayList<>();
            for(CaseApiRequest caseApiRequest: caseApiArray){
                JSONObject caseApiObject = (JSONObject) JSON.toJSON(caseApiRequest);
                CaseApi caseApi = caseApiObject.toJavaObject(CaseApi.class);
                caseApi.setCaseId(testCase.getId());
                caseApi.setId(UUID.randomUUID().toString());
                caseApis.add(caseApi);
            }
            caseApiMapper.addCaseApi(caseApis);
        }else{ // 新增web操作步骤
            caseWebMapper.deleteCaseWeb(testCase.getId());  //先删除全部用例接口
            List<CaseWebRequest> caseWebArray = caseRequest.getCaseWebs();
            List<CaseWeb> caseWebs = new ArrayList<>();
            for(CaseWebRequest caseWebRequest: caseWebArray){
                String operationId = caseWebRequest.getOperationId();
                JSONArray elements = caseWebRequest.getElement();
                for(int i=0;i<elements.size();i++){
                    JSONObject element = elements.getJSONObject(i);
                    if(element.getBoolean("custom")){
                        Element ele = new Element();
                        ele.setId(UUID.randomUUID().toString());
                        ele.setName(element.getString("name"));
                        ele.setBy(element.getString("by"));
                        ele.setExpression(element.getString("expression"));
                        ele.setModuleId(element.getString("moduleId"));
                        ele.setProjectId(testCase.getProjectId());
                        ele.setCreateTime(System.currentTimeMillis());
                        ele.setUpdateTime(System.currentTimeMillis());
                        ele.setCreateUser(testCase.getUpdateUser());
                        ele.setUpdateUser(testCase.getUpdateUser());
                        ele.setStatus("Normal");
                        elementMapper.addElement(ele);
                        // 回写元素
                        element.put("id", ele.getId());
                        element.put("custom", false);
                    }
                }
                JSONObject caseWebObject = (JSONObject) JSON.toJSON(caseWebRequest);
                CaseWeb caseWeb = caseWebObject.toJavaObject(CaseWeb.class);
                caseWeb.setOperationId(operationId);
                caseWeb.setCaseId(testCase.getId());
                caseWeb.setId(UUID.randomUUID().toString());
                caseWebs.add(caseWeb);
            }
            caseWebMapper.addCaseWeb(caseWebs);
        }
    }

    public void deleteCase(CaseRequest caseRequest) {
        caseMapper.deleteCase(caseRequest.getId());
    }

    public CaseDTO getCaseDetail(String caseType, String caseId) {
        CaseDTO caseDTO = caseMapper.getCaseDetail(caseId);
        if(caseType.equalsIgnoreCase("API")){
            List<CaseApiDTO> caseApis = caseApiMapper.getCaseApiList(caseId);
            caseDTO.setCaseApis(caseApis);
        }else {
            List<CaseWebDTO> caseWebs = caseWebMapper.getCaseWebList(caseId);
            // 加载最新的UI元素
            for(CaseWebDTO caseWebDTO:caseWebs){
                JSONArray elementList = JSONArray.parseArray(caseWebDTO.getElement());
                for(int i=0;i<elementList.size();i++){
                    JSONObject element = elementList.getJSONObject(i);
                    String elementId = element.getString("id");
                    ElementDTO elementDTO = elementMapper.getElementById(elementId);
                    element.put("by", elementDTO.getBy());
                    element.put("name", elementDTO.getName());
                    element.put("expression", elementDTO.getExpression());
                    element.put("moduleId", elementDTO.getModuleId());
                    element.put("moduleName", elementDTO.getModuleName());
                }
                caseWebDTO.setElement(JSONArray.toJSONString(elementList));
            }
            caseDTO.setCaseWebs(caseWebs);
        }

        return caseDTO;
    }

    public List<CaseDTO> getCaseList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return caseMapper.getCaseList(request);
    }
}
