/**
 * 设置中心  系统设置
 */
<template>
  <div>
    <el-tabs v-model="activeName" tab-position="left">
      <el-tab-pane label="域名标识" name="domainSign">
        <domain-sign-setting :showOpt="showOpt" :activeName="activeName"></domain-sign-setting>
      </el-tab-pane>
      <el-tab-pane label="迭代版本" name="version">
        <version-setting :showOpt="showOpt" :activeName="activeName"></version-setting>
      </el-tab-pane>
      <el-tab-pane label="应用信息" name="application">
        <application-setting :showOpt="showOpt" :activeName="activeName"></application-setting>
      </el-tab-pane>
      <el-tab-pane label="通知配置" name="notification">
        <notification-setting :showOpt="showOpt" :activeName="activeName"></notification-setting>
      </el-tab-pane>
      <el-tab-pane label="驱动配置" name="driver">
        <driver-setting :showOpt="showOpt" :activeName="activeName"></driver-setting>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import VersionSetting from './common/versionSetting'
import DomainSignSetting from './common/domainSignSetting'
import ApplicationSetting from './common/applicationSetting'
import NotificationSetting from './common/notificationSetting'
import DriverSetting from './common/driverSetting'

export default {
    components: {
      VersionSetting, DomainSignSetting, ApplicationSetting, NotificationSetting, DriverSetting
    },
    data() {
        return{
          activeName: null,
          showOpt: false
        }
    },
    created(){
      this.$root.Bus.$emit('initBread', ["设置中心", "系统设置"]);
      this.getOptPerm();
    },
    methods: {
      getOptPerm() {
        let url = "/autotest/setting/permission?userId="+ this.$store.state.userInfo.id + "&projectId=" + this.$store.state.projectId;
        this.$get(url, response => {
          this.showOpt = response.data;
          this.activeName = "domainSign";
        });
      },
    }
}
</script>

<style scoped>

</style>
