package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.*;
import com.autotest.LiuMa.request.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private CaseAppMapper caseAppMapper;

    @Resource
    private ElementMapper elementMapper;

    @Resource
    private ControlMapper controlMapper;

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
        }else if (caseRequest.getType().equals("WEB")){ // 新增web操作步骤
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
                        try {
                            elementMapper.addElement(ele);
                        }catch (Exception e){
                            throw new LMException("自定义的元素新增失败 请检查命名是否重复");
                        }
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
        }else { // 新增app操作步骤
            caseAppMapper.deleteCaseApp(testCase.getId());  //先删除全部用例接口
            List<CaseAppRequest> caseAppArray = caseRequest.getCaseApps();
            List<CaseApp> caseApps = new ArrayList<>();
            for(CaseAppRequest caseAppRequest: caseAppArray){
                String operationId = caseAppRequest.getOperationId();
                JSONArray controls = caseAppRequest.getElement();
                for(int i=0;i<controls.size();i++){
                    JSONObject control = controls.getJSONObject(i);
                    if(control.getBoolean("custom")){
                        Control con = new Control();
                        con.setId(UUID.randomUUID().toString());
                        con.setName(control.getString("name"));
                        con.setSystem(control.getString("system"));
                        con.setBy(control.getString("by"));
                        con.setExpression(control.getString("expression"));
                        con.setModuleId(control.getString("moduleId"));
                        con.setProjectId(testCase.getProjectId());
                        con.setCreateTime(System.currentTimeMillis());
                        con.setUpdateTime(System.currentTimeMillis());
                        con.setCreateUser(testCase.getUpdateUser());
                        con.setUpdateUser(testCase.getUpdateUser());
                        con.setStatus("Normal");
                        try {
                            controlMapper.addControl(con);
                        }catch (Exception e){
                            throw new LMException("自定义的控件新增失败 请检查命名是否重复");
                        }
                        // 回写元素
                        control.put("id", con.getId());
                        control.put("custom", false);
                    }
                }
                JSONObject caseAppObject = (JSONObject) JSON.toJSON(caseAppRequest);
                CaseApp caseApp = caseAppObject.toJavaObject(CaseApp.class);
                caseApp.setOperationId(operationId);
                caseApp.setCaseId(testCase.getId());
                caseApp.setId(UUID.randomUUID().toString());
                caseApps.add(caseApp);
            }
            caseAppMapper.addCaseApp(caseApps);
        }
    }

    public void deleteCase(CaseRequest caseRequest) {
        caseMapper.deleteCase(caseRequest.getId());
    }

    public String getCaseSystem(String caseId){
        return caseMapper.getCaseSystem(caseId);
    }

    public CaseDTO getCaseDetail(String caseType, String caseId) {
        CaseDTO caseDTO = caseMapper.getCaseDetail(caseId);
        if(caseType.equalsIgnoreCase("API")){
            List<CaseApiDTO> caseApis = caseApiMapper.getCaseApiList(caseId);
            caseDTO.setCaseApis(caseApis);
        }else if(caseType.equalsIgnoreCase("WEB")){
            List<CaseWebDTO> caseWebs = caseWebMapper.getCaseWebList(caseId, caseType.toLowerCase(Locale.ROOT));
            // 加载最新的UI元素
            for(CaseWebDTO caseWebDTO:caseWebs){
                JSONArray elementList = JSONArray.parseArray(caseWebDTO.getElement());
                for(int i=0;i<elementList.size();i++){
                    JSONObject element = elementList.getJSONObject(i);
                    String elementId = element.getString("id");
                    if(elementId == null || elementId.equals("")){
                        continue;
                    }
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
        }else {
            List<CaseAppDTO> caseApps = caseAppMapper.getCaseAppList(caseId, caseType.toLowerCase(Locale.ROOT));
            // 加载最新的UI元素
            for(CaseAppDTO caseAppDTO:caseApps){
                JSONArray controlList = JSONArray.parseArray(caseAppDTO.getElement());
                for(int i=0;i<controlList.size();i++){
                    JSONObject control = controlList.getJSONObject(i);
                    String controlId = control.getString("id");
                    if(controlId == null || controlId.equals("")){
                        continue;
                    }
                    ControlDTO controlDTO = controlMapper.getControlById(controlId);
                    control.put("system", controlDTO.getSystem());
                    control.put("by", controlDTO.getBy());
                    control.put("name", controlDTO.getName());
                    control.put("expression", controlDTO.getExpression());
                    control.put("moduleId", controlDTO.getModuleId());
                    control.put("moduleName", controlDTO.getModuleName());
                }
                caseAppDTO.setElement(JSONArray.toJSONString(controlList));
            }
            caseDTO.setCaseApps(caseApps);
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
