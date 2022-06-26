/**
* 响应断言
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="12">
                <div class="req-json-editor">
                    <vue-json-editor style="height:478px;" v-model="jsonData" :showBtns="false" mode="code"
                    lang="zh" @json-change="onJsonChange"/>
                </div>
            </el-col>
            <el-col :span="12">
                <div class="tree-data">
                    <json-tree :data="treeData" @addContent="addContent($event)"/>
                </div>
            </el-col>
        </el-row>
    </div>
</template>
<script>
import vueJsonEditor from 'vue-json-editor'
import JsonTree from './jsonTree'
export default {
    name: 'JsonPath',
    props:{
        apiId: String,
    },
    components: { vueJsonEditor, JsonTree },
    data() {
        return{
            jsonData:{},
            treeData:[],
        }
    },
    created() {
        this.getJson();
    },
    methods: {
        getJson(){
            let url = '/autotest/case/api/report/' + this.apiId;
            this.$get(url, response =>{
                this.jsonData = response.data;
                if(this.jsonData === null){
                    this.$message.warning('未获取到正确接口响应 请先执行接口请求');
                }else if (this.jsonData.length === 0){
                    this.$message.warning('解析接口响应数据失败 请确认是否为json');
                }else{
                    this.treeData.splice(0, this.treeData.length);
                    this.toJsonPath(this.treeData, this.jsonData, "$");
                }
            });
        },
        onJsonChange(value){
            this.jsonData = value;
            this.treeData.splice(0, this.treeData.length);
            this.toJsonPath(this.treeData, this.jsonData, "$");
        },
        toJsonPath(arr, json, basePath) {
            // 生成jsonpath
            const type = this.validateType(json)
            if (type === 'object') {
                for(let key in json) {
                    const item = {
                        key,
                        path: `${basePath}.${key}`,
                        childValue: json[key]
                    }
                    const childType = this.validateType(json[key])
                    item.type = childType
                    if (childType === 'object' || childType === 'array') {
                        item.leaf = true
                        item.children = []
                        this.toJsonPath(item.children, json[key], item.path);
                    } else {
                        item.leaf = false;
                        item.value = json[key];
                    }
                    arr.push(item);
                }
            } else if (type === 'array') {
                json.forEach((item,index) => {
                    const childType = this.validateType(item);
                    const obj = {
                        key: index,
                        childValue: item
                    };
                    obj.type = childType;
                    obj.path = `${basePath}[${index}]`;
                    if (childType === 'object' || childType === 'array') {
                        obj.leaf = true;
                        obj.children = [];
                        this.toJsonPath(obj.children, item, obj.path);
                    } else {
                        obj.value = item;
                        obj.leaf = false;
                    }
                    arr.push(obj);
                })
            }
        },
        validateType(val) {
            // 校验JSON数据类型
            const type = typeof val
            if (type === 'object') {
                if (Array.isArray(val)) {
                return 'array';
                } else if (val === null) {
                return 'null';
                } else {
                return 'object';
                }
            } else {
                switch(type) {
                case 'boolean':
                    return 'boolean';
                case 'string':
                    return 'string';
                case 'number':
                    return 'number';
                default:
                    return 'error';
                }
            }
        },
        addContent(item){
            this.$emit('addContent', item);
        }
    }
}
</script>
<style scoped>
.req-json-editor{
    border: 1px solid rgb(219, 219, 219);
}
.req-json-editor >>> .jsoneditor-vue{
    height: 478px;
}
.req-json-editor >>> .ace-jsoneditor{
    height: 478px !important;
}
.req-json-editor >>> .jsoneditor-menu{
    display: none;
}
.req-json-editor >>> .jsoneditor{
    border: none;
}
.tree-data {
    height: 480px;
    border: 1px solid rgb(219, 219, 219);
    overflow: hidden;
    overflow-x: scroll;
    white-space: nowrap;
    box-sizing: border-box;
    padding: 15px 10px;
    overflow-y: scroll;
}       
</style>