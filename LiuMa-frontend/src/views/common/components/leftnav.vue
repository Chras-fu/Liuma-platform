/**
* 左边菜单
*/
<template>
  <div class="left-menu">
  <el-menu :collapse="collapsed" collapse-transition router unique-opened class="el-menu-vertical-demo" background-color="#334157" text-color="#fff" active-text-color="#ffd04b">
    <div class="logobox" @click="toggle(collapsed)">
      <img class="logoimg" src="../../../assets/img/logo.png" alt="">
      <span :class="pftitle">流马测试平台</span>
    </div>
    <el-submenu v-for="menu in allmenu" :key="menu.id" :index="menu.name" class="el-menu-item-demo">
      <template slot="title">
        <i class="iconfont" :class="menu.icon"></i>
        <span>{{menu.name}}</span>
      </template>
      <el-menu-item-group>
        <el-menu-item v-for="chmenu in menu.menus" :index="chmenu.path" :key="chmenu.id">
          <template slot="title">
            <i class="iconfont" :class="chmenu.icon"></i>
            <span>{{chmenu.name}}</span>
          </template>
        </el-menu-item>
      </el-menu-item-group>
    </el-submenu>
  </el-menu>
  </div>
</template>
<script>
export default {

  name: 'leftnav',
  data() {
    return {
      pftitle: 'title-hidden',
      collapsed: true,
      allmenu: []
    }
  },
  // 创建完毕状态(里面是操作)
  created() {
    this.getMenus();
  },
  methods:{
    // 获取菜单
    getMenus() {
      let url = "/autotest/menu/list?userId="+ this.$store.state.userInfo.id + "&projectId=" + this.$store.state.projectId;
      this.$get(url, response => {
        this.allmenu = response.data;  
      });
    },
    // 切换显示
    toggle(showtype) {
      this.collapsed = !showtype;
      if (showtype) {
        this.pftitle = 'title-show';
      } else {
        this.pftitle = 'title-hidden';
      }
      this.$root.Bus.$emit('toggle', showtype);
    }
  }
}
</script>
<style>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
.el-menu-vertical-demo{
  width: 48px;
}
.el-menu-item-demo .el-submenu__title{
  height: 48px;
  line-height: 48px;
  padding-left: 16px !important;
}
.el-menu-item-group__title {
  padding: 0px;
}
.el-menu-bg {
  background-color: #1f2d3d !important;
}
.el-menu {
  border: none;
}
.logobox {
  height: 32px;
  font-size: 16px;
  text-align: center;
  padding: 10px 0px;
  white-space:nowrap;
}
.logoimg {
  color: #ffffff;
  height: 28px;
  vertical-align: middle;
}
.title-hidden{
  vertical-align: middle;
  height: 0;
  width: 0;
  display: none;
}
.title-show{
  vertical-align: middle;
  height:20px;
  margin-left: 10px;
  margin-right: 20px;
  color: rgb(255, 255, 255);
}
</style>
