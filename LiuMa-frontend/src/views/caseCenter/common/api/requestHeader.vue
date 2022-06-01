/**
* 请求头
*/ 
<template>
    <div>
        <el-table :data="reqHeader">
            <el-table-column label="请求头名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入请求头名称" v-model="reqHeader[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="请求头内容" prop="value">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入请求头内容" v-model="reqHeader[scope.$index].value"/>
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
  name: 'RequestHeader',
  props:{
    reqHeader:Array,
  },
  methods: {
    add(){
        this.reqHeader.push({name:"",value:""});
    },
    remove(index){
        this.reqHeader.splice(index, 1);
    },
    deleteAll(){
        this.reqHeader.splice(0, this.reqHeader.length);
    },
  }
    
}
</script>
<style scoped>

</style>