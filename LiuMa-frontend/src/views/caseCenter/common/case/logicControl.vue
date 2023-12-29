/**
* 逻辑控制器
*/
<template>
    <div>
        <!-- 循环控制器表单编辑 -->
        <el-card style="margin-top: 10px">
            <el-row>
                <el-col :span="21">
                    <p class="tip">
                        <span>循环控制器</span>
                        <el-tooltip content="开启后会根据循环条件循环执行当前接口及之后的n个接口，n为循环步数" placement="bottom">
                            <i class="el-icon-info"></i>
                        </el-tooltip>
                    </p>
                </el-col>
                <el-col :span="3">
                    <div style="font-size: 16px; margin-top: -1px; margin-right: 5px; float:left;color: #409EFF">
                        {{enableLoop?'已启用':'已禁用'}}
                    </div>
                    <el-switch v-model="enableLoop" active-color="#13ce66" inactive-color="#ff4949" @change="changeEnable('loopExec', enableLoop)"/> 
                </el-col>
            </el-row>
            <el-form label-width="90px" style="padding-right: 30px; margin-top: 10px" :disabled="!enableLoop" :model="loop" :rules="rules" ref="loop">
                <el-row :gutter="30">
                    <el-col :span="12">
                        <el-form-item label="循环类型" prop="type">
                            <el-radio-group  v-model="loop.type" @change="changeLoop">
                                <el-radio :label="'FOR'">For循环</el-radio>
                                <el-radio :label="'WHILE'">While循环</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="循环步数" prop="num">
                            <el-input-number size="small" style="width: 180px" v-model="loop.num" @change="changeLoop"
                                label="循环执行接口数" step-strictly :step="1" :min="1"/>
                            <el-tooltip content="循环步数：以本接口开始计算往后的n个接口请求都在循环范围内" placement="bottom">
                                <i class="el-icon-info"></i>
                            </el-tooltip>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="30" v-if="loop.type==='FOR'">
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='FOR'" label="循环索引" prop="indexName">
                            <el-input size="small" style="width: 300px" v-model="loop.indexName" 
                            @change="changeLoop" placeholder="自定义索引名称 同用例下的循环索引不得重名"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='FOR'" label="循环次数" prop="times">
                            <el-input size="small" style="width: 200px" v-model="loop.times"
                            @change="changeLoop" placeholder="循环执行次数"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="30" v-if="loop.type==='WHILE'">
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="判断对象" prop="target">
                            <el-input size="small" style="width: 300px" v-model="loop.target"
                            @change="changeLoop" placeholder="请输入判断对象 即关联参数 填写{{}}格式"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="判断条件" prop="assertion">
                            <el-select size="small" style="width: 180px" @change="changeLoop"
                            placeholder="判断条件" filterable v-model="loop.assertion">
                                <el-option v-for="item in functionList" :key="item.id" :label="item.name" :value="item.id"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="30" v-if="loop.type==='WHILE'">
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="预期值" prop="expect">
                            <el-input size="small" style="width: 300px" v-model="loop.expect"
                            @change="changeLoop" placeholder="请输入预期值"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item v-if="loop.type==='WHILE'" label="超时时间" prop="timeout">
                            <el-input-number size="small" style="width: 180px" v-model="loop.timeout"
                            @change="changeLoop" label="超时时间" step-strictly :step="10" :min="0"/>  ms
                        </el-form-item>
                    </el-col>
                </el-row> 
            </el-form>
        </el-card>
        <!-- 条件控制器条件编辑 -->
        <el-card style="margin-top: 20px">
            <el-row style="margin-bottom: 20px">
                <el-col :span="21">
                    <p class="tip">
                        <span>条件控制器</span>
                        <el-tooltip content="开启后只有当所有条件均为真当前接口才会执行，否则跳过" placement="bottom">
                            <i class="el-icon-info"></i>
                        </el-tooltip>
                    </p>
                </el-col>
                <el-col :span="3">
                    <div style="font-size: 16px; margin-top: -1px; margin-right: 5px; float:left;color: #409EFF">
                        {{enableCondition?'已启用':'已禁用'}}
                    </div>
                    <el-switch v-model="enableCondition" active-color="#13ce66" inactive-color="#ff4949" @change="changeEnable('whetherExec', enableCondition)"/> 
                </el-col>
            </el-row>
            <el-form v-for="(condition, index) in conditions" :key="index" :model="condition" :disabled="!enableCondition">
                <el-row :gutter="20">
                    <el-col :span="1" style=" text-align: center;">
                        <el-form-item>
                            <div style="font-size:16px; color: #409EFF">{{index===0?'If':'And'}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item>
                            <el-input size="small" placeholder="请输入判断对象 即关联参数 填写{{}}格式" 
                                @change="changeCondition" v-model="condition.target"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item>
                            <el-select size="small" placeholder="判断条件"
                                @change="changeCondition" filterable v-model="condition.assertion">
                                <el-option v-for="item in functionList" :key="item.id" :label="item.name" :value="item.id"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item>
                            <el-input size="small" @change="changeCondition"
                                 placeholder="请输入预期值" v-model="condition.expect"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button :disabled="conditions.length === 1" size="mini" type="text" @click="removeCondition(index)">删除</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <el-button :disabled="!enableCondition" size="small" icon="el-icon-plus" type="text" @click="addCondition">新增条件</el-button>
        </el-card>
    </div>
</template>
<script>
export default {
    name: 'LogicControl',
    props:{
        logics: Array,
    },
    data() {
        return{
            enableLoop: false,
            enableCondition: false,
            functionList: [],
            conditions: [{target:null,assertion:'equals',expect:null}],
            loop: {
                type: "FOR",
                target: "",
                assertion: "equals",
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
        }
    },
    created() {
        this.initData();
        this.getAssertion();
    },
    methods: {
        initData(){
            for(let i=0;i<this.logics.length;i++){
                if(this.logics[i].name === 'loopExec'){
                    this.enableLoop = true;
                    if(this.logics[i].value === "{}"){
                        this.loop = {
                            type: "FOR",
                            target: "",
                            assertion: "equals",
                            expect: "",
                            timeout: 0,
                            indexName: "",
                            times: 1,
                            num: 1
                        };
                    }else{
                        this.loop = JSON.parse(this.logics[i].value);
                    }
                }else if(this.logics[i].name === 'whetherExec'){
                    this.enableCondition = true;
                    this.conditions = JSON.parse(this.logics[i].value);
                }
            }
        },
        getAssertion(){
            let url = '/autotest/system/assertion/list';
            this.$get(url, response =>{
                this.functionList = response.data;
            });
        },
        changeEnable(type, enable){
            if(!enable){ // 只处理禁用 禁用时删除 启用时不做处理 只有编辑时才处理
                for(let i=0;i<this.logics.length;i++){
                    if(this.logics[i].name === type){
                        this.logics.splice(i, 1);
                    }
                }
            }
        },
        changeLoop(){
            this.$refs["loop"].validate(valid => {
                if (valid) {
                    let edit = false;
                    for(let i=0;i<this.logics.length;i++){
                        if(this.logics[i].name === 'loopExec'){
                            this.logics[i].value = JSON.stringify(this.loop);
                            edit = true;
                            break;
                        }
                    }
                    if(!edit){  // 没有则新增
                        this.logics.push({name: 'loopExec', value: JSON.stringify(this.loop)});
                    }
                }else{
                    return false;
                }
            })
        },
        changeCondition(){
            let edit = false;
            for(let i=0;i<this.logics.length;i++){
                if(this.logics[i].name === 'whetherExec'){
                    this.logics[i].value = JSON.stringify(this.conditions);
                    edit = true;
                    break;
                }
            }
            if(!edit){  // 没有则新增
                this.logics.push({name: 'whetherExec', value: JSON.stringify(this.conditions)});
            }
        },
        addCondition(){
            this.conditions.push({target:"", assertion:"equals", expect:""});
        },
        removeCondition(index){
            this.conditions.splice(index, 1);
            this.changeCondition();
        },
    }
}
</script>
<style scoped>

</style>
