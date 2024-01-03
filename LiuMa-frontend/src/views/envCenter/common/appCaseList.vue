/**
* 选择用例弹窗
*/ 
<template>
    <div>
        <el-form :inline="true" :model="searchForm">
            <el-form-item label="" prop="condition">
                <el-input size="small" style="width:180px" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入用例NO、名称"/>
            </el-form-item>
            <el-form-item label="" prop="moduleName">
                <select-tree placeholder="模块分类" style="width: 300px" :selectedValue="searchForm.moduleId" 
                        :selectedLabel="searchForm.moduleName" :treeData="modules" @selectModule="selectModule($event)"/>
            </el-form-item>
            <el-form-item>
                <el-button size="small" type="primary" @click="search">搜索</el-button>
                <el-button size="small" @click="reset">重置</el-button>
            </el-form-item>
            <el-form-item style="float: right">
                <el-button size="small" type="primary" icon="el-icon-plus" @click="addCase">新增用例</el-button>
            </el-form-item>
        </el-form>
        <!--列表-->
        <el-table size="small" :data="caseListData" v-loading="loading" tooltip-effect="dark" ref="multipleTable">
            <el-table-column prop="num" label="NO" width="80"/>
            <el-table-column prop="name" label="用例名称" min-width="200"/>
            <el-table-column prop="moduleName" label="所属模块" width="180"/>
            <el-table-column align="operation" label="操作" width="120px">
                <template slot-scope="scope">
                    <el-button type="text" size="mini" @click="runCase(scope.row)">执行</el-button>
                    <el-button type="text" size="mini" @click="editCase(scope.row)">编辑</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageParam" @callFather="callFather"/>
        <!-- 用例编辑 -->
        <el-drawer title="用例详情" :visible.sync="editCaseAppVisible" :modal="false" direction="rtl" :with-header="false" destroy-on-close :size="drawerWidth+'px'">
            <app-case-edit style="padding: 20px" :system="system" :caseId="caseId" @closeDrawer="editCaseAppVisible=false" @debugCase="debugCase($event)"/>
        </el-drawer>
        <!-- 用例调试选择引擎和环境 -->
        <run-form :runForm="runForm" :runVisible="runVisible" :showDevice="false" @closeRun="closeRun" @run="run($event)"/>
        <!-- 用例执行结果展示 -->
        <run-result :taskId="taskId" caseType="app" :resultVisable="resultVisable" @closeResult="closeResult" @endRun="endRun"/>
    </div>
</template>
<script>
import SelectTree from '../../common/business/selectTree'
import Pagination from '../../common/components/pagination'
import AppCaseEdit from '../../caseCenter/appCaseEdit'
import RunForm from '../../common/business/runForm'
import RunResult from '../../caseCenter/common/case/runResult'

export default {
    name: 'AppCaseList',
    components:{
        SelectTree, Pagination, AppCaseEdit, RunForm, RunResult
    },
    props:{
        system: String,
        deviceId: String,
        drawerWidth: {
            type: Number,
            default: 900
        }
    },
    data() {
        return{
            loading: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: "",
                moduleId: ""
            },
            pageParam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            modules: [],
            caseListData: [],
            caseId: null,
            editCaseAppVisible: false,
            runVisible: false,
            runForm: {
                engineId: "",
                environmentId: []
            },
            resultVisable: false,
            taskId: ""
        }
    },
    created(){
        this.getModule();
        this.getdata(this.searchForm);
    },
    methods: {
        getModule(){
            let url = '/autotest/module/list/case/' + this.$store.state.projectId;
            this.$get(url, response =>{
                response.data.unshift({id: "0", name:"默认模块", label: "默认模块"});
                this.modules = response.data;
            });
        },
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true;
            let url = '/autotest/case/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                moduleId: searchParam.moduleId,
                caseType: "APP",
                system: this.system,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                this.caseListData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageParam.currentPage = this.searchForm.page;
                this.pageParam.pageSize = this.searchForm.limit;
                this.pageParam.total = data.total;
            });   
        },
        // 分页插件事件
        callFather(param) {
            this.searchForm.page = param.currentPage;
            this.searchForm.limit = param.pageSize;
            this.getdata(this.searchForm);
        },
        // 搜索按钮
        search() {
            this.getdata(this.searchForm);
        },
        // 重置按钮
        reset() {
            this.searchForm.condition = "";
            this.searchForm.moduleId = "";
            this.searchForm.moduleName = "";
            this.getdata(this.searchForm);
        },
        selectModule(data){
            this.searchForm.moduleId = data.id;
            this.searchForm.moduleName = data.label;
        },
        addCase(){
            this.caseId = null;
            this.editCaseAppVisible = true;
        },
        editCase(row){
            this.caseId = row.id;
            this.editCaseAppVisible = true;
        },
        runCase(row){
            // 用例执行
            this.runForm.engineId = 'system';
            this.runForm.environmentId = null;
            this.runForm.deviceId = this.deviceId;
            this.runForm.sourceType = "case";
            this.runForm.sourceId = row.id;
            this.runForm.sourceName = row.name;
            this.runForm.taskType = "debug";
            this.runForm.projectId = this.$store.state.projectId;
            this.runVisible = true;
        },
        debugCase(form){
            // 用例调试
            this.runForm.engineId = 'system';
            this.runForm.environmentId = null;
            this.runForm.deviceId = this.deviceId;
            this.runForm.sourceType = "temp";
            this.runForm.sourceId = form.id;
            this.runForm.sourceName = form.name;
            this.runForm.taskType = "debug";
            this.runForm.projectId = this.$store.state.projectId;
            this.runForm.debugData = form;
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
        },
        endRun(){
            if(this.system==='apple'){
                this.$emit("initSession");
            }
        }
    }
}
</script>
<style scoped>

</style>