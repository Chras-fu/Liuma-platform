/**
* 关联参数
*/ 
<template>
    <div>
        <el-table :data="relation"  size="small">
            <el-table-column label="取值来源" prop="from" width="130px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 95%" placeholder="取值来源" v-model="relation[scope.$index].from">
                        <el-option v-for="item in fromList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="取值函数" prop="expression" min-width="400px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 35%" placeholder="取值方式" v-model="relation[scope.$index].method">
                        <el-option v-for="item in methodList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                    <el-input size="small" style="width: 60%" placeholder="请输入表达式" v-model="relation[scope.$index].expression"/>
                </template>
            </el-table-column>
            <el-table-column label="关联变量名" prop="name" min-width="200px">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 95%" placeholder="请输入关联变量名" v-model="relation[scope.$index].name"/>
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
        <el-button size="small" type="text" @click="showJsonpath=true">关联辅助</el-button>

        <div v-if="showJsonpath" style="margin: 20px 0px">
            <el-row>
                <el-col :span="22">
                    <p class="tip">
                        <span>关联辅助</span>
                    </p>
                </el-col>
                <el-col :span="2">
                    <el-button size="small" type="primary" @click="showJsonpath=false">关闭</el-button>
                </el-col>
            </el-row>
            <json-path :apiId="apiId" @addContent="addContent($event)"/>
        </div>
        
    </div>
</template>
<script>
import JsonPath from './jsonPath'
export default {
    name: 'Relation',
    props:{
        relation: Array,
        apiId: String
    },
    components: { JsonPath },
    data() {
        return{
            fromList:[
                {label: "响应头", value: "resHeader"},
                {label: "响应体", value: "resBody"},
                {label: "请求头", value: "reqHeader"},
                {label: "请求参数", value: "reqQuery"},
                {label: "请求体", value: "reqBody"},
            ],
            methodList:[
                {label: "jsonpath", value: "jsonpath"},
                {label: "正则表达式", value: "regular"},
            ],
            showJsonpath: false
        }
    },
    methods: {
        add(){
            this.relation.push({from:"", method:"", expression:"", name:""});
        },
        remove(index){
            this.relation.splice(index, 1);
        },
        deleteAll(){
            this.relation.splice(0, this.relation.length);
        },
        addContent(item){
            let relation = {
                from: "resBody", 
                method: "jsonpath", 
                expression: item.path, 
                name: item.key
            }
            this.relation.push(relation);
        }
    }
}
</script>
<style scoped>

</style>