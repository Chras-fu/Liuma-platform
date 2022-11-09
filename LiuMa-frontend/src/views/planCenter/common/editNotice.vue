/**
* 计划通知配置
*/ 
<template>
    <div>
        <!-- 计划通知配置 -->
        <el-dialog title="通知配置" :visible.sync="noticeVisible" width="600px" destroy-on-close @close="cancel">
            <el-form label-width="120px" style="padding-right: 30px;" :model="noticeForm" :rules="rules" ref="noticeForm">
                <el-form-item label="群聊名称" prop="notificationId">
                    <el-select size="small" style="width: 90%" v-model="noticeForm.notificationId" filterable placeholder="请选择群聊名称">
                        <el-option v-for="item in notifications" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="触发条件" prop="condition">
                    <el-radio-group style="width: 60%" v-model="noticeForm.condition">
                        <el-radio :label="'A'">全部通知</el-radio>
                        <el-radio :label="'F'">仅失败</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="small" type="primary" @click="submitNotice('noticeForm', noticeForm)">确定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>

export default {
    name: 'noticeForm',
    props:{
        noticeForm: Object,
        noticeVisible:{
            type: Boolean,
            default: false
        }
    },
    data() {
        return{
            notifications: [],
            rules: {
                notificationId: [{ required: true, message: '群聊名称不能为空', trigger: 'blur' }],
                condition: [{ required: true, message: '触发条件不能为空', trigger: 'blur' }]
            }
        }
    },
    created(){
        if(this.noticeVisible){
            this.getNotification();
        }
    },
    watch: {
        noticeVisible(){
            if(this.noticeVisible){
                this.getNotification();
            }
        }
    },
    methods: {
        getNotification(){
            let url = "/autotest/notification/list/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.notifications = response.data;
            });
        },
        cancel(){
            this.$emit("closeNotice");
        },
        submitNotice(confirm, noticeForm) {
            this.$refs[confirm].validate(valid => {
                if (valid) {
                    this.$emit("submitNotice", noticeForm);
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