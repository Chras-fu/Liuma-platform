/**
 * 设置中心  应用配置
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入应用名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" v-if="showOpt" type="primary" icon="el-icon-plus" @click="addApplication">新增应用</el-button>
      </el-form-item>
    </el-form>
    <el-table size="small" :data="applicationData" v-loading="loading">
        <el-table-column prop="index" label="序号" align="center" width="50px"/>
        <el-table-column prop="name" label="应用名称"/>
        <el-table-column prop="system" label="所属系统">
          <template slot-scope="scope">
            {{scope.row.system==='ANDROID'? "安卓": "苹果"}}
          </template>
        </el-table-column>
        <el-table-column prop="appId" label="应用ID"/>
        <el-table-column prop="mainActivity" label="应用主页"/>
        <el-table-column prop="description" label="应用说明" min-width="180px"/>
        <el-table-column prop="updateTime" label="更新时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button v-if="showOpt" type="text" size="mini" @click="editApplication(scope.row)">编辑</el-button>
                <el-button v-if="showOpt" type="text" size="mini" @click="deleteApplication(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 新增应用弹窗 -->
    <el-dialog title="新增应用" :visible.sync="applicationVisible" width="600px" destroy-on-close>
        <el-form label-width="100px" style="padding-right: 30px;" :model="applicationForm" :rules="rules" ref="applicationForm">
            <el-form-item label="应用名称" prop="name">
                <el-input size="small" style="width:95%" v-model="applicationForm.name" placeholder="请输入应用名称"/>
            </el-form-item>
            <el-form-item label="所属系统" prop="system">
                <el-select size="small" style="width:95%" :disabled="isEdit" v-model="applicationForm.system" placeholder="请选择所属系统">
                    <el-option v-for="item in systems" :key="item.value" :label="item.label" :value="item.value"/>
                </el-select>
            </el-form-item>
            <el-form-item label="应用ID" prop="appId">
                <el-input size="small" style="width:95%" v-model="applicationForm.appId" placeholder="请输入应用ID"/>
                <el-tooltip content="应用ID: 安卓的packageName或苹果的bundleId" placement="bottom">
                    <i class="el-icon-info"></i>
                </el-tooltip>
            </el-form-item>
            <el-form-item label="应用主页" prop="mainActivity">
                <el-input size="small" style="width:95%" v-model="applicationForm.mainActivity" placeholder="请输入应用主页"/>
                <el-tooltip content="应用主页: 应用首页的activity名称 启动app使用" placement="bottom">
                    <i class="el-icon-info"></i>
                </el-tooltip>
            </el-form-item>
            <el-form-item label="应用描述" prop="description">
                <el-input size="small" style="width:95%" v-model="applicationForm.description" type="textarea" 
                  :autosize="{ minRows: 3}" clearable placeholder="请输入应用描述" maxlength="200" show-word-limit/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="applicationVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitApplication">确定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/views/common/components/pagination'
import {timestampToTime} from '@/utils/util'
export default {
    name: "ApplicationSetting",
    props: {
      showOpt: {
        type: Boolean,
        default: false
      },
      activeName: {
        type: String,
        default: ""
      }
    },
    components: {
      Pagination
    },
    data() {
        return{
          isEdit: false,
          loading: false,
          systems: [
            {label: "安卓", value: "ANDROID"},
            {label: "苹果", value: "APPLE"}
          ],
          applicationData: [],
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
          applicationVisible: false,
          applicationForm: {},
          rules: {
              name: [{ required: true, message: '应用名称不能为空', trigger: 'blur' }],
              system: [{ required: true, message: '所属系统不能为空', trigger: 'blur' }],
              appId: [{ required: true, message: '应用ID不能为空', trigger: 'blur' }]
          }
        }
    },
    watch: {
        activeName(){
          if(this.activeName === "application"){
            this.getData(this.searchForm);
          }
        }
    },
    methods: {
      getData(searchParam){
        let url = "/autotest/application/list/" + searchParam.page + '/' + searchParam.limit;;
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
            this.applicationData = data.list;
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
      addApplication(){
        this.applicationForm = {
          system: "ANDROID"
        };
        this.isEdit = false;
        this.applicationVisible = true;
      },
      editApplication(row){
        this.applicationForm = {
          id: row.id,
          name: row.name,
          system: row.system,
          appId: row.appId,
          mainActivity: row.mainActivity,
          description: row.description,
          createTime: row.createTime
        };
        this.isEdit = true;
        this.applicationVisible = true;
      },
      deleteApplication(row){
        let text = "应用删除后 APP用例无法使用该应用 影响用例执行 确定要删除应用吗";
        let url = "/autotest/application/delete";
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
      submitApplication(){
        this.$refs["applicationForm"].validate(valid => {
            if (valid) {
                this.applicationForm.projectId = this.$store.state.projectId;
                let url = "/autotest/application/save";
                this.$post(url, this.applicationForm, response =>{
                    this.$message.success("保存成功");
                    this.applicationVisible = false;
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
