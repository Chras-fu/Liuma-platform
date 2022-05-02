/**
* 逻辑控制器
*/
<template>
    <div>
        <el-table :data="controller"  size="small">
            <el-table-column label="名称" prop="name">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 95%" placeholder="设置名称" v-model="controller[scope.$index].name"  @change="changeController($event, scope.$index)">
                        <el-option v-for="item in controllerList" :key="item.name" :label="item.label" :value="item.name"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="值" prop="value">
                <template slot-scope="scope">
                    <el-input v-if="controller[scope.$index].name === 'sleepBeforeRun' | controller[scope.$index].name === 'sleepAfterRun'"
                        size="small" style="width: 95%" placeholder="请输入等待时间 单位秒" v-model="controller[scope.$index].value"/>
                    <el-select v-else size="small" style="width: 95%" v-model="controller[scope.$index].value">
                        <el-option v-for="item in optList" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
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
    name: 'Controller',
    props:{
        controller: Array,
    },
    data() {
        return{
            controllerList:[
                {label: "请求前等待", name: "sleepBeforeRun"},
                {label: "响应后等待", name: "sleepAfterRun"},
                {label: "使用Session", name: "useSession"},
                {label: "保存Session", name: "saveSession"},
                {label: "文件下载缓冲", name: "requireStream"},
                {label: "证书验证", name: "requireVerify"},
            ],
            optList: [
                {label: "是", value: true},
                {label: "否", value: false},
            ]
        }
    },
    methods: {
        add(){
            this.controller.push({name:"", value:null });
        },
        remove(index){
            this.controller.splice(index, 1);
        },
        deleteAll(){
            this.controller.splice(0, this.controller.length);
        },
        changeController(val, index){
            if(val === "sleepBeforeRun" | val ==="sleepAfterRun"){
                this.controller[index].value = 30;
            }else{
                this.controller[index].value = false;
            }
        }
    }
}
</script>
<style scoped>

</style>
