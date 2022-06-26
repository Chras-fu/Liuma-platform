<template>
  <div class="json-tree">
    <div v-for="(item, index) in data" :key="`${item.key}-${index}`" class="json-tree-item">
      <span class="json-tree-line">
        <span class="lm-info">
          {{ item.key }}
        </span>
        <span v-if="item.type === 'object'">
          <span v-text="': \{'"></span>
        </span>
        <span v-else-if="item.type === 'array'">
          <span v-text="': \['"></span>
        </span>
        <span v-else>
          <span v-pre style="margin-right: 5px;">:</span>
          <span v-if="item.type==='string'" class="lm-success" style="font-size:10px">
            {{`"${item.value}"`}}
          </span>
          <span v-else class="lm-error"  style="font-size:10px">
            {{`${item.value}`}}
          </span>
          <span v-if="index < data.length-1">,</span>
        </span>
        <el-tooltip class="item" effect="dark" :content="'复制 '+ item.path + ' 到剪切板'" placement="top-start">
          <span>
            <i class="el-icon-document-copy lm-loading" style="margin-left:20px" @click="copyPath(item.path)"/>
          </span>   
        </el-tooltip>
        <el-tooltip class="item" effect="dark" content="一键添加断言/关联" placement="top-start">
          <span>
            <i class="el-icon-document-add lm-loading" style="margin-left:5px" @click="addContent(item)"/>
          </span>   
        </el-tooltip>
      </span>
      <div v-if="item.leaf" class="children-tree">
        <json-tree :data="item.children" @addContent="addContent($event)"/>
      </div>
      <span v-if="item.type === 'object'">
        <span v-text="'\}'"></span>
        <span v-if="index < data.length-1">,</span>
      </span>
      <span v-else-if="item.type === 'array'">
        <span v-text="'\]'"></span>
        <span v-if="index < data.length-1">,</span>
      </span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'JsonTree',
  props: {
    data: {
      type: Array,
      default: []
    }
  },
  methods: {
    copyPath(path) {
      const input = document.createElement('input');
      document.body.appendChild(input);
      input.setAttribute('value', path);
      input.select();
      if (document.execCommand('copy')) {
          document.execCommand('copy');
          this.$message.success('复制成功');
      }
      document.body.removeChild(input);
    },
    addContent(item) {
      this.$emit('addContent', item);
    }
  },
};
</script>

<style scoped>
.json-tree-line{
  margin: 5px 0px;
}

.path-btn {
  background: #37f;
  color: #fff;
  padding: 2px 6px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 3px;
  margin-left: 20px;
}

.children-tree {
  margin-left: 20px;
}
</style>