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
            <el-radio-group size="mini" v-model="props.row.type" @change="changeShowType(props.row)">
              <el-radio-button label="domain">域名配置</el-radio-button>
              <el-radio-button label="database">数据库配置</el-radio-button>
            </el-radio-group>
            <el-button size="mini" style="float:right;margin-right:10px" type="primary" icon="el-icon-plus"
              @click="addSetting(props.row)">新增{{props.row.type==='domain'?'域名':'数据库'}}</el-button>
            <!-- 子表 -->
            <el-table size="mini" style="margin-top:10px" :data="props.row.data">
              <el-table-column label="序号" prop="index" width="50px" align="center"/>
              <el-table-column v-if="props.row.type==='domain'" label="类型" prop="domainKeyType">
                <template slot-scope="scope">
                  <span v-if="scope.row.domainKeyType === 'path'">路由匹配</span>
                  <span v-if="scope.row.domainKeyType === 'sign'">域名标识</span>
                </template>
              </el-table-column>
              <el-table-column v-else label="类型" prop="databaseType">
                <template slot-scope="scope">
                  <span v-if="scope.row.databaseType === 'mysql'">MySQL数据库</span>
                  <span v-if="scope.row.databaseType === 'mssql'">SQL Server数据库</span>
                  <span v-if="scope.row.databaseType === 'pgsql'">PostgreSQL数据库</span>
                  <span v-if="scope.row.databaseType === 'oracle'">Oracle数据库</span>
                </template>
              </el-table-column>
              <el-table-column v-if="props.row.type==='domain'" label="标识">
                <template slot-scope="scope">
                  <span v-if="scope.row.domainKeyType === 'path'">{{scope.row.domainKey}}</span>
                  <span v-if="scope.row.domainKeyType === 'sign'">{{scope.row.domainSignName}}</span>
                </template>
              </el-table-column>
              <el-table-column v-else label="数据库名" prop="databaseKey"/>
              <el-table-column v-if="props.row.type==='domain'" label="域名" prop="domainData" min-width="200px"/>
              <el-table-column v-else label="数据库地址" min-width="200px">
                <template slot-scope="scope">
                  {{scope.row.host}}:{{scope.row.port}}
                </template>
              </el-table-column>
              <el-table-column fixed="right" align="operation" label="操作" width="120px">
                <template slot-scope="scope">
                <el-button type="text" size="mini" @click="editSetting(props.row, scope.row)">编辑</el-button>
                <el-button type="text" size="mini" @click="deleteSetting(props.row, scope.row)">删除</el-button>
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
      <el-table-column fixed="right" align="operation" label="操作" width="120px">
        <template slot-scope="scope">
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
    <!-- 添加数据库界面 -->
    <el-dialog :title="title" :visible.sync="editDatabaseVisible" width="600px" destroy-on-close>
      <el-form label-width="120px" style="padding-right: 30px;" :model="databaseForm" :rules="rules" ref="databaseForm">
          <el-form-item label="数据库类型" prop="databaseType">
            <el-select size="small" style="width: 90%" v-model="databaseForm.databaseType" placeholder="请选择数据库类型">
              <el-option v-for="item in databaseTypes" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </el-form-item>
          <el-form-item label="数据库名" prop="databaseKey">
            <el-input size="small" style="width: 90%" v-model="databaseForm.databaseKey" placeholder="请输入数据库名称"/>
          </el-form-item>
          <el-form-item label="地址" prop="host">
            <el-input size="small" style="width: 90%" v-model="databaseForm.host" placeholder="请输入数据库地址"/>
          </el-form-item>
          <el-form-item label="端口" prop="port">
            <el-input size="small" style="width: 90%" v-model="databaseForm.port" placeholder="请输入数据库端口"/>
          </el-form-item>
          <el-form-item label="用户名" prop="user">
            <el-input size="small" style="width: 90%" v-model="databaseForm.user" placeholder="请输入数据库用户名"/>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input size="small" style="width: 90%" v-model="databaseForm.password" placeholder="请输入数据库密码"/>
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
          <el-button size="small" @click="editDatabaseVisible=false">取消</el-button>
          <el-button size="small" type="primary" class="title" @click="submitDatabaseForm('databaseForm', databaseForm)">保存</el-button>
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
      editDatabaseVisible: false,
      domainKeyTypes: [
        {id: "path", name:"路由匹配"},
        {id: "sign", name:"域名标识"}
      ],
      databaseTypes: [
        {id: "mysql", name:"MySQL数据库"},
        {id: "mssql", name:"SQL Server数据库"},
        {id: "pgsql", name:"PostgreSQL数据库"},
        {id: "oracle", name:"Oracle数据库"}
      ],
      domainSigns: [],
      currentEnvRow: null,
      environmentForm:{},
      domainForm:{},
      databaseForm:{},
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
        domainData: [{ required: true, message: '域名不能为空', trigger: 'blur' }],
        databaseType: [{ required: true, message: '数据库类型不能为空', trigger: 'blur' }],
        databaseKey: [{ required: true, message: '数据库名称不能为空', trigger: 'blur' }],
        host: [{ required: true, message: '数据库地址不能为空', trigger: 'blur' }],
        port: [{ required: true, message: '数据库端口不能为空', trigger: 'blur' }],
        user: [{ required: true, message: '数据库用户名不能为空', trigger: 'blur' }],
        password: [{ required: true, message: '数据库密码不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.$root.Bus.$emit('initBread', ["环境中心", "环境管理"]);
    this.getEnvironmentData(this.searchForm);
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
          data.list[i].data = [];
          data.list[i].type = "domain";
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
    getDomainData(row){
      let url = '/autotest/domain/list/' + row.id;
      this.$get(url, response => {
        let data = response.data;
        for(let i=0; i<data.length; i++){
          data[i].index = i+1;
        }
        row.data = data;
      });
    },
    getDatabaseData(row){
      let url = '/autotest/database/list/' + row.id;
      this.$get(url, response => {
        let data = response.data;
        for(let i=0; i<data.length; i++){
          data[i].index = i+1;
          data[i].host = data[i].info.host;
          data[i].port = data[i].info.port;
        }
        row.data = data;
      });
    },
    getDomainSigns(){
      let url = '/autotest/domainSign/list/' + this.$store.state.projectId;
      this.$get(url, response =>{
        this.domainSigns = response.data;
      });
    },
    changeShowType(row){
      if(row.type==='domain'){
          this.getDomainData(row);
        }else{
          this.getDatabaseData(row);
        }
    },
    expandSelect(row, expandedRows) {
      if(expandedRows.length != 0){
        this.changeShowType(row);
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
    // 新增域名或数据库
    addSetting(row){
      this.currentEnvRow = row;
      if(row.type==='domain'){
        this.title = "新增域名";
        this.domainForm = {
          domainKeyType: "sign",
          domainKey: "",
          environmentId: row.id
        };
        this.getDomainSigns();
        this.editDomainVisible = true;
      }else{
        this.title = "新增数据库";
        this.databaseForm = {
          databaseType: "mysql",
          databaseKey: "",
          host: "",
          port: "",
          user: "",
          password: "",
          environmentId: row.id
        };
        this.editDatabaseVisible = true;
      }
    },
    // 编辑域名或数据库
    editSetting(envRow, row){
      this.currentEnvRow = envRow;
      if(envRow.type==='domain'){
        this.title = "编辑域名";
        this.domainForm = {
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
      }else{
        this.title = "编辑数据库";
        this.databaseForm = {
          id: row.id,
          environmentId: row.environmentId,
          databaseType: row.databaseType,
          databaseKey: row.databaseKey,
          host: row.info.host,
          port: row.info.port,
          user: row.info.user,
          password: row.info.password,
          createUser: row.createUser,
          createTime: row.createTime
        };
        this.editDatabaseVisible = true;
      }
    },
    // 删除域名或数据库
    deleteSetting(envRow, row){
      this.$confirm('确定要删除'+(envRow.type==='domain'?'域名':'数据库')+'吗?', '删除提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
      })
      .then(() => {
          let url = '/autotest/'+ envRow.type+'/delete';
          this.$post(url, {id: row.id}, response => {
              this.$message.success("删除成功");
              this.changeShowType(envRow);
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
            this.getDomainData(this.currentEnvRow);
          });
        }else{
          return false;
        }
      });
    },
    submitDatabaseForm(confirm, form){
      this.$refs[confirm].validate(valid => {
        if (valid) {
          let url = '/autotest/database/save';
          form.info = {
            host: form.host,
            port: form.port,
            user: form.user,
            password: form.password,
          }
          this.$post(url, form, response =>{
            this.$message.success("保存成功");
            this.editDatabaseVisible = false;
            this.getDatabaseData(this.currentEnvRow);
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