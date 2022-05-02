/**
* 请求Form参数
*/
<template>
    <div>
        <el-table :data="reqForm">
            <el-table-column label="参数名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入参数名称" v-model="reqForm[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="参数类型" prop="type" width="200px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="reqForm[scope.$index].type">
                        <el-option v-for="item in types" :key="item" :label="item" :value="item"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="必填项" prop="required" width="200px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="reqForm[scope.$index].required">
                        <el-option v-for="item in requires" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="参数内容" prop="value">
                <template slot-scope="scope">
                    <!-- 非文件时输入框 -->
                    <el-input v-if="reqForm[scope.$index].type !== 'File'" size="small" style="width: 90%" placeholder="请输入参数内容" v-model="reqForm[scope.$index].value"/>
                    <!-- 文件时选择框 -->
                    <el-select v-if="reqForm[scope.$index].type === 'File'"  size="small" style="width: 90%;" v-model="reqForm[scope.$index].value" filterable clearable placeholder="请选择文件">
                        <el-option v-for="item in files" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="small" type="text" @click="remove(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="add">新增</el-button>
        <el-button size="small" type="text" @click="deleteAll">删除全部</el-button>
    </div>
</template>
<script>
export default {
  name: 'RequestForm',
  props:{
    reqForm:Array,
  },
  data() {
      return{
        requires: [
            {label: '必填', value: true},
            {label: '非必填', value: false},
        ],
        types: ["String", "Int", "Float", "Boolean", "JSONObject", "JSONArray", "File"],
        files:[]
      }
    },
    created() {
        this.getFiles();
    },
    methods: {
        getFiles(){
            let url = '/autotest/file/all/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.files = response.data;
            });
        },
        add(){
            this.reqForm.push({name:"", value:"", type:"String", required: true});
        },
        remove(index){
            this.reqForm.splice(index, 1);
        },
        deleteAll(){
            this.reqForm.splice(0, this.reqForm.length);
        },
    }

}
</script>
<style scoped>

</style>
