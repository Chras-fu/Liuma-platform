/**
 * 系统管理  用户管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addUser">新增用户</el-button>
        </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="userData" v-loading="loading">
        <el-table-column prop="index" label="序号" width="50px" align="center"/>
        <el-table-column prop="username" label="用户名" min-width="100px"/>
        <el-table-column prop="account" label="登录账号"/>
        <el-table-column prop="email" label="邮箱" min-width="200px"/>
        <el-table-column prop="createTime" label="注册时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="editUser(scope.row)">编辑</el-button>
                <el-button type="text" size="mini" @click="deleteUser(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 新增用户界面 -->
    <el-dialog :title="title" :visible.sync="userVisible" width="600px" destroy-on-close>
        <el-form label-width="120px" style="padding-right: 30px;" :model="userForm" label-position="top" :rules="rules" ref="userForm">
            <el-form-item label="项目名称" prop="projectName">
                <el-input size="small" disabled style="width: 100%" v-model="userForm.projectName"/>
            </el-form-item>
            <el-form-item label="选择用户" prop="userIds">
                <el-select size="small" style="width: 100%" v-model="userForm.userIds" :disabled="userForm.isEdit" multiple filterable placeholder="输入登录账号查找" 
                remote reserve-keyword :remote-method="searchUser" :loading="uLoading">
                  <el-option v-for="item in userList" :key="item.id" :label="item.username" :value="item.id"/>
                </el-select>
            </el-form-item>
            <el-form-item label="选择角色" prop="roleIds">
                <el-select size="small" style="width: 100%" v-model="userForm.roleIds" multiple filterable placeholder="请选择项目角色">
                    <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="userVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitUserForm('userForm', userForm)">确定</el-button>
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
            title: "新增项目用户",
            uLoading: false,
            loading:false,
            userVisible: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            userList:[],
            roleList: [],
            userData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            currentProject: {
              projectId: "",
              projectName: ""
            },
            userForm: {
              isEdit: false
            },
            rules: {
                projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
                userIds: [{ required: true, message: '用户不能为空', trigger: 'blur' }],
                roleIds: [{ required: true, message: '角色不能为空', trigger: 'blur' }],
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["系统管理", "用户管理"]);
        this.getdata(this.searchForm);
        this.getRoles();
        this.getProject();
    },
    methods: {
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true
            let url = '/autotest/project/user/list/' + searchParam.page + '/' + searchParam.limit;
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
                this.userData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
        // 获取当前项目角色
        getRoles() {
          let url = "/autotest/project/role/list?projectId=" + this.$store.state.projectId;
          this.$get(url, response => {
            this.roleList = response.data;
          });
        },
        // 获取当前项目信息
        getProject(){
          let url = "/autotest/project/info?projectId=" + this.$store.state.projectId;
           this.$get(url, response => {
            this.currentProject.projectId = response.data.id;
            this.currentProject.projectName = response.data.name;
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
        addUser(){
            this.title = "新增项目用户";
            this.userList = [];
            this.userForm = {
              isEdit: false,
              projectId: this.currentProject.projectId,
              projectName: this.currentProject.projectName
            };
            this.userVisible = true;
        },
        submitUserForm(confirm, form){
            this.$refs[confirm].validate(valid => {
                if (valid) {
                    let url = '/autotest/project/user/save';
                    this.$post(url, form, response =>{
                        this.$message.success("保存成功");
                        this.userVisible = false;
                        this.getdata(this.searchForm);
                    });
                }else{
                    return false;
                }
            });
        },
        searchUser(query){
          this.uLoading = true;
          let url = '/autotest/user/query?account=' + query;
          this.$get(url, response => {
            this.uLoading = false;
            this.userList = response.data;
          });
        },
        deleteUser(row){
            this.$confirm('确定要删除该项目下的用户吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/project/user/delete';
                let param = {
                  projectId: this.$store.state.projectId,
                  userId: row.id
                }
                this.$post(url, param, response => {
                    this.$message.success("删除成功");
                    this.getdata(this.searchForm);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        editUser(row){
          this.title = "编辑项目用户";
          this.userList = [
            {id: row.id, username: row.username}
          ];
          this.userForm = {
            isEdit: true,
            projectId: this.currentProject.projectId,
            projectName: this.currentProject.projectName,
            userIds: [row.id],
            roleIds: [],
          };
          let url = "/autotest/user/role/list?projectId=" + this.currentProject.projectId + "&userId=" + row.id;
          this.$get(url, response =>{
            this.userForm.roleIds = response.data;
          });
          this.userVisible = true;
        },
    }
}
</script>

<style scoped>

</style>