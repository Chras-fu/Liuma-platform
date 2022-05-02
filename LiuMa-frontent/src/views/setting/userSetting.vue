/**
 * 个人中心  用户信息
 */
<template>
  <div>
    <page-header title="用户信息" :showDebug="false" :showCancel="false" :showSave="false"/>
    <el-form ref="userForm" :rules="rules" :model="userForm" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input size="small" style="width:400px" v-model="userForm.username" placeholder="请输入用户名"/>
      </el-form-item>
      <el-form-item label="登录账号" prop="account">
        <el-input size="small" style="width:400px" disabled v-model="userForm.account"/>
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input size="small" style="width:400px" disabled v-model="userForm.mobile"/>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input size="small" style="width:400px" v-model="userForm.email" placeholder="请填写邮箱地址"/>
      </el-form-item>
      <el-form-item label="注册时间" prop="createTimeStr">
        <el-input size="small" style="width:400px" disabled v-model="userForm.createTimeStr"/>
      </el-form-item>
    </el-form>
    <el-button size="small" type="text" @click="updatePassword">修改密码</el-button>
    <el-button size="small" type="text" @click="updateInfo">保存更改</el-button>
    <!-- 修改密码弹框 -->
    <el-dialog title="修改密码" :visible.sync="passwordVisible" width="400px" destroy-on-close>
        <el-form label-width="100px" style="padding-right: 30px;" :model="passwordForm" :rules="rules" ref="passwordForm">
            <el-form-item label="旧密码" prop="oldPassword">
                <el-input size="small" type="password" style="width:95%" v-model="passwordForm.oldPassword" placeholder="请输入旧密码"/>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
                <el-input size="small" type="password" style="width:95%" v-model="passwordForm.newPassword" placeholder="请输入新密码"/>
            </el-form-item>
            <el-form-item label="重复新密码" prop="repeatPassword">
                <el-input size="small" type="password" style="width:95%" v-model="passwordForm.repeatPassword" placeholder="请输入新密码"/>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="passwordVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitPassword">确定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '@/views/common/components/pageheader'
import {timestampToTime} from '@/utils/util'
export default {
    components: { PageHeader },
    data() {
        var validatePass = (rule, value, callback) => {
          if (value === '') {
              callback(new Error('请输入密码'));
          } else {
          if (this.passwordForm.repeatPassword !== '') {
            this.$refs.passwordForm.validateField('repeatPassword');
          }
          callback();
          }
        };
        var validatePass2 = (rule, value, callback) => {
          if (value === '') {
            callback(new Error('请再次输入密码'));
          } else if (value !== this.passwordForm.newPassword) {
            callback(new Error('两次输入密码不一致!'));
          } else {
            callback();
          }
        };
        return{
          userForm: {},
          passwordVisible:false,
          passwordForm: {},
          rules: {
              username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
              account: [{ required: true, message: '登录账号不能为空', trigger: 'blur' }],
              mobile: [{ required: true, message: '手机号不能为空', trigger: 'blur' }],
              email: [{ required: true, message: '邮箱不能为空', trigger: 'blur' }],
              createTime: [{ required: true, message: '注册时间不能为空', trigger: 'blur' }],
              oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
              newPassword: [{ required: true, message: '新密码不能为空', trigger: 'blur' },
                            { validator: validatePass, trigger: 'blur' }],
              repeatPassword: [{ required: true, message: '重复密码不能为空', trigger: 'blur' },
                              { validator: validatePass2, trigger: 'blur', required: true }]
          },
        }
    },
    created(){
      this.$root.Bus.$emit('initBread', ["个人中心", "用户信息"]);
      this.getUser();
    },
    methods: {
      getUser(){
        let url = '/autotest/user/info/' + this.$store.state.userInfo.id;
        this.$get(url, response =>{
            let data = response.data;
            data.createTimeStr = timestampToTime(data.createTime);
            this.userForm = data;
        });
      },
      updatePassword(){
        this.passwordForm = {};
        this.passwordVisible = true;
      },
      submitPassword(){
        this.$refs["passwordForm"].validate(valid => {
              if (valid) {
                  let Base64 = require('js-base64').Base64;
                  let psdForm = {
                    userId: this.$store.state.userInfo.id,
                    oldPassword: Base64.encode(this.passwordForm.oldPassword),
                    newPassword: Base64.encode(this.passwordForm.newPassword)
                  };
                  let url = '/autotest/user/update/password';
                  this.$post(url, psdForm, response =>{
                      this.$message.success("修改成功");
                      this.passwordVisible = false;
                  });
              }else{
                  return false;
              }
          });
      },
      updateInfo(){
        this.$refs["userForm"].validate(valid => {
            if (valid) {
                let url = '/autotest/user/update/info';
                this.$post(url, this.userForm, response =>{
                    this.$message.success("保存成功");
                });
            }else{
                return false;
            }
        });
      }
    }

}
</script>

<style scoped>

</style>
