/**
* 模块树
*/ 
<template>
    <div class="module-tree">
        <div class="tree-header">
            <div class="tree-title">{{title}}</div>
            <el-button class="tree-add" size="mini" icon="el-icon-plus" @click="appendModule(null)"/>
        </div>
        <div class="tree-input">
            <el-input size="mini" placeholder="输入模块名称过滤" v-model="filterText"/>
        </div>
        <div class="tree-body">
            <el-tree class="filter-tree" :accordion="true" :data="treeData" :current-node-key="currentModule" 
            :props="defaultProps" node-key="id" :expand-on-click-node="false" :filter-node-method="filterNode" 
            @node-click="clickModule" ref="tree" draggable @node-drag-end="dragNode">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span class="tree-label">{{node.label}}</span>
                    <span class="tree-opt">
                        &emsp;
                        <el-button size="mini" type="text" icon="el-icon-plus" @click="() => appendModule(data)"></el-button>
                        <el-button size="mini" type="text" icon="el-icon-delete" @click="() => removeModule(node, data)"></el-button>
                    </span>
                </span>
            </el-tree>
        </div>
        
    </div>
</template>
<script>
export default {
  name: 'ModuleTree',
  props:{
    title:String,
    treeData:Array,
    currentModule:String,
  },
  data(){
      return {
        filterText: '',
        defaultProps: {
            children: 'children',
            label: 'label'
        }
      }
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
        return data.name.indexOf(value) !== -1;
    },
    clickModule(data) {
        this.$emit("clickModule", data);
    },
    appendModule(data) {
        this.$emit("appendModule", data);
    },
    dragNode(dragNode, tarNode, position, event) {
        if(position === "inner"){
            // 如果是拖拽进目标节点内 且目标节点不是父节点 做更改父节点请求
            if(tarNode.data.id !== dragNode.data.parentId){
                this.$emit("dragNode", dragNode, tarNode.data.id);
            }  
        }else{
            // 如果是拖拽至目标节点前后 且目标节点不是兄弟节点 做更改父节点请求
            if(tarNode.data.parentId !== dragNode.data.parentId ){
                this.$emit("dragNode", dragNode, tarNode.data.parentId);
            }
        } 
    },
    removeModule(node, data) {
        this.$confirm('确定要删除模块吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        .then(() => {
            this.$emit("removeModule", node, data);
        })
        .catch(() => {
            this.$message.info('取消成功');
        })
    }
  }
    
}
</script>
<style scoped>
.tree-title {
  float: left; 
  padding: 6px 12px; 
  font-weight: bold; 
  font-size: 12px;
}
.tree-add{
  float: right;
  width: 24px;
  padding: 5px 5px;
  height: 24px;
}
.tree-input{
  height: 30px;
  padding: 5px 5px;
}
.module-tree {
    border:1px solid rgb(219, 219, 219);
}
.tree-header{
    height: 39px; 
    padding-right: 5px; 
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 12px;
    padding-right: 8px;
}
.custom-tree-node:hover .tree-opt{
    display: block;
    align-items: top;
}
.custom-tree-node:hover .tree-label{
    word-wrap: break-word;
}
.tree-opt{
    display: none;
}
.filter-tree >>> .el-tree-node__children{
    overflow: visible !important;
}
.tree-body{
    min-height: 450px;
    overflow: hidden;
    overflow-x: auto;
    white-space: nowrap;
}
</style>