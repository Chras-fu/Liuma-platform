/**
* 逻辑控制器
*/
<template>
    <div>
        <el-table :data="controller"  size="small">
            <el-table-column label="名称" prop="name">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 95%" placeholder="设置名称" v-model="controller[scope.$index].name"  @change="changeController($event, scope.$index)">
                        <el-option v-for="item in controllerList" :key="item.name" :label="item.label" :value="item.name"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="值" prop="value">
                <template slot-scope="scope">
                    <el-button v-if="controller[scope.$index].name === 'whetherExec'"
                        size="mini" type="text" @click="editCondition(scope.$index)">编辑判断条件</el-button>
                    <el-button v-else-if="controller[scope.$index].name === 'loopExec'"
                        size="mini" type="text" @click="editLoop(scope.$index)">编辑循环机制</el-button>
                    <el-button v-else-if="controller[scope.$index].name === 'preScript' || controller[scope.$index].name === 'postScript'"
                        size="mini" type="text" @click="editCode(scope.$index)">编辑脚本</el-button>
                    <div v-else-if="controller[scope.$index].name === 'timeout' || controller[scope.$index].name === 'sleepBeforeRun' || controller[scope.$index].name === 'sleepAfterRun'" >
                        <el-input-number size="small" v-model="controller[scope.$index].value" step-strictly :step="1" :min="1"/> S
                    </div>
                    <el-select v-else size="small" style="width: 95%" v-model="controller[scope.$index].value">
                        <el-option v-for="item in optList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="60px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="remove(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="add">新增</el-button>
        <el-button size="small" type="text" @click="deleteAll">删除全部</el-button>
        <!-- 前后置脚本代码编辑 -->
        <div v-if="showType === 'code'" style="margin-top: 20px">
            <el-row>
                <el-col :span="20">
                    <p class="tip">
                        <span>编辑脚本</span>
                        <el-tooltip :content="text" placement="bottom">
                        <i class="el-icon-info"></i>
                        </el-tooltip>
                    </p>
                </el-col>
                <el-col :span="4">
                    <el-button size="small" type="cancel" @click="showType=null">取消</el-button>
                    <el-button size="small" type="primary" @click="saveCode">保存</el-button>
                </el-col>
            </el-row>
            <code-edit ref="editor" :data.sync='code' :height='480' mode="python"/>
        </div>
        <!-- 循环控制器表单编辑 -->
        <div v-if="showType === 'form'" style="margin-top: 20px">
            <el-row>
                <el-col :span="20">
                    <p class="tip">
                        <span>编辑循环控制器</span>
                    </p>
                </el-col>
                <el-col :span="4">
                    <el-button size="small" type="cancel" @click="showType=null">取消</el-button>
                    <el-button size="small" type="primary" @click="saveLoop">保存</el-button>
                </el-col>
            </el-row>
            <el-form label-width="90px" style="padding-right: 30px; margin-top: 10px" :model="loop" :rules="rules" ref="loop">
                <el-row :gutter="30">
                    <el-col :span="12">
                        <el-form-item label="循环类型" prop="type">
                            <el-radio-group  v-model="loop.type">
                                <el-radio :label="'FOR'">For循环</el-radio>
                                <el-radio :label="'WHILE'">While循环</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="循环步数" prop="num">
                            <el-input-number size="small" style="width: 150px" v-model="loop.num" label="循环执行接口数" step-strictly :step="1" :min="1"/>
                            <el-tooltip content="循环步数：以本接口开始计算往后的n个接口请求都在循环范围内" placement="bottom">
                                <i class="el-icon-info"></i>
                            </el-tooltip>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="30" v-if="loop.type==='FOR'">
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='FOR'" label="循环索引" prop="indexName">
                            <el-input size="small" style="width: 300px" v-model="loop.indexName" placeholder="自定义索引名称 同用例下的循环索引不得重名"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='FOR'" label="循环次数" prop="times">
                            <el-input size="small" style="width: 200px" v-model="loop.times" placeholder="循环执行次数"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="30" v-if="loop.type==='WHILE'">
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="判断对象" prop="target">
                            <el-input size="small" style="width: 300px" v-model="loop.target" placeholder="请输入判断对象 即关联参数 填写{{}}格式"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="判断条件" prop="assertion">
                            <el-select size="small" style="width: 150px" placeholder="判断条件" filterable v-model="loop.assertion">
                                <el-option v-for="item in functionList" :key="item.id" :label="item.name" :value="item.id"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="30" v-if="loop.type==='WHILE'">
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="预期值" prop="expect">
                            <el-input size="small" style="width: 300px" v-model="loop.expect" placeholder="请输入预期值"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="超时时间" prop="timeout">
                            <el-input-number size="small" style="width: 150px" v-model="loop.timeout" label="超时时间" step-strictly :step="10" :min="0"/>  ms
                        </el-form-item>
                    </el-col>
                </el-row> 
            </el-form>
        </div>
        <!-- 条件控制器条件编辑 -->
        <div v-if="showType === 'table'" style="margin-top: 20px">
            <el-row>
                <el-col :span="20">
                    <p class="tip">
                        <span>编辑条件控制器</span>
                    </p>
                </el-col>
                <el-col :span="4">
                    <el-button size="small" type="cancel" @click="showType=null">取消</el-button>
                    <el-button size="small" type="primary" @click="saveCondition">保存</el-button>
                </el-col>
            </el-row>
            <el-table :data="conditions"  size="small">
                <el-table-column label="判断对象" prop="expect" min-width="160px">
                    <template slot-scope="scope">
                        <el-input size="small" style="width: 90%" placeholder="请输入判断对象 即关联参数 填写{{}}格式" v-model="conditions[scope.$index].target"/>
                    </template>
                </el-table-column>
                <el-table-column label="判断条件" prop="assertion" width="150px">
                    <template slot-scope="scope">
                        <el-select size="small" style="width: 90%" placeholder="判断条件" filterable v-model="conditions[scope.$index].assertion">
                            <el-option v-for="item in functionList" :key="item.id" :label="item.name" :value="item.id"/>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column label="预期值" prop="expect" min-width="160px">
                    <template slot-scope="scope">
                        <el-input size="small" style="width: 90%" placeholder="请输入预期值" v-model="conditions[scope.$index].expect"/>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="60px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="removeCondition(scope.$index)">删除</el-button>
                </template>
                </el-table-column>
            </el-table>
            <el-button size="small" icon="el-icon-plus" type="text" @click="addCondition">新增</el-button>
            <el-button size="small" type="text" @click="deleteAllCondition">删除全部</el-button>
        </div>
    </div>
