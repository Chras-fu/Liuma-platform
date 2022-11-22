<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入群聊名称"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" v-if="showOpt" type="primary" icon="el-icon-plus" @click="addNotification">新增通知</el-button>
      </el-form-item>
    </el-form>

    <el-table size="small" :data="notificationData" v-loading="loading">
      <el-table-column prop="index" label="序号" align="center" width="50px"/>
      <el-table-column prop="name" label="群聊名称"/>
      <el-table-column prop="type" label="群聊类型">
        <template slot-scope="scope">
          {{getType(scope.row.type)}}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status==='enable'" size="small" type="success">启用</el-tag>
          <el-tag v-else size="small" type="info">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="150px"/>
      <el-table-column fixed="right" align="operation" label="操作" width="100px">
        <template slot-scope="scope">
          <el-button v-if="showOpt" type="text" size="mini" @click="editNotification(scope.row)">编辑</el-button>
          <el-button v-if="showOpt" type="text" size="mini" @click="deleteNotification(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 新增通知的弹窗 -->
    <el-dialog title="新增通知配置" :visible.sync="notificationVisible" width="600px" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 30px;" :model="notificationForm" :rules="rules" ref="notificationForm">
        <el-form-item label="通知渠道" prop="type">
          <el-select size="small" v-model="notificationForm.type" style="width:95%" placeholder="请选择通知渠道">
            <el-option v-for="item in notificationOptions" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="群聊名称" prop="name">
          <el-input size="small" style="width:95%" v-model="notificationForm.name" placeholder="请输入群聊名称"/>
        </el-form-item>
        <el-form-item label="启用状态" prop="status">
          <el-switch v-model="notificationForm.status" active-value="enable" inactive-value="disable"/>
        </el-form-item>
        <el-form-item label="webhook" prop="webhookUrl">
          <el-input size="small" style="width:95%" v-model="notificationForm.webhookUrl" placeholder="请输入webhook地址"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="notificationVisible=false">取消</el-button>
        <el-button size="small" type="primary" @click="submitNotification">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import Pagination from '@/views/common/components/pagination'
  import {timestampToTime} from '@/utils/util'

  export default {
    name: "notificationSetting",
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
      return {
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
        notificationVisible: false, 
        notificationForm: {},
        notificationData: [],
        notificationOptions: [
          {
            value: "Wechat",
            label: "企业微信"
          },
          {
            value: "Dingding",
            label: "钉钉"
          },
          {
            value: "Feishu",
            label: "飞书"
          }
        ],
        loading: false,
        rules: {
          type: [{required: true, message: '群聊类型不能为空', trigger: 'blur'}],
          name: [{required: true, message: '群聊名称不能为空', trigger: 'blur'}],
          webhookUrl: [{required: true, message: 'webhook地址不能为空', trigger: 'blur'}]
        },
      }
    },
    watch: {
        activeName(){
          if(this.activeName === "notification"){
            this.getData(this.searchForm);
          }
        }
    },
    methods: {
      getData(searchParam){
        let url = "/autotest/notification/list/" + searchParam.page + '/' + searchParam.limit;;
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
            this.notificationData = data.list;
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
      addNotification(){
        this.notificationForm = {
          type: "Wechat",
          status: "enable",
        };
        this.notificationVisible = true;
      },
      editNotification(row){
        this.notificationForm = {
          id: row.id,
          name: row.name,
          type: row.type,
          status: row.status,
          webhookUrl: row.webhookUrl,
          createTime: row.createTime
        };
        this.notificationVisible = true;
      },
      deleteNotification(row) {
        let text = "通知配置删除后 相关计划执行完成后无法通知 确定要删除通知配置吗";
        let url = "/autotest/notification/delete";
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
      submitNotification() {
        this.$refs["notificationForm"].validate(valid => {
            if (valid) {
                this.notificationForm.projectId = this.$store.state.projectId;
                this.notificationForm.params = this.setParams(this.notificationForm.type);
                let url = "/autotest/notification/save";
                this.$post(url, this.notificationForm, response =>{
                    this.$message.success("保存成功");
                    this.notificationVisible = false;
                    this.getData(this.searchForm);
                });
            }else{
                return false;
            }
        });
      },
      setParams(val){
        let params;
        switch (val){
          case 'Dingding':
            params = {
              msgtype: "markdown",
              markdown: {
                title: "流马测试计划执行结果通知",
                text: "#### {reportTitle}\n##### •  任务类型：{taskType}\n##### •  执行人: {user}\n##### •  总用例数: {caseNum}\n##### •  成功数: {caseSuccess}\n##### •  失败数：{caseFail}\n##### •  错误数：{caseError}\n##### •  测试成功率：{successPercent}\n##### •  测试执行时长: {executeTime}"
              },
              at: {
                isAtAll: true
              }
            }
            break;
          case 'Feishu':
            params = {
              msg_type: "text",
              content: {
                text: "{reportTitle}\n •  任务类型：{taskType}\n •  执行人: {user}\n •  总用例数: {caseNum}\n •  成功数: {caseSuccess}\n •  失败数：{caseFail}\n •  错误数：{caseError}\n •  测试成功率：{successPercent}\n •  测试执行时长: {executeTime}"
              }
            }
            break;
          case 'Wechat':
            params = {
              msgtype: "markdown",
              markdown: {
                content: "#### {reportTitle}\n##### •  任务类型：{taskType}\n##### •  执行人: {user}\n##### •  总用例数: {caseNum}\n##### •  成功数: {caseSuccess}\n##### •  失败数：{caseFail}\n##### •  错误数：{caseError}\n##### •  测试成功率：{successPercent}\n##### •  测试执行时长: {executeTime}"
              }
            }
            break;
          default:
            params = {};
        }
        return JSON.stringify(params);
      },
      getType(val){
        switch (val){
          case 'Dingding':
            return "钉钉";
          case 'Feishu':
            return "飞书";
          case 'Wechat':
            return "企业微信";
          default:
            return 
        }
      }
    }
  }
</script>

<style scoped>

</style>
