/**
 * 计划管理  计划编辑
 */
<template>
  <div>
    <page-header title="编辑计划" :cancel="cancelAdd" :save="saveAdd"/>
    <el-form ref="planForm" :rules="rules" :model="planForm" label-width="80px">
        <el-row :gutter="40">
            <el-col :span="16">
                <el-form-item label="计划名称" prop="name">
                    <el-input size="small" style="width: 100%" v-model="planForm.name" placeholder="请输入计划名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="版本" prop="versionId">
                    <el-select  size="small" style="width: 100%" v-model="planForm.versionId" placeholder="请选择计划版本">
                        <el-option v-for="item in versionList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="8">
                <el-form-item label="执行环境" prop="environmentId">
                    <el-select size="small" style="width: 100%" v-model="planForm.environmentId" placeholder="请选择执行环境">
                        <el-option v-for="item in environmentList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="执行引擎" prop="engineId">
                    <el-select size="small" style="width: 100%" v-model="planForm.engineId" placeholder="请选择执行引擎">
                        <el-option v-for="item in engineList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="失败重试" prop="retry">
                    <el-select size="small" style="width: 100%" v-model="planForm.retry" placeholder="请选择是否失败重试">
                        <el-option v-for="item in retryList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="8">
                <el-form-item label="执行时间" prop="startTime">
                    <el-date-picker size="small" style="width: 100%" v-model="planForm.startTime" type="datetime" :picker-options="expireTimeOption" placeholder="请选择执行开始时间"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="执行频率" prop="frequency">
                    <el-select size="small" style="width: 100%" v-model="planForm.frequency" placeholder="请选择执行频率">
                        <el-option v-for="item in frequencyList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="16">
                <el-form-item label="计划描述" style="margin-bottom:0px">
                    <el-input size="small" style="width: 100%" :autosize="{ minRows: 4}" type="textarea" clearable placeholder="请输入计划描述" 
                        v-model="planForm.description" maxlength="200" show-word-limit>
                    </el-input>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item style="margin-left:-80px;" prop="planCollections"/>
        <el-table :data="planForm.planCollections" size="small">
            <el-table-column label="序号" prop="index" width="100px">
            </el-table-column>
            <el-table-column label="集合名称" prop="collectionName" min-width="180px">
            </el-table-column>
            <el-table-column label="版本号" prop="collectionVersion">
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="deletePlanCollection(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="selectCollectionVisible=true">新增集合</el-button>
    </el-form>
    <!-- 添加集合界面 -->
    <el-dialog title="选择集合" :visible.sync="selectCollectionVisible" width="800px" destroy-on-close>
        <select-collection :selections="selections" :selectCollectionVisible="selectCollectionVisible"/>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="selectCollectionVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="selectCollectionSave">保存</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import PageHeader from '../common/components/pageheader'
import SelectCollection from './common/selectCollection'
import {getUUID} from '@/utils/util'
export default {
    components:{PageHeader, SelectCollection},
    data() {
        var validateStartTime = (rule, value, callback) => {
            if (typeof value === 'string'){
                callback();
            }else if (value.getTime() < Date.now()) {
                callback(new Error('开始时间不能小于当前时间'));
            } else {
                callback();
            }
        };
        return{
            selections: [],
            selectCollectionVisible: false,
            planForm: {
                id: "",
                name: "",
                versionId: "",
                environmentId: "",
                engineId: "",
                retry:"N",
                startTime: "",
                frequency: "",
                timedTask: "",
                description: "",
                planCollections:[]
            },
            versionList: [],
            environmentList: [],
            engineList: [],
            retryList: [
                {value: "Y", label: "是"},
                {value: "N", label: "否"}
            ],
            expireTimeOption:{
                disabledDate (date) {
                    return date.getTime() < Date.now() - 24 * 60 * 60 * 1000;
                }
            },
            frequencyList: [
                {value: "ONLY_ONE", label: "仅一次"},
                {value: "HALF_HOUR", label: "每半小时"},
                {value: "ONE_HOUR", label: "每小时"},
                {value: "HALF_DAY", label: "每半天"},
                {value: "ONE_DAY", label: "每天"},
                {value: "ONE_WEEK", label: "每周"},
                {value: "ONE_MONTH", label: "每月"},
            ],
            collectionListData: [], 
            rules: {
                name: [{ required: true, message: '计划名称不能为空', trigger: 'blur' }],
                versionId: [{ required: true, message: '版本不能为空', trigger: 'blur' }],
                environmentId: [{ required: true, message: '执行环境不能为空', trigger: 'blur' }],
                engineId: [{ required: true, message: '执行不能为空', trigger: 'blur' }],
                retry: [{ required: true, message: '失败重试不能为空', trigger: 'blur' }],
                startTime: [{ required: true, message: '执行开始时间不能为空', trigger: 'blur' },
                            { validator: validateStartTime, trigger: 'blur', required: true }],
                frequency: [{ required: true, message: '执行频率不能为空', trigger: 'blur' }],
                planCollections: [{ required: true, message: '请至少选择一条测试集合', trigger: 'blur' }]
            }    
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["计划中心", "测试计划", "计划编辑"]);
        this.getVersion();
        this.getEnvironment();
        this.getEngine();
        this.getDetail(this.$route.params);
    },
    methods: {
        getVersion(){
            let url = "/autotest/version/list/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.versionList = response.data;
            });
        },
        getEnvironment(){
            let url = "/autotest/environment/all/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.environmentList = response.data;
            });
        },
        getEngine(){
            let url = "/autotest/engine/all/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.engineList = response.data;
            });
        },
        getDetail(param){
            if (param.planId){
                let url = "/autotest/plan/detail/" + param.planId;
                this.$get(url, response => {
                    let data = response.data;
                    for(let i=0;i<data.planCollections.length;i++){
                        data.planCollections[i].index = i+1;
                    }
                    this.planForm = data;
                });
            }
        },
        // 保存用例选择
        selectCollectionSave(){
            for(let i=0;i<this.selections.length;i++){
                let planCollection = {
                    id: getUUID(),
                    index: this.planForm.planCollections.length+1,
                    collectionId: this.selections[i].id,
                    collectionName: this.selections[i].name,
                    collectionVersion: this.selections[i].versionName
                }
                this.planForm.planCollections.push(planCollection);
            }
            this.selectCollectionVisible = false;
        },
        deletePlanCollection(index){
            this.planForm.planCollections.splice(index, 1);
            for(let i=0; i<this.planForm.planCollections.length; i++){
                this.planForm.planCollections[i].index = i+1;
            }
        },
        cancelAdd(){
            this.$router.push({path: '/planManage/testPlan'})
        },
        saveAdd(){
            this.$refs["planForm"].validate(valid => {
                if (valid) {
                    this.planForm.projectId = this.$store.state.projectId;
                    let url = '/autotest/plan/save';
                    this.$post(url, this.planForm, response =>{
                        this.$message.success("保存成功");
                        this.$router.push({path: '/planManage/testPlan'});
                    });
                }else{
                    return false;
                }
            });
            
        },
    }
}
</script>

<style scoped>

</style>