</template>
<script>
import CodeEdit from '@/views/common/business/codeEdit'
export default {
    name: 'Controller',
    components: { CodeEdit },
    props:{
        controller: Array,
    },
    data() {
        return{
            controllerList:[
                {label: "前置脚本", name: "preScript"},
                {label: "后置脚本", name: "postScript"},
                {label: "前置等待", name: "sleepBeforeRun"},
                {label: "后置等待", name: "sleepAfterRun"},
                {label: "引用Session", name: "useSession"},
                {label: "存储Session", name: "saveSession"},
                {label: "超时时间", name: "timeout"},
                {label: "下载缓冲", name: "requireStream"},
                {label: "证书验证", name: "requireVerify"},
                {label: "错误屏蔽", name: "errorContinue"},
                {label: "条件控制器", name: "whetherExec"},
                {label: "循环控制器", name: "loopExec"},
            ],
            functionList: [],
            optList: [
                {label: "是", value: true},
                {label: "否", value: false},
            ],
            showType: null,
            code: "",
            conditions: Array,
            loop: {
                type: "FOR",
                target: "",
                assertion: "",
                expect: "",
                timeout: 0,
                indexName: "",
                times: 1,
                num: 1
            },
            rules: {
                type: [{ required: true, message: '循环类型不能为空', trigger: 'blur' }],
                target: [{ required: true, message: '判断对象不能为空', trigger: 'blur' }],
                assertion: [{ required: true, message: '判断条件不能为空', trigger: 'blur' }],
                timeout: [{ required: true, message: '超时时间不能为空', trigger: 'blur' }],
                indexName: [{ required: true, message: '循环索引名不能为空', trigger: 'blur' }],
                times: [{ required: true, message: '循环次数不能为空', trigger: 'blur' }],
                num: [{ required: true, message: '循环接口数不能为空', trigger: 'blur' }],
            },
            index: null,
            text: "获取关联参数/公共参数使用sys_get(name)函数，保存关联参数使用sys_put(name, val)函数，支持直接使用变量名res_code/res_header/res_data/res_cookies/res_bytes获取响应内容"
        }
    },
    created() {
        this.getAssertion();
    },
    methods: {
        getAssertion(){
            let url = '/autotest/system/assertion/list';
            this.$get(url, response =>{
                this.functionList = response.data;
            });
        },
        add(){
            this.controller.push({name:"", value:null });
        },
        remove(index){
            this.controller.splice(index, 1);
        },
        deleteAll(){
            this.controller.splice(0, this.controller.length);
        },
        changeController(val, index){
            if(val === "sleepBeforeRun" | val ==="sleepAfterRun"){
                this.controller[index].value = 30;
            }else if(val ==="preScript" | val ==="postScript"){
                this.controller[index].value = "";
            }else if(val === "whetherExec"){
                this.controller[index].value = "[]";
            }else if(val === "loopExec"){
                this.controller[index].value = "{}";
            }else if(val === "timeout"){
                this.controller[index].value = 60;
            }else{
                this.controller[index].value = true;
            }
        },
        editCode(index){
            this.index = index;
            this.code = this.controller[index].value;
            this.showType = "code";
        },
        saveCode(){
            this.controller[this.index].value = this.code;
            this.showType = null;
        },
        editCondition(index){
            this.index = index;
            this.conditions = JSON.parse(this.controller[index].value);
            this.showType = "table";
        },
        saveCondition(){
            this.controller[this.index].value = JSON.stringify(this.conditions);
            this.showType = null;
        },
        addCondition(){
            this.conditions.push({target:"", assertion:"equals", expect:""});
        },
        removeCondition(index){
            this.conditions.splice(index, 1);
        },
        deleteAllCondition(){
            this.conditions.splice(0, this.conditions.length);
        },
        editLoop(index){
            this.index = index;
            if(this.controller[index].value === "{}"){
                this.loop = {
                    type: "FOR",
                    target: "",
                    assertion: "",
                    expect: "",
                    timeout: 0,
                    indexName: "",
                    times: 1,
                    num: 1
                };
            }else{
                this.loop = JSON.parse(this.controller[index].value);
            }
            this.showType = "form";
        },
        saveLoop(){
            this.$refs["loop"].validate(valid => {
                if (valid) {
                    this.controller[this.index].value = JSON.stringify(this.loop);
                    this.showType = null;
                }else{
                    return false;
                }
            })
        },
    }
}
</script>
<style scoped>

</style>
