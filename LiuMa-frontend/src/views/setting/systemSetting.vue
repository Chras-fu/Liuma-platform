/**
 * 设置中心  系统设置
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入配置名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" v-if="showOpt" type="primary" icon="el-icon-plus" @click="addSetting">新增配置</el-button>
      </el-form-item>
    </el-form>
    <el-tabs v-model="activeName" tab-position="left" @tab-click="handleClick">
      <el-tab-pane label="域名标识" name="domainSign">
        <el-table size="small" :data="settingData" v-loading="loading">
            <el-table-column prop="index" label="序号" align="center" width="50px"/>
            <el-table-column prop="name" label="标识名称"/>
            <el-table-column prop="description" label="标识说明" min-width="200px"/>
            <el-table-column prop="updateTime" label="更新时间" width="150px"/>
            <el-table-column fixed="right" align="operation" label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button v-if="showOpt" type="text" size="mini" @click="editSetting(scope.row)">编辑</el-button>
                    <el-button v-if="showOpt" type="text" size="mini" @click="deleteSetting(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
      </el-tab-pane>
      <el-tab-pane label="迭代版本" name="version">
        <el-table size="small" :data="settingData" v-loading="loading">
            <el-table-column prop="index" label="序号" align="center" width="50px"/>
            <el-table-column prop="name" label="版本名称"/>
            <el-table-column prop="description" label="版本说明" min-width="200px"/>
            <el-table-column prop="updateTime" label="更新时间" width="150px"/>
            <el-table-column fixed="right" align="operation" label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button v-if="showOpt" type="text" size="mini" @click="editSetting(scope.row)">编辑</el-button>
                    <el-button v-if="showOpt" type="text" size="mini" @click="deleteSetting(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
      </el-tab-pane>
    </el-tabs>
    <!-- 新增设置弹窗 -->
    <el-dialog :title="title" :visible.sync="settingVisible" width="600px" destroy-on-close>
        <el-form label-width="100px" style="padding-right: 30px;" :model="settingForm" :rules="rules" ref="settingForm">
            <el-form-item label="名称" prop="name">
                <el-input size="small" style="width:95%" v-model="settingForm.name" placeholder="请输入名称"/>
            </el-form-item>
            <el-form-item label="描述" prop="description">
                <el-input size="small" style="width:95%" v-model="settingForm.description" type="textarea" :autosize="{ minRows: 3}" clearable placeholder="请输入描述" maxlength="200" show-word-limit/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="settingVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitSetting">确定</el-button>
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
          title: "",
          loading: false,
          settingData: [],
          activeName: "domainSign",
          searchForm: {
            page: 1,
            limit: 10,
            condition: ""
          },
          pageparam: {
            currentPage: 1,
            pageSize: 10,
            total: 0
          },
          settingVisible: false,
          settingForm: {},
          rules: {
              name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
              description: [{ required: true, message: '描述不能为空', trigger: 'blur' }]
          },
          showOpt: false
        }
    },
    created(){
      this.$root.Bus.$emit('initBread', ["设置中心", "系统设置"]);
      this.getData(this.searchForm);
      this.getOptPerm();
    },
    methods: {
      handleClick(tab, event){
        this.searchForm.page = 1;
        this.searchForm.limit = 10;
        this.pageparam.currentPage = 1;
        this.pageparam.pageSize = 10;
        this.pageparam.total = 0;
        this.getData(this.searchForm);
      },
      getOptPerm() {
        let url = "/autotest/setting/permission?userId="+ this.$store.state.userInfo.id + "&projectId=" + this.$store.state.projectId;
        this.$get(url, response => {
          this.showOpt = response.data;
        });
      },
      getData(searchParam){
        let url = '';
        if(this.activeName === "domainSign"){
          url = "/autotest/domainSign/list/" + searchParam.page + '/' + searchParam.limit;;
        }else if(this.activeName === "version"){
          url = "/autotest/version/list/" + searchParam.page + '/' + searchParam.limit;;
        }
        let param = {
            projectId: this.$store.state.projectId,
            condition: searchParam.condition
        };
        this.$post(url, param, response => {
            let data = response.data;
            for(let i =0; i<data.list.length; i++){
                data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
                data.list[i].index = (this.pageparam.currentPage-1) * this.pageparam.pageSize + i+1;
            }
            this.settingData = data.list;
            this.loading = false;
            // 分页赋值
            this.pageparam.currentPage = this.searchForm.page;
            this.pageparam.pageSize = this.searchForm.limit;
            this.pageparam.total = data.total;
        });
      },
      // 搜索按钮
      search() {
          this.getData(this.searchForm);
      },
      // 重置按钮
      reset() {
          this.searchForm.condition = "";
          this.getData(this.searchForm);
      },
      // 分页插件事件
      callFather(param) {
          this.searchForm.page = param.currentPage;
          this.searchForm.limit = param.pageSize;
          this.getData(this.searchForm);
      },
      addSetting(){
        this.settingForm = {};
        this.settingVisible = true;
      },
      editSetting(row){
        this.settingForm = {
          id: row.id,
          name: row.name,
          description: row.description,
          createTime: row.createTime
        };
        this.settingVisible = true;
      },
      deleteSetting(row){
        let text = "确定要删除吗?";
        let url = "";
        if(this.activeName === "domainSign"){
          text = "域名标识删除后导致用例无法获取执行环境 确定要删除域名标识吗?";
          url = "/autotest/domainSign/delete";
        }else if(this.activeName === "version"){
          text = "迭代版本删除后 集合和计划无法标识所属版本 确定要删除迭代版本吗";
          url = "/autotest/version/delete";
        }
        this.$confirm(text, '删除提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        .then(() => {
            this.$post(url, {id: row.id}, response => {
                this.$message.success("删除成功");
                this.getData(this.searchForm);
            });
        })
        .catch(() => {
            this.$message.success("取消成功");
        })
      },
      submitSetting(){
        this.$refs["settingForm"].validate(valid => {
            if (valid) {
                this.settingForm.projectId = this.$store.state.projectId;
                let url = '';
                if(this.activeName === "domainSign"){
                  url = "/autotest/domainSign/save";
                }else if(this.activeName === "version"){
                  url = "/autotest/version/save";
                }
                this.$post(url, this.settingForm, response =>{
                    this.$message.success("保存成功");
                    this.settingVisible = false;
                    this.getData(this.searchForm);
                });
            }else{
                return false;
            }
        });
      }
    }

}
</script>

<style scoped>

</style>
