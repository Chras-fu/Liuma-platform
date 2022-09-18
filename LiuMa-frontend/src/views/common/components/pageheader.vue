/**
* 页面头部组件
*/ 
<template>
  <div class = "page-header">
      <span style="font-weight: bold">{{title}}</span>
      <div>
          <el-button v-if="showCancel" size="small" @click="cancel">返回</el-button>
          <el-button v-if="showDebug" size="small" type="success" @click="debug">调试</el-button>
          <el-button v-if="showSave" size="small" type="primary" @click="save">保存</el-button>
      </div>
  </div>
</template>
<script>
export default {
  name: 'PageHeader',
  props:{
    title:{},
    showDebug:{
      type: Boolean,
      default: false,
    },
    showSave:{
      type: Boolean,
      default: true,
    },
    showCancel:{
      type: Boolean,
      default: true,
    },
    cancel:{
      type: Function,
      default: null,
    }, 
    save:{
      type: Function,
      default: null,
    },
    debug:{
      type: Function,
      default: null,
    }
  }
}
</script>
<style scoped>
.page-header{
    border-bottom: 1px solid rgb(219, 219, 219);
    height: 48px;
    display: flex;
    width: 100%;
    margin-bottom: 20px;
    justify-content: space-between;
    align-items: center;
    margin-top: -18px;
}
</style>
