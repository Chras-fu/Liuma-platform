/**
* 请求文件
*/ 
<template>
    <div>
        <el-table :data="reqFile">
            <el-table-column label="文件" prop="file">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 50%" v-model="reqFile[scope.$index].id" filterable clearable placeholder="请选择文件" @change="changeFile($event, scope.$index)">
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
  name: 'RequestFile',
  props:{
    reqFile:Array,
  },
  data(){
      return{
          files: [],
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
        this.reqFile.push({id:"",name:""});
    },
    remove(index){
        this.reqFile.splice(index, 1);
    },
    deleteAll(){
        this.reqFile.splice(0, this.reqFile.length);
    },
    changeFile(val, index){
        for(let i=0; i<this.files.length; i++){
            if(val === this.files[i].id){
                this.reqFile[index].name = this.files[i].name;
                break;
            }
        }
    }
  }
    
}
</script>
<style scoped>

</style>