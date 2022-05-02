/**
 * 公共组件  控件管理
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入控件名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" type="primary" icon="el-icon-plus" @click="addOperation">新增控件</el-button>
      </el-form-item>
    </el-form>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="浏览器操作" name="browser">
        <operation-table :operationData="operationData" :loading="loading" :pageparam="pageparam" 
          @callFather="callFather($event)" @deleteOperation="deleteOperation($event)" />
      </el-tab-pane>
      <el-tab-pane label="网页操作" name="page">
        <operation-table :operationData="operationData" :loading="loading" :pageparam="pageparam" 
          @callFather="callFather($event)" @deleteOperation="deleteOperation($event)" />
      </el-tab-pane>
      <el-tab-pane label="关联" name="relation">
        <operation-table :operationData="operationData" :loading="loading" :pageparam="pageparam" 
          @callFather="callFather($event)" @deleteOperation="deleteOperation($event)" />
      </el-tab-pane>
      <el-tab-pane label="断言" name="assertion">
        <operation-table :operationData="operationData" :loading="loading" :pageparam="pageparam" 
          @callFather="callFather($event)" @deleteOperation="deleteOperation($event)" />
      </el-tab-pane>
      <el-tab-pane label="条件" name="condition">
        <operation-table :operationData="operationData" :loading="loading" :pageparam="pageparam" 
          @callFather="callFather($event)" @deleteOperation="deleteOperation($event)" />
      </el-tab-pane>
      <el-tab-pane label="自定义场景" name="scenario">
        <operation-table :operationData="operationData" :loading="loading" :pageparam="pageparam" 
          @callFather="callFather($event)" @deleteOperation="deleteOperation($event)" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import OperationTable from './common/operationTable'
import {timestampToTime} from '@/utils/util'
export default {
  components: {
    Pagination, OperationTable
  },
  data() {
    return{
      activeName: "browser",
      operationData: [],
      loading: false,
      searchForm: {
        page: 1,
        limit: 10,
        condition: "",
      },
      pageparam: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
    }
  },
  created() {
    this.$root.Bus.$emit('initBread', ["公共组件", "控件管理"]);
    this.getdata(this.searchForm);
  },
  methods: {
    handleClick(tab, event){
      this.searchForm.page = 1;
      this.searchForm.limit = 10;
      this.pageparam.currentPage = 1;
      this.pageparam.pageSize = 10;
      this.pageparam.total = 0;
      this.getdata(this.searchForm);
    },
    // 获取列表数据方法
    getdata(searchParam) {
      this.loading = true;
      let url = '/autotest/operation/list/' + searchParam.page + '/' + searchParam.limit;
      let param = {
          projectId: this.$store.state.projectId,
          condition: searchParam.condition,
          operationType: this.activeName
      };
      this.$post(url, param, response => {
          let data = response.data;
          for(let i =0; i<data.list.length; i++){
              data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
              data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
          }
          this.operationData = data.list;
          this.loading = false;
          // 分页赋值
          this.pageparam.currentPage = this.searchForm.page;
          this.pageparam.pageSize = this.searchForm.limit;
          this.pageparam.total = data.total;
      });
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
    // 分页插件事件
    callFather(param) {
        this.searchForm.page = param.currentPage;
        this.searchForm.limit = param.pageSize;
        this.getdata(this.searchForm);
    },
    // 新增操作
    addOperation(){
      this.$router.push({path: '/common/operationManage/add/' + this.activeName});
    },
    deleteOperation(row){
      let url = '/autotest/operation/delete';
      this.$post(url, {id: row.id}, response => {
          this.$message.success("删除成功");
          this.getdata(this.searchForm);
      });
    }
  }
}
</script>

<style scoped>

</style>