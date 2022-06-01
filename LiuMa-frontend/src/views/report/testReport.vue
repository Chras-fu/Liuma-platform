/**
 * 测试追踪  测试报告
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入报告名称"/>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="reportData" v-loading="loading">
        <el-table-column prop="index" label="序号" align="center" width="50px"/>
        <el-table-column prop="name" label="报告名称" min-width="160px"/>
        <el-table-column prop="format" label="报告状态" width="100px"/>
        <el-table-column prop="runProgress" label="执行进度" width="120px">
            <template slot-scope="scope">
                <el-progress :percentage="scope.row.progress" :color="scope.row.color"/>
            </template>
        </el-table-column>
        <el-table-column prop="total" label="总用例数"/>
        <el-table-column prop="passCount" label="成功用例数"/>
        <el-table-column prop="passRate" label="成功率"/>
        <el-table-column prop="username" label="执行人"/>
        <el-table-column prop="createTime" label="执行时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="viewReport(scope.row)">查看</el-button>
                <el-button type="text" size="mini" @click="deleteReport(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"></Pagination>
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
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            reportData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["测试追踪", "测试报告"]);
        this.getdata(this.searchForm)
    },
    methods: {
        // 获取列表数据方法
        getdata(searchParam) {
            // 获取报告
            this.loading = true;
            let url = '/autotest/report/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                projectId: this.$store.state.projectId,
                condition: searchParam.condition
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
                    data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
                }
                this.reportData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
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
        // 查看报告
        viewReport(row){
            this.$router.push({path: '/report/testReport/detail/' + row.id});
        },
        // 删除报告
        deleteReport(row){
            this.$confirm('确定要删除报告吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/report/delete';
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

</style>