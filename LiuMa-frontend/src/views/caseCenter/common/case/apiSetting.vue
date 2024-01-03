/**
* 请求设置
*/
<template>
    <div>
        <el-table :data="settingsList"  size="small">
            <el-table-column label="设置名称" prop="label" width="120"/>
            <el-table-column label="设置说明" prop="desc" width="280"/>
            <el-table-column label="是否启用" width="80">
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.enable" active-color="#13ce66" inactive-color="#ff4949" @change="changeEnable(scope.row)"/>                
                </template>
            </el-table-column>
            <el-table-column label="配置值" prop="value">
                <template slot-scope="scope">
                    <div v-if="scope.row.name === 'timeout' || scope.row.name === 'sleepBeforeRun' || scope.row.name === 'sleepAfterRun'" >
                        <el-input-number :disabled="!scope.row.enable" size="small" v-model="scope.row.value"
                         @change="changeValue(scope.row)" step-strictly :step="1" :min="0"/> 单位秒
                    </div>
                    <div v-else-if="scope.row.name === 'proxy'">
                        <el-input :disabled="!scope.row.enable" size="mini" style="width:150px" v-model="scope.row.value.url"
                         @change="changeValue(scope.row)" placeholder="代理URL"/>
                        <el-input :disabled="!scope.row.enable" size="mini" style="width:100px" v-model="scope.row.value.username"
                         @change="changeValue(scope.row)" placeholder="登录用户名"/>
                        <el-input :disabled="!scope.row.enable" size="mini" style="width:100px" v-model="scope.row.value.password"
                         @change="changeValue(scope.row)" placeholder="登录密码"/>
                    </div>
                    <div v-else>
                        <el-radio-group :disabled="!scope.row.enable" v-model="scope.row.value"  @change="changeValue(scope.row)">
                            <el-radio :label="true">开启</el-radio>
                            <el-radio :label="false">关闭</el-radio>
                        </el-radio-group>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script>
export default {
    name: 'ApiSetting',
    props:{
        settings: Array
    },
    data() {
        return{
            settingsList:[
                {label: "超时时间", name: "timeout", enable:false, value: 30, desc: "接口请求超时时间"},
                {label: "前置等待", name: "sleepBeforeRun", enable:false, value: 0, desc: "设置发送请求前的等待时间"},
                {label: "后置等待", name: "sleepAfterRun", enable:false, value: 0, desc: "设置请求完成后的等待时间"},
                {label: "引用Session", name: "useSession", enable:false, value: false, desc: "复用前面接口的session，主要用于验证登录状态"},
                {label: "存储Session", name: "saveSession", enable:false, value: false, desc: "保存当前接口的session，主要用于保存登录状态"},
                {label: "下载缓冲", name: "requireStream", enable:false, value: false, desc: "下载大文件时使用缓冲下载"},
                {label: "证书验证", name: "requireVerify", enable:false, value: false, desc: "接口是否开启SSL证书验证"},
                {label: "错误屏蔽", name: "errorContinue", enable:false, value: false, desc: "接口请求失败时是否继续执行后续接口"},
                {label: "网络代理", name: "proxy", enable:false, value: {url:'',username:'',password:''}, desc: "设置网络代理请求接口，只针对当前接口"},
            ],
        }
    },
    created() {
        let settingsObj = {};
        for(let i=0;i<this.settings.length;i++){
            settingsObj[this.settings[i].name] = this.settings[i].name === 'proxy'?
                JSON.parse(this.settings[i].value):this.settings[i].value;
        }
        for(let j=0;j<this.settingsList.length;j++){
            if(this.settingsList[j].name in settingsObj){
                this.settingsList[j].enable = true;
                this.settingsList[j].value = settingsObj[this.settingsList[j].name];
            }
        }
    },
    methods: {
        changeEnable(row){
            if(row.enable){
                this.settings.push({name: row.name,value: row.name==="proxy"?JSON.stringify(row.value):row.value});
            }else{
                for(let i=0;i<this.settings.length;i++){
                    if(this.settings[i].name === row.name){
                        this.settings.splice(i, 1);
                        break;
                    }
                }
            }
        },
        changeValue(row){
            for(let i=0;i<this.settings.length;i++){
                if(this.settings[i].name === row.name){
                    this.settings[i].value = row.name==="proxy"?JSON.stringify(row.value):row.value;
                    break;
                }
            }
        },
    }
}
</script>
<style scoped>

</style>
