/**
 * 公共组件  操作编辑
 */
<template>
  <div>
    <!-- 自己创建的才能修改 -->
    <page-header v-if="isAdd" :title="'新增'+ operationForm.uiType +'操作'" :cancel="cancelAdd" :save="saveAdd"/>
    <page-header v-else-if="operationForm.createUser!==currentUser" :title="'查看'+ operationForm.uiType +'操作'" :showSave="false" :cancel="cancelAdd"/>
    <page-header v-else :title="'编辑'+ operationForm.uiType +'操作'" :cancel="cancelAdd" :save="saveAdd"/>
    <el-form ref="operationForm" :rules="rules" :model="operationForm" label-width="80px">
        <p class="tip">基础信息</p>
        <el-row :gutter="10">
            <el-col :span="(operationForm.uiType==='web') ? 8:6">
                <el-form-item label="操作名称" prop="name">
                    <el-input size="small" :disabled="!isAdd" v-model="operationForm.name" placeholder="请输入操作名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="4">
                <el-form-item label="操作类型" prop="type">
                    <el-select size="small" disabled style="width: 100%" v-model="operationForm.type">
                        <el-option v-for="item in operationTypes" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col v-if="operationForm.uiType!=='web'" :span="4">
                <el-form-item label="操作系统" prop="system">
                    <el-select size="small" :disabled="!isAdd" style="width: 100%" v-model="operationForm.system">
                        <el-option v-for="item in systems" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="(operationForm.uiType==='web') ? 12:10">
                <el-form-item label="操作说明" prop="description">
                    <el-input size="small" v-model="operationForm.description" placeholder="请输入操作说明"/>
                </el-form-item>
            </el-col>
        </el-row>
        <p class="tip">操作元素定义</p>
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
        <p class="tip">操作数据定义</p>
        <el-table :data="operationForm.data">
            <el-table-column label="数据名称" prop="paramName">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请定义数据名称" v-model="scope.row.paramName"
                    :disabled="getDisabled('paramName', scope.row.paramName)"/>
                </template>
            </el-table-column>
            <el-table-column label="数据类型" prop="type">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="scope.row.type" 
                    :disabled="getDisabled('type', scope.row.paramName)">
                        <el-option v-for="item in dataTypes" :key="item" :label="item" :value="item"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="数据说明" prop="description">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入数据说明" v-model="scope.row.description"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="mini" v-if="!getDisabled('operation', scope.row.paramName)" 
                        type="text" @click="removeData(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="addData">新增</el-button>
        <el-button size="small" type="text" @click="deleteAllData">删除全部</el-button>
        <p class="tip">
            <span>操作代码</span>
            <el-tooltip :content="text" placement="bottom">
                <i class="el-icon-info"></i>
            </el-tooltip>
        </p>
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
            dataTypes: ["String", "Int", "Float", "Number", "Boolean", "JSONObject", "JSONArray", "Other"],
            systems: [
                {id: "android", name: "安卓"},
                {id: "apple", name: "苹果"}
            ],
            operationTypes: [
                {id: "browser", name: "浏览器操作"},
                {id: "system", name: "系统操作"},
                {id: "page", name: "页面操作"},
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
                system: "android",
                element: [],
                data: [],
                code: "",
                uiType: "",
                description: "",
                createUser: ""
            },
            rules: {
                name: [{ required: true, message: '操作名称不能为空', trigger: 'blur' }],
                type: [{ required: true, message: '操作类型不能为空', trigger: 'blur' }],
                system: [{ required: true, message: '操作系统不能为空', trigger: 'blur' }],
                description: [{ required: true, message: '操作说明不能为空', trigger: 'blur' }]
            },
            currentUser: "",
            isAdd: true,
            text: "代码内可直接使用定义的元素和数据名 关联/断言/条件提取值必须以sys_return(result)形式返回"
            }
    },
    created(){
        this.$root.Bus.$emit('initBread', ["公共组件", "操作管理", "操作编辑"]);
        this.currentUser = this.$store.state.userInfo.id;
        this.operationForm.uiType = this.$route.params.uiType;
        this.getDetail(this.$route.params);
    },
    methods: {
        getDetail(param){
            if (param.operationId){ 
                this.isAdd = false;
                this.systems.push({id: "common", name: "通用"});
                let url = '/autotest/operation/detail/' + param.uiType + '/' + param.operationId;
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
                    let operationForm = JSON.parse(JSON.stringify(this.operationForm));
                    operationForm.projectId = this.$store.state.projectId;
                    operationForm.element = JSON.stringify(operationForm.element);
                    operationForm.data = JSON.stringify(operationForm.data);
                    let url = '/autotest/operation/save';
                    this.$post(url, operationForm, response =>{
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
        getDisabled(field, paramName){
            switch(this.operationForm.type) {
                case 'assertion':
                    if(field !== 'type'){
                        if(paramName === "assertion" | paramName === "expect" | paramName === "continue"){
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        if(paramName === "assertion" | paramName === "continue"){
                            return true;
                        }else{
                            return false;
                        }
                    }
                case 'relation':
                    if(paramName === 'save_name'){
                        return true;
                    }else{
                        return false;
                    }
                case 'condition':
                    if(paramName === 'true' | paramName === 'false'){
                        return true;
                    }else{
                        return false;
                    }
                case 'looper':
                    return true;
                default:
                    return false;
            }
        }
    }
    
}
</script>

<style scoped>

</style>