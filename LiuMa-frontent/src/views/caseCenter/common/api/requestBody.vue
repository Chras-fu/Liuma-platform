/**
* 请求体
*/
<template>
    <div>
        <el-row>
            <el-col :span="20">
                <el-radio-group v-model="reqBody.type" size="small" style="margin-bottom:10px" @change="changeType">
                    <el-radio label="form-data">form-data</el-radio>
                    <el-radio label="form-urlencoded">x-www-form-urlencoded</el-radio>
                    <el-radio label="json">json</el-radio>
                    <el-radio :label="rawType">raw</el-radio>
                    <el-radio label="file">file</el-radio>
                </el-radio-group>
            </el-col>
            <el-col :span="4" >
                <el-dropdown v-if="reqBody.type === 'text' || reqBody.type === 'xml' ||reqBody.type === 'html'"
                    size="small" @command="changeRaw" placement="bottom" style="float:right">
                    <span class="dropdown-proj">
                        {{ reqBody.type }}<i class="el-icon-caret-bottom el-icon--right"/>
                    </span>
                    <template v-slot:dropdown>
                        <el-dropdown-menu>
                            <div class="dropdown-content">
                                <el-dropdown-item :command="item" v-for="(item, index) in rawList" :key="index">
                                {{ item }}
                                </el-dropdown-item>
                            </div>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-col>
        </el-row>
        <!-- form格式数据 -->
        <div v-if="reqBody.type==='form-data' || reqBody.type==='form-urlencoded'">
            <request-form :reqForm="reqBody.form" style="width: 100%"/>
        </div>
        <!-- json格式数据 -->
        <div v-if="reqBody.type==='json'" class="req-json-editor">
            <vue-json-editor style="height:480px;" v-model="jsonData" :showBtns="false" mode="code"
                    lang="zh" @json-change="onJsonChange"/>
        </div>
        <!-- raw格式数据 -->
        <div v-if="reqBody.type === 'text' || reqBody.type === 'xml' ||reqBody.type === 'html'">
            <code-edit ref="editor" :data.sync='reqBody.raw' :height='480' :mode="reqBody.type"/>
        </div>
        <!-- 文件 -->
        <div v-if="reqBody.type==='file'">
            <request-file :reqFile="reqBody.file" style="width: 100%"/>
        </div>
    </div>
</template>
<script>
import vueJsonEditor from 'vue-json-editor'
import RequestForm from './requestForm';
import RequestFile from './requestFile';
import CodeEdit from '@/views/common/business/codeEdit'

export default {
    components: { RequestForm, RequestFile, CodeEdit, vueJsonEditor },
    name: 'RequestHeader',
    props:{
        reqBody:Object,
    },
    data() {
        return{
            rawType: 'text',
            rawList: ['text','xml','html'],
            jsonData: null,
        }
    },
    created(){
        if(this.reqBody.json){
            this.jsonData = JSON.parse(this.reqBody.json);
        }else{
            this.jsonData = {};
        }
        if(this.reqBody.type === 'text' || this.reqBody.type === 'xml' || this.reqBody.type === 'html'){
            this.rawType = this.reqBody.type;
        }
    },
    watch:{
        reqBody(){
            if(this.reqBody.json){
                this.jsonData = JSON.parse(this.reqBody.json);
            }else{
                this.jsonData = {};
            }
            if(this.reqBody.type === 'text' || this.reqBody.type === 'xml' || this.reqBody.type === 'html'){
                this.rawType = this.reqBody.type;
            }
        }
    },
    methods: {
        onJsonChange(value){
            this.reqBody.json = JSON.stringify(value);
        },
        changeRaw(command){
            this.rawType = command;
            this.reqBody.type = command;
        },
        changeType(){
            this.$forceUpdate();
        }
    }
}
</script>
<style scoped>
.req-json-editor >>> .jsoneditor-vue{
    height: 480px;
}
.req-json-editor >>> .ace-jsoneditor{
    height: 480px !important;
}
.req-json-editor >>> .jsoneditor-menu{
    display: none;
}
.req-json-editor >>> .jsoneditor{
    border: none;
}
</style>
