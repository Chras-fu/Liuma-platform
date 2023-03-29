/**
* 用例执行结果
*/
<template>
    <div>
        <el-dialog title="执行结果" :visible.sync="resultVisable" width="650px" destroy-on-close :close-on-click-modal="false" @close="cancel">
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
                <el-table-column label="执行结果" prop="status" width="80px">
                    <template slot-scope="scope">
                        <span v-if="scope.row.status==='success'" class="lm-success"><i class="el-icon-success"/> 成功</span>
                        <span v-if="scope.row.status==='fail'" class="lm-fail"><i class="el-icon-warning"/> 失败</span>
                        <span v-if="scope.row.status==='error'" class="lm-error"><i class="el-icon-error"/> 错误</span>
                        <span v-if="scope.row.status==='skip'" class="lm-info"><i class="el-icon-remove"/> 跳过</span>
                    </template>
                </el-table-column>
                <el-table-column :label="caseType ==='API'?'接口名称':'操作名称'" prop="transName" min-width="120px" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span class="long-text">{{scope.row.transName}}</span>
                    </template>
                </el-table-column>
                <el-table-column :label="caseType ==='API'?'接口地址':'操作元素'" prop="content" width="100px" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span class="long-text">{{scope.row.content}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="步骤描述" prop="description" min-width="120px" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span class="long-text">{{scope.row.description}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="执行日志" prop="execLog" width="80px">
                    <template slot-scope="scope">
                        <el-button size="small" type="text" @click="viewLog(scope.row.execLog)">查看日志</el-button>
                    </template>
                </el-table-column>
                <el-table-column label="响应时长" prop="during" v-if="caseType ==='API'" width="80px"/>
                <el-table-column label="执行截图" prop="screenshotList" v-else width="80px">
                    <template slot-scope="scope">
                        <el-button v-if="scope.row.screenshotList.length !== 0" size="small" type="text" @click="viewImage(scope.row)">查看</el-button>
                        <el-image-viewer :z-index="imageZindex" v-if="scope.row.showViewer" :on-close="()=>{scope.row.showViewer=false}" :url-list="scope.row.screenshotList"/>
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
        <el-dialog title="查看日志" :visible.sync="logVisable" width="500px" destroy-on-close @close="closeLog">
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
            result: {},
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
                if(response.data){
                    let data = response.data;
                    if(this.caseType !== 'API'){
                        for(let i=0;i<data.transList.length;i++){
                            let trans = data.transList[i];
                            trans.screenshotList = JSON.parse(trans.screenshotList);
                            trans.showViewer = false;
                        }
                    }
                    this.result = data;
                    this.loading = false;
                    window.clearInterval(this.timer);
                    this.$emit("endRun");
                }
            });
        },
        cancel(){
            this.result = {};
            this.$emit("closeResult");
        },
        viewLog(log){
            let req = log.substring(log.indexOf("<span>请求体: ")+11, log.indexOf("</span><br>"));
            if(req){
                log = log.replace(req, req.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
            }
            let res = log.substring(log.indexOf("<br><b>响应体: ")+12, log.indexOf("</b><br><br>"));
            if(res){
                log = log.replace(res, res.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
            }
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
.long-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 100%;
    text-align: left;
}
</style>
