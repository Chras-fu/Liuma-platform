/**
 * 测试追踪  报告详情
 */
<template>
  <div>
    <!-- 报告基本信息 -->
    <div class="report-header">
        <div style="font-size: 20px">
            <span>{{report.name}}</span>
        </div>
        <div style="display: flex; margin-left: 20px;font-size:12px">
            <span style="margin-right: 10px">执行状态: {{report.format}}</span>
            <el-progress style="width:120px;" :percentage="report.progress" :color="report.color"/>
        </div>
    </div>
    <div class="report-base">
        <el-row :gutter="40" style="margin: 20px -20px">
            <el-col :span="3">
                <span>成功： <span class="lm-success">{{report.passCount}}</span></span>
            </el-col>
            <el-col :span="3">
                <span>失败： <span class="lm-fail">{{report.failCount}}</span></span>
            </el-col>
            <el-col :span="3">
                <span>错误： <span class="lm-error">{{report.errorCount}}</span></span>
            </el-col>
        </el-row>
        <el-row :gutter="40" style="margin: 20px -20px">
            <el-col :span="5">
                <span>开始时间： {{report.startTime}}</span>
            </el-col>
            <el-col :span="5">
                <span>结束时间： {{report.endTime}}</span>
            </el-col>
            <el-col :span="5">
                <span>执行时长： {{report.during}}</span>
            </el-col>
        </el-row>  
    </div>
    <!--结果列表-->
    <el-table size="small" :data="report.collectionList" stripe v-loading="loading">
        <el-table-column type="expand" width="40px">
            <template slot-scope="collectionData">
                <div style="padding-left: 40px">
                <el-table size="mini" :data="collectionData.row.caseList" stripe>
                    <el-table-column type="expand" width="40px">
                        <template slot-scope="caseData">
                            <div style="padding-left: 40px">
                            <el-table size="mini" :data="caseData.row.transList" stripe>
                                <el-table-column label="执行结果" prop="status" width="120px">
                                    <template slot-scope="scope">
                                        <span v-if="scope.row.status==='success'" class="lm-success"><i class="el-icon-success"/> 成功</span>
                                        <span v-if="scope.row.status==='fail'" class="lm-fail"><i class="el-icon-warning"/> 失败</span>
                                        <span v-if="scope.row.status==='error'" class="lm-error"><i class="el-icon-error"/> 错误</span>
                                    </template>
                                </el-table-column>
                                <el-table-column label="事务名称" prop="transName" min-width="150px"/>
                                <el-table-column label="事务内容" prop="content" min-width="200px"/>
                                <el-table-column label="执行日志" prop="execLog" width="120px">
                                    <template slot-scope="scope">
                                        <el-button size="small" type="text" @click="viewLog(scope.row.execLog)">查看日志</el-button>
                                    </template>
                                </el-table-column>
                                <el-table-column label="响应时长" prop="during" v-if="caseData.row.caseType ==='API'" width="120px"/>
                                <el-table-column label="执行截图" prop="screenshotList" v-if="caseData.row.caseType ==='WEB'" width="120px">
                                    <template slot-scope="scope">
                                        <el-button v-if="scope.row.screenshotList.length !== 0" size="small" type="text" @click="scope.row.showViewer=true">查看</el-button>
                                        <el-image-viewer v-if="scope.row.showViewer" :on-close="()=>{scope.row.showViewer=false}" :url-list="scope.row.screenshotList"/>
                                    </template>
                                </el-table-column>
                            </el-table>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="执行结果" prop="status" width="120px">
                        <template slot-scope="scope">
                            <span v-if="scope.row.status==='success'" class="lm-success"><i class="el-icon-success"/> 成功</span>
                            <span v-if="scope.row.status==='fail'" class="lm-fail"><i class="el-icon-warning"/> 失败</span>
                            <span v-if="scope.row.status==='error'" class="lm-error"><i class="el-icon-error"/> 错误</span>
                            <span v-if="scope.row.status==='skip'" class="lm-info"><i class="el-icon-remove"/> 跳过</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="用例名称" prop="caseName" min-width="150px">
                        <template slot-scope="scope">
                            <el-button type="text" size="mini" @click="viewCase(scope.row)">{{scope.row.caseName}}</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="用例描述" prop="caseDesc" min-width="200px"/>
                    <el-table-column label="开始时间" prop="startTime" width="150px"/>
                    <el-table-column label="结束时间" prop="endTime" width="150px"/>
                    <el-table-column label="执行时长" prop="during" width="120px"/>
                </el-table>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="collectionVersion" label="集合版本" width="160"/>
        <el-table-column prop="collectionName" label="集合名称" min-width="200"/>
        <el-table-column prop="caseTotal" label="用例总数" width="120px"/>
        <el-table-column prop="passCount" label="成功数" width="120px"/>
        <el-table-column prop="failCount" label="失败数" width="120px"/>
        <el-table-column prop="errorCount" label="错误数" width="120px"/>
    </el-table>
    <el-dialog title="查看日志" :visible.sync="logVisable" width="600px" destroy-on-close @close="closeLog">
        <span v-html="log"/>
    </el-dialog>
  </div>
