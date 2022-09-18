/**
 * 设置中心  域名标识设置
 */
<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入标识名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" v-if="showOpt" type="primary" icon="el-icon-plus" @click="addDomainSign">新增标识</el-button>
      </el-form-item>
    </el-form>
    <el-table size="small" :data="domainSignData" v-loading="loading">
        <el-table-column prop="index" label="序号" align="center" width="50px"/>
        <el-table-column prop="name" label="标识名称"/>
        <el-table-column prop="description" label="标识说明" min-width="200px"/>
        <el-table-column prop="updateTime" label="更新时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button v-if="showOpt" type="text" size="mini" @click="editDomainSign(scope.row)">编辑</el-button>
                <el-button v-if="showOpt" type="text" size="mini" @click="deleteDomainSign(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 新增标识弹窗 -->
    <el-dialog title="新增标识" :visible.sync="domainSignVisible" width="600px" destroy-on-close>
        <el-form label-width="100px" style="padding-right: 30px;" :model="domainSignForm" :rules="rules" ref="domainSignForm">
            <el-form-item label="标识名称" prop="name">
                <el-input size="small" style="width:95%" v-model="domainSignForm.name" placeholder="请输入标识名称"/>
            </el-form-item>
            <el-form-item label="标识描述" prop="description">
                <el-input size="small" style="width:95%" v-model="domainSignForm.description" type="textarea" 
                  :autosize="{ minRows: 3}" clearable placeholder="请输入标识描述" maxlength="200" show-word-limit/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="domainSignVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitDomainSign">确定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/views/common/components/pagination'
import {timestampToTime} from '@/utils/util'
export default {
    name: "DomainSignSetting",
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
          loading: false,
          domainSignData: [],
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
          domainSignVisible: false,
          domainSignForm: {},
          rules: {
              name: [{ required: true, message: '标识名称不能为空', trigger: 'blur' }],
              description: [{ required: true, message: '标识描述不能为空', trigger: 'blur' }]
          }
        }
    },
    watch: {
        activeName(){
          if(this.activeName === "domainSign"){
            this.getData(this.searchForm);
          }
        }
    },
    methods: {
      getData(searchParam){
        let url = "/autotest/domainSign/list/" + searchParam.page + '/' + searchParam.limit;;
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
            this.domainSignData = data.list;
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
      addDomainSign(){
        this.domainSignForm = {};
        this.domainSignVisible = true;
      },
      editDomainSign(row){
        this.domainSignForm = {
          id: row.id,
          name: row.name,
          description: row.description,
          createTime: row.createTime
        };
        this.domainSignVisible = true;
      },
      deleteDomainSign(row){
        let text = "域名标识删除后导致用例无法获取执行环境 确定要删除域名标识吗?";
        let url = "/autotest/domainSign/delete";
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
      submitDomainSign(){
        this.$refs["domainSignForm"].validate(valid => {
            if (valid) {
                this.domainSignForm.projectId = this.$store.state.projectId;
                let url = "/autotest/domainSign/save";
                this.$post(url, this.domainSignForm, response =>{
                    this.$message.success("保存成功");
                    this.domainSignVisible = false;
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
