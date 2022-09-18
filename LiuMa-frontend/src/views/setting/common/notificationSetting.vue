<template>
  <div>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="">
        <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search"
                  placeholder="请输入通知渠道(例: 钉钉)"/>
      </el-form-item>
      <el-form-item>
        <el-button size="small" type="primary" @click="search">搜索</el-button>
        <el-button size="small" @click="reset">重置</el-button>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button size="small" v-if="showOpt" type="primary" icon="el-icon-plus" @click="addNotification">新增通知
        </el-button>
        <el-button size="small" @click="mydebug">debug</el-button>
      </el-form-item>
    </el-form>

    <el-table size="small" :data="tableData" v-loading="loading">
      <el-table-column prop="index" label="序号" align="center" width="50px"/>
      <el-table-column prop="projectName" label="项目名称" align="center" width="200px"/>
<!--      <el-table-column prop="channel" label="渠道名称" min-width="60px"/>-->
      <el-table-column prop="chatName" label="群聊名称" min-width="180px"/>
      <el-table-column prop="status" label="状态" min-width="50px"/>
      <el-table-column prop="createUser" label="创建者" min-width="60px"/>
      <el-table-column prop="updateUser" label="更新者" min-width="60px"/>
      <el-table-column prop="createTime" label="创建时间" min-width="65px"/>
      <el-table-column prop="updateTime" label="更新时间" width="70px"/>
      <el-table-column fixed="right" align="operation" label="操作" width="100px">
        <template slot-scope="scope">
          <el-button v-if="showOpt" type="text" size="mini" @click="editNotification(scope.row)">编辑</el-button>
          <el-button v-if="showOpt" type="text" size="mini" @click="deleteNotification(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增通知的弹窗 -->
    <el-dialog title="新增通知配置" :visible.sync="dialogNotifyVisible" width="600px" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 30px;" :model="notificationForm" :rules="rules" ref="notificationForm">
        <el-form-item label="项目名称" prop="projectName">
          <el-select v-model="notificationForm.projectId" placeholder="请选择项目名称">
            <el-option
              v-for="item in listUserProjectInfo"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>

        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="notificationForm.notifyType" placeholder="请选择通知渠道">
            <el-option
              v-for="item in notificationOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>

        </el-form-item>
        <el-form-item label="群聊名称" prop="chatName">
          <el-input size="small" style="width:95%" v-model="notificationForm.chatName" placeholder="请输入推送消息的群聊名称"/>
        </el-form-item>
        <el-form-item label="启用状态" prop="isUseSetting">
          <el-switch
            v-model="notificationForm.useStatus"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
        </el-form-item>
        <el-form-item label="webhook" prop="webhookUrl">
          <el-input size="small" style="width:95%" v-model="notificationForm.webhookUrl" placeholder="请输入webhook地址"/>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogNotifyVisible=false">取消</el-button>
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
        dialogNotifyVisible: false,  //控制添加配置弹窗的显示
        notificationForm: {
          projectId:"", //用户从下拉框中,选择的项目id
          notifyType:"",  //通知渠道,如:钉钉,飞书, 企业微信
          chatName:"", //群聊名称
          useStatus:true, //是否启用
          webhookUrl:"" //webhook地址
        },
        tableData: [], //页面table展示的数据
        isUseSetting:true,
        notificationOptions: [
          {
            value: "Dingding",
            label: "钉钉"
          },
          {
            value: "Feishu",
            label: "飞书"
          },
          {
            value: "Wechat",
            label: "企业微信"
          }
        ],
        loading: false,
        rules: {
          name: [{required: true, message: '群聊名称不能为空', trigger: 'blur'}],
          webhookUrl: [{required: true, message: 'webhook地址不能为空', trigger: 'blur'}]
        },
        listUserProjectInfo:[], //添加配置dialog中, 用户选择自己拥有项目的数据
        listUserProjectId:[], //添加配置dialog中, 用户选择自己拥有项目id的数据
        listUserProjectName:[] //项目名称列表
      }
    },
    created(){
      //调用获取用户project的接口
      this.queryUserProject()
      this.getAllNotificationSetting()
    },
    methods: {
      //获取用户project的接口
      queryUserProject(){
          //从localStorage中获取userId, 然后根据userId查询该用户拥有的项目
        let userId = JSON.parse(localStorage.getItem("userInfo"))["id"]
        // console.log("用户id是: " + userId);
        let url = "/autotest/project/user/" + userId
        // console.log("url: "+url);
        this.$get(url, response => {
          for (let i = 0; i < response.data.length; i++) {
            let dictProjectInfo = {}
            dictProjectInfo["name"] = response.data[i]["name"]
            dictProjectInfo["id"] = response.data[i]["id"]
            this.listUserProjectInfo.push(dictProjectInfo)

            //往列表中添加当前用户拥有的所有项目id
            this.listUserProjectId.push(response.data[i]["id"])
            this.listUserProjectName.push(response.data[i]["name"])
          }
        });

      },
      addNotification() {
        console.log("add notification");
        this.dialogNotifyVisible = true;
      },
      search() {
        console.log("search");
      },
      reset() {
        console.log("reset");
      },
      editNotification() {
        console.log("editNotification");
      },
      deleteNotification() {
        console.log("deleteNotification");
      },
      submitNotification() {
        console.log("submitNotification");
        let saveUrl = "/autotest/notice/save"
        let saveBody = {}
        saveBody.projectId = this.notificationForm.projectId
        saveBody.type = this.notificationForm.notifyType
        saveBody.name = this.notificationForm.chatName
        saveBody.params = {
          "msgtype": "markdown",
          "markdown": {
            "title": "{noticeTitle}",
            "text": "#### {reportTitle}\n##### •  任务类型：{taskType}\n##### •  操作者: {user}\n##### •  总用例数: {caseNum}\n##### •  成功数: {caseSucc}\n##### •  失败数：{caseFail}\n##### •  测试成功率：{succPercent}\n##### •  测试执行时间: {executeTime}"
          },
          "at": "personName" //暂未使用
        }
        if(this.notificationForm.useStatus === true ){
          saveBody.status = "1"
        }else {
          saveBody.status = "0"
        }
        saveBody.webhookUrl = this.notificationForm.webhookUrl
        this.$post(saveUrl, saveBody, response =>{
          console.log("发送添加配置请求");
          let res = response.data
          console.log(res);
        })
        this.dialogNotifyVisible = false


      },

      getAllNotificationSetting(){ //获取当前用户的所有消息通知的配置信息
        //this.listUserProjectId  当前用户拥有的所有项目id列表
        console.log("项目id列表: "+ this.listUserProjectId);
        for (let i = 0; i < this.listUserProjectId.length; i++) {
          // let bodyData = {"projectId":this.listUserProjectId[i]}
          let url = "/autotest/notice/query/all/notification?projectId=" + this.listUserProjectId[i]

          this.$get(url, response=>{
            // console.log("发送all请求");
            // console.log("response: "+ response.data)
            // console.log("message: "+ response.message)
            if (response.message === "成功"){
              let listNotification = response.data
              console.log("listNotification: " + listNotification);
              for (let j = 0; j < listNotification.length; j++) {
                console.log(listNotification[j]);
                let everyTableData = {}
                everyTableData["index"] = j + 1
                everyTableData["projectName"] = this.listUserProjectName[j]
                everyTableData["chatName"] = listNotification[j].name
                everyTableData["status"] = listNotification[j].status
                everyTableData["createUser"] = listNotification[j].createUser
                everyTableData["updateUser"] = listNotification[j].updateUser
                everyTableData["createTime"] = listNotification[j].createTime
                everyTableData["updateTime"] = listNotification[j].updateTime
                this.tableData.push(everyTableData)

              }

            }



          })
        }
        console.log("table数据: " + this.tableData)




      },

      mydebug(){
        // console.log("调试: "+this.notificationForm.useStatus);
        this.getAllNotificationSetting()
        console.log("table数据: " + this.tableData)
      }
    }
  }
</script>

<style scoped>

</style>
