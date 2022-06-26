/**
* 用例执行结果
*/
<template>
    <div>
        <el-dialog title="执行结果" :visible.sync="resultVisable" width="800px" destroy-on-close :close-on-click-modal="false" @close="cancel">
            <el-row :gutter="40" style="margin-top:-20px">
                <el-form>
                <el-col :span="12">
                    <el-form-item label="执行结果:">
                        <span v-if="result.status==='success'" class="lm-success"><i class="el-icon-success"/> 成功</span>
                        <span v-if="result.status==='fail'" class="lm-fail"><i class="el-icon-warning"/> 失败</span>
                        <span v-if="result.status==='error'" class="lm-error"><i class="el-icon-error"/> 错误</span>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="执行时长:">
                        <span>{{result.during}}</span>
                    </el-form-item>
                </el-col>
                </el-form>
            </el-row>
            <el-table :data="result.transList" v-loading="loading" element-loading-text="执行中 请耐心等待">
                <el-table-column label="执行结果" prop="status" width="100px">
                    <template slot-scope="scope">
                        <span v-if="scope.row.status==='success'" class="lm-success"><i class="el-icon-success"/> 成功</span>
                        <span v-if="scope.row.status==='fail'" class="lm-fail"><i class="el-icon-warning"/> 失败</span>
                        <span v-if="scope.row.status==='error'" class="lm-error"><i class="el-icon-error"/> 错误</span>
                        <span v-if="scope.row.status==='skip'" class="lm-info"><i class="el-icon-remove"/> 跳过</span>
                    </template>
                </el-table-column>
                <el-table-column label="事务名称" prop="transName" min-width="150px"/>
                <el-table-column label="事务内容" prop="content" min-width="200px"/>
                <el-table-column label="执行日志" prop="execLog" width="100px">
                    <template slot-scope="scope">
                        <el-button size="small" type="text" @click="viewLog(scope.row.execLog)">查看日志</el-button>
                    </template>
                </el-table-column>
                <el-table-column label="响应时长" prop="during" v-if="caseType ==='API'" width="100px"/>
                <el-table-column label="执行截图" prop="screenshotList" v-if="caseType ==='WEB'" width="100px">
                    <template slot-scope="scope">
                        <el-button v-if="scope.row.screenshotList.length !== 0" size="small" type="text" @click="viewImage(scope.row)">查看</el-button>
                        <el-image-viewer :z-index="imageZindex" v-if="scope.row.showViewer" :on-close="()=>{scope.row.showViewer=false}" :url-list="scope.row.screenshotList"/>
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
        <el-dialog title="查看日志" :visible.sync="logVisable" width="600px" destroy-on-close @close="closeLog">
            <span v-html="log"/>
        </el-dialog>
    </div>
</template>
<script>
import { PopupManager } from 'element-ui/lib/utils/popup'
export default {
    name: 'RunResult',
    components: {
        'el-image-viewer': () => import('element-ui/packages/image/src/image-viewer')
    },
    props:{
        taskId: String,
        caseType: String,
        resultVisable:{
            type: Boolean,
            default: false
        }
    },
    data() {
        return{
            loading: true,
            result: {
                status: null,
                during: null,
            },
            timer: null,
            logVisable: false,
            log: "",
            imageZindex:2040
        }
    },
    created(){
        if(this.resultVisable){
            this.result = {};
            this.getResult();
        }
    },
    watch: {
        resultVisable(){
            if(this.resultVisable){
                this.result = {};
                this.getResult();
            }
        }
    },
    methods: {
        getResult(){
            this.loading = true;
            this.sendRequest();
            this.timer = window.setInterval(() => {
                setTimeout(() => {
                    this.sendRequest()
                }, 0)
            },3000);
        },
        sendRequest(){
            let url = "/autotest/report/debug/" + this.taskId;
            this.$get(url, response => {
                this.result = response.data;
                if(this.result){
                    if(this.caseType === 'WEB'){
                        for(let i=0;i<this.result.transList.length;i++){
                            let trans = this.result.transList[i];
                            trans.screenshotList = JSON.parse(trans.screenshotList);
                            trans.showViewer = false;
                        }
                    }
                    this.loading = false;
                    window.clearInterval(this.timer);
                }
            });
        },
        cancel(){
            this.result = {};
            this.$emit("closeResult");
        },
        viewLog(log){
            this.log = log;
            this.logVisable = true;
        },
        viewImage(row){
            this.getZindex();
            row.showViewer=true;
        },
        closeLog(){
            window.clearInterval(this.timer);
            this.log = "";
            this.logVisable = false;
        },
        getZindex(){
	        this.$nextTick(() =>{
	            this.imageZindex = PopupManager.nextZIndex();
	        })
	    }
    }
}
</script>
<style scoped>

</style>
