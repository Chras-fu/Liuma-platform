/**
 * 用例中心  接口编辑
 */
<template>
  <div>
    <page-header title="编辑接口" :cancel="cancelAdd" :save="saveAdd"></page-header>
    <el-form ref="apiForm" :rules="rules" :model="apiForm" label-width="80px">
        <p class="tip">基础信息</p>
        <el-row :gutter="40">
            <el-col :span="8">
                <el-form-item label="接口名称" prop="name">
                    <el-input  size="small" v-model="apiForm.name" placeholder="请输入接口名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="请求协议" prop="protocol">
                    <el-select size="small" v-model="apiForm.protocol" placeholder="请选择请求协议">
                        <el-option v-for="item in protocols" :key="item" :label="item" :value="item"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="10">
                <el-form-item label="接口请求" prop="path">
                    <el-input size="small" v-model="apiForm.path" placeholder="请输入接口地址" style="margin-top: 5px" maxlength="200" show-word-limit>
                        <el-select v-model="apiForm.method" slot="prepend" style="width: 80px" size="small">
                            <el-option v-for="item in methods" :key="item" :label="item" :value="item"/>
                        </el-select>
                    </el-input>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="8">
                <el-form-item label="接口等级">
                    <el-select size="small" style="width: 100%" v-model="apiForm.level" placeholder="请选择接口等级">
                        <el-option v-for="item in levels" :key="item" :label="item" :value="item"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="域名标识" prop="domainSign">
                    <el-select size="small" v-model="apiForm.domainSign" clearable placeholder="请选择域名标识">
                        <el-option v-for="item in domains" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="10">
                <el-form-item label="模块分类" prop="moduleId">
                    <select-tree placeholder="请选择模块分类" :selectedValue="apiForm.moduleId"
                        :selectedLabel="apiForm.moduleName" :treeData="modules" @selectModule="selectModule($event)"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="16">
                <el-form-item label="接口描述">
                    <el-input size="small" :autosize="{ minRows: 4}" type="textarea" clearable placeholder="请输入接口描述" v-model="apiForm.description" maxlength="200" show-word-limit/>
                </el-form-item>
            </el-col>
        </el-row>
        <p class="tip">请求参数</p>
        <el-tabs style="width: 100%" v-model="activeTab">
            <el-tab-pane label="请求头" name="header">
                <div class="request-param">
                    <request-header :reqHeader="apiForm.header" style="width: 100%"/>
                </div>
            </el-tab-pane>
            <el-tab-pane label="请求体" name="body">
                <div class="request-param">
                    <request-body :reqBody="apiForm.body" style="width: 100%"/>
                </div>
            </el-tab-pane>
            <el-tab-pane label="QUERY参数" name="query">
                <div class="request-param">
                    <request-query :reqQuery="apiForm.query" style="width: 100%"/>
                </div>
            </el-tab-pane>
            <el-tab-pane label="REST参数" name="rest">
                <div class="request-param">
                    <request-rest :reqRest="apiForm.rest" style="width: 100%"/>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-form>
  </div>
</template>

<script>
import PageHeader from '../common/components/pageheader'
import SelectTree from '../common/business/selectTree'
import RequestHeader from './common/api/requestHeader'
import RequestQuery from './common/api/requestQuery'
import RequestRest from './common/api/requestRest'
import RequestBody from './common/api/requestBody'

export default {
    components:{
        PageHeader, SelectTree, RequestHeader, RequestQuery, RequestRest,RequestBody
    },
    data() {
        return{
            activeTab: "body",
            apiForm: {
                id: "",
                name: "",
                level: "P0",
                path: "",
                method: "GET",
                protocol: "HTTP",
                domainSign: "",
                moduleId: "",
                moduleName: "",
                description: "",
                header: [],
                body: {},
                query: [],
                rest: []
            },
            levels: ["P0", "P1", "P2", "P3"],
            methods: ['POST', 'GET', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS'],
            protocols: ["HTTP"],
            domains: [],
            modules: [],
            rules: {
                name: [{ required: true, message: '接口名称不能为空', trigger: 'blur' }],
                protocol: [{ required: true, message: '请求协议不能为空', trigger: 'blur' }],
                path: [{ required: true, message: '接口地址不能为空', trigger: 'blur' }],
                moduleId: [{ required: true, message: '接口模块不能为空', trigger: 'blur' }]
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["用例中心", "接口管理", "接口编辑"]);
        this.getDomain();
        this.getModule();
        this.getDetail(this.$route.params);
    },
    methods: {
        getDomain(){
            let url = '/autotest/domainSign/list/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.domains = response.data;
            });
        },
        getModule(){
            let url = '/autotest/module/list/api/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.modules = response.data;
            });
        },
        getDetail(apiParam){
            if (!apiParam.apiId){ // 新增接口
                this.apiForm.body = {
                    type: 'json',
                    form: [],
                    json: '',
                    raw: '',
                    file: []
                }
            }else{  // 编辑接口
                let url = '/autotest/api/detail/' + apiParam.apiId;
                this.$get(url, response =>{
                    let data = response.data;
                    if(data.header){
                        data.header = JSON.parse(data.header);
                    }
                    if(data.body){
                        data.body = JSON.parse(data.body);
                    }
                    if(data.query){
                        data.query = JSON.parse(data.query);
                    }
                    if(data.rest){
                        data.rest = JSON.parse(data.rest);
                    }
                    this.apiForm = data;
                })
            }
        },
        selectModule(data){
            this.apiForm.moduleId = data.id;
            this.apiForm.moduleName = data.label;
        },
        cancelAdd(){
            this.$router.push({path: '/caseCenter/interfaceManage'})
        },
        saveAdd(){
            this.$refs["apiForm"].validate(valid => {
                if (valid) {
                    this.apiForm.projectId = this.$store.state.projectId;
                    let url = '/autotest/api/save';
                    this.$post(url, this.apiForm, response =>{
                        this.$message.success("保存成功");
                        this.$router.push({path: '/caseCenter/interfaceManage'});
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
.request-param{
    min-height:480px;
}
</style>
