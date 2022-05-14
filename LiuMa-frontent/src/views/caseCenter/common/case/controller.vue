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
                    <el-button v-if="controller[scope.$index].name === 'preScript' || controller[scope.$index].name === 'postScript'"
                        size="mini" type="text" @click="editCode(scope.$index)">编辑脚本</el-button>
                    <el-input v-else-if="controller[scope.$index].name === 'sleepBeforeRun' || controller[scope.$index].name === 'sleepAfterRun'"
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
        <div v-if="showCode" style="margin-top: 20px">
            <el-row>
                <el-col :span="20">
                    <p class="tip">
                        <span>编辑脚本</span>
                        <el-tooltip :content="text" placement="bottom" effect="light">
                        <i class="el-icon-info"></i>
                        </el-tooltip>
                    </p>
                </el-col>
                <el-col :span="4">
                    <el-button size="small" type="cancel" @click="showCode=false">取消</el-button>
                    <el-button size="small" type="primary" @click="saveCode">保存</el-button>
                </el-col>
            </el-row>
            <code-edit ref="editor" :data.sync='code' :height='480' mode="python"/>
        </div>
    </div>
</template>
<script>
import CodeEdit from '@/views/common/business/codeEdit'
export default {
    name: 'Controller',
    components: { CodeEdit },
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
                {label: "前置脚本", name: "preScript"},
                {label: "后置脚本", name: "postScript"},
            ],
            optList: [
                {label: "是", value: true},
                {label: "否", value: false},
            ],
            showCode: false,
            code: "",
            index: null,
            text: "获取关联参数/公共参数使用sys_get(name)函数，保存关联参数使用sys_put(name, val)函数，支持直接使用变量名res_code/res_header/res_data/res_cookies/res_bytes获取响应内容"
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
        },
        editCode(index){
            this.index = index;
            this.code = this.controller[index].value;
            this.showCode = true;
        },
        saveCode(){
            this.controller[this.index].value = this.code;
            this.showCode = false;
        }
    }
}
</script>
<style scoped>

</style>
