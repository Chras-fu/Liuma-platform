<template>
    <div class="echart" :id="id" :style="{float:'left',width: '100%', height: '100%'}"></div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "BarChart",
  props: ["id", "title", "data", "name"],
  data() {
    return {
      option: {
        title: {
            text: this.title,
            subtext: '',
            left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            label: {
              backgroundColor: '#283b56'
            }
          }
        },
        xAxis: {
          type: 'value',
          max: 'dataMax'
        },
        yAxis: {
          type: 'category',
          data: this.data.y,
          inverse: true,
          nameTextStyle: {
            width: 600,
            ellipsis: "..."
          }
        },
        series: [{
          name: this.name,
          data: this.data.x,
          type: 'bar',
        }],
        grid: {
            left: '2%',
            right: '5%',
            bottom: '5%',
            containLabel: true
        }
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

      this.option.yAxis.data = data.y;
      this.option.series[0].data = data.x;

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
