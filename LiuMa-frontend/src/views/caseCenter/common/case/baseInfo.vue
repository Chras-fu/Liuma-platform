/**
* 用例基础信息
*/ 
<template>
    <div>
        <p class="tip">基础信息</p>
        <el-row :gutter="40">
            <el-col :span="8">
                <el-form-item label="用例名称" prop="name">
                    <el-input  size="small" v-model="caseForm.name" placeholder="请输入用例名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="用例类型" prop="type">
                    <el-select size="small" style="width: 100%" v-model="caseForm.type" disabled placeholder="请选择用例类型">
                        <el-option v-for="item in caseTypes" :key="item" :label="item" :value="item"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="10">
                <el-form-item label="环境标签">
                    <el-select size="small" style="width: 100%" v-model="caseForm.environmentIds" multiple filterable clearable placeholder="请选择环境标签">
                        <el-option v-for="item in environments" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="8">
                <el-form-item label="用例等级">
                    <el-select size="small" style="width: 100%" v-model="caseForm.level" placeholder="请选择用例等级">
                        <el-option v-for="item in levels" :key="item" :label="item" :value="item"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="第三方标识">
                    <el-input  size="small" v-model="caseForm.thirdParty" placeholder="请输入第三方标识"/>
                </el-form-item>
            </el-col>
            <el-col :span="10">
                <el-form-item label="模块分类" prop="moduleId">
                    <select-tree placeholder="请选择模块分类" :selectedValue="caseForm.moduleId" 
                        :selectedLabel="caseForm.moduleName" :treeData="modules" @selectModule="selectModule($event)"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="24">
                <el-form-item label="用例描述">
                    <el-input size="small" :autosize="{ minRows: 3}" type="textarea" clearable placeholder="请输入用例描述" v-model="caseForm.description" maxlength="200" show-word-limit/>
                </el-form-item>
            </el-col>
        </el-row>
        <p class="tip">
            <span>配置信息</span>
            <el-tooltip content="如若使用自定义函数和公参 请先导入 否则无法使用" placement="bottom">
                <i class="el-icon-info"></i>
            </el-tooltip>
        </p>
        <el-row :gutter="40">
            <el-col :span="12">
                <el-form-item label="导入函数">
                    <el-select size="small" style="width: 100%" v-model="caseForm.commonParam.functions" filterable multiple clearable placeholder="请选择本用例需要使用的自定义函数">
                        <el-option v-for="item in functionList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="导入公参">
                    <el-select size="small" style="width: 100%" v-model="caseForm.commonParam.params" filterable multiple clearable placeholder="请选择本用例需要使用的自定义公参">
                        <el-option v-for="item in paramList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40" v-if="caseForm.type === 'API'">
            <el-col :span="12">
                <el-form-item label="公用Header">
                    <el-select size="small" style="width: 100%" v-model="caseForm.commonParam.header" clearable placeholder="公共使用的请求头 接口若配置请求头会合并且替换同名请求头参数">
                        <el-option v-for="item in headers" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="公用Proxy">
                    <el-select size="small" style="width: 100%" v-model="caseForm.commonParam.proxy" clearable placeholder="公共使用的代理">
                        <el-option v-for="item in proxys" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40" v-if="caseForm.type === 'WEB'">
            <el-col :span="12">
                <el-form-item label="启动Driver">
                    <el-switch size="small" v-model="caseForm.commonParam.startDriver" active-text="用例开始前重启浏览器"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="关闭Driver">
                    <el-switch size="small" v-model="caseForm.commonParam.closeDriver" active-text="用例结束后关闭浏览器"/>
                </el-form-item>
            </el-col>
        </el-row>
        
    </div>
</template>
<script>
import SelectTree from '../../../common/business/selectTree'

export default {
  name: 'BaseInfo',
  components:{
        SelectTree
    },
  props:{
    caseForm: Object,
  },
  data() {
      return{
        levels: ["P0", "P1", "P2", "P3"],
        caseTypes:["API", "WEB"],
        modules: [],
        headers: [],
        proxys: [],
        environments: [],
        functionList: [],
        paramList: []
      }
    },
    created() {
        this.getModule();
        this.getEnvironment();
        this.getFunction();
        this.getParam();
        if(this.caseForm.type === "API"){
            this.getHeader();
            this.getProxy();

        }
    },
    methods: {
        getFunction(){
            let url = '/autotest/function/custom/list/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.functionList = response.data;
            });
        },
        getParam(){
            let url = '/autotest/commonParam/custom/list/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.paramList = response.data;
            });
        },
        getModule(){
            let url = '/autotest/module/list/case/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.modules = response.data;
            });
        },
        getHeader(){
            let url = "/autotest/commonParam/param/list/Header/" + this.$store.state.projectId;
            this.$get(url, response =>{
                this.headers = response.data;
            });
        },
        getProxy(){
            let url = "/autotest/commonParam/param/list/Proxy/" + this.$store.state.projectId;
            this.$get(url, response =>{
                this.proxys = response.data;
            });
        },
        getEnvironment(){
            let url = "/autotest/environment/all/" + this.$store.state.projectId;
            this.$get(url, response =>{
                this.environments = response.data;
            });
        },
        selectModule(data){
            this.caseForm.moduleId = data.id;
            this.caseForm.moduleName = data.label;
        },
    }
}
</script>
<style scoped>

</style>