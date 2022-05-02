/**
 * 树数据选择框
 */
<template>
  <div>
    <el-select class="select-tree" size="small" style="width:100%" v-model="selectedValue" :placeholder="placeholder">
      <el-input class="input-filter" size="mini" placeholder="输入名称过滤" v-model="filterText"/>
      <el-option :value="selectedValue" :label="selectedLabel" style="height: auto; background-color: white !important;">
        <el-tree :data="treeData" :accordion="true" node-key="id" ref="tree" :current-node-key="selectedValue"
          :props="defaultProps" :filter-node-method="filterNode" @node-click="clickModule"></el-tree>
      </el-option>
    </el-select>
  </div>
</template>

<script>
  export default {
    name: "SelectTree",
    props:{
      placeholder:String,
      selectedValue:String,
      selectedLabel:String,
      treeData:Array,
    },
    data() {
      return {
        filterText:"",
        defaultProps: {
          children: "children",
          label: "label"
        }
      };
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    methods: {
      // 过滤节点
      filterNode(value, data) {
          if (!value) return true;
          return data.label.indexOf(value) !== -1;
      },
      clickModule(data) {
        this.$emit("selectModule", data);
      }
    }
  }
</script>>


<style scoped>
.input-filter{
  padding-bottom: 6px;
  width: 90%;
  padding-left: 5%;
}
</style>