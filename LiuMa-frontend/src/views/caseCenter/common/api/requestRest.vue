/**
* 请求Rest参数
*/ 
<template>
    <div>
        <el-table :data="reqRest">
            <el-table-column label="REST参数名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入REST参数名称" v-model="reqRest[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="REST参数值" prop="value">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入REST参数值" v-model="reqRest[scope.$index].value"/>
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
  name: 'RequestRest',
  props:{
    reqRest:Array,
  },
  methods: {
    add(){
        this.reqRest.push({name:"",value:""});
    },
    remove(index){
        this.reqRest.splice(index, 1);
    },
    deleteAll(){
        this.reqRest.splice(0, this.reqRest.length);
    },
  }
    
}
</script>
<style scoped>

</style>