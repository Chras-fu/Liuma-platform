/**
* 请求Query参数
*/ 
<template>
    <div>
        <el-table :data="reqQuery">
            <el-table-column label="QUERY参数名称" prop="name">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入QUERY参数名称" v-model="reqQuery[scope.$index].name"/>
                </template>
            </el-table-column>
            <el-table-column label="必填项" width="200px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 90%" v-model="reqQuery[scope.$index].required">
                        <el-option v-for="item in requires" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="QUERY参数值" prop="value">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 90%" placeholder="请输入QUERY参数值" v-model="reqQuery[scope.$index].value"/>
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
  name: 'RequestQuery',
  props:{
    reqQuery:Array,
  },
  data() {
      return{
        requires: [
            {label: '必填', value: true},
            {label: '非必填', value: false},
        ]
      }
  },
  methods: {
    add(){
        this.reqQuery.push({name:"", value:"", required: true});
    },
    remove(index){
        this.reqQuery.splice(index, 1);
    },
    deleteAll(){
        this.reqQuery.splice(0, this.reqQuery.length);
    },
  }
    
}
</script>
<style scoped>

</style>