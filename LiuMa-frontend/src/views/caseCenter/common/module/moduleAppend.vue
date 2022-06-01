/**
* 模块新增
*/ 
<template>
    <div>
        <!-- 添加模块界面 -->
        <el-dialog :title="title" :visible.sync="show" width="35%" destroy-on-close @close="cancel">
            <el-form label-width="120px" style="padding-right: 30px;" :model="moduleForm" :rules="rules" ref="moduleForm">
                <el-form-item label="模块名称" prop="name">
                    <el-input size="small" v-model="moduleForm.name" auto-complete="off" placeholder="模块名称"></el-input>
                </el-form-item>
                <el-form-item label="所属父级" prop="parentName">
                    <el-input size="small" v-model="moduleForm.parentName" auto-complete="off" placeholder="所属父级" :disabled="true"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="small" @click="cancel">取消</el-button>
                <el-button size="small" type="primary" @click="submitModule('moduleForm', moduleForm)">保存</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
export default {
    name: 'ModuleAppend',
    props:{
        title:String,
        show:{
            type: Boolean,
            default: false
        },
        moduleForm:Object,
    },
    data(){
        return{
            rules: {
                name: [{ required: true, message: '模块名称不能为空', trigger: 'blur' }],
                parentName: [{ required: true, message: '所属父级不能为空', trigger: 'blur' }]
            },
        }
    },
    methods: {
        cancel(){
            this.$emit("closeDialog");
        },
        submitModule(confirm, moduleForm) {
            this.$refs[confirm].validate(valid => {
                if (valid) {
                    this.$emit("submitModule", moduleForm);
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