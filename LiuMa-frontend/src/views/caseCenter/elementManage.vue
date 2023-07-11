/**
 * 用例中心  元素管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入元素NO、名称"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addElement">新增元素</el-button>
        </el-form-item>
    </el-form>
    <!-- 页面模块 -->
    <el-col :span="4" class="left-tree">
        <module-tree title="页面模块" :treeData="treeData" :currentModule="searchForm.moduleId" @clickModule="clickModule($event)" @appendModule="appendModule($event)"
            @removeModule="removeModule(arguments)" @dragNode="dragNode(arguments)"/>
    </el-col>
    <!-- 元素列表 -->
    <el-col :span="20" class="right-table">
        <!--列表-->
        <el-table size="small" :data="elementListData" v-loading="loading" element-loading-text="拼命加载中">
            <el-table-column prop="num" label="NO" width="60px"/>
            <el-table-column prop="name" label="元素名称" min-width="180"/>
            <el-table-column prop="by" label="定位方式"/>
            <el-table-column prop="expression" label="表达式" min-width="150"/>
            <el-table-column prop="moduleName" label="所属页面"/>
            <el-table-column prop="username" label="创建人"/>
            <el-table-column prop="updateTime" label="更新时间" width="150px"/>
            <el-table-column fixed="right" align="operation" label="操作" width="100">
                <template slot-scope="scope">
                    <el-button type="text" size="mini" @click="editElement(scope.row)">编辑</el-button>
                    <el-button type="text" size="mini" @click="deleteElement(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    </el-col>
    <!-- 添加模块弹框 -->
    <module-append title="添加页面模块" :show.sync="moduleVisible" :moduleForm="moduleForm" @closeDialog="closeDialog" @submitModule="submitModule($event)"/>
    <!-- 添加元素弹框 -->
    <el-dialog title="编辑元素" :visible.sync="elementVisible" width="40%" destroy-on-close>
        <el-form label-width="120px" style="padding-right: 30px;" :model="addElementForm" :rules="rules" ref="addElementForm">
            <el-form-item label="元素名称" prop="name">
                <el-input size="small" style="width:95%" v-model="addElementForm.name" auto-complete="off" placeholder="元素名称"/>
            </el-form-item>
            <el-form-item label="定位方式" prop="by">
                <el-select size="small" style="width:95%;" v-model="addElementForm.by" placeholder="定位方式">
                    <el-option v-for="item in byList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="表达式" prop="expression">
                <el-input size="small" style="width:95%" v-model="addElementForm.expression" auto-complete="off" placeholder="表达式"/>
            </el-form-item>
            <el-form-item label="所属页面" prop="moduleId">
                <select-tree style="width:95%;" placeholder="所属页面" :selectedValue="addElementForm.moduleId" :selectedLabel="addElementForm.moduleName" :treeData="treeData" @selectModule="selectModule($event)"/>
            </el-form-item>
            <el-form-item label="元素描述">
                <el-input size="small" style="width:95%" v-model="addElementForm.description" :autosize="{ minRows: 3}" type="textarea" maxlength="200" show-word-limit auto-complete="off" placeholder="元素描述"/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="elementVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitElement">确定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import ModuleTree from './common/module/moduleTree'
import ModuleAppend from './common/module/moduleAppend'
import SelectTree from '../common/business/selectTree'
import {timestampToTime} from '@/utils/util'

export default {
    // 注册组件
    components: {
        Pagination, ModuleTree, ModuleAppend, SelectTree
    },
    data() {
        return{
            loading:false,
            moduleVisible: false,
            elementVisible: false,
            moduleForm: {
                moduleName: "",
                parentId: "",
                parentName: "",
                data: "",
            },
            byList:[
                { label: "ID", value: "ID" },
                { label: "NAME", value: "NAME" },
                { label: "TAG", value: "TAG" },
                { label: "CLASS", value: "CLASS" },
                { label: "CSS", value: "CSS" },
                { label: "LINK", value: "LINK" },
                { label: "PARTIAL_LINK", value: "PARTIAL" },
                { label: "XPATH", value: "XPATH" },
            ],
            addElementForm: {
                id:"",
                name:"",
                by: "",
                expression: "",
                moduleId: "0",
                moduleName:"默认模块",
                description: ""
            },
            searchForm: {
                page: 1,
                limit: 10,
                condition: "",
                moduleId: ""
            },
            elementListData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            treeData: [],
            rules: {
                name: [{ required: true, message: '元素名称不能为空', trigger: 'blur' }],
                by: [{ required: true, message: '定位方式不能为空', trigger: 'blur' }],
                expression: [{ required: true, message: '表达式不能为空', trigger: 'blur' }]
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["用例中心", "元素管理"])
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
        closeDialog(){
            this.moduleVisible = false;
        },
        // 提交模块
        submitModule(moduleForm) {
            moduleForm.projectId = this.$store.state.projectId;
            moduleForm.moduleType = 'page_module';
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
            let url = '/autotest/module/list/page/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.treeData = response.data;
            });
        },
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true;
            let url = '/autotest/element/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                moduleId: searchParam.moduleId,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i=0;i<data.list.length;i++){
                    if(data.list[i].moduleId==='0'){
                        data.list[i].moduleName='默认模块';
                    }
                    data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
                }
                this.elementListData = data.list;
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
        // 新增元素
        addElement(){
            this.addElementForm = {
                id:"",
                name:"",
                by: "",
                expression: "",
                moduleId: "0",
                moduleName:"默认模块",
                description: ""
            };
            this.elementVisible = true;
        },
        selectModule(data){
            this.addElementForm.moduleId = data.id;
            this.addElementForm.moduleName = data.label;
        },
        // 提交元素
        submitElement() {
            // 请求接口
            this.$refs["addElementForm"].validate(valid => {
                if (valid) {
                    this.addElementForm.projectId = this.$store.state.projectId;
                    let url = '/autotest/element/save';
                    this.$post(url, this.addElementForm, response =>{
                        this.$message.success("保存成功");
                        this.elementVisible = false;
                        this.loading = true;
                        this.getdata(this.searchForm);
                    });

                }else{
                    return false;
                }
            })
        },
        // 编辑元素
        editElement(row){
            this.addElementForm.id = row.id;
            this.addElementForm.name = row.name;
            this.addElementForm.by = row.by;
            this.addElementForm.expression = row.expression;
            this.addElementForm.moduleId = row.moduleId;
            if(row.moduleId==='0'){
                this.addElementForm.moduleName = "默认模块";
            }else{
                this.addElementForm.moduleName = row.moduleName;
            }
            this.addElementForm.description = row.description;
            this.elementVisible = true;
        },
        // 删除元素
        deleteElement(row){
            this.$confirm('确定要删除元素吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/element/delete';
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
