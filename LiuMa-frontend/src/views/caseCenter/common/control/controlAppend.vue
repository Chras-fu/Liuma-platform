/**
* 模块新增
*/ 
<template>
    <div>
        <!-- 添加控件界面 -->
        <el-dialog title="编辑控件" :visible.sync="controlVisible" width="50%" destroy-on-close @close="cancel">
            <el-form label-width="120px" style="padding-right: 30px;" :model="controlForm" :rules="rules" ref="controlForm">
                <el-form-item label="控件名称" prop="name">
                    <el-input size="small" style="width:95%" v-model="controlForm.name" auto-complete="off" placeholder="控件名称"/>
                </el-form-item>
                <el-form-item label="所属系统" prop="system">
                    <el-select size="small" style="width:95%;" v-model="controlForm.system" placeholder="所属系统">
                        <el-option v-for="item in systems" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="定位方式" prop="by">
                    <el-select size="small" style="width:95%;" v-model="controlForm.by" placeholder="定位方式" @change="changeBy">
                        <el-option v-for="item in byList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="表达式" prop="expression">
                    <div v-if="controlForm.by === 'PROP'" style="width:95%">
                        <el-row v-for="(item, index) in controlForm.expressionList" :key="index">
                            <el-col :span="6">
                                <el-select size="small" style="width:95%" v-model="item.propName" placeholder="属性名">
                                    <el-option v-for="prop in propList" :key="prop" :label="prop" :value="prop"></el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="15">
                                <el-input size="small" style="width:100%" v-model="item.propValue" placeholder="请输入属性值"/>
                            </el-col>
                            <el-col :span="3">
                                <div style="font-size: 24px; margin-top:8px; margin-left:5px; display: flex">
                                    <i class="el-icon-circle-plus lm-success" @click="addProp(index)"></i>
                                    <i v-if="controlForm.expressionList.length > 1" class="el-icon-remove lm-error" @click="deleteProp(index)"></i>
                                </div>
                            </el-col>
                        </el-row>
                    </div>
                    <el-input v-else size="small" style="width:95%" v-model="controlForm.expression" placeholder="请输入元素定位表达式"/>
                </el-form-item>
                <el-form-item label="所属页面" prop="moduleId">
                    <select-tree style="width:95%;" placeholder="所属页面" :selectedValue="controlForm.moduleId" :selectedLabel="controlForm.moduleName" :treeData="treeData" @selectModule="selectModule($event)"/>
                </el-form-item>
                <el-form-item label="控件描述">
                    <el-input size="small" style="width:95%" v-model="controlForm.description" :autosize="{ minRows: 3}" type="textarea" maxlength="200" show-word-limit auto-complete="off" placeholder="控件描述"/>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="small" @click="cancel">取消</el-button>
                <el-button size="small" type="primary" @click="submitControlForm">确定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import {locateBys, locateProps} from '@/utils/constant'
import SelectTree from '../../../common/business/selectTree'
export default {
    name: 'ControlAppend',
    props:{
        controlVisible: Boolean,
        controlForm:Object,
    },
    // 注册组件
    components: {
        SelectTree
    },
    data(){
        return{
            byList:[],
            propList: [],
            treeData: [],
            systems:[
                { label: "安卓", value: "android" },
                { label: "苹果", value: "apple" },
            ],
            rules: {
                name: [{ required: true, message: '元素名称不能为空', trigger: 'blur' }],
                system: [{ required: true, message: '所属系统不能为空', trigger: 'blur' }],
                by: [{ required: true, message: '定位方式不能为空', trigger: 'blur' }],
                expression: [{ required: true, message: '表达式不能为空', trigger: 'blur' }],
                moduleId: [{ required: true, message: '所属页面不能为空', trigger: 'blur' }]
            },
        }
    },
    created(){
        this.getTree();
    },
    methods: {
        cancel(){
            this.$emit("closeDialog");
        },
        submitControlForm(){
            // 请求接口
            if(this.controlForm.by === "PROP"){
                let re = true;
                for(let i=0;i<this.controlForm.expressionList.length;i++){
                    let expression = this.controlForm.expressionList[i];
                    if(expression.propName === "" || expression.propValue === ""){
                        re = false;
                        break;
                    }
                }
                if(re){
                    this.controlForm.expression = JSON.stringify(this.controlForm.expressionList);
                }else{
                    this.controlForm.expression = "";
                }
            }
            this.$refs["controlForm"].validate(valid => {
                if (valid) {
                    this.controlForm.projectId = this.$store.state.projectId;
                    let url = '/autotest/control/save';
                    this.$post(url, this.controlForm, response =>{
                        this.$message.success("保存成功");
                        this.$emit("submitControlForm");
                    });
                }else{
                    return false;
                }
            })
        },
        // 获取树数据
        getTree(){
            let url = '/autotest/module/list/view/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.treeData = response.data;
            });
        },
        selectModule(data){
            this.controlForm.moduleId = data.id;
            this.controlForm.moduleName = data.label;
        },
        // 更改定位方式
        changeBy(val){
            if(val === "PROP"){
                if(this.controlForm.expressionList.length === 0){
                    this.controlForm.expressionList.push({propName:"",propValue:""});
                }
            }
        },
        // 新增属性定位
        addProp(index){
            this.controlForm.expressionList.splice(index+1, 0, {propName:"",propValue:""});
        },
        // 删除属性定位
        deleteProp(index){
            this.controlForm.expressionList.splice(index, 1);
        }
    },
    watch: {
        'controlForm.system'(newVal, oldVal){
            if(newVal === "android"){
                this.propList = locateProps.android;
                this.byList = locateBys.android;
            }else{
                this.propList = locateProps.apple;
                this.byList = locateBys.apple;
            }
        }
    }
}
</script>

<style scoped>

</style>