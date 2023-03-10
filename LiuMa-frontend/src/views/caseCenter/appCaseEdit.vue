/**
 * 用例中心  APP用例
 */
<template>
  <div>
    <page-header title="用例编辑" :showDebug="true" :cancel="cancelAdd" :debug="debugCase" :save="saveAdd"/>
    <el-form ref="caseForm" :rules="rules" :model="caseForm" label-width="90px">
      <base-info :caseForm="caseForm" :applications="applications"/>
      <p class="tip">操作步骤</p>
      <el-form-item style="margin-left:-80px;" prop="caseApps"/>
      <el-table :data="caseForm.caseApps" row-key="id" class="sort-table" size="small">
        <el-table-column label="" width="60px">
            <template>
                <i class="iconfont icon-paixu" @mousedown="rowDrop" style="font-size: 24px"/>
            </template>
        </el-table-column>
        <el-table-column label="序号" prop="index" width="100px">
        </el-table-column>
        <el-table-column label="操作名称" prop="operationName" width="180px">
        </el-table-column>
        <el-table-column label="操作对象" prop="elementText">
          <template slot-scope="scope">
              <span v-html="scope.row.elementText"/>
          </template>
        </el-table-column>
        <el-table-column label="操作数据" prop="dataText">
          <template slot-scope="scope">
              <span v-html="scope.row.dataText"/>
          </template>
        </el-table-column>
        <el-table-column label="步骤描述" min-width="200px">
            <template slot-scope="scope">
                <div v-if="scope.row.edit==true" >
                  <el-input size="mini" style="width: 85%" v-model="scope.row.description" placeholder="请输入步骤描述" @change="scope.row.edit=false"/>
                  <i class="el-icon-success" @click="scope.row.edit=false"/>
                </div>
                <span v-else>{{scope.row.description}} <i class="el-icon-edit"  @click="scope.row.edit=true"/></span>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="150px">
            <template slot-scope="scope">
                <el-button size="mini" type="text" @click="editCaseApp(scope.$index, scope.row)">编辑</el-button>
                <el-button size="mini" type="text" @click="copyCaseApp(scope.row)">复用</el-button>
                <el-button size="mini" type="text" @click="deleteCaseApp(scope.$index)">删除</el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-form>
    <el-button size="small" icon="el-icon-plus" type="text" @click="addCaseApp(-1)">新增操作</el-button>
    <!-- 添加操作界面 -->
    <el-dialog title="选择操作" :visible.sync="editOperationVisible" width="750px" destroy-on-close :modal-append-to-body="false">
        <el-form ref="operationForm" :rules="rules" :model="operationForm" label-width="100px" label-position="top">
          <el-form-item label="操作名称" prop="operationId">
            <el-cascader size="small" style="width: 100%" filterable :options="operations" v-model="operationForm.operationIds" :show-all-levels="false"
              :props="{ expandTrigger: 'hover', value: 'id', label: 'name', children:'operationList' }" placeholder="请选择操作" @change="changeOperation"/>
          </el-form-item>
          <el-form-item v-if="operationForm.element.length !== 0" label="操作对象" prop="element">
            <el-row :gutter="10" v-for="(ele, index) in operationForm.element" :key="index">
              <el-col :span="4">
                <span>{{ele.paramName}}</span>
              </el-col>
              <el-col :span="20">
                <el-row :gutter="10">
                  <el-col :span="10">
                    <select-tree placeholder="请选择页面模块" :selectedValue="ele.moduleId"
                            :selectedLabel="ele.moduleName" :treeData="viewModules" @selectModule="selectModule($event, ele)"/>
                  </el-col>
                  <el-col :span="12">
                    <el-input size="small" v-if="ele.custom" v-model="ele.name" placeholder="请输入元素名称"/>
                    <el-select size="small" style="width:100%" v-else v-model="ele.id" filterable
                      :placeholder="ele.description" value-key="item" @change="changeElement($event, ele)">
                        <el-option v-for="item in ele.selectElements" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                  </el-col>
                  <el-col :span="2">
                    <el-switch size="small" v-model="ele.custom" @change="changeCustom($event, ele)"/>
                  </el-col>
                </el-row>
                <el-row v-if="ele.custom" :gutter="10">
                  <el-col :span="10">
                    <el-select size="small" style="width:100%" v-model="ele.by" placeholder="请选择定位方式" @change="changeBy(ele)">
                        <el-option v-for="item in byList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                  </el-col>
                  <el-col :span="12">
                    <el-button v-if="ele.by==='PROP'" size="small" type="text" @click="editExpression(ele)">编辑属性</el-button>
                    <el-input v-else size="small" v-model="ele.expression" placeholder="请输入元素定位表达式"/>
                  </el-col>
                </el-row>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item v-if="operationForm.data.length !== 0" label="操作数据" prop="data">
            <el-row :gutter="10" v-for="(data, index) in operationForm.data" :key="index">
              <el-col :span="4">
                <span>{{data.paramName}}</span>
              </el-col>
              <el-col :span="20">
                <el-select v-if="data.paramName === 'appId'" size="small" style="width:100%" filterable v-model="data.value" :placeholder="data.description">
                    <el-option v-for="item in applications" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-select v-else-if="data.paramName === 'direction'" size="small" style="width:100%" filterable v-model="data.value" :placeholder="data.description">
                    <el-option v-for="item in directions" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-select v-else-if="data.paramName === 'keycode'" size="small" style="width:100%" filterable v-model="data.value" :placeholder="data.description">
                    <el-option v-for="item in keycodes" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-select v-else-if="data.paramName === 'attribute'" size="small" style="width:100%" filterable v-model="data.value" :placeholder="data.description">
                    <el-option v-for="item in attributes" :key="item" :label="item" :value="item"/>
                </el-select>
                <el-select v-else-if="data.paramName === 'assertion'" size="small" style="width:100%" filterable v-model="data.value" :placeholder="data.description">
                    <el-option v-for="item in assertions" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-select v-else-if="data.paramName === 'continue'" size="small" style="width:100%" v-model="data.value" :placeholder="data.description">
                    <el-option v-for="item in continues" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
                <el-input v-else size="small" v-model="data.value" :placeholder="data.description"/>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="editOperationVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="saveOperationEdit('operationForm', operationForm)">保存</el-button>
        </div>
    </el-dialog>
    <!-- 添加属性定位 -->
    <el-dialog title="编辑属性" :visible.sync="editExpressionVisible" width="550px" destroy-on-close :modal-append-to-body="false">
        <el-row v-for="(item, index) in expressionForm" :key="index" style="margin-bottom:10px">
            <el-col :span="8">
                <el-select size="small" style="width:95%" v-model="item.propName" placeholder="属性名">
                    <el-option v-for="prop in propList" :key="prop" :label="prop" :value="prop"></el-option>
                </el-select>
            </el-col>
            <el-col :span="13">
                <el-input size="small" style="width:100%" v-model="item.propValue" placeholder="请输入属性值"/>
            </el-col>
            <el-col :span="3">
                <div style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                    <i class="el-icon-circle-plus lm-success" @click="addProp(index)"></i>
                    <i v-if="expressionForm.length > 1" class="el-icon-remove lm-error" @click="deleteProp(index)"></i>
                </div>
            </el-col>
        </el-row>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="editExpressionVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitExpression">确定</el-button>
        </div>
    </el-dialog>
    <!-- 用例调试选择引擎和环境 -->
    <run-form :runForm="runForm" :runVisible="runVisible" :showDevice="true" :deviceSystem="caseForm.system" @closeRun="closeRun" @run="run($event)"/>
    <!-- 用例执行结果展示 -->
    <run-result :taskId="taskId" :caseType="caseForm.type" :resultVisable="resultVisable" @closeResult="closeResult"/>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import {getUUID} from '@/utils/util'
