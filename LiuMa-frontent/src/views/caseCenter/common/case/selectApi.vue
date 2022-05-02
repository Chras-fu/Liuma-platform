/**
* 选择接口弹窗
*/ 
<template>
    <div>
        <el-form :inline="true" :model="searchForm">
            <el-form-item label="" prop="condition">
                <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入接口ID、名称、地址"/>
            </el-form-item>
            <el-form-item label="" prop="moduleName">
                <select-tree placeholder="模块分类" style="width: 300px" :selectedValue="searchForm.moduleId" 
                        :selectedLabel="searchForm.moduleName" :treeData="modules" @selectModule="selectModule($event)"/>
            </el-form-item>
            <el-form-item>
                <el-button size="small" type="primary" @click="search">搜索</el-button>
                <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        </el-form>
        <!--列表-->
        <el-table size="small" :data="apiListData" v-loading="loading" @selection-change="handleSelectionChange" tooltip-effect="dark" ref="multipleTable">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column prop="num" label="NO" width="80">
            </el-table-column>
            <el-table-column prop="name" label="接口名称" min-width="160">
            </el-table-column>
            <el-table-column prop="path" label="接口地址" width="160">
            </el-table-column>
            <el-table-column prop="moduleName" label="所属模块" width="120">
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageParam" @callFather="callFather"/>
    </div>
</template>
<script>
import SelectTree from '../../../common/business/selectTree'
import Pagination from '../../../common/components/pagination'

export default {
    name: 'SelectApi',
    components:{
        SelectTree, Pagination
    },
    props:{
        selections: Array,
        selectApiVisible: Boolean
    },
    data() {
        return{
            loading: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: "",
                moduleId: "",
            },
            pageParam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            modules: [],
            apiListData: [],
        }
    },
    created(){
        if(this.selectApiVisible){
            this.getModule();
            this.getdata(this.searchForm);
        }
    },
    watch: {
        selectApiVisible(){
            if(this.selectApiVisible){
                this.getModule();
                this.getdata(this.searchForm);
            }
        }
    },
    methods: {
        getModule(){
            let url = '/autotest/module/list/api/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.modules = response.data;
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
        selectModule(data){
            this.searchForm.moduleId = data.id;
            this.searchForm.moduleName = data.label;
        },
        handleSelectionChange(rows){
            this.selections.splice(0, this.selections.length);
            for(let i=0;i<rows.length;i++){
                this.selections.push(rows[i]);
            }
        }
    }
}
</script>
<style scoped>

</style>