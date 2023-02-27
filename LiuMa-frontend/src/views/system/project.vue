/**
 * 系统管理  项目管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入项目名称"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addProject">新增项目</el-button>
        </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="projectData" v-loading="loading">
        <el-table-column prop="index" label="序号" width="50px" align="center"/>
        <el-table-column prop="name" label="项目名称" min-width="150px"/>
        <el-table-column prop="description" label="项目描述" min-width="200px"/>
        <el-table-column prop="status" label="项目状态">
            <template slot-scope="scope">
                <span v-if="scope.row.status === 'Trash'"> 已删除</span>
                <span v-else> 使用中</span>
            </template>
        </el-table-column>
        <el-table-column prop="username" label="管理员"/>
        <el-table-column prop="createTime" label="创建时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button v-if="scope.row.status === 'Trash'" type="text" size="mini" @click="recoverProject(scope.row.id)">恢复</el-button>
                <el-button v-else type="text" size="mini" @click="deleteProject(scope.row.id)">停用</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 新增项目界面 -->
    <el-dialog title="新增项目" :visible.sync="projectVisible" width="600px" destroy-on-close>
        <el-form label-width="120px" style="padding-right: 30px;" :model="projectForm" label-position="top" :rules="rules" ref="projectForm">
            <el-form-item label="项目名称" prop="name">
                <el-input size="small" style="width: 100%" v-model="projectForm.name" placeholder="请输入项目名称"/>
            </el-form-item>
            <el-form-item label="项目管理员" prop="projectAdmin">
                <el-select size="small" style="width: 100%" v-model="projectForm.projectAdmin" filterable placeholder="请选择项目管理员">
                    <el-option v-for="item in userList" :key="item.id" :label="item.username" :value="item.id"/>
                </el-select>
            </el-form-item>
            <el-form-item label="项目描述" prop="description">
                <el-input size="small" style="width: 100%" :autosize="{ minRows: 4}" type="textarea" clearable placeholder="请输入项目描述" 
                        v-model="projectForm.description" maxlength="200" show-word-limit/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="projectVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitProjectForm('projectForm', projectForm)">确定</el-button>
        </div>
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
            projectVisible: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            userList:[],
            projectData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            projectForm: {},
            rules: {
                name: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
                projectAdmin: [{ required: true, message: '项目管理员不能为空', trigger: 'blur' }],
                description: [{ required: true, message: '项目描述不能为空', trigger: 'blur' }],
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["系统管理", "项目管理"]);
        this.getdata(this.searchForm);
        this.getUsers();
    },
    methods: {
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true
            let url = '/autotest/project/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].createTime= timestampToTime(data.list[i].createTime);
                    data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
                }
                this.projectData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
        // 获取全部用户
        getUsers() {
          let url = "/autotest/user/all";
          this.$get(url, response => {
            this.userList = response.data;
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
        // 新增项目
        addProject(){
            this.projectForm = {};
            this.projectVisible = true;
        },
        submitProjectForm(confirm, form){
            this.$refs[confirm].validate(valid => {
                if (valid) {
                    let url = '/autotest/project/add';
                    this.$post(url, form, response =>{
                        this.$message.success("保存成功");
                        this.projectVisible = false;
                        this.getdata(this.searchForm);
                    });
                }else{
                    return false;
                }
            });
        },
        deleteProject(projectId){
            this.$confirm('确定要删除项目吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/project/delete';
                this.$post(url, {id: projectId}, response => {
                    this.$message.success("删除成功");
                    this.getdata(this.searchForm);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        recoverProject(projectId){
            this.$confirm('确定要恢复项目吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/project/recover';
                this.$post(url, {id: projectId}, response => {
                    this.$message.success("恢复成功");
                    this.getdata(this.searchForm);
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