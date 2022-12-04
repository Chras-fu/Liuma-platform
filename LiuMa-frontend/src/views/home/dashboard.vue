/**
 * 首页看板
 */
<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card card-fix">
          <div>API用例总数</div>
          <div class="statistics-total">{{statisticsData.apiCaseTotal}}</div>
          <div>本周新增: {{statisticsData.apiCaseNewWeek}}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card card-fix">
          <div>WEB用例总数</div>
          <div class="statistics-total">{{statisticsData.webCaseTotal}}</div>
          <div>本周新增: {{statisticsData.webCaseNewWeek}}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card card-fix">
          <div>APP用例总数</div>
          <div class="statistics-total">{{statisticsData.appCaseTotal}}</div>
          <div>本周新增: {{statisticsData.appCaseNewWeek}}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card card-fix">
          <div>用例执行总数</div>
          <div class="statistics-total">{{statisticsData.caseRunTotal}}</div>
          <div>今日执行: {{statisticsData.caseRunToday}}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :xl="12" :lg="24">
        <el-card class="box-card card-fix">
          <el-col :span="24">
            <div class="title">近一周用例新增数据</div>
          </el-col>
          <LineBarChart ref="lineBarChart1" id="lineBarChart1" title="" subTitle="" :data="statisticsData.caseAddData" style="height:400px"/>
        </el-card>
      </el-col> 
      <el-col :xl="12" :lg="24">
        <el-card class="box-card card-fix">
          <el-col :span="24">
            <div class="title">近一周用例执行数据</div>
          </el-col>
          <LineBarChart ref="lineBarChart2" id="lineBarChart2" title="" subTitle="" :data="statisticsData.caseRunData" style="height:400px"/>
        </el-card>
      </el-col>   
    </el-row>
    <el-row :gutter="20">
      <el-col :xl="12" :lg="24">
        <el-card class="box-card card-fix">
          <el-col :span="24">
            <div class="title">近一周计划执行TOP10</div>
          </el-col>
          <LineBarChart ref="lineBarChart3" id="lineBarChart3" title="" subTitle="" :data="statisticsData.planRunWeekTop" style="height:400px"/>
        </el-card>
      </el-col> 
      <el-col :xl="12" :lg="24">
        <el-card class="box-card card-fix">
          <el-col :span="24">
            <div class="title">近一周用例失败TOP10</div>
          </el-col>
          <BarChart ref="barChart1" id="barChart1" title="" :data="statisticsData.caseFailWeekTop" style="height: 400px;"/>
        </el-card>
      </el-col> 
    </el-row>
  </div>
</template>

