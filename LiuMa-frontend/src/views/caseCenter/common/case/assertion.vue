/**
* 响应断言
*/ 
<template>
    <div>
        <el-table :data="assertion"  size="small">
            <el-table-column label="取值来源" prop="from" width="130px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 95%" placeholder="取值来源" v-model="assertion[scope.$index].from">
                        <el-option v-for="item in fromList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="取值函数" prop="expression" min-width="360px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 35%" placeholder="取值方式" v-model="assertion[scope.$index].method">
                        <el-option v-for="item in methodList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                    <el-input size="small" style="width: 60%" placeholder="请输入表达式" v-model="assertion[scope.$index].expression"/>
                </template>
            </el-table-column>
            <el-table-column label="断言方法" prop="assertion" width="150px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" placeholder="断言方法" filterable v-model="assertion[scope.$index].assertion">
                        <el-option v-for="item in functionList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="预期值" prop="expect" min-width="160px">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入预期值" v-model="assertion[scope.$index].expect"/>
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
        
    </div>
</template>
<script>

export default {
    name: 'Assertion',
    props:{
        assertion: Array,
    },
    data() {
        return{
            fromList:[
                {label: "响应码", value: "resCode"},
                {label: "响应头", value: "resHeader"},
                {label: "响应体", value: "resBody"},
            ],
            methodList:[
                {label: "jsonpath", value: "jsonpath"},
                {label: "正则表达式", value: "regular"},
            ],
            functionList: []
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
            this.assertion.push({from:"", method:"", expression:"", assertion:"", expect:""});
        },
        remove(index){
            this.assertion.splice(index, 1);
        },
        deleteAll(){
            this.assertion.splice(0, this.assertion.length);
        },
    }
}
</script>
<style scoped>

</style>