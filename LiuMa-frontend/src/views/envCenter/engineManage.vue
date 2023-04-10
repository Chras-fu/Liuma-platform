/**
 * 环境中心  引擎管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入引擎名称"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="registerEngine">注册引擎</el-button>
        </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="engineData" v-loading="loading">
        <el-table-column prop="index" label="序号" width="50px" align="center"/>
        <el-table-column prop="name" label="引擎名称" min-width="200px"/>
        <el-table-column prop="id" label="引擎code" width="250px"/>
        <el-table-column prop="status" label="引擎状态">
            <template slot-scope="scope">
                <span v-if="scope.row.status === 'online'" class="el-icon-circle-check lm-success" style="font-weight:bold"> 在线</span>
                <span v-if="scope.row.status === 'offline'" class="el-icon-circle-close lm-info" style="font-weight:bold"> 离线</span>
                <span v-if="scope.row.status === 'running'" class="el-icon-video-pause lm-error" style="font-weight:bold"> 测试中</span>
            </template>
        </el-table-column>
        <el-table-column prop="engineType" label="引擎类型">
            <template slot-scope="scope">
                <span v-if="scope.row.engineType === 'system'">系统内置</span>
                <span v-if="scope.row.engineType === 'custom'">用户注册</span>
            </template>
        </el-table-column>
        <el-table-column prop="username" label="创建人"/>
        <el-table-column prop="createTime" label="注册时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" v-if="scope.row.createUser===currentUser" 
                    size="mini" @click="viewEngine(scope.row.id)">查看</el-button>
                <el-button type="text" v-if="scope.row.engineType ==='custom'& scope.row.createUser===currentUser" 
                    size="mini" @click="deleteEngine(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 注册引擎界面 -->
    <el-dialog title="注册引擎" :visible.sync="registerEngineVisible" width="600px" destroy-on-close>
        <el-form label-width="120px" style="padding-right: 30px;" :model="registerEngineForm" :rules="rules" ref="registerEngineForm">
            <el-form-item label="引擎名称" prop="name">
                <el-input size="small" style="width: 90%" v-model="registerEngineForm.name" placeholder="请输入引擎名称"/>
            </el-form-item>
            <span style="font-size:8px; margin-left: 42px">* 注册前请先阅读使用手册中相关引擎使用说明</span>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="registerEngineVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitSignEngineForm('registerEngineForm', registerEngineForm)">注册</el-button>
        </div>
    </el-dialog>
    <!-- 注册成功返回信息 -->
    <el-dialog title="注册成功" :visible.sync="registerBackVisible" width="500px" destroy-on-close>
        <el-form label-width="120px" style="padding-right: 30px;">
            <el-form-item label="引擎code:">
                <span style="font-weight:bold">{{engineForm.id}}</span>
            </el-form-item>
            <el-form-item label="引擎秘钥:">
                <span style="font-weight:bold">{{engineForm.secret}}</span>
            </el-form-item>
        </el-form>
        <el-button style="margin-left: 50px" type="text" @click="downloadEngine">下载引擎</el-button>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" type="primary" @click="registerBackVisible=false">确定</el-button>
        </div>
    </el-dialog>
    <!-- 查看引擎界面 -->
    <el-dialog title="引擎任务状态" :visible.sync="viewEngineVisible" width="900px" destroy-on-close>
        <el-form :inline="true" :model="engineForm">
            <el-col :span="11">
                <el-form-item label="引擎code:">
                    <span>{{engineForm.id}}</span>
                </el-form-item>
            </el-col>
            <el-col :span="10">
                <el-form-item label="引擎秘钥:">
                    <span>{{engineForm.secret}}</span>
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item >
                    <el-button size="small" type="primary" @click="stopAllTask(engineForm.id)">一键终止</el-button>
                </el-form-item>
            </el-col>
        </el-form>
        <!--任务列表-->
        <el-table size="small" :data="engineForm.taskList">
            <el-table-column prop="index" label="序号" width="50px"/>
            <el-table-column prop="name" label="任务名称" min-width="180px"/>
            <el-table-column prop="type" label="任务类型" min-width="180px">
                <template slot-scope="scope">
                    <span v-if="scope.row.type==='debug'">用例执行</span>
                    <span v-if="scope.row.type==='run'">集合/计划执行</span>
                    <span v-if="scope.row.type==='schedule'">定时任务</span>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="150px"/>
            <el-table-column prop="username" label="创建人"/>
            <el-table-column fixed="right" align="operation" label="操作" width="100px">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="stopTask(scope.row.id, engineForm.id)">终止</el-button>
              </template>
          </el-table-column>
        </el-table>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import {timestampToTime} from '@/utils/util'
export default {
    components: {
        Pagination
    },
    data() {
        return{
            loading:false,
            viewEngineVisible: false,
            registerBackVisible: false,
            registerEngineVisible: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            engineData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            engineForm: {},
            registerEngineForm: {},
            rules: {
                name: [{ required: true, message: '环境名称不能为空', trigger: 'blur' }],
            },
            currentUser: ""
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["环境中心", "引擎管理"]);
        this.currentUser = this.$store.state.userInfo.id;
        this.getdata(this.searchForm)
    },
    methods: {
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true
            let url = '/autotest/engine/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].createTime= timestampToTime(data.list[i].createTime);
                    data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
                }
                this.engineData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
        // 分页插件事件
        callFather(parm) {
            this.searchForm.page = parm.currentPage;
            this.searchForm.limit = parm.pageSize;
            this.getdata(this.searchForm);
        },
        // 搜索按钮
        search() {
            this.getdata(this.searchForm);
        },
        // 重置按钮
        reset() {
            this.searchForm.condition = "";
            this.getdata(this.searchForm);
        },
        // 注册引擎
        registerEngine(){
            this.registerEngineForm = {};
            this.registerEngineVisible = true;
        },
        submitSignEngineForm(confirm, form){
            this.$refs[confirm].validate(valid => {
                if (valid) {
                    let url = '/autotest/engine/register';
                    form.projectId = this.$store.state.projectId;
                    this.$post(url, form, response =>{
                        this.$message.success("保存成功");
                        this.engineForm = response.data;
                        this.registerEngineVisible = false;
                        this.registerBackVisible = true;
                        this.getdata(this.searchForm);
                    });
                }else{
                    return false;
                }
            });
        },
        // 查看引擎
        viewEngine(engineId){
            let url = '/autotest/engine/detail/' + engineId;
            this.$get(url, response =>{
                let data = response.data;
                for(let i =0; i<data.taskList.length; i++){
                    data.taskList[i].createTime= timestampToTime(data.taskList[i].createTime);
                    data.taskList[i].index = i+1;
                }
                this.engineForm = data;
                this.viewEngineVisible = true;
            });
        },
        // 删除引擎
        deleteEngine(row){
            this.$confirm('确定要删除引擎吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/engine/delete';
                this.$post(url, {id: row.id}, response => {
                    this.$message.success("删除成功");
                    this.getdata(this.searchForm);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        stopAllTask(engineId){
            this.$confirm('确定要终止所有任务吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/engine/stop/all/task';
                this.$post(url, {id: engineId}, response => {
                    this.$message.success("终止成功");
                    this.viewEngine(engineId);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        stopTask(taskId, engineId){
            this.$confirm('确定要终止该任务吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/engine/stop/task';
                this.$post(url, {id: taskId, engineId: engineId}, response => {
                    this.$message.success("终止成功");
                    this.viewEngine(engineId);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        downloadEngine(){
            window.open("https://github.com/Chras-fu/LiuMa-engine.git");
        }

    }
}
</script>

<style scoped>

</style>