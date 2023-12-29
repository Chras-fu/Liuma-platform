/**
* 后置脚本
*/
<template>
    <div>
        <el-table :data="posts"  size="small" row-key="index" :expand-row-keys="expands" @expand-change="expandSelect">
            <el-table-column type="expand">
                <template slot-scope="props">
                    <!-- 前后置脚本代码编辑 -->
                    <div v-if="props.row.name === 'postScript'" style="margin-top: 10px;">
                        <el-row>
                            <el-col :span="22">
                                <p class="tip">
                                    <span>编辑脚本</span>
                                </p>
                            </el-col>
                            <el-col :span="2">
                                <el-button size="mini" type="primary" @click="saveScript(props.row)">保存</el-button>
                            </el-col>
                        </el-row>
                        <code-edit ref="codeEditor" :data.sync='props.row.value' :height='480' mode="python"/>
                    </div>
                    <!-- 前后置SQL编辑 -->
                    <div v-if="props.row.name === 'postSql'" style="margin-top: 10px">
                        <el-row>
                            <el-col :span="22">
                                <p class="tip">
                                    <span>编辑SQL</span>
                                </p>
                            </el-col>
                            <el-col :span="2">
                                <el-button size="mini" type="primary" @click="saveSql(props.row)">保存</el-button>
                            </el-col>
                        </el-row>
                        <el-form style="margin-top: 10px" :model="props.row.value" :rules="rules" :ref="'sql'+props.row.index">
                            <el-row>
                                <el-col :span="6">
                                    <el-form-item prop="db">
                                        <el-select size="small" style="width: 90%" placeholder="数据库名称" v-model="props.row.value.db">
                                            <el-option v-for="item in dbList" :key="item" :label="item" :value="item"/>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="6">
                                    <el-form-item prop="sqlType">
                                        <el-select size="small" style="width: 90%" placeholder="sql类型" v-model="props.row.value.sqlType">
                                            <el-option v-for="item in sqlTypes" :key="item.value" :label="item.label" :value="item.value"/>
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col v-if="props.row.value.sqlType==='query'" :span="12">
                                    <el-form-item prop="names">
                                        <el-input size="small" style="width: 100%" v-model="props.row.value.names" placeholder="保存变量名, 多个变量名以','隔开, 确保个数与查询结果相匹配"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-form-item prop="sqlText">
                                <code-edit ref="sqlEditor" :data.sync='props.row.value.sqlText' :height='240' mode="sql"/>
                            </el-form-item>
                        </el-form>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="序号" prop="index" width="80"/>
            <el-table-column label="名称" prop="name" width="240">
                <template slot-scope="scope">
                    {{scope.row.name==='postScript'?"后置脚本":"后置SQL"}}
                    <el-tooltip placement="bottom">
                        <div v-if="scope.row.name==='postScript'" slot="content">
                            获取关联参数/公共参数使用sys_get(name)函数，<br/>
                            保存关联参数使用sys_put(name, val)函数，<br/>
                            保存公共参数使用sys_put(name, val, true)，<br/>
                            使用变量名res_request获取请求内容，<br/>
                            使用变量名res_code/res_header/res_data/res_cookies/res_bytes获取响应内容
                        </div>
                        <div v-else slot="content">
                            每条sql仅支持执行一条sql语句，<br/>
                            sql语句中如需使用变量以{ {name} }表示，<br/>
                            如果str类型变量需加引号
                        </div>
                        <i class="el-icon-info"></i>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column label="描述" prop="desc" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                    <div v-if="scope.row.edit===true" >
                        <el-input size="mini" style="width: 85%" v-model="scope.row.desc" placeholder="请输入步骤描述" @change="scope.row.edit=false"/>
                        <i class="el-icon-success" @click="scope.row.edit=false"/>
                    </div>
                    <span v-else>{{scope.row.desc}} <i class="el-icon-edit"  @click="scope.row.edit=true"/></span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="remove(scope.$index)">删除</el-button>
                    <el-button v-if="scope.$index!==0" size="mini" type="text" icon="el-icon-top" @click="up(scope.$index)"/>
                    <el-button v-if="scope.$index!==(posts.length-1)" size="mini" type="text" icon="el-icon-bottom" @click="down(scope.$index)"/>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="addScript">新增脚本</el-button>
        <el-button size="small" icon="el-icon-plus" type="text" @click="addSql">新增SQL</el-button>
        <el-button size="small" type="text" @click="deleteAll">删除全部</el-button>
    </div>
