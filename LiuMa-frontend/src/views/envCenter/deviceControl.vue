/**
 * 环境中心  设备控制
 */
<template>
  <div>
      <android-remote v-if="system==='android'" :serial="serial"/>
      <apple-remote v-if="system==='apple'" :serial="serial"/>
  </div>
</template>

<script>
import AndroidRemote from './device/androidRemote'
import AppleRemote from './device/appleRemote'
export default {
    components: {
        AndroidRemote, AppleRemote
    },
    data() {
        return{
            serial: null,
            system: null
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["环境中心", "设备控制"]);
        this.currentUser = this.$store.state.userInfo.id;
        this.serial = this.$route.params.serial;
        this.system = this.$route.params.system;
    }
}
</script>

<style scoped>

</style>