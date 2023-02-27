/**
 * 环境中心  设备控制
 */
<template>
  <div>
      <android-remote v-if="system==='android'" :deviceId="deviceId"/>
      <apple-remote v-if="system==='apple'" :deviceId="deviceId"/>
  </div>
</template>

<script>
import AndroidRemote from './common/androidRemote'
import AppleRemote from './common/appleRemote'
export default {
    components: {
        AndroidRemote, AppleRemote
    },
    data() {
        return{
            deviceId: null,
            system: null
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["环境中心", "设备控制"]);
        this.currentUser = this.$store.state.userInfo.id;
        this.deviceId = this.$route.params.deviceId;
        this.system = this.$route.params.system;
    }
}
</script>

<style scoped>

</style>