</template>
<script>
import CodeEdit from '@/views/common/business/codeEdit'
export default {
    name: 'PostScript',
    components: { CodeEdit },
    props:{
        posts: Array,
    },
    data() {
        return{
            expands: [], // 默认展开行
            sqlTypes: [
                {label: "查询语句", value: 'query'},
                {label: "非查询语句", value: 'nonQuery'},
            ],
            dbList: [],
            rules: {
                db: [{ required: true, message: '数据库名不能为空', trigger: 'blur' }],
                sqlType: [{ required: true, message: 'sql类型不能为空', trigger: 'blur' }],
                names: [{ required: true, message: '保存变量名不能为空', trigger: 'blur' }],
                sqlText: [{ required: true, message: 'sql语句不能为空', trigger: 'blur' }],
            }
        }
    },
    created() {
        this.getDbList();
    },
    methods: {
        getDbList(){
            let url = '/autotest/database/name/list/' + this.$store.state.projectId;
            this.$get(url, response =>{
                this.dbList = response.data;
            })
        },
        expandSelect(row, expandedRows) {
            if(expandedRows.indexOf(row) === -1){  //关闭行
                this.expands.splice(this.expands.indexOf(row.index), 1);
                if(row.name === 'postSql'){
                    this.saveSql(row);
                    if(this.expands.indexOf(row.index) > -1){
                        expandedRows.push(row); // 如果关闭失败 则重新打开 避免后续失败
                    }
                }
            } else{ // 打开行
                if(this.expands.indexOf(row.index) === -1){
                    this.expands.push(row.index);
                }
                if(row.name === 'postSql'){
                    row.value = JSON.parse(row.value);
                }
            }
        },
        addScript(){
            this.posts.push({index:this.posts.length+1, edit: false, name:"postScript", value: null });
            this.expands.push(this.posts.length);
        },
        addSql(){
            this.posts.push({index:this.posts.length+1, edit: false, name:"postSql", value: {
                db: "",
                sqlType: "",
                names: "", // 变量名称，以,隔开,确保长度与查询结果参数一致
                sqlText: ""
            } });
            this.expands.push(this.posts.length);

        },
        resetIndex(){
            for(let i=0;i<this.posts.length;i++){
                this.posts[i].index = i+1;
            }
        },
        up(index){
            this.posts[index-1]=this.posts.splice(index,1,this.posts[index-1])[0];
            this.resetIndex();
        },
        down(index){
            this.posts[index]=this.posts.splice(index+1,1,this.posts[index])[0];
            this.resetIndex();
        },
        remove(index){
            this.posts.splice(index, 1);
            this.resetIndex();
        },
        deleteAll(){
            this.posts.splice(0, this.posts.length);
        },
        saveScript(row){
            this.expands.splice(this.expands.indexOf(row.index), 1);
        },
        saveSql(row){
            this.$refs["sql"+row.index].validate(valid => {
                if (valid) {
                    row.value = JSON.stringify(row.value);
                    if(this.expands.indexOf(row.index) > -1){
                        this.expands.splice(this.expands.indexOf(row.index), 1);
                    }
                }else{
                    if(this.expands.indexOf(row.index) === -1){
                        this.expands.push(row.index);
                    }
                    return false;
                }
            })
        },
    }
}
</script>
<style scoped>

</style>
