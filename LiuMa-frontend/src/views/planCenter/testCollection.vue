/**
 * 计划管理  测试集合
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入集合名称"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addCollection">新增集合</el-button>
        </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="collectionData" v-loading="loading" @expand-change="expandSelect">
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
                        <el-table-column fixed="right" align="center"  label="操作" width="150px">
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
        <el-table-column prop="index" label="序号" width="50px" align="center"/>
        <el-table-column prop="name" label="集合名称" min-width="160px"/>
        <el-table-column prop="versionName" label="版本"/>
        <el-table-column prop="description" label="集合描述" min-width="180px"/>
        <el-table-column prop="username" label="创建人"/>
        <el-table-column prop="updateTime" label="更新时间" width="150px"/>
        <el-table-column fixed="right" align="center" label="操作" width="150px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="runCollection(scope.row)">执行</el-button>
                <el-button type="text" size="mini" @click="editCollection(scope.row)">编辑</el-button>
                <el-button type="text" size="mini" @click="deleteCollection(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="collectionCallFather"/>
    <!-- 集合执行选择引擎和环境 -->
    <run-form :runForm="runForm" :runVisible="runVisible" :showEnvironment="showEnvironment" :showDevice="showDevice" 
        :deviceSystem="deviceSystem" @closeRun="closeRun" @run="run($event)"/>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import {timestampToTime} from '@/utils/util'
import RunForm from '@/views/common/business/runForm'

export default {
    components: {
        Pagination, RunForm
    },
    data() {
        return{
            loading:false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            collectionData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            runVisible: false,
            showEnvironment: false,
            showDevice: false,
            deviceSystem: null,
            runForm: {
                engineId: "",
                environmentId: null,
                deviceId: null
            },
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["计划中心", "测试集合"]);
        this.getCollectionData(this.searchForm)
    },
    methods: {
        // 获取列表数据方法
        getCollectionData(searchParam) {
            this.loading = true;
            let url = '/autotest/collection/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].updateTime= timestampToTime(data.list[i].updateTime);
                    data.list[i].reportData = [];
                    data.list[i].pageparam = {
                        currentPage: 1,
                        pageSize: 10,
                        total: 0
                    };
                    data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
                }
                this.collectionData = data.list;
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
                collectionId: row.id
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
        // 分页插件事件
        collectionCallFather(parm) {
            this.searchForm.page = parm.currentPage;
            this.searchForm.limit = parm.pageSize;
            this.getCollectionData(this.searchForm);
        },
        reportCallFather(param, row){
            row.pageparam.currentPage = param.currentPage;
            row.pageparam.pageSize = param.pageSize;
            this.getReportData(row);
        },
        // 搜索按钮
        search() {
            this.getCollectionData(this.searchForm);
        },
        // 重置按钮
        reset() {
            this.searchForm.condition = "";
            this.getCollectionData(this.searchForm);
        },
        // 新增集合
        addCollection(){
            this.$router.push({path: '/planManage/testCollection/add'})
        },
        runCollection(row){
            let url = "/autotest/collection/types/" + row.id;
            this.$get(url, response =>{
                this.showEnvironment = response.data.needEnvironment;
                this.showDevice = response.data.hasAndroid || response.data.hasApple;
                if(response.data.hasAndroid){
                    this.deviceSystem = "android";
                }
                if(response.data.hasApple){
                    this.deviceSystem = "apple";
                }

                this.runForm.engineId = 'system';
                this.runForm.environmentId = null;
                this.runForm.deviceId = null;
                this.runForm.sourceType = "collection";
                this.runForm.sourceId = row.id;
                this.runForm.sourceName = row.name;
                this.runForm.taskType = "run";
                this.runForm.projectId = this.$store.state.projectId;
                this.runVisible = true;
            });
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
        // 编辑集合
        editCollection(row){
            this.$router.push({path: '/planManage/testCollection/edit/' + row.id})
        },
        // 查看报告
        viewReport(row){
            this.$router.push({path: '/report/testReport/detail/' + row.id});
        },
        // 删除报告
        deleteReport(cRow, rRow){
            this.$confirm('确定要删除报告吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/report/delete';
                this.$post(url, {id: rRow.id}, response => {
                    this.$message.success("删除成功");
                    this.getReportData(cRow);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        // 删除集合
        deleteCollection(row){
            this.$confirm('确定要删除集合吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/collection/delete';
                this.$post(url, {id: row.id}, response => {
                    this.$message.success("删除成功");
                    this.getCollectionData(this.searchForm);
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

</style>
