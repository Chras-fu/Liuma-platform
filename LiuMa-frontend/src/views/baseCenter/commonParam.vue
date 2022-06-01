/**
 * 公共组件  公共参数
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入参数名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right" v-if="activeName==='custom'">
          <el-button size="small" type="primary" icon="el-icon-plus" @click="addCustomeParam">新增自定义参数</el-button>
      </el-form-item>
    </el-form>
    <el-tabs v-model="activeName" style="magin-left: -10px">
      <el-tab-pane label="系统参数" name="system">
        <!-- 系统参数组表 -->
        <el-table size="small" v-loading="systemClassLoading" :data="systemClassList" @expand-change="expandSelect" row-key="index" :expand-row-keys="expands">
          <el-table-column type="expand" width="40px">
            <template slot-scope="props">
              <div style="padding-left: 40px">
                <!-- 系统参数数据表 -->
                <el-table size="mini" :data="props.row.systemParamList">
                  <el-table-column label="序号" prop="index" width="50px" align="center"/>
                  <el-table-column label="参数名" prop="name"/>
                  <el-table-column label="参数类型" prop="dataType"/>
                  <el-table-column label="参数值" prop="paramData" min-width="150px"/>
                  <el-table-column label="参数描述" prop="description" min-width="150px"/>
                  <el-table-column label="创建人" prop="username"/>
                  <el-table-column label="更新时间" prop="updateTime" width="150px"/>
                  <el-table-column fixed="right" align="operation" label="操作" width="100px">
                    <template slot-scope="scope">
                      <el-button type="text" size="mini" @click="editParam(scope.row)">编辑</el-button>
                      <el-button type="text" size="mini" @click="deleteParam(props.$index, scope.row)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <!-- 分页组件 -->
              <Pagination style="float: right;" size="mini" v-bind:child-msg="props.row.pageparam" @callFather="systemParamCallFather($event, props.$index)"/>
            </template>
          </el-table-column>
          <el-table-column label="序号" prop="index" width="50px" align="center"/>
          <el-table-column label="系统参数名" prop="name"/>
          <el-table-column label="系统参数描述" prop="description"/>
          <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="addSystemParam(scope.row)">新增系统参数</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="自定义参数" name="custom">
        <!-- 自定义参数数据表 -->
        <el-table size="small" :data="customParamList" v-loading="customParamLoading">
          <el-table-column label="序号" prop="index" width="50px" align="center"/>
            <el-table-column label="参数名" prop="name"/>
            <el-table-column label="参数类型" prop="dataType"/>
            <el-table-column label="参数值" prop="paramData" min-width="150px"/>
            <el-table-column label="参数描述" prop="description" min-width="150px"/>
            <el-table-column label="创建人" prop="username"/>
            <el-table-column label="更新时间" prop="updateTime" width="150px"/>
            <el-table-column fixed="right" align="operation" label="操作" width="100px">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="editParam(scope.row)">编辑</el-button>
                <el-button type="text" size="mini" @click="deleteParam(scope.$index, scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        <!-- 分页组件 -->
        <Pagination size="small" v-bind:child-msg="customPageparam" @callFather="customParamCallFather"/>
      </el-tab-pane>
    </el-tabs>
    <!-- 添加参数界面 -->
    <el-dialog :title="title" :visible.sync="editParamVisible" width="600px" destroy-on-close>
      <el-form label-width="120px" style="padding-right: 30px;" :model="editParamForm" :rules="rules" ref="editParamForm">
          <el-form-item label="参数名" prop="name">
            <el-input size="small" style="width: 90%" v-model="editParamForm.name" placeholder="请输入参数名"/>
          </el-form-item>
          <el-form-item label="参数类型" prop="dataType">
            <el-select size="small" style="width: 90%" v-model="editParamForm.dataType" placeholder="请选择参数类型">
              <el-option v-for="item in dataTypes" :key="item" :label="item" :value="item"/>
            </el-select>
          </el-form-item>
          <el-form-item label="参数值" prop="paramData">
            <el-input size="small" style="width: 90%" :autosize="{ minRows: 6}" type="textarea" clearable placeholder="请输入参数值" v-model="editParamForm.paramData"/>
          </el-form-item>
          <el-form-item label="参数描述" prop="description">
            <el-input size="small" style="width: 90%" :autosize="{ minRows: 4}" type="textarea" clearable placeholder="请输入参数描述" v-model="editParamForm.description" maxlength="200" show-word-limit/>
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
          <el-button size="small" @click="editParamVisible=false">取消</el-button>
          <el-button size="small" type="primary" @click="saveParam('editParamForm', editParamForm)">保存</el-button>
      </div> 
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import {timestampToTime} from '@/utils/util'
export default {
  components: { Pagination },
  data() {
    return{
      title: "新增参数",
      dataTypes:["String", "Int", "Float", "Boolean", "JSONObject", "JSONArray"],
      activeName: "system",
      editParamVisible: false,
      editParamForm:{},
      systemClassLoading: false,
      customParamLoading: false,
      systemClassList:[],
      customParamList:[],
      expands: [], // 默认展开行
      customClassId:"", // 自定义参数组的id
      searchForm: {
        condition: ""
      },
      customPageparam: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      rules: {
          name: [{ required: true, message: '参数名称不能为空', trigger: 'blur' }],
          dataType: [{ required: true, message: '参数类型不能为空', trigger: 'blur' }],
          paramData: [{ required: true, message: '参数值不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    // 加载面包屑
    this.$root.Bus.$emit('initBread', ["公共组件", "公共参数"]);
    this.getSystemClassData();
  },
  methods: {
    getSystemClassData() {
      this.systemClassLoading = true;
      this.customParamLoading = true;
      let url = '/autotest/commonParam/group/list/' + this.$store.state.projectId;
      this.$get(url, response => {
        let data = response.data;
        this.systemClassLoading = false;
        let systemIndex = 1;
        for(let i =0; i<data.length; i++){
          if(data[i].paramType === "custom"){
            this.customClassId = data[i].id; 
            this.getCustomParamData();  // 初始化自定义数据
          }else{
            data[i].index = systemIndex;
            systemIndex += 1;
            data[i].systemParamList = [];
            data[i].pageparam = {
              currentPage: 1,
              pageSize: 10,
              total: 0
            };
            this.systemClassList.push(data[i]);
          }
        }
      });
    },
    getSystemParamData(index) {
      let systemClassId = this.systemClassList[index].id;
      let currentPage = this.systemClassList[index].pageparam.currentPage;
      let pageSize = this.systemClassList[index].pageparam.pageSize;
      let url = '/autotest/commonParam/param/' + systemClassId + '/list/' + currentPage + '/' + pageSize;
      let param = {
          condition: this.searchForm.condition
      };
      this.$post(url, param, response => {
        let data = response.data;
        for(let i=0; i<data.list.length; i++){
          data.list[i].index = (currentPage-1) * pageSize + i+1;
          data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
        }
        this.systemClassList[index].systemParamList = data.list;
        // 分页赋值
        this.systemClassList[index].pageparam.total = data.total;
      });
    },
    getCustomParamData() {
      this.customParamLoading = true;
      let url = '/autotest/commonParam/param/' + this.customClassId + '/list/' + this.customPageparam.currentPage + '/' + this.customPageparam.pageSize;
      let param = {
          condition: this.searchForm.condition
      };
      this.$post(url, param, response => {
        let data = response.data;
        for(let i=0; i<data.list.length; i++){
          data.list[i].index = (this.customPageparam.currentPage -1) * this.customPageparam.pageSize + i+1;
          data.list[i].updateTime = timestampToTime(data.list[i].updateTime);
        }
        this.customParamList = data.list;
        this.customParamLoading = false;
        // 分页赋值
        this.customPageparam.total = data.total;
      });
    },
    expandSelect(row, expandedRows) {
      if(expandedRows.length != 0){
        this.getSystemParamData(row.index-1);
      }
    },
    search() {
      if(this.activeName == "system"){
        for(let i=0;i<this.systemClassList.length;i++){
          this.systemClassList[i].pageparam.currentPage = 1;
          this.systemClassList[i].pageparam.pageSize = 10;
          this.getSystemParamData(i);
          this.expands.push(i+1);
        }
      }else{
        this.getCustomParamData();
      }
    },
    reset() {
      this.searchForm.condition = "";
      if(this.activeName == "system"){
        for(let i=0;i<this.systemClassList.length;i++){
          this.getSystemParamData(i);
        }
        this.expands.splice(0, this.expands.length);
      }else{
        this.getCustomParamData();
      }
    },
    systemParamCallFather(param, index){
      this.systemClassList[index].pageparam.currentPage = param.currentPage;
      this.systemClassList[index].pageparam.pageSize = param.pageSize;
      this.getSystemParamData(index);
    },
    customParamCallFather(param){
      this.customPageparam.currentPage = param.currentPage;
      this.customPageparam.pageSize = param.pageSize;
      this.getCustomParamData();
    },
    addSystemParam(row){
      this.editParamForm = {
        id: "",
        name: "",
        dataType: "",
        paramData: "",
        description: "",
        groupId: row.id
      };
      this.title = "新增系统参数";
      this.editParamVisible = true;
    },
    addCustomeParam(){
      this.editParamForm = {
        id: "",
        name: "",
        dataType: "",
        paramData: "",
        description: "",
        groupId: this.customClassId
      };
      this.title = "新增自定义参数";
      this.editParamVisible = true;
    },
    deleteParam(index, row){
      this.$confirm('确定要删除参数吗?', '删除提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
      })
      .then(() => {
          let url = '/autotest/commonParam/param/delete';
          this.$post(url, {id: row.id}, response => {
            this.$message.success("删除成功");
            if(this.activeName == "system"){
              this.getSystemParamData(index);
            }else{
              this.getCustomParamData();
            }
          });
      })
      .catch(() => {
          this.$message.success("取消成功");
      })
    },
    editParam(row){
      this.editParamForm = {
        id: row.id,
        name: row.name,
        dataType: row.dataType,
        paramData: row.paramData,
        description: row.description,
        groupId: row.groupId,
        createUser: row.createUser,
        createTime: row.createTime
      };
      this.title = "编辑参数";
      this.editParamVisible = true;
    },
    saveParam(confirm, form){
      this.$refs[confirm].validate(valid => {
        if (valid) {
          let url = '/autotest/commonParam/param/save';
          this.$post(url, form, response =>{
            this.$message.success("保存成功");
            if(this.activeName == "system"){
              for(let i=0;i<this.systemClassList.length;i++){
                if(this.systemClassList[i].id == form.groupId){
                  this.getSystemParamData(i);
                  break;
                }
              }
            }else{
              this.getCustomParamData();
            }
            this.editParamVisible = false;
          });
        }else{
          return false;
        }
      });
    },
  }
}
</script>

<style scoped>

</style>