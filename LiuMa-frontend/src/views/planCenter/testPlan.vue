/**
 * 计划管理  测试计划
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入计划名称"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addplan">新增计划</el-button>
        </el-form-item>
    </el-form>
    <!--计划列表-->
    <el-table size="small" :data="planData" v-loading="loading" @expand-change="expandSelect">
        <el-table-column type="expand" width="40px">
            <template slot-scope="props">
                <div style="padding-left: 40px">
                    <!-- 报告列表 -->
                    <el-table size="mini" :data="props.row.reportData">
                        <el-table-column label="序号" prop="index" width="50px" align="center"/>
                        <el-table-column label="报告名称" prop="name" min-width="200px"/>
                        <el-table-column label="报告状态" prop="format" width="100px"/>
                        <el-table-column label="执行进度" prop="runProgress" width="120px">
                            <template slot-scope="scope">
                                <el-progress :percentage="props.row.reportData[scope.$index].progress" :color="props.row.reportData[scope.$index].color"/>
                            </template>
                        </el-table-column>
                        <el-table-column label="用例总条数" prop="total"/>
                        <el-table-column label="成功条数" prop="passCount"/>
                        <el-table-column label="成功率" prop="passRate"/>
                        <el-table-column label="执行时间" prop="createTime" width="150px"/>
                        <el-table-column fixed="right" align="center" label="操作" width="150px">
                            <template slot-scope="scope">
                                <el-button type="text" size="mini" @click="viewReport(scope.row)">查看</el-button>
                                <el-button type="text" size="mini" @click="deleteReport(props.row, scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <!-- 分页组件 -->
                    <Pagination style="float: right;" size="mini" v-bind:child-msg="props.row.pageparam" @callFather="reportCallFather($event, props.row)"/>
                </div>
            </template>
        </el-table-column>
        <el-table-column label="序号" prop="index" width="50px" align="center"/>
        <el-table-column label="计划名称" prop="name" min-width="160px" :show-overflow-tooltip="true">
            <template slot-scope="scope">
                <el-button type="text" class="plan-name" @click="editPlan(scope.row)">
                    <a>{{scope.row.name}}</a>
                </el-button>
            </template>
        </el-table-column>
        <el-table-column label="版本" prop="versionName"/>
        <el-table-column label="计划描述" prop="description" min-width="180px"/>
        <el-table-column label="创建人" prop="username"/>
        <el-table-column label="更新时间" prop="updateTime" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="150px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="runPlan(scope.row)">执行</el-button>
                <el-button type="text" size="mini" @click="deletePlan(scope.row)">删除</el-button>
                <el-button type="text" size="mini" @click="editNotice(scope.row)">通知配置</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="planCallFather"/>
    <!-- 配置计划通知弹框 -->
    <edit-notice :noticeForm="noticeForm" :noticeVisible="noticeVisible" @closeNotice="closeNotice" @submitNotice="submitNotice($event)"/>
    <!-- 计划执行选择引擎和环境 -->
    <run-form :runForm="runForm" :runVisible="runVisible" :showEnvironment="showEnvironment" @closeRun="closeRun" @run="run($event)"/>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import {timestampToTime} from '@/utils/util'
import RunForm from '@/views/common/business/runForm'
import EditNotice from './common/editNotice'
export default {
    components: {
        Pagination, RunForm, EditNotice
    },
    data() {
        return{
            loading:false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            planData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            runVisible: false,
            showEnvironment: false,
            runForm: {
                engineId: "",
                environmentId: null,
                deviceId: null
            },
            noticeVisible: false,
            noticeForm: {
                planId: null,
                notificationId: null,
                condition: null
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["计划中心", "测试计划"]);
        this.getPlanData(this.searchForm)
    },
    methods: {
        // 获取列表数据方法
        getPlanData(searchParam) {
            this.loading = true
            let url = '/autotest/plan/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
                    data.list[i].reportData = [];
                    data.list[i].pageparam = {
                        currentPage: 1,
                        pageSize: 10,
                        total: 0
                    };
                    data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
                }
                this.planData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
        getReportData(row){
            // 获取报告
            let url = '/autotest/report/list/' + row.pageparam.currentPage + '/' + row.pageparam.pageSize;
            let param = {
                projectId: this.$store.state.projectId,
                planId: row.id
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].createTime = timestampToTime(data.list[i].createTime);
                    let status = data.list[i].status
                    if(status === 'success'){
                        data.list[i].format = 'SUCCESS';
                        data.list[i].color = '#67C23A';
                    }else if(status === 'fail'){
                        data.list[i].format = 'FAIL';
                        data.list[i].color = '#E6A23C';
                    }else if(status === 'error'){
                        data.list[i].format = 'ERROR';
                        data.list[i].color = '#F56C6C';
                    }else if(status === 'skip'){
                        data.list[i].format = 'SKIP';
                        data.list[i].color = '#535457';
                    }else if(status === 'prepared'){
                        data.list[i].format = '等待执行';
                        data.list[i].color = '#409EFF';
                    }else if(status === 'running'){
                        data.list[i].format = "RUNNING";
                        data.list[i].color = '#409EFF';
                    }else if(status === 'discontinue'){
                        data.list[i].format = "已终止";
                        data.list[i].color = '#535457';
                    }
                    data.list[i].index = (row.pageparam.currentPage-1) * row.pageparam.pageSize + i+1;
                }
                row.reportData = data.list;
                row.pageparam.total = data.total;
            });
        },
        expandSelect(row, expandedRows) {
            if(expandedRows.length != 0){
                this.getReportData(row);
            }
        },
        planCallFather(param) {
            this.searchForm.page = param.currentPage;
            this.searchForm.limit = param.pageSize;
            this.getPlanData(this.searchForm);
        },
        reportCallFather(param, row){
            row.pageparam.currentPage = param.currentPage;
            row.pageparam.pageSize = param.pageSize;
            this.getReportData(row);
        },
        // 搜索按钮
        search() {
            this.getPlanData(this.searchForm);
        },
        // 重置按钮
        reset() {
            this.searchForm.condition = "";
            this.getPlanData(this.searchForm);
        },
        // 新增计划
        addplan(){
            this.$router.push({path: '/planManage/testplan/add'});
        },
        // 编辑计划
        editPlan(row){
            this.$router.push({path: '/planManage/testplan/edit/' + row.id});
        },
        runPlan(row){
            if(row.environmentId != null){
                this.showEnvironment = true;
            }else{
                this.showEnvironment = false;
            }
            this.runForm.engineId = row.engineId;
            this.runForm.environmentId = row.environmentId;
            this.runForm.deviceId = null;
            this.runForm.sourceType = "plan";
            this.runForm.sourceId = row.id;
            this.runForm.sourceName = row.name;
            this.runForm.taskType = "run";
            this.runForm.projectId = this.$store.state.projectId;
            this.runVisible = true;
        },
        closeRun(){
            this.runVisible = false;
        },
        run(runForm){
            let url = '/autotest/run';
            this.$post(url, runForm, response =>{
                this.$message.success("执行成功 执行结果请查看报告");
            });
            this.runVisible = false;
        },
        editNotice(row){
            let url = '/autotest/plan/notice/'+row.id;
            this.$get(url, response => {
                let data = response.data;
                if(data !== null){
                    this.noticeForm.id = data.id;
                    this.noticeForm.planId = data.planId;
                    this.noticeForm.notificationId = data.notificationId;
                    this.noticeForm.condition = data.condition;
                }else{
                    this.noticeForm.planId = row.id;
                    this.noticeForm.condition = 'A';
                }
                this.noticeVisible = true;
            });
        },
        closeNotice(){
            this.noticeVisible = false;
        },
        submitNotice(noticeForm){
            let url = '/autotest/plan/save/notice';
            this.$post(url, noticeForm, response =>{
                this.$message.success("保存成功");
                this.noticeVisible = false;
            });
        },
        // 查看报告
        viewReport(row){
            this.$router.push({path: '/report/testReport/detail/' + row.id});
        },
        // 删除报告
        deleteReport(pRow, rRow){
            this.$confirm('确定要删除报告吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/report/delete';
                this.$post(url, {id: rRow.id}, response => {
                    this.$message.success("删除成功");
                    this.getReportData(pRow);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        // 删除计划
        deletePlan(row){
            this.$confirm('确定要删除计划吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/plan/delete';
                this.$post(url, {id: row.id}, response => {
                    this.$message.success("删除成功");
                    this.getPlanData(this.searchForm);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
    }
}
</script>

<style scoped>
.plan-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 100%;
    text-align: left;
}
</style>