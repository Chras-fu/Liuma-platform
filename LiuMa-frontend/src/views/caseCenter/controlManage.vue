/**
 * 用例中心  控件管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-radio-group size="small" v-model="searchForm.system" @change="selectSystem">
                <el-radio-button label="android"/>
                <el-radio-button label="apple"/>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入控件NO、名称"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addControl">新增控件</el-button>
        </el-form-item>
    </el-form>
    <!-- 页面模块 -->
    <el-col :span="4" class="left-tree">
        <module-tree title="视图模块" :treeData="treeData" :currentModule="searchForm.moduleId" @clickModule="clickModule($event)" @appendModule="appendModule($event)" 
            @removeModule="removeModule(arguments)" @dragNode="dragNode(arguments)"/>
    </el-col>
    <!-- 控件列表 -->
    <el-col :span="20" class="right-table">
        <!--列表-->
        <el-table size="small" :data="controlListData" v-loading="loading" control-loading-text="拼命加载中">
            <el-table-column prop="num" label="NO" width="60px"/>
            <el-table-column prop="name" label="控件名称" min-width="150"/>
            <el-table-column prop="system" label="所属系统"/>
            <el-table-column prop="by" label="定位方式">
                <template slot-scope="scope">
                    <span v-if="scope.row.by ==='PROP'">属性定位</span>
                    <span v-if="scope.row.by ==='XPATH'">Xpath定位</span>
                    <span v-if="scope.row.by ==='PRED'">Predicate定位</span>
                    <span v-if="scope.row.by ==='CLASS'">ClassChain定位</span>
                </template>
            </el-table-column>
            <el-table-column prop="expressionText" label="表达式" min-width="120"/>
            <el-table-column prop="moduleName" label="所属页面"/>
            <el-table-column prop="username" label="创建人"/>
            <el-table-column prop="updateTime" label="更新时间" width="150px"/>
            <el-table-column fixed="right" align="operation" label="操作" width="100">
                <template slot-scope="scope">
                    <el-button type="text" size="mini" @click="editControl(scope.row)">编辑</el-button>
                    <el-button type="text" size="mini" @click="deleteControl(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    </el-col>
    <!-- 添加模块弹框 -->
    <module-append title="添加页面模块" :show.sync="moduleVisible" :moduleForm="moduleForm" @closeDialog="closeDialog('module')" @submitModule="submitModule($event)"/>
    <!-- 添加控件弹框 -->
    <control-append :controlVisible="controlVisible" :controlForm="controlForm" @closeDialog="closeDialog('control')" @submitControlForm="submitControlForm"/>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import ModuleTree from './common/module/moduleTree'
import ModuleAppend from './common/module/moduleAppend'
import {timestampToTime} from '@/utils/util'
import controlAppend from './common/control/controlAppend'

export default {
    // 注册组件
    components: {
        Pagination, ModuleTree, ModuleAppend, controlAppend
    },
    data() {
        return{
            loading:false,
            moduleVisible: false,
            controlVisible: false,
            controlForm: {
                id:"",
                name:"",
                system: "",
                by: "",
                expression: "XPATH",
                expressionList: [],
                moduleId: "0",
                moduleName:"默认模块",
                description: ""
            },
            moduleForm: {
                moduleName: "",
                parentId: "",
                parentName: "",
                data: "",
            },
            systems:[
                { label: "安卓", value: "android" },
                { label: "苹果", value: "apple" },
            ],
            searchForm: {
                page: 1,
                limit: 10,
                condition: "",
                moduleId: "",
                system: "android"
            },
            controlListData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            treeData: []
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["用例中心", "控件管理"]);
        this.getTree();
        this.getdata(this.searchForm);
    },
    methods: {
        selectSystem(val){
            this.searchForm.system = val;
            this.searchForm.page = 1;
            this.getdata(this.searchForm);
        },
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
            if(data.children.length != 0){
                this.$message.warning("当前模块有子模块, 无法删除");
                return;
            }
            let url = '/autotest/module/delete';
            this.$post(url, data, response =>{
                const parent = node.parent;
                const children = parent.data.children || parent.data;
                const index = children.findIndex(d => d.id === data.id);
                children.splice(index, 1);
                this.$message.success("模块删除成功");
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
                this.$message.success("更改成功");
            });
        },
        // 关闭弹框
        closeDialog(val){
            if(val==='module'){
                this.moduleVisible = false;
            }else{
                this.controlVisible = false;
            }
        },
        // 提交模块
        submitModule(moduleForm) {
            moduleForm.projectId = this.$store.state.projectId;
            moduleForm.moduleType = 'view_module';
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
            let url = '/autotest/module/list/view/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.treeData = response.data;
            });
        },
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true;
            let url = '/autotest/control/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                moduleId: searchParam.moduleId,
                projectId: this.$store.state.projectId,
                system: searchParam.system
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i=0;i<data.list.length;i++){
                    if(data.list[i].moduleId==='0'){
                        data.list[i].moduleName='默认模块';
                    }
                    if(data.list[i].by === "PROP"){
                        let expressions = JSON.parse(data.list[i].expression);
                        let text = "";
                        for(let j=0;j<expressions.length;j++){
                            text = text + expressions[j].propName + ": " + expressions[j].propValue + ";\n";
                        }
                        data.list[i].expressionText = text;
                    }else{
                        data.list[i].expressionText = data.list[i].expression;
                    }
                    
                    data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
                }
                this.controlListData = data.list;
                this.loading = false
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
        // 分页插件事件
        callFather(parm) {
            this.searchForm.page = parm.currentPage
            this.searchForm.limit = parm.pageSize
            this.getdata(this.searchForm)
        },
        // 搜索按钮
        search() {
            this.getdata(this.searchForm)
        },
        // 重置按钮
        reset() {
            this.searchForm.moduleId = "";
            this.searchForm.condition = "";
            this.getdata(this.searchForm)
        },
        // 新增控件
        addControl(){
            this.controlForm = {
                system: this.searchForm.system,
                id:"",
                name:"",
                by: "XPATH",
                expression: "",
                expressionList: [],
                moduleId: "0",
                moduleName:"默认模块",
                description: ""
            };
            this.controlVisible = true;
        },
        // 编辑控件
        editControl(row){
            this.controlForm.id = row.id;
            this.controlForm.name = row.name;
            this.controlForm.system = row.system;
            this.controlForm.by = row.by;
            this.controlForm.expression = row.expression;
            if(row.by === "PROP"){
                this.controlForm.expressionList = JSON.parse(row.expression);
            }
            this.controlForm.moduleId = row.moduleId;
            if(row.moduleId==='0'){
                this.controlForm.moduleName = "默认模块";
            }else{
                this.controlForm.moduleName = row.moduleName;
            }
            this.controlForm.description = row.description;
            this.controlVisible = true;
        },
        // 提交控件
        submitControlForm() {
            this.controlVisible = false;
            this.getdata(this.searchForm);
        },
        // 删除控件
        deleteControl(row){
            this.$confirm('确定要删除控件吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/control/delete';
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
