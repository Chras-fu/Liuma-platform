/**
* 头部菜单
*/
<template>
  <el-menu class="el-menu-demo" mode="horizontal" background-color="#fff" text-color="#000">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator-class="el-icon-arrow-right" size="small" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/home/dashboard' }"><i class="iconfont icon-icon-test"
          style="font-size:14px"></i> 首页</el-breadcrumb-item>
      <el-breadcrumb-item v-for="(name, index) in this.breadList" :key="index">
        {{name}}
      </el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 用户菜单栏 -->
    <el-dropdown size="small" @command="handleCommand" class="align-right">
      <span class="dropdown-user">
          <i class="iconfont icon-yonghuzhongxin" style="font-size: 24px;color: #b0b2b6"/>
          <span class="user-name">{{user.username}}</span>
      </span>
      <template v-slot:dropdown>
        <el-dropdown-menu style="font-size:14px">
          <el-dropdown-item command="personal">个人中心</el-dropdown-item>
          <el-dropdown-item command="help">使用手册</el-dropdown-item>
          <el-dropdown-item command="follow">关注我们</el-dropdown-item>
          <el-dropdown-item command="logout">退出</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <!-- 配置中心 -->
    <el-button size="small" class="align-right" type="text" style="margin-right: 15px" @click="openSetting">
      <i class="el-icon-s-tools" style="color: #606266"> 配置中心</i>
    </el-button>
    <!-- 项目选择栏 -->
    <el-dropdown size="small" @command="changeProject" placement="bottom" class="align-right">
      <span class="dropdown-proj">
        {{ currentProject.name }}<i class="el-icon-caret-bottom el-icon--right"/>
      </span>
      <template v-slot:dropdown>
        <el-dropdown-menu>
          <div class="dropdown-content">
            <el-dropdown-item :command="item" v-for="(item, index) in projectList" :key="index">
              {{ item.name }}
              <i class="el-icon-check" v-if="currentProject.id === item.id"/>
            </el-dropdown-item>
          </div>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    
    <!-- 公众号图片 -->
    <el-image-viewer v-if="showQR" :on-close="()=>{showQR=false}" :url-list="[qrUrl]"/>
  </el-menu>
</template>
<script>
export default {
  name: 'navcon',
  props:["breadList"],
  components: {
      'el-image-viewer': () => import('element-ui/packages/image/src/image-viewer')
  },
  data() {
    return {
      currentProject: {},
      projectList:[],
      user: {},
      showQR: false,
      qrUrl: require('../../../assets/img/qr.png')
    }
  },
  created() {
    this.user = JSON.parse(localStorage.getItem('userInfo'));
    this.getProjectInfo(this.user.id);
  },
  methods: {
    exit() {
      // 退出登录
      this.$confirm('退出登录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      .then(() => {
        setTimeout(() => {
          this.$store.commit('del_userInfo');
          this.$store.commit('del_token');
          this.$router.push({ path: '/login', query:{ redirect: this.$router.currentRoute.fullPath } });
          this.$message.success('已退出登录');
        }, 1000);
      })
      .catch(() => {
        this.$message.info('已取消');
      })
    },
    getProjectInfo(userId){
      let url = '/autotest/project/user/' + userId;
      this.$get(url, response =>{
        this.projectList = response.data;
        for(let i=0;i<this.projectList.length;i++){
          if(this.user.lastProject === this.projectList[i].id){
            this.currentProject = this.projectList[i];
            break;
          }
        }
      });
    },
    handleCommand(command) {
      switch (command) {
        case "personal":
          this.$router.push({path: '/setting/userSetting'});
          break;
        case "help":
          window.open("https://docs.qq.com/doc/p/1e36932d41b40df896c1627a004068df9a28fc3f");
          break;
        case "follow":
          this.showQR = true;
          break;
        case "logout":
          this.exit();
          break;
        default:
          break;
      }
    },
    openSetting() {
      this.$router.push({path: '/setting/systemSetting'});
    },
    changeProject(command){
      if(command.id != this.currentProject.id){
        let url = '/autotest/user/switch/project';
        let data = {
          id: this.user.id,
          lastProject: command.id
        }
        this.currentProject = command;
        this.$post(url, data, response =>{
          this.user = response.data;
          localStorage.setItem('userInfo', JSON.stringify(this.user));
          this.$router.push({path: '/home/dashboard'});
          setTimeout(() => {
            location.reload();
          }, 100)
        });
      }
    }
  }
}
</script>
<style scoped>
.breadcrumb{
  float: left;
  padding: 13px 0px;
}
.dropdown-user {
  cursor: pointer;
  font-size: 14px;
  line-height: 40px;
  display: flex;
}
.user-name{
  margin-left: 5px;
  min-width: 40px;
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space:nowrap;
}
.dropdown-proj {
  cursor: pointer;
  font-size: 14px;
  color: #334157;
  line-height: 40px;
  margin-right: 20px;
}
.align-right {
  float: right;
  height: 40px;
}
</style>
