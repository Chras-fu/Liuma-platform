<template>
  <el-container class="index-con">
    <el-aside :class="showclass">
      <leftnav></leftnav>
    </el-aside>
    <el-container class="main-con">
      <el-header class="index-header">
        <navcon :breadList="breadList" ></navcon>
      </el-header>
      <el-main class="index-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
// 导入组件
import navcon from './common/components/navcon.vue'
import leftnav from './common/components/leftnav.vue'
export default {
  name: 'index',
  data() {
    return {
      showclass: 'aside',
      breadList:[]
    }
  },
  // 注册组件
  components: {
    navcon,
    leftnav
  },
  methods: {},
  created() {
    // 监听
    this.$root.Bus.$on('toggle', value => {
      if (value) {
        this.showclass = 'asideshow'
      } else {
        setTimeout(() => {
          this.showclass = 'aside'
        }, 300)
      }
    });
    this.$root.Bus.$on('initBread', breadList=>{
      this.breadList = breadList
    });
  },
  beforeCreate() {
    this.$store.commit('set_token', localStorage.getItem('token'));
    this.$store.commit('set_userInfo', JSON.parse(localStorage.getItem('userInfo')));
    this.$store.commit('set_project', JSON.parse(localStorage.getItem('userInfo')).lastProject);
    this.$store.commit('set_permission', JSON.parse(localStorage.getItem('userInfo')).permissions);
  }
}
</script>
<style >
.index-con {
  height: 100%;
  width: 100%;
  box-sizing: border-box;
}
.aside {
  width: 48px !important;
  height: 100%;
  background-color: #334157;
  margin: 0px;
}
.asideshow {
  width: 200px !important;
  height: 100%;
  background-color: #334157;
  margin: 0px;
}
.index-header{
  max-height: 40px;
}
.main-card{
  min-height: 100%;
  border: none;
}
</style>
