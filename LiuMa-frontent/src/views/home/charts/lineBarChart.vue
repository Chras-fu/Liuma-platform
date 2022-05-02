<template>
    <div class="echart" :id="id" :style="{float:'left',width: '98%', height: '100%'}">
    </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "LineBarChart",
  props: ["id", "title", "subTitle", "data"],
  data() {
    return {
      myChart: null,
      option: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        toolbox: {
          feature: {
          }
        },
        legend: {
            left: "center",
            orient: 'horizontal',
            itemGap: 8
        },
        xAxis: [
          {
            type: 'category',
            axisPointer: {
                type: 'shadow'
            },
            data: this.data.xAxis
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: this.data.yLeft.name, 
            min: 0,
            max: this.data.yLeft.max,
            interval: this.data.yLeft.max/5,
            axisLabel: {
              formatter: '{value}'
            },
            axisPointer: {
                status: 'hide'
            },
          },
          {
            type: 'value',
            name: this.data.yRight.name,
            min: 0,
            max: this.data.yRight.max,
            interval: this.data.yRight.max/5,
            axisLabel: {
              formatter: '{value}'
            },
            axisPointer: {
                status: 'hide'
            },
          }
        ],
        title: {
          text: this.title,
          subtext: this.subTitle,
          left: 'center'
        },
        grid: {
            left: '3%',
            right: '3%',
            bottom: '5%',
            containLabel: true
        },
		
        series: this.data.series
      }
    }
  },
  mounted() {
    this.myChart = echarts.init(document.getElementById(this.id));
    this.initChart();
  },
  methods:{
    initChart() {
      this.myChart.setOption(this.option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        this.myChart.resize();
      });
    },
    updateChart(data) {
      if (data === null) { return; }
      this.option.xAxis[0].data = data.xAxis;
      this.option.series = data.series;
      this.option.yAxis[0].name = data.yLeft.name;
      this.option.yAxis[0].max = data.yLeft.max;
      this.option.yAxis[0].interval = data.yLeft.max/5;
      this.option.yAxis[1].name = data.yRight.name;
      this.option.yAxis[1].max = data.yRight.max;
      this.option.yAxis[1].interval = data.yRight.max/5;

      this.myChart.setOption(this.option);
      this.$forceUpdate();

      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        this.myChart.resize();
      });
    }
  }

}
</script>
