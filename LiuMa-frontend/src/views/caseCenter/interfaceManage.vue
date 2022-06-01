/**
 * 用例中心  接口管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入接口NO、名称、地址"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addApi">新增接口</el-button>
        </el-form-item>
    </el-form>
    <!-- 接口模块 -->
    <el-col :span="4" class="left-tree">
        <module-tree title="接口模块" :treeData="treeData" :currentModule="searchForm.moduleId" @clickModule="clickModule($event)" @appendModule="appendModule($event)" 
            @removeModule="removeModule(arguments)" @dragNode="dragNode(arguments)"/>
    </el-col>
    <!--接口列表-->
    <el-col :span="20" class="right-table">
        <el-table size="small" :data="apiListData" v-loading="loading" element-loading-text="拼命加载中">
        <el-table-column prop="num" label="NO" width="60px"/>
        <el-table-column prop="name" label="接口名称" min-width="180"/>
        <el-table-column prop="path" label="接口地址" min-width="150"/>
        <el-table-column prop="moduleName" label="所属模块"/>
        <el-table-column prop="username" label="创建人"/>
        <el-table-column prop="updateTime" label="更新时间" width="150"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100">
            <template slot-scope="scope">
            <el-button type="text" size="mini" @click="editApi(scope.row)">编辑</el-button>
            <el-button type="text" size="mini" @click="deleteApi(scope.row)">删除</el-button>
            </template>
        </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageParam" @callFather="callFather"></Pagination>
    </el-col>
    <!-- 添加模块弹窗 -->
    <module-append :title="title" :show.sync="moduleVisible" :moduleForm="moduleForm" @closeDialog="closeDialog" @submitModule="submitModule($event)"/>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import ModuleTree from './common/module/moduleTree'
import ModuleAppend from './common/module/moduleAppend'
import {timestampToTime} from '@/utils/util'

export default {
    // 注册组件
    components: {
        Pagination, ModuleTree, ModuleAppend
    },
    data() {
        return{
            loading:false,
            moduleVisible: false,
            moduleForm: {
                name: "",
                parentId: "",
                parentName: "",
                data: "",
            },
            title: '添加接口模块',
            searchForm: {
                page: 1,
                limit: 10,
                condition: "",
                moduleId: "",
            },
            apiListData: [],
            pageParam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            treeData: [],
        }
    },
    created() {
        // 加载面包屑
        this.$root.Bus.$emit('initBread', ["用例中心", "接口管理"])
        this.getTree()
        this.getdata(this.searchForm)
    },
    methods: {
        // 点击模块
        clickModule(data){
            this.searchForm.moduleId = data.id;
            this.getdata(this.searchForm);
        },
        // 添加模块
        appendModule(data) {
            if (data){
                this.moduleForm.parentId = data.id;
                this.moduleForm.parentName = data.label;
                this.moduleForm.data = data;
            }else{
                this.moduleForm.parentId = 0;
                this.moduleForm.parentName = "根节点";
                this.moduleForm.data = "";
            }
            this.moduleVisible = true;
        },
        // 删除模块
        removeModule(args) {
            let node = args[0];
            let data = args[1];
            let url = '/autotest/module/delete';
            this.$post(url, data, response =>{
                const parent = node.parent;
                const children = parent.data.children || parent.data;
                const index = children.findIndex(d => d.id === data.id);
                children.splice(index, 1);
                this.$message.success("模块删除成功")
            });
        },
        // 拖拽模块
        dragNode(args){
            let dragNode = args[0];
            let newParent = args[1];
            let url = '/autotest/module/save';
            let moduleForm = dragNode.data;
            moduleForm.parentId = newParent;
            this.$post(url, moduleForm, response =>{
                this.$message.success("更改成功")
            });
        },
        // 关闭弹框
        closeDialog(){
            this.moduleVisible = false;
        },
        // 提交模块保存
        submitModule(moduleForm) {
            moduleForm.projectId = this.$store.state.projectId;
            moduleForm.moduleType = 'api_module';
            let url = '/autotest/module/save';
            this.$post(url, moduleForm, response =>{
                const newChild = response.data;
                if (moduleForm.parentId === 0){
                    this.treeData.push(newChild);
                }else{
                    if (!this.moduleForm.data.children){
                        this.$set(this.moduleForm.data, 'children', []);
                    }
                    this.moduleForm.data.children.push(newChild);
                }
                this.moduleVisible = false;
                this.moduleForm.name = "";
            });
        },
        // 获取树数据
        getTree(){
            let url = '/autotest/module/list/api/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.treeData = response.data;
            });
        },
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true;
            let url = '/autotest/api/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                moduleId: searchParam.moduleId,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i=0;i<data.list.length;i++){
                    data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
                }
                this.apiListData = data.list;
                this.loading = false
                // 分页赋值
                this.pageParam.currentPage = this.searchForm.page;
                this.pageParam.pageSize = this.searchForm.limit;
                this.pageParam.total = data.total;
            });
        },
        // 分页插件事件
        callFather(param) {
            this.searchForm.page = param.currentPage
            this.searchForm.limit = param.pageSize
            this.getdata(this.searchForm)
        },
        // 搜索按钮
        search() {
            this.getdata(this.searchForm)
        },
        // 重置按钮
        reset() {
            this.searchForm.condition = "";
            this.searchForm.moduleId = "";
            this.getdata(this.searchForm);
        },
        // 新增接口
        addApi(){
            this.$router.push({path: '/caseCenter/interfaceManage/add'})
        },
        // 编辑接口
        editApi(row){
            this.$router.push({path: '/caseCenter/interfaceManage/edit/' + row.id})
        },
        // 删除接口
        deleteApi(row){
            this.$confirm('确定要删除接口吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/api/delete';
                this.$post(url, {id: row.id}, response => {
                    this.$message.success("删除成功");
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
.left-tree {
    padding-right: 5px;
    border-right:1px solid rgb(219, 219, 219);
}
.right-table {
    padding-left: 5px;
}
</style>