<script>
import BarChart from './charts/BarChart';
import LineBarChart from './charts/lineBarChart';
export default {
  components: {
    BarChart, LineBarChart
  },
  data() {
      return{
        statisticsData: {
          apiCaseTotal: 0,
          apiCaseNewWeek: 0,
          webCaseTotal: 0,
          webCaseNewWeek: 0,
          appCaseTotal: 0,
          appCaseNewWeek: 0,
          caseRunTotal: 0,
          caseRunToday: 0,
          caseAddData: {
            xAxis: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            yLeft: { name:"每日新增数", max: 100},
            yRight:{ name: "用例总数", max: 100},
            legend: ['API用例新增', 'WEB用例新增', 'APP用例新增','API用例总数', 'WEB用例总数', 'APP用例总数'],
            series: [
              {name: "API用例新增",type: 'bar',data: [0,0,0,0,0,0,0]},
              {name: "WEB用例新增",type: 'bar',data: [0,0,0,0,0,0,0]},
              {name: "APP用例新增",type: 'bar',data: [0,0,0,0,0,0,0]},
              {name: "API用例总数",type: 'line',yAxisIndex: 1,data: [0,0,0,0,0,0,0]},
              {name: "WEB用例总数",type: 'line',yAxisIndex: 1,data: [0,0,0,0,0,0,0]},
              {name: "APP用例总数",type: 'line',yAxisIndex: 1,data: [0,0,0,0,0,0,0]}
            ]
          },
          caseRunData:{
            xAxis: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            yLeft: { name:"每日执行数", max: 100},
            yRight:{ name: "通过率(%)", max: 100},
            legend: ['API用例执行数', 'WEB用例执行数', 'APP用例执行数', 'API用例通过率', 'WEB用例通过率', 'APP用例通过率'],
            series: [
              {name: "API用例执行数",type: 'bar',data: [0,0,0,0,0,0,0]},
              {name: "WEB用例执行数",type: 'bar',data: [0,0,0,0,0,0,0]},
              {name: "APP用例执行数",type: 'bar',data: [0,0,0,0,0,0,0]},
              {name: "API用例通过率",type: 'line',yAxisIndex: 1,data: [0,0,0,0,0,0,0]},
              {name: "WEB用例通过率",type: 'line',yAxisIndex: 1,data: [0,0,0,0,0,0,0]},
              {name: "APP用例通过率",type: 'line',yAxisIndex: 1,data: [0,0,0,0,0,0,0]}
            ]
          },
          planRunWeekTop:{
            xAxis: ['plan1', 'plan2', 'plan3', 'plan4', 'plan5', 'plan6', 'plan7', 'plan8', 'plan9', 'plan10'],
            yLeft: { name:"计划执行数", max: 100},
            yRight:{ name: "通过率(%)", max: 100},
            legend: ['执行总数', '成功次数', '平均通过率'],
            series: [
              {name: "执行总数",type: 'bar',data: [0,0,0,0,0,0,0,0,0,0]},
              {name: "成功次数",type: 'bar',data: [0,0,0,0,0,0,0,0,0,0]},
              {name: "平均通过率",type: 'line', yAxisIndex: 1,data: [0,0,0,0,0,0,0,0,0,0]},
            ]
          },
          caseFailWeekTop: {
            x: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            y: ['case1', 'case2', 'case3', 'case4', 'case5', 'case6', 'case7', 'case8', 'case9', 'case10']
          }
        }
      }
  },
  created(){
    this.$root.Bus.$emit('initBread', []);
    this.getData();
  },
  methods: {
    getData(){
      // 获取图表数据
      let url = "/autotest/dashboard/get/" + this.$store.state.projectId;
      this.$get(url, response => {
        let data = response.data;
        if(data.apiCaseTotal !== undefined){
          this.statisticsData.apiCaseTotal = data.apiCaseTotal;
          this.statisticsData.apiCaseNewWeek = data.apiCaseNewWeek;
          this.statisticsData.webCaseTotal = data.webCaseTotal;
          this.statisticsData.webCaseNewWeek = data.webCaseNewWeek;
          this.statisticsData.appCaseTotal = data.appCaseTotal;
          this.statisticsData.appCaseNewWeek = data.appCaseNewWeek;
          this.statisticsData.caseRunTotal = data.caseRunTotal;
          this.statisticsData.caseRunToday = data.caseRunToday;
          
          this.statisticsData.caseAddData.xAxis = data.caseAddData.xAxis;
          this.statisticsData.caseAddData.yLeft.max = this.getMax(data.caseAddData.yMaxLeft);
          this.statisticsData.caseAddData.yRight.max = this.getMaxRight(data.caseAddData.yMaxRight);
          this.statisticsData.caseAddData.series[0].data = data.caseAddData.apiCaseNew;
          this.statisticsData.caseAddData.series[1].data = data.caseAddData.webCaseNew;
          this.statisticsData.caseAddData.series[2].data = data.caseAddData.appCaseNew;
          this.statisticsData.caseAddData.series[3].data = data.caseAddData.apiCaseSum;
          this.statisticsData.caseAddData.series[4].data = data.caseAddData.webCaseSum;
          this.statisticsData.caseAddData.series[5].data = data.caseAddData.appCaseSum;

          this.statisticsData.caseRunData.xAxis = data.caseRunData.xAxis;
          this.statisticsData.caseRunData.yLeft.max = this.getMax(data.caseRunData.yMaxLeft);
          this.statisticsData.caseRunData.series[0].data = data.caseRunData.apiCaseRun;
          this.statisticsData.caseRunData.series[1].data = data.caseRunData.webCaseRun;
          this.statisticsData.caseRunData.series[2].data = data.caseRunData.appCaseRun;
          this.statisticsData.caseRunData.series[3].data = data.caseRunData.apiCasePassRate;
          this.statisticsData.caseRunData.series[4].data = data.caseRunData.webCasePassRate;
          this.statisticsData.caseRunData.series[5].data = data.caseRunData.appCasePassRate;

          this.statisticsData.planRunWeekTop.xAxis = data.planRunWeekTop.xAxis;
          this.statisticsData.planRunWeekTop.yLeft.max = this.getMax(data.planRunWeekTop.yMaxLeft);
          this.statisticsData.planRunWeekTop.series[0].data = data.planRunWeekTop.planRunTotal;
          this.statisticsData.planRunWeekTop.series[1].data = data.planRunWeekTop.planRunPass;
          this.statisticsData.planRunWeekTop.series[2].data = data.planRunWeekTop.planRunPassRate;

          this.statisticsData.caseFailWeekTop.x = data.caseFailWeekTop.x;
          this.statisticsData.caseFailWeekTop.y = data.caseFailWeekTop.y;

          this.$refs.lineBarChart1.updateChart(this.statisticsData.caseAddData);
          this.$refs.lineBarChart2.updateChart(this.statisticsData.caseRunData);
          this.$refs.lineBarChart3.updateChart(this.statisticsData.planRunWeekTop);
          this.$refs.barChart1.updateChart(this.statisticsData.caseFailWeekTop);
        }
      });
    },
    getMax(num){
      if(num === 0){
        return 100;
      }else{
        return (parseInt((num*2)/5)+1)*5;
      }
    },
    getMaxRight(num){
      if(num === 0){
        return 100;
      }else{
        return (parseInt(num/5) + 1)*5;
      }
    }
  }
}
</script>

<style scoped>
.card-fix {
  height: 100%; 
  margin-bottom: 20px;
  border: none;
}
.statistics-total{
  font-size: 32px;
  font: bold;
  margin: 20px 0px;
}
.title {
  font-size: 18px;
  text-align: center;
  font-weight: bold;
  margin: 10px auto;
}
</style>