</template>

<script>
import {timestampToTime} from '@/utils/util'
export default {
    components: {
        'el-image-viewer': () => import('element-ui/packages/image/src/image-viewer')
    },
    data() {
        return{
            loading:false,
            report: {},
            logVisable: false,
            log: ""
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["测试追踪", "测试报告", "报告详情"]);
        this.getdata(this.$route.params);
    },
    methods: {
        getdata(param) {
            let reportId = param.reportId;
            this.loading = true;
            let url = "/autotest/report/run/" + reportId;
            this.$get(url, response => {
                let report = response.data;
                if(report.status === 'success'){
                    report.format = 'SUCCESS';
                    report.color = '#67C23A';
                }else if(report.status === 'fail'){
                    report.format = 'FAIL';
                    report.color = '#E6A23C';
                }else if(report.status === 'error'){
                    report.format = 'ERROR';
                    report.color = '#F56C6C';
                }else if(report.status === 'skip'){
                    report.format = 'SKIP';
                    report.color = '#535457';
                }else if(report.status === 'prepared'){
                    report.format = '等待执行';
                    report.color = '#409EFF';
                }else if(report.status === 'running'){
                    report.format = "RUNNING";
                    report.color = '#409EFF';
                }else if(report.status === 'discontinue'){
                    report.format = "已终止";
                    report.color = '#535457';
                }
                if(!report.startTime){
                    report.startTime = Date.now();
                }
                if(!report.endTime){
                    report.endTime = Date.now();
                }
                report.during = (report.endTime - report.startTime)/1000 + 'S';
                report.startTime = timestampToTime(report.startTime);
                report.endTime = timestampToTime(report.endTime);
                for(let i=0;i<report.collectionList.length;i++){
                    let collection = report.collectionList[i];
                    for(let j=0;j<collection.caseList.length;j++){
                        let collectionCase = collection.caseList[j];
                        collectionCase.startTime = timestampToTime(collectionCase.startTime);
                        collectionCase.endTime = timestampToTime(collectionCase.endTime);
                        if(collectionCase.caseType === 'WEB'){
                        for(let k=0;k<collectionCase.transList.length;k++){
                            let trans = collectionCase.transList[k];
                            trans.screenshotList = JSON.parse(trans.screenshotList);
                            trans.showViewer = false;
                        }
                    }
                    }
                }
                this.report = report;
                this.loading = false;
            });
        },
        viewCase(row){
            if (row.caseType == "API"){
              this.$router.push({path: '/caseCenter/caseManage/apiCase/edit/' + row.caseId})
            }else if (row.caseType == "WEB"){
              this.$router.push({path: '/caseCenter/caseManage/webCase/edit/' + row.caseId})
            }
        },
        viewLog(log){
            this.log = log;
            this.logVisable = true;
        },
        closeLog(){
            this.log = "";
            this.logVisable = false;
        }
    }
}
</script>

<style scoped>
.report-header{
    border-bottom: 1px solid rgb(219, 219, 219);
    height: 48px;
    display: flex;
    margin-bottom: 20px;
    align-items: center;
    margin-top: -18px;
}
.report-base{
    border-bottom: 1px solid rgb(219, 219, 219);
    margin-bottom: 10px;
    margin-top: 10px;
}
</style>