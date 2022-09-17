/**
 * 公共组件  函数编辑
 */
<template>
  <div>
    <!-- 自己创建的才能修改 -->
    <page-header v-if="isAdd===true" title="新增函数" :cancel="cancelAdd" :save="saveAdd"/>
    <page-header v-else-if="functionForm.createUser!==currentUser" title="查看函数" :showSave="false" :cancel="cancelAdd"/>
    <page-header v-else title="编辑函数" :cancel="cancelAdd" :save="saveAdd"/>
    <el-form ref="functionForm" :rules="rules" :model="functionForm" label-width="80px">
        <p class="tip">
            <span>基础信息</span>
            <el-tooltip content="函数名称限字母及下划线 且首字符不得为下划线" placement="bottom">
                <i class="el-icon-info"></i>
            </el-tooltip>
        </p>
        <el-row :gutter="10">
            <el-col :span="6">
                <el-form-item label="函数名称" prop="name">
                    <el-input size="small" :disabled="!isAdd" v-model="functionForm.name" placeholder="请输入函数名称 例name_str"/>
                </el-form-item>
            </el-col>
            <el-col :span="7">
                <el-form-item label="调用方式" prop="expression">
                    <el-input size="small" v-model="functionForm.expression" :placeholder="place"/>
                </el-form-item>
            </el-col>
            <el-col :span="11">
                <el-form-item label="函数说明" prop="description">
                    <el-input size="small" v-model="functionForm.description" placeholder="请输入函数说明"/>
                </el-form-item>
            </el-col>
        </el-row>
        <p class="tip">函数入参</p>
        <el-table :data="functionForm.param">
            <el-table-column label="入参名称" prop="paramName">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入入参名称" v-model="functionForm.param[scope.$index].paramName"/>
                </template>
            </el-table-column>
            <el-table-column label="入参类型" prop="type">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="functionForm.param[scope.$index].type">
                        <el-option v-for="item in types" :key="item" :label="item" :value="item"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="入参描述" prop="description">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入入参描述" v-model="functionForm.param[scope.$index].description"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="remove(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="add">新增</el-button>
        <el-button size="small" type="text" @click="deleteAll">删除全部</el-button>
        <p class="tip">
            <span>函数代码</span>
            <el-tooltip content="代码内可直接使用定义的参数名 执行结果必须以sys_return(result)形式返回" placement="bottom">
                <i class="el-icon-info"></i>
            </el-tooltip>
        </p>
        <code-edit ref="editor" :data.sync='functionForm.code' :height='480' mode="python"/>
    </el-form>
  </div>
</template>

<script>
import PageHeader from '@/views/common/components/pageheader'
import CodeEdit from '@/views/common/business/codeEdit'
export default {
    components: { CodeEdit, PageHeader },
    data() {
        return{
          place: "格式如 {{@func(param)}}",
          types: ["String", "Int", "Float", "Boolean", "Bytes", "JSONObject", "JSONArray", "Other"],
          functionForm: {
            id: "",
            name: "",
            from: "custom",
            param: [],
            code: "",
            expression: "",
            description: "",
            createUser: ""
          },
          rules: {
              name: [{ required: true, message: '函数名称不能为空', trigger: 'blur' }],
              expression: [{ required: true, message: '调用方式不能为空', trigger: 'blur' }],
              description: [{ required: true, message: '函数说明不能为空', trigger: 'blur' }]
          },
          currentUser: "",
          isAdd: true,
        }
    },
    created(){
      this.$root.Bus.$emit('initBread', ["公共组件", "函数管理", "函数编辑"]);
      this.currentUser = this.$store.state.userInfo.id;
      this.getDetail(this.$route.params);
    },
    methods: {
      getDetail(param){
        if (param.functionId){
            this.isAdd = false;
            let url = '/autotest/function/detail/' + param.functionId;
            this.$get(url, response =>{
                let data = response.data;
                data.param = JSON.parse(data.param);
                this.functionForm = data;
            });
        }
      },
      cancelAdd(){
          this.$router.push({path: '/common/funcManage'})
      },
      saveAdd(){
          this.$refs["functionForm"].validate(valid => {
              if (valid) {
                  let form = JSON.parse(JSON.stringify(this.functionForm));
                  form.projectId = this.$store.state.projectId;
                  form.param = JSON.stringify(form.param);
                  let url = '/autotest/function/save';
                  this.$post(url, form, response =>{
                      this.$message.success("保存成功");
                      this.$router.push({path: '/common/funcManage'});
                  });
              }else{
                  return false;
              }
          });
      },
      add(){
          this.functionForm.param.push({paramName:"", type:"String", description: ""});
      },
      remove(index){
          this.functionForm.param.splice(index, 1);
      },
      deleteAll(){
          this.functionForm.param.splice(0, this.functionForm.param.length);
      },
    }

}
</script>

<style scoped>

</style>
