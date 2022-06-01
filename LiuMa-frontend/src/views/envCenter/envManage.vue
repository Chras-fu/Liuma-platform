/**
 * 环境中心  环境管理
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入环境名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" type="primary" icon="el-icon-plus" @click="addEnv">新增环境</el-button>
      </el-form-item>
    </el-form>
    <!-- 环境表 -->
    <el-table size="small" v-loading="loading" :data="environmentData" @expand-change="expandSelect">
      <el-table-column type="expand" width="40px">
        <template slot-scope="props">
          <div style="padding-left: 40px">
            <!-- 域名表 -->
            <el-table size="mini" :data="props.row.domainData">
              <el-table-column label="序号" prop="index" width="50px" align="center"/>
              <el-table-column label="类型" prop="domainKeyType">
                <template slot-scope="scope">
                  <span v-if="props.row.domainData[scope.$index].domainKeyType === 'path'">路由匹配</span>
                  <span v-if="props.row.domainData[scope.$index].domainKeyType === 'sign'">域名标识</span>
                </template>
              </el-table-column>
              <el-table-column label="标识">
                <template slot-scope="scope">
                  <span v-if="props.row.domainData[scope.$index].domainKeyType === 'path'">{{scope.row.domainKey}}</span>
                  <span v-if="props.row.domainData[scope.$index].domainKeyType === 'sign'">{{scope.row.domainSignName}}</span>
                </template>
              </el-table-column>
              <el-table-column label="域名" prop="domainData" min-width="200px"/>
              <el-table-column fixed="right" align="operation" label="操作" width="150px">
                <template slot-scope="scope">
                <el-button type="text" size="mini" @click="editDomain(props.$index, scope.row)">编辑</el-button>
                <el-button type="text" size="mini" @click="deleteDomain(props.$index, scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="序号" prop="index" width="50px" align="center"/>
      <el-table-column label="环境名称" prop="name"/>
      <el-table-column label="环境描述" prop="description" min-width="200px"/>
      <el-table-column label="创建人" prop="username"/>
      <el-table-column label="更新时间" prop="updateTime" width="150px"/>
      <el-table-column fixed="right" align="operation" label="操作" width="150px">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="addDomain(scope.$index, scope.row)">新增域名</el-button>
          <el-button type="text" size="mini" @click="editEnv(scope.row)">编辑</el-button>
          <el-button type="text" size="mini" @click="deleteEnv(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination size="small" v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 添加环境界面 -->
    <el-dialog :title="title" :visible.sync="editEnvironmentVisible" width="600px" destroy-on-close>
      <el-form label-width="120px" style="padding-right: 30px;" :model="environmentForm" :rules="rules" ref="environmentForm">
          <el-form-item label="环境名称" prop="name">
            <el-input size="small" style="width: 90%" v-model="environmentForm.name" placeholder="请输入环境名称"/>
          </el-form-item>
          <el-form-item label="环境描述" prop="description">
            <el-input size="small" style="width: 90%" v-model="environmentForm.description" :autosize="{ minRows: 4}" 
            maxlength="200" show-word-limit type="textarea" clearable auto-complete="off" placeholder="请输入环境描述"/>
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
          <el-button size="small" @click="editEnvironmentVisible=false">取消</el-button>
          <el-button size="small" type="primary" class="title" @click="submitEnvForm('environmentForm', environmentForm)">保存</el-button>
      </div>
    </el-dialog>
    <!-- 添加域名界面 -->
    <el-dialog :title="title" :visible.sync="editDomainVisible" width="600px" destroy-on-close>
      <el-form label-width="120px" style="padding-right: 30px;" :model="domainForm" :rules="rules" ref="domainForm">
          <el-form-item label="匹配类型" prop="domainKeyType">
            <el-select size="small" style="width: 90%" v-model="domainForm.domainKeyType" placeholder="请选择域名匹配方式" @change="haddleDomainChange">
              <el-option v-for="item in domainKeyTypes" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="匹配标识" prop="domainKey">
            <el-input v-if="domainForm.domainKeyType==='path'" size="small" style="width: 90%" v-model="domainForm.domainKey" placeholder="请输入路由匹配表达式"/>
            <el-select v-if="domainForm.domainKeyType==='sign'" size="small" style="width: 90%" v-model="domainForm.domainKey" placeholder="请选择域名标识">
              <el-option v-for="item in domainSigns" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="域名" prop="domainData">
            <el-input size="small" style="width: 90%" v-model="domainForm.domainData" placeholder="请输入域名"/>
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
          <el-button size="small" @click="editDomainVisible=false">取消</el-button>
          <el-button size="small" type="primary" class="title" @click="submitDomainForm('domainForm', domainForm)">保存</el-button>
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
      title: "新增环境",
      editEnvironmentVisible: false,
      editDomainVisible: false,
      domainKeyTypes: [
        {id: "path", name:"路由匹配"},
        {id: "sign", name:"域名标识"}
      ],
      domainSigns: [],
      environmentForm:{},
      domainForm:{},
      loading: false,
      environmentData:[],
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
      rules: {
        name: [{ required: true, message: '环境名称不能为空', trigger: 'blur' }],
        domainKeyType: [{ required: true, message: '匹配类型不能为空', trigger: 'blur' }],
        domainKey: [{ required: true, message: '匹配标识不能为空', trigger: 'blur' }],
        domainData: [{ required: true, message: '域名不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.$root.Bus.$emit('initBread', ["环境中心", "环境管理"]);
    this.getEnvironmentData(this.searchForm)
  },
  methods: {
    getEnvironmentData(searchParam) {
      this.loading = true
      let url = '/autotest/environment/list/' + searchParam.page + '/' + searchParam.limit;
      let param = {
        condition: searchParam.condition,
        projectId: this.$store.state.projectId
      };
      this.$post(url, param, response => {
        let data = response.data;
        for(let i =0; i<data.list.length; i++){
          data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
          data.list[i].domainData = [];
          data.list[i].index = (searchParam.page-1) * searchParam.limit + i+1;
        }
        this.environmentData = data.list;
        this.loading = false;
        // 分页赋值
        this.pageparam.currentPage = this.searchForm.page;
        this.pageparam.pageSize = this.searchForm.limit;
        this.pageparam.total = data.total;
      });
    },
    getDomainData(index){
      let environmentId = this.environmentData[index].id;
      let url = '/autotest/domain/list/' + environmentId;
      this.$get(url, response => {
        let data = response.data;
        for(let i=0; i<data.length; i++){
          data[i].index = i+1;
        }
        this.environmentData[index].domainData = data;
      });
    },
    getDomainSigns(){
      let url = '/autotest/domainSign/list/' + this.$store.state.projectId;
      this.$get(url, response =>{
        this.domainSigns = response.data;
      });
    },
    expandSelect(row, expandedRows) {
      if(expandedRows.length != 0){
        let index = row.index - 1 - (this.pageparam.currentPage-1)*this.pageparam.pageSize;
        this.getDomainData(index);
      }
    },
    // 搜索按钮
    search() {
        this.getEnvironmentData(this.searchForm);
    },
    // 重置按钮
    reset() {
        this.searchForm.condition = "";
        this.getEnvironmentData(this.searchForm);
    },
    // 分页插件事件
    callFather(param) {
        this.searchForm.page = param.currentPage;
        this.searchForm.limit = param.pageSize;
        this.getEnvironmentData(this.searchForm);
    },
    // 新增域名
    addDomain(index, row){
      this.title = "新增域名";
      this.domainForm = {
        index: index,
        domainKeyType: "sign",
        domainKey: "",
        environmentId: row.id
      };
      this.getDomainSigns();
      this.editDomainVisible = true;
    },
    // 编辑域名
    editDomain(index, row){
      this.title = "编辑域名";
      this.domainForm = {
        index: index,
        id: row.id,
        environmentId: row.environmentId,
        domainKeyType: row.domainKeyType,
        domainKey: row.domainKey,
        domainData: row.domainData,
        createUser: row.createUser,
        createTime: row.createTime
      };
      this.getDomainSigns();
      this.editDomainVisible = true;
    },
    // 删除域名
    deleteDomain(index, row){
      this.$confirm('确定要删除域名吗?', '删除提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
      })
      .then(() => {
          let url = '/autotest/domain/delete';
          this.$post(url, {id: row.id}, response => {
              this.$message.success("删除成功");
              this.getDomainData(index);
          });
      })
      .catch(() => {
          this.$message.success("取消成功");
      })
    },
    haddleDomainChange(){
      this.domainForm.domainKey = "";
    },
    submitDomainForm(confirm, form){
      this.$refs[confirm].validate(valid => {
        if (valid) {
          let url = '/autotest/domain/save';
          this.$post(url, form, response =>{
            this.$message.success("保存成功");
            this.editDomainVisible = false;
            this.getDomainData(form.index);
          });
        }else{
          return false;
        }
      });
    },
    // 新增环境
    addEnv(){
      this.title = "新增环境";
      this.environmentForm = {
        projectId: this.$store.state.projectId
      };
      this.editEnvironmentVisible = true;
    },
    // 编辑环境
    editEnv(row){
      this.title = "编辑环境";
      this.environmentForm = {
        id: row.id,
        name: row.name,
        description: row.description,
        projectId: this.$store.state.projectId,
        createUser: row.createUser,
        createTime: row.createTime
      };
      this.editEnvironmentVisible = true;
    },
    // 删除环境
    deleteEnv(row){
      this.$confirm('确定要删除环境吗?', '删除提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
      })
      .then(() => {
          let url = '/autotest/environment/delete';
          this.$post(url, {id: row.id}, response => {
              this.$message.success("删除成功");
              this.getEnvironmentData(this.searchForm);
          });
      })
      .catch(() => {
          this.$message.success("取消成功");
      })
    },
    submitEnvForm(confirm, form){
      this.$refs[confirm].validate(valid => {
        if (valid) {
          let url = '/autotest/environment/save';
          this.$post(url, form, response =>{
            this.$message.success("保存成功");
            this.editEnvironmentVisible = false;
            this.getEnvironmentData(this.searchForm);
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