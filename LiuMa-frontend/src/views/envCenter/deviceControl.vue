/**
 * 环境中心  设备控制
 */
<template>
  <div>
      <android-remote v-if="device.system==='android'" :device="device"/>
      <apple-remote v-if="device.system==='apple'" :device="device"/>
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
            device: {
                system: "android"
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["环境中心", "设备控制"]);
        this.currentUser = this.$store.state.userInfo.id;
        this.serial = this.$route.params.serial;
        this.getDevice(this.serial);
    },
    methods: {
        getDevice(serial) {
            let url = '/autotest/device/detail/' + serial;
            this.$get(url, response =>{
                let data = response.data;
                data.sources = JSON.parse(data.sources);
                this.device = data;
            });
        }
    }
}
</script>

<style scoped>

</style>