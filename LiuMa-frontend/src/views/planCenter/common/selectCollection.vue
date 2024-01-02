/**
* 选择集合弹窗
*/ 
<template>
    <div>
        <el-form :inline="true" :model="searchForm">
            <el-form-item label="" prop="condition">
                <el-input size="small" style="width:180px" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入集合名称"/>
            </el-form-item>
            <el-form-item>
                <el-button size="small" type="primary" @click="search">搜索</el-button>
                <el-button size="small" @click="reset">重置</el-button>
            </el-form-item>
        </el-form>
        <!--列表-->
        <el-table size="small" :data="collectionListData" v-loading="loading" @selection-change="handleSelectionChange" tooltip-effect="dark" ref="multipleTable">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column prop="index" label="编号" width="80">
            </el-table-column>
            <el-table-column prop="name" label="集合名称" min-width="160" :show-overflow-tooltip="true">
            </el-table-column>
            <el-table-column prop="versionName" label="集合版本" width="160" :show-overflow-tooltip="true">
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageParam" @callFather="callFather"/>
    </div>
</template>
<script>
import Pagination from '../../common/components/pagination'

export default {
    name: 'SelectCollection',
    components:{
        Pagination
    },
    props:{
        selections: Array,
        selectCollectionVisible: Boolean
    },
    data() {
        return{
            loading: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            pageParam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            modules: [],
            collectionListData: [],
        }
    },
    created(){
        if(this.selectCollectionVisible){
            this.getdata(this.searchForm);
        }
    },
    watch: {
        selectCollectionVisible(){
            if(this.selectCollectionVisible){
                this.getdata(this.searchForm);
            }
        }
    },
    methods: {
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true;
            let url = '/autotest/collection/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
                }
                this.collectionListData = data.list;
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
            this.getdata(this.searchForm);
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