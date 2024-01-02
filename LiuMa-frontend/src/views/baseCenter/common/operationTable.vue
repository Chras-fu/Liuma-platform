/**
 * 公共组件  控件列表
 */
<template>
  <div>
    <el-table size="small" :data="operationData" v-loading="loading">
        <el-table-column prop="index" label="序号" align="center" width="50px"/>
        <el-table-column prop="name" label="操作名称" :show-overflow-tooltip="true"/>
        <el-table-column prop="from" label="操作类型">
            <template slot-scope="scope">
                <span v-if="scope.row.from==='system'">系统内置操作</span>
                <span v-if="scope.row.from==='custom'">用户定义操作</span>
            </template>
        </el-table-column>
        <el-table-column v-if="uiType==='app'" label="操作系统">
            <template slot-scope="scope">
                <span v-if="scope.row.system==='android'">安卓</span>
                <span v-if="scope.row.system==='apple'">苹果</span>
                <span v-if="scope.row.system==='common'">通用</span>
            </template>
        </el-table-column>
        <el-table-column prop="description" label="等效代码" min-width="200px" :show-overflow-tooltip="true"/>
        <el-table-column prop="username" label="创建人"/>
        <el-table-column prop="updateTime" label="更新时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="viewOperation(scope.row)">查看</el-button>
                <el-button type="text" size="mini" v-if="scope.row.createUser ===currentUser" @click="deleteOperation(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"></Pagination>
  </div>
</template>

<script>
import Pagination from '@/views/common/components/pagination'
export default {
    name: "OperationTable",
    components: {
        Pagination
    },
    props:{
        operationData: Array,
        loading: Boolean,
        pageparam: Object,
        uiType: String
    },
    data(){
        return {
            currentUser: ""
        }
    },
    created() {
        this.currentUser = this.$store.state.userInfo.id;
    },
    methods: {
        // 获取列表数据方法
        callFather(param) {
            this.$emit("callFather", param);
        },
        // 查看控件
        viewOperation(row){
            this.$router.push({path: '/common/operationManage/' + this.uiType + '/edit/' + row.id});
        },
        // 删除控件
        deleteOperation(row){
            this.$confirm('控件删除后相关用例无法执行 确定要删除控件吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                this.$emit("deleteOperation", row);
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
    }
}
</script>

<style scoped>

</style>
