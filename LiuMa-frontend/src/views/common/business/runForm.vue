/**
* 选择执行选项
*/ 
<template>
    <div>
        <!-- 用例调试选择引擎和环境 -->
        <el-dialog title="执行选项" :visible.sync="runVisible" width="600px" destroy-on-close @close="cancel">
            <el-form label-width="120px" style="padding-right: 30px;" :model="runForm" :rules="rules" ref="runForm">
                <el-form-item v-if="showEnvironment" label="执行环境" prop="environmentId">
                    <el-select size="small" style="width: 90%" v-model="runForm.environmentId" filterable placeholder="请选择执行环境">
                        <el-option v-for="item in environments" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="showDevice" label="执行设备" prop="deviceId">
                    <el-select size="small" style="width: 90%" v-model="runForm.deviceId" filterable placeholder="请选择执行设备">
                        <el-option v-for="item in devices" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="执行引擎" prop="engineId">
                    <el-select size="small" style="width: 90%" v-model="runForm.engineId" filterable placeholder="请选择执行引擎">
                        <el-option v-for="item in engines" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="small" type="primary" @click="run('runForm', runForm)">确定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>

export default {
    name: 'RunForm',
    props:{
        runForm: Object,
        runVisible:{
            type: Boolean,
            default: false
        },
        showEnvironment: {
            type: Boolean,
            default: false
        },
        showDevice: {
            type: Boolean,
            default: false
        },
        deviceSystem: {
            type: String,
            default: "android"
        }
    },
    data() {
        return{
            loading: false,
            environments: [],
            engines: [],
            devices: [],
            rules: {
                environmentId: [{ required: true, message: '执行环境不能为空', trigger: 'blur' }],
                engineId: [{ required: true, message: '执行引擎不能为空', trigger: 'blur' }],
                deviceId: [{ required: true, message: '执行设备不能为空', trigger: 'blur' }]
            }
        }
    },
    created(){
        if(this.runVisible){
            if(this.showEnvironment){
                this.getEnvironment();
            }
            if(this.showDevice){
                this.getDevice();
            }
            this.getEngine();
        }
    },
    watch: {
        runVisible(){
            if(this.runVisible){
                if(this.showEnvironment){
                    this.getEnvironment();
                }
                if(this.showDevice){
                    this.getDevice();
                }
                this.getEngine();
            }
        }
    },
    methods: {
        getEnvironment(){
            let url = "/autotest/environment/all/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.environments = response.data;
            });
        },
        getEngine(){
            let url = "/autotest/engine/all/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.engines = response.data;
            });
        },
        getDevice(){
            let url = "/autotest/device/" + this.deviceSystem + "/list/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.devices = response.data;
            });
        },
        cancel(){
            this.$emit("closeRun");
        },
        run(confirm, runForm) {
            this.$refs[confirm].validate(valid => {
                if (valid) {
                    this.$emit("run", runForm);
                }else{
                    return false;
                }
            })
        }
    }
}
</script>
<style scoped>

</style>