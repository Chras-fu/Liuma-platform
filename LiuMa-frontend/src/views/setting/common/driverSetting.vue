/**
 * 设置中心  驱动配置
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入驱动名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" v-if="showOpt" type="primary" icon="el-icon-plus" @click="addDriver">新增驱动</el-button>
      </el-form-item>
    </el-form>
    <el-table size="small" :data="driverData" v-loading="loading">
        <el-table-column prop="index" label="序号" align="center" width="50px"/>
        <el-table-column prop="name" label="驱动名称"/>
        <el-table-column prop="description" label="驱动说明" min-width="180px"/>
        <el-table-column prop="updateTime" label="更新时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="editDriver(scope.row)">编辑</el-button>
                <el-button v-if="showOpt" type="text" size="mini" @click="deleteDriver(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 新增驱动弹窗 -->
    <el-dialog title="新增驱动" :visible.sync="driverVisible" width="600px" destroy-on-close>
        <el-form label-position='top' label-width="100px" :model="driverForm" :rules="rules" ref="driverForm">
            <el-form-item label="驱动名称" prop="name">
                <el-input size="small" style="width:100%" v-model="driverForm.name" placeholder="请输入驱动名称"/>
            </el-form-item>
            <el-form-item label="文件地址">
                <el-input size="small" style="width:100%" v-model="driverForm.setting.binary" placeholder="设置Chrome二进制文件位置(binary_location) 非必填"/>
            </el-form-item>
            <el-form-item label="启动参数">
                <el-row v-for="(item, index) in driverForm.setting.arguments" :key="index">
                    <el-col :span="22">
                        <el-input size="small" style="width:100%" v-model="item.value" placeholder="添加启动参数(add_argument) 非必填"/>
                    </el-col>
                    <el-col :span="2">
                        <div style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                            <i class="el-icon-circle-plus lm-success" @click="addItem(index, 'arg')"></i>
                            <i class="el-icon-remove lm-error" @click="deleteItem(index, 'arg')"></i>
                        </div>
                    </el-col>
                </el-row>
                <div v-if="driverForm.setting.arguments.length===0" style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                    <i class="el-icon-circle-plus lm-success" @click="addItem(-1, 'arg')"></i>
                </div>
            </el-form-item>
            <el-form-item label="设置参数">
                <el-row v-for="(item, index) in driverForm.setting.experimentals" :key="index">
                    <el-col :span="6">
                        <el-input size="small" style="width:98%" v-model="item.name" placeholder="设置名称"/>
                    </el-col>
                    <el-col :span="5">
                        <el-select size="small" style="width:98%" v-model="item.type" placeholder="数据类型">
                            <el-option v-for="t in types" :key="t" :label="t" :value="t"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="11">
                        <el-input size="small" style="width:100%" v-model="item.value" placeholder="实验性质的设置参数(add_experimental_option)"/>
                    </el-col>
                    <el-col :span="2">
                        <div style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                            <i class="el-icon-circle-plus lm-success" @click="addItem(index, 'exp')"></i>
                            <i class="el-icon-remove lm-error" @click="deleteItem(index, 'exp')"></i>
                        </div>
                    </el-col>
                </el-row>
                <div v-if="driverForm.setting.experimentals.length===0" style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                    <i class="el-icon-circle-plus lm-success" @click="addItem(-1, 'exp')"></i>
                </div>
            </el-form-item>
            <el-form-item label="扩展应用">
                <el-row v-for="(item, index) in driverForm.setting.extensions" :key="index">
                    <el-col :span="22">
                        <el-input size="small" style="width:100%" v-model="item.value" placeholder="添加扩展应用Base64(add_encoded_extension) 非必填"/>
                    </el-col>
                    <el-col :span="2">
                        <div style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                            <i class="el-icon-circle-plus lm-success" @click="addItem(index, 'ext')"></i>
                            <i class="el-icon-remove lm-error" @click="deleteItem(index, 'ext')"></i>
                        </div>
                    </el-col>
                </el-row>
                <div v-if="driverForm.setting.extensions.length===0" style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                    <i class="el-icon-circle-plus lm-success" @click="addItem(-1, 'ext')"></i>
                </div>
            </el-form-item>
            <el-form-item label="扩展文件">
                <el-row v-for="(item, index) in driverForm.setting.files" :key="index">
                    <el-col :span="22">
                        <el-input size="small" style="width:100%" v-model="item.value" placeholder="添加扩展应用文件(add_extension) 非必填"/>
                    </el-col>
                    <el-col :span="2">
                        <div style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                            <i class="el-icon-circle-plus lm-success" @click="addItem(index, 'file')"></i>
                            <i class="el-icon-remove lm-error" @click="deleteItem(index, 'file')"></i>
                        </div>
                    </el-col>
                </el-row>
                <div v-if="driverForm.setting.files.length===0" style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                    <i class="el-icon-circle-plus lm-success" @click="addItem(-1, 'file')"></i>
                </div>
            </el-form-item>
            <el-form-item label="驱动描述" prop="description">
                <el-input size="small" style="width:100%" v-model="driverForm.description" type="textarea" 
                  :autosize="{ minRows: 3}" clearable placeholder="请输入驱动描述" maxlength="200" show-word-limit/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="driverVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitDriver">确定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/views/common/components/pagination'
import {timestampToTime} from '@/utils/util'
export default {
    name: "DriverSetting",
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
          driverData: [],
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
          driverVisible: false,
          driverForm: {
            setting: {
              arguments: [],
              experimentals: [],
              extensions: [],
              files: [],
              binary: ""
            },
            name: "",
            description: ""
          },
          types: ["String", "Int", "Float", "Boolean", "JSONObject", "JSONArray"],
          rules: {
              name: [{ required: true, message: '驱动名称不能为空', trigger: 'blur' }]
          }
        }
    },
    watch: {
        activeName(){
          if(this.activeName === "driver"){
            this.getData(this.searchForm);
          }
        }
    },
    methods: {
      getData(searchParam){
        let url = "/autotest/driver/list/" + searchParam.page + '/' + searchParam.limit;;
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
            this.driverData = data.list;
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
      addDriver(){
        this.driverForm = {
            setting: {
                arguments: [{value:""}],
                experimentals: [{name:"",type:"String",value:""}],
                extensions: [{value:""}],
                files: [{value:""}],
                binary: ""
            },
            name: "",
            description: ""
        };
        this.isEdit = false;
        this.driverVisible = true;
      },
      editDriver(row){
        this.driverForm = {
          id: row.id,
          name: row.name,
          setting: JSON.parse(row.setting),
          description: row.description,
          createTime: row.createTime
        };
        this.isEdit = true;
        this.driverVisible = true;
      },
      addItem(index, type){
          switch(type){
            case 'arg':
              this.driverForm.setting.arguments.splice(index+1, 0, {value:""}); 
              break;
            case 'exp':
              this.driverForm.setting.experimentals.splice(index+1, 0, {name:"",type:"String",value:""});
              break; 
            case 'ext':
              this.driverForm.setting.extensions.splice(index+1, 0, {value:""}); 
              break;
            case 'file':
              this.driverForm.setting.files.splice(index+1, 0, {value:""}); 
              break;
            default:
              return;
          }
      },
      deleteItem(index, type){
          switch(type){
            case 'arg':
              this.driverForm.setting.arguments.splice(index, 1); 
              break;
            case 'exp':
              this.driverForm.setting.experimentals.splice(index, 1);
              break; 
            case 'ext':
              this.driverForm.setting.extensions.splice(index, 1); 
              break;
            case 'file':
              this.driverForm.setting.files.splice(index, 1); 
              break;
            default:
              return;
          }
      },
      deleteDriver(row){
        let text = "驱动配置删除后 WEB用例无法使用该驱动配置 影响用例执行 确定要删除驱动吗";
        let url = "/autotest/driver/delete";
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
      submitDriver(){
        this.$refs["driverForm"].validate(valid => {
            if (valid) {
                this.driverForm.projectId = this.$store.state.projectId;
                let data = JSON.parse(JSON.stringify(this.driverForm));
                data.setting = JSON.stringify(this.driverForm.setting);
                let url = "/autotest/driver/save";
                this.$post(url, data, response =>{
                    this.$message.success("保存成功");
                    this.driverVisible = false;
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