import PageHeader from '../common/components/pageheader'
import BaseInfo from './common/case/baseInfo'
import RunForm from '@/views/common/business/runForm'
import SelectTree from '@/views/common/business/selectTree'
import RunResult from './common/case/runResult'
import {locateBys, locateProps, systemKeys, elementProps} from '@/utils/constant'

export default {
    components:{PageHeader, BaseInfo, RunForm, SelectTree, RunResult},
    name: 'AppCaseEdit',
    props:{
        caseId: {
          type: String,
          default: null
        },
        system: {
          type: String,
          default: null
        }
    },
    data() {
        return{
            caseForm: {
                id: "",
                name: "",
                level: "P0",
                type: "APP",
                system: null,
                environmentIds: [],
                thirdParty: "",
                moduleId: "",
                moduleName: "",
                commonParam: {
                  functions: [],
                  params: [],
                  appId: null,
                  activity: null,
                },
                caseApps: []
            },
            viewModules: [],
            byList: [],
            propList: [],
            operations: [],
            applications: [],
            keycodes: [],
            attributes: [],
            directions: [
              {id: "up", name: "向上"},
              {id: "down", name: "向下"},
              {id: "left", name: "向左"},
              {id: "right", name: "向右"},
            ],
            assertions: [],
            continues: [{id: true, name: "是"}, {id: false, name:"否"}],
            operationForm: {
              id: "",
              index: -1,
              operationId: "",
              operationName: "",
              element: [],
              data: []
            },
            customElement: {},
            expressionForm: [],
            editOperationVisible: false,
            editExpressionVisible: false,
            runVisible: false,
            runForm: {
                engineId: "",
                environmentId: [],
                deviceId: ""
            },
            resultVisable: false,
            taskId: "",
            rules: {
                name: [{ required: true, message: '用例名称不能为空', trigger: 'blur' }],
                type: [{ required: true, message: '用例类型不能为空', trigger: 'blur' }],
                moduleId: [{ required: true, message: '用例模块不能为空', trigger: 'blur' }],
                operationId: [{ required: true, message: '操作名称不能为空', trigger: 'blur' }],
                element: [{ required: true, message: '操作对象不能为空', trigger: 'blur' }],
                data: [{ required: true, message: '操作数据不能为空', trigger: 'blur' }],
                caseApps: [{ required: true, message: '请至少添加一条操作步骤', trigger: 'blur' }],
                'commonParam.appId': [{ required: true, message: '被测应用不能为空', trigger: 'blur' }],
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["用例中心", "APP用例"]);
        if(this.system!==null){
          this.caseForm.system = this.system;
        }else{
          this.caseForm.system = this.$route.params.system;
        }
        if(this.caseForm.system === "android"){
            this.propList = locateProps.android;
            this.byList = locateBys.android;
            this.keycodes = systemKeys.android;
            this.attributes = elementProps.android;
        }else{
            this.propList = locateProps.apple;
            this.byList = locateBys.apple;
            this.keycodes = systemKeys.apple;
            this.attributes = elementProps.apple;
        }
        this.getApplication();
        this.getOperations();
        this.getAssertion();
        this.getViews();
        if(this.system!==null){
          this.getDetail({caseId: this.caseId});
        }else{
          this.getDetail(this.$route.params);
        }
    },
    methods: {
        // 行拖拽
        rowDrop () {
            // 此时找到的元素是要拖拽元素的父容器
            const tbody = document.querySelector('.sort-table tbody');
            const _this = this;
            Sortable.create(tbody, {
                //  指定父元素下可被拖拽的子元素
                draggable: ".el-table__row",
                onEnd ({ newIndex, oldIndex }) {
                    const currRow = _this.caseForm.caseApps.splice(oldIndex, 1)[0];
                    _this.caseForm.caseApps.splice(newIndex, 0, currRow);
                    _this.sortCaseApp();
                }
            });
        },
        // 重新排序
        sortCaseApp(){
            for(let i=0; i<this.caseForm.caseApps.length; i++){
                this.caseForm.caseApps[i].index = i+1;
            }
        },
        getOperationObj(val, opt) {
          for(let i=0; i<opt.length; i++){
            if(opt[i].id === val[0]){
              let operationList = opt[i].operationList;
              for(let j=0; j< operationList.length; j++){
                if(operationList[j].id === val[1]){
                  return operationList[j];
                }
              }
              return null;
            }
          }
          return null;
        },
        elementToText(elements){
          let text = "";
          for(let i=0;i<elements.length;i++){
            if(i===0){
              text = elements[i].paramName+ " : " +elements[i].moduleName+ " / " +elements[i].name;
            }else{
              text = text + "<br>" + elements[i].paramName+ " : " +elements[i].moduleName+ " / " +elements[i].name;
            }
          }
          return text;
        },
        dataToText(datas){
          let text = "";
          for(let i=0;i<datas.length;i++){
            let newText = '';
            if(datas[i].paramName === "assertion"){
              for(let j=0;j<this.assertions.length;j++){
                if(this.assertions[j].id === datas[i].value){
                  newText = datas[i].paramName+ " : " + this.assertions[j].name;
                  break;
                }
              }
            }else if(datas[i].paramName === "appId"){
              for(let j=0;j<this.applications.length;j++){
                if(this.applications[j].id === datas[i].value){
                  newText = datas[i].paramName+ " : " + this.applications[j].name;
                  break;
                }
              }
            }else if(datas[i].paramName === "continue"){
              if(datas[i].value === true){
                newText = datas[i].paramName+ " : "  + "是";
              }else{
                newText = datas[i].paramName+ " : "  + "否";
              }
            }
            else{
              newText = datas[i].paramName+ " : " +datas[i].value;
            }
            if(i===0){
              text = newText;
            }else{
              text = text + "<br>" + newText;
            }
          }
          return text;
        },
        addCaseApp(index){
          this.operationForm =  {
              id: getUUID(),
              index: index,
              operationIds: [],
              operationId: "",
              operationName: "",
              element: [],
              data: [],
              edit: false,
              description: ""
          };
          this.editOperationVisible = true;
        },
        editCaseApp(index, row){
          this.operationForm = {
            id: row.id,
            index: index,
            operationIds: row.operationIds,
            operationId: row.operationId,
            operationName: row.operationName,
            element: row.element,
            data: row.data,
            edit: row.edit,
            description: row.description
          };
          for(let i=0;i<row.element.length;i++){
            if(row.element[i].selectElements != undefined & row.element[i].selectElements > 0){
              continue;
            }else{
              this.getControls(row.element[i].moduleId, row.element[i]);
            }
          }
          this.editOperationVisible = true;
        },
        copyCaseApp(row){
          this.operationForm = {
            id: getUUID(),
            index: -1,
            operationIds: row.operationIds,
            operationId: row.operationId,
            operationName: row.operationName,
            element: JSON.parse(JSON.stringify(row.element)),
            data: JSON.parse(JSON.stringify(row.data)),
            edit: false,
            description: row.description
          };
          for(let i=0;i<row.element.length;i++){
            if(row.element[i].selectElements != undefined & row.element[i].selectElements > 0){
              continue;
            }else{
              this.getControls(row.element[i].moduleId, row.element[i]);
            }
          }
          this.editOperationVisible = true;
        },
        deleteCaseApp(index){
            this.caseForm.caseApps.splice(index, 1);
            for(let i=0; i<this.caseForm.caseApps.length; i++){
                this.caseForm.caseApps[i].index = i+1;
            }
        },
        changeOperation(val){
          let operation = this.getOperationObj(val, this.operations);
          this.operationForm.operationId = operation.id;
          this.operationForm.operationName = operation.name;
          this.operationForm.element = JSON.parse(JSON.stringify(operation.elementList));
          let data = JSON.parse(JSON.stringify(operation.dataList));
          for(let i=0;i<data.length;i++){
            if(data[i].paramName === 'continue'){
              data[i].value = false;
            }else{
              data[i].value = "";
            }
          }
          this.operationForm.data = data;

        },
        changeElement(val, element){
          let item = null;
          for(let i=0;i<element.selectElements.length;i++){
            if(element.selectElements[i].id === val){
              item = element.selectElements[i];
              break;
            }
          }
          element.id = item.id;
          element.name = item.name;
          element.system = item.system;
          element.by = item.by;
          element.expression = item.expression;
        },
        changeCustom(custom, element){
          element.custom = custom;
          element.id = "";
          element.system = this.caseForm.system;
          element.name = "";
          element.by = "XPATH";
          element.expression = "";
        },
        changeBy(ele){
          // 更改定位方式时 将表达式处理成对应格式
          if(ele.by === "PROP"){
            let expressionForm = null;
            try{
              expressionForm = JSON.parse(ele.expression);
            }catch(e){
              expressionForm = [];
            }
            if(expressionForm.length === 0){
                expressionForm.push({propName:"",propValue:""});
            }
            ele.expression = JSON.stringify(expressionForm);
          }else{
            ele.expression = "";
          }
        },
        editExpression(ele){
          this.expressionForm.length = 0;
          this.expressionForm.push(...JSON.parse(ele.expression));
          this.customElement = ele;
          this.editExpressionVisible = true;
        },
        submitExpression(){
          let re = true;
          for(let i=0;i<this.expressionForm.length;i++){
            let expression = this.expressionForm[i];
            if(expression.propName === "" || expression.propValue === ""){
              re = false;
              break;
            }
          }
          if(re){
            this.customElement.expression = JSON.stringify(this.expressionForm);
            this.editExpressionVisible = false;
          }else{
            this.$message.warning("属性值或属性名不能为空");
          }
          
        },
        // 新增属性定位
        addProp(index){
            this.expressionForm.splice(index+1, 0, {propName:"",propValue:""});
        },
        // 删除属性定位
        deleteProp(index){
            this.expressionForm.splice(index, 1);
        },
        selectModule(data, element){
            element.moduleId = data.id;
            element.moduleName = data.label;
            if(!element.custom){
              element.id = "";
              element.name = "";
              element.system = "";
              element.by = "";
              element.expression = "";
            }
            this.getControls(data.id, element);
        },
        saveOperationEdit(confirm, form){
          this.$refs[confirm].validate(valid => {
              if (valid) {
                form.elementText = this.elementToText(form.element);
                form.dataText = this.dataToText(form.data);
                if(form.index === -1){
                  form.index = this.caseForm.caseApps.length + 1;
                  this.caseForm.caseApps.push(form);
                }else{
                  form.index = form.index + 1;
                  // this.caseForm.caseApps[form.index-1] = form;
                  this.$set(this.caseForm.caseApps, form.index-1, form);
                }
                this.editOperationVisible = false;
              }else{
                  return false;
              }
          });
        },
        getControls(moduleId, element){
            let url = '/autotest/control/list/module';
            let param = {
                moduleId: moduleId,
                projectId: this.$store.state.projectId,
                system: this.caseForm.system
            };
            this.$post(url, param, response => {
                element.selectElements = response.data;
            });
        },
        getAssertion(){
            let url = '/autotest/system/assertion/list';
            this.$get(url, response =>{
                this.assertions = response.data;
            });
        },
        getDetail(param){
            if (param.caseId){  // 编辑
                let url = "/autotest/case/detail/" + this.caseForm.type.toLowerCase() + "/" + param.caseId;
                this.$get(url, response => {
                    let data = response.data;
                    if(data.environmentIds){
                        data.environmentIds = JSON.parse(data.environmentIds);
                    }
                    if(data.commonParam){
                        data.commonParam = JSON.parse(data.commonParam);
                    }
                    for(let i=0;i<data.caseApps.length;i++){
                        let caseApp = data.caseApps[i];
                        // 处理app
                        caseApp.operationIds = [caseApp.operationType, caseApp.operationId];
                        caseApp.element = JSON.parse(caseApp.element);
                        caseApp.data = JSON.parse(caseApp.data);
                        caseApp.elementText = this.elementToText(caseApp.element);
                        caseApp.dataText = this.dataToText(caseApp.data);
                        caseApp.edit = false;
                    }
                    if(param.type === "copy"){ //复用
                        data.id = "";
                    }
                    this.caseForm = data;
                });
            }
        },
        getViews(){
            let url = '/autotest/module/list/view/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.viewModules = response.data;
            });
        },
        getOperations(){
            let url = '/autotest/operation/group/app/list/' + this.$store.state.projectId + '?system=' +this.caseForm.system;
            this.$get(url, response =>{
                this.operations = response.data;
            });
        },
        getApplication(){
            let url = '/autotest/application/list/' + this.caseForm.system + "/" + this.$store.state.projectId;
            this.$get(url, response =>{
                this.applications = response.data;
            });
        },
        cancelAdd(){
            if(this.system!==null){
              this.$emit("closeDrawer");
            }else{
              this.$router.push({path: '/caseCenter/caseManage'});
            }
        },
        saveAdd(){
            this.$refs["caseForm"].validate(valid => {
                if (valid) {
                    this.caseForm.projectId = this.$store.state.projectId;
                    for(let i=0; i<this.caseForm.caseApps.length; i++){
                        this.caseForm.caseApps[i].index = i+1;
                        for(let j=0; j<this.caseForm.caseApps[i].element.length; j++){
                          this.caseForm.caseApps[i].element[j].selectElements = [];
                        }
                    }
                    let url = '/autotest/case/save';
                    this.$post(url, this.caseForm, response =>{
                        this.$message.success("保存成功");
                        if(this.system !==null){
                          this.$emit("closeDrawer");
                        }else{
                          this.$router.push({path: '/caseCenter/caseManage'});
                        }
                    });
                }else{
                    return false;
                }
            });
        },
        debugCase(){
            if(!this.caseForm.commonParam.appId){
                this.$message.warning("被测应用为空 无法调试");
                return;
            }
            if(this.system !== null){
              this.$emit("debugCase", this.caseForm);
              return;
            }
            // 用例调试
            this.runForm.engineId = 'system';
            this.runForm.environmentId = null;
            this.runForm.deviceId = null;
            this.runForm.sourceType = "temp";
            this.runForm.sourceId = this.caseForm.id;
            this.runForm.sourceName = this.caseForm.name;
            this.runForm.taskType = "debug";
            this.runForm.projectId = this.$store.state.projectId;
            this.runForm.debugData = this.caseForm;
            this.runVisible = true;
        },
        closeRun(){
            this.runVisible = false;
        },
        run(runForm){
            let url = '/autotest/run';
            this.$post(url, runForm, response =>{
                this.taskId = response.data.id;
                this.resultVisable = true;
            });
            this.runVisible = false;
        },
        closeResult(){
            this.resultVisable = false;
        }
    }
}
</script>

<style scoped>

</style>
