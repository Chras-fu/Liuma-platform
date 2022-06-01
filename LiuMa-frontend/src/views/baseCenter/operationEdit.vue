/**
 * 公共组件  控件编辑
 */
<template>
  <div>
    <!-- 自己创建的才能修改 -->
    <page-header v-if="isAdd===true" title="新增控件" :cancel="cancelAdd" :save="saveAdd"/>
    <page-header v-else-if="operationForm.createUser!==currentUser" title="查看控件" :showSave="false" :cancel="cancelAdd"/>
    <page-header v-else title="编辑控件" :cancel="cancelAdd" :save="saveAdd"/>
    <el-form ref="operationForm" :rules="rules" :model="operationForm" label-width="80px">
        <p class="tip">基础信息</p>
        <el-row :gutter="10">
            <el-col :span="8">
                <el-form-item label="控件名称" prop="name">
                    <el-input size="small" :disabled="!isAdd" v-model="operationForm.name" placeholder="请输入控件名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="4">
                <el-form-item label="控件类型" prop="type">
                    <el-select size="small" disabled style="width: 100%" v-model="operationForm.type">
                        <el-option v-for="item in operationTypes" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="控件说明" prop="description">
                    <el-input size="small" v-model="operationForm.description" placeholder="请输入控件说明"/>
                </el-form-item>
            </el-col>
        </el-row>
        <p class="tip">控件元素定义</p>
        <el-table :data="operationForm.element">
            <el-table-column label="元素名称" prop="paramName">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请定义元素名称" v-model="operationForm.element[scope.$index].paramName"/>
                </template>
            </el-table-column>
            <el-table-column label="元素说明" prop="description">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入元素说明" v-model="operationForm.element[scope.$index].description"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="removeElement(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="addElement">新增</el-button>
        <el-button size="small" type="text" @click="deleteAllElement">删除全部</el-button>
        <p class="tip">控件数据定义</p>
        <el-table :data="operationForm.data">
            <el-table-column label="数据名称" prop="paramName">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请定义数据名称" v-model="operationForm.data[scope.$index].paramName"
                    :disabled="operationForm.data[scope.$index].paramName ==='assertion'|| operationForm.data[scope.$index].paramName ==='expect' || operationForm.data[scope.$index].paramName ==='continue' || operationForm.data[scope.$index].paramName ==='save_name' || operationForm.data[scope.$index].paramName ==='true' || operationForm.data[scope.$index].paramName ==='false'"/>
                </template>
            </el-table-column>
            <el-table-column label="数据类型" prop="type">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="operationForm.data[scope.$index].type" 
                    :disabled="operationForm.data[scope.$index].paramName==='assertion' || operationForm.data[scope.$index].paramName ==='continue' || operationForm.data[scope.$index].paramName==='save_name' || operationForm.data[scope.$index].paramName==='true' || operationForm.data[scope.$index].paramName==='false'">
                        <el-option v-for="item in dataTypes" :key="item" :label="item" :value="item"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="数据说明" prop="description">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入数据说明" v-model="operationForm.data[scope.$index].description"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="mini" v-if="operationForm.data[scope.$index].paramName!=='assertion' && operationForm.data[scope.$index].paramName!=='expect' && operationForm.data[scope.$index].paramName!=='continue' && operationForm.data[scope.$index].paramName!=='save_name' && operationForm.data[scope.$index].paramName!=='true' && operationForm.data[scope.$index].paramName!=='false'" 
                        type="text" @click="removeData(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="addData">新增</el-button>
        <el-button size="small" type="text" @click="deleteAllData">删除全部</el-button>
        <p class="tip">控件代码<span style="font-size:5px;margin-left:10px">*代码内可直接使用定义的元素和数据名 关联/断言/条件提取值必须以sys_return(result)形式返回</span></p>
        <code-edit ref="editor" :data.sync='operationForm.code' :height='480' mode="python"/>
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
          dataTypes: ["String", "Int", "Float", "Boolean", "JSONObject", "JSONArray", "Other"],
          operationTypes: [
              {id: "system", name: "系统操作"},
              {id: "element", name: "元素操作"},
              {id: "relation", name: "关联"},
              {id: "assertion", name: "断言"},
              {id: "condition", name: "条件"},
              {id: "scenario", name: "场景"},
          ],
          operationForm: {
            id: "",
            name: "",
            type: "",
            from: "custom",
            element: [],
            data: [],
            code: "",
            type: "",
            description: "",
            createUser: ""
          },
          rules: {
              name: [{ required: true, message: '控件名称不能为空', trigger: 'blur' }],
              description: [{ required: true, message: '控件说明不能为空', trigger: 'blur' }]
          },
          currentUser: "",
          isAdd: true,
        }
    },
    created(){
      this.$root.Bus.$emit('initBread', ["公共组件", "控件管理", "控件编辑"]);
      this.currentUser = this.$store.state.userInfo.id;
      this.getDetail(this.$route.params);
    },
    methods: {
      getDetail(param){
        if (param.operationId){ 
            this.isAdd = false;
            let url = '/autotest/operation/detail/' + param.operationId;
            this.$get(url, response =>{
                let data = response.data;
                data.element = JSON.parse(data.element);
                data.data = JSON.parse(data.data);
                this.operationForm = data;
            });
        }else{
          this.operationForm.type = param.operationType;
          if(param.operationType == "assertion"){
              this.operationForm.data = [
                  {paramName:"assertion", type:"String", description: "断言方法"},
                  {paramName:"expect", type:"String", description: "预期值"}
              ];
          }else if(param.operationType == "condition"){
              this.operationForm.data = [
                  {paramName:"assertion", type:"String", description: "判断方法"},
                  {paramName:"expect", type:"String", description: "预期值"},
                  {paramName:"true", type:"Int", description: "执行行数m 条件为真执行接下来[0, m)行"},
                  {paramName:"false", type:"Int", description: "执行行数n 条件为假执行接下来[m, m+n)行"},
              ];
          }else if(param.operationType == "relation"){
              this.operationForm.data = [
                  {paramName:"save_name", type:"String", description: "保存参数名称"}
              ];
          }
        }
      },
      cancelAdd(){
          this.$router.push({path: '/common/operationManage'})
      },
      saveAdd(){
          this.$refs["operationForm"].validate(valid => {
              if (valid) {
                  this.operationForm.projectId = this.$store.state.projectId;
                  this.operationForm.element = JSON.stringify(this.operationForm.element);
                  this.operationForm.data = JSON.stringify(this.operationForm.data);
                  let url = '/autotest/operation/save';
                  this.$post(url, this.operationForm, response =>{
                      this.$message.success("保存成功");
                      this.$router.push({path: '/common/operationManage'});
                  });
              }else{
                  return false;
              }
          });
      },
      addData(){
          this.operationForm.data.push({paramName:"", type:"String", description: ""});
      },
      removeData(index){
          this.operationForm.data.splice(index, 1);
      },
      deleteAllData(){
          this.operationForm.data.splice(0, this.operationForm.data.length);
      },
      addElement(){
          this.operationForm.element.push({paramName:"", description: ""});
      },
      removeElement(index){
          this.operationForm.element.splice(index, 1);
      },
      deleteAllElement(){
          this.operationForm.element.splice(0, this.operationForm.element.length);
      },
    }
    
}
</script>

<style scoped>

</style>