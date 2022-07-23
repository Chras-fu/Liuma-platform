/**
 * 计划管理  集合编辑
 */
<template>
  <div>
    <page-header title="编辑集合" :cancel="cancelAdd" :save="saveAdd"/>
    <el-form ref="collectionForm" :rules="rules" :model="collectionForm" label-width="80px">
        <el-row :gutter="40">
            <el-col :span="15">
                <el-form-item size="small" label="集合名称" prop="name">
                    <el-input style="width: 100%" v-model="collectionForm.name" placeholder="请输入集合名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item size="small" label="版本" prop="versionId">
                    <el-select style="width: 100%" v-model="collectionForm.versionId" placeholder="请选择版本">
                        <el-option v-for="item in versionList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="40">
            <el-col :span="15">
                <el-form-item size="small" label="集合描述" style="margin-bottom:0px">
                    <el-input style="width: 100%" :autosize="{ minRows: 4}" type="textarea" clearable placeholder="请输入集合描述" v-model="collectionForm.description" maxlength="200" show-word-limit/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item style="margin-left:-80px;" prop="collectionCases"/>
        <el-table :data="collectionForm.collectionCases" row-key="id" class="sort-table" size="small">
            <el-table-column label="" width="60px">
                <template>
                    <i class="iconfont icon-paixu"  @mousedown="rowDrop" style="font-size: 24px"/>
                </template>
            </el-table-column>
            <el-table-column label="序号" prop="index" width="100px">
            </el-table-column>
            <el-table-column label="用例名称" prop="caseName" min-width="180px">
            </el-table-column>
            <el-table-column label="用例模块" prop="caseModule">
            </el-table-column>
            <el-table-column label="用例类型" prop="caseType">
            </el-table-column>
            <el-table-column label="操作" width="120px">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" @click="deleteCollectionCase(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button size="small" icon="el-icon-plus" type="text" @click="selectCaseVisible=true">新增</el-button>  
    </el-form>
    <!-- 添加用例界面 -->
    <el-dialog title="选择用例" :visible.sync="selectCaseVisible" width="800px" destroy-on-close>
        <select-case :selections="selections" :selectCaseVisible="selectCaseVisible"/>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="selectCaseVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="selectCaseSave">保存</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import PageHeader from '../common/components/pageheader'
import SelectCase from './common/selectCase'
import {getUUID} from '@/utils/util'
export default {
    components:{PageHeader, SelectCase},
    data() {
        return{
            selections:[],
            selectCaseVisible: false,
            collectionForm: {
                id: "",
                name: "",
                versionId: "",
                description: "",
                collectionCases:[]
            },
            versionList: [],
            rules: {
                name: [{ required: true, message: '集合名称不能为空', trigger: 'blur' }],
                versionId: [{ required: true, message: '版本不能为空', trigger: 'blur' }],
                collectionCases: [{ required: true, message: '请至少选择一条测试用例', trigger: 'blur' }],
                
            }
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["计划中心", "测试集合", "集合编辑"]);
        this.getDetail(this.$route.params);
        this.getVersion();
    },
    methods: {
        // 行拖拽
        rowDrop () {
            // 此时找到的元素是要拖拽元素的父容器
            const tbody = document.querySelector('.sort-table tbody');
            const _this = this;
            Sortable.create(tbody, {
                //  指定父元素下可被拖拽的子元素
                draggable: ".el-table__row",
                onEnd ({ newIndex, oldIndex }) {
                    const currRow = _this.collectionForm.collectionCases.splice(oldIndex, 1)[0];
                    _this.collectionForm.collectionCases.splice(newIndex, 0, currRow);
                    _this.sortCollectiionCase();
                }
            });
        },
        // 重新排序
        sortCollectiionCase(){
            for(let i=0; i<this.collectionForm.collectionCases.length; i++){
                this.collectionForm.collectionCases[i].index = i+1;
            }
        },
        // 保存用例选择
        selectCaseSave(){
            for(let i=0;i<this.selections.length;i++){
                let collectionCase = {
                    id: getUUID(),
                    index: this.collectionForm.collectionCases.length+1,
                    caseId: this.selections[i].id,
                    caseName: this.selections[i].name,
                    caseModule: this.selections[i].moduleName,
                    caseType: this.selections[i].type
                }
                this.collectionForm.collectionCases.push(collectionCase);
            }
            this.selections.splice(0, this.selections.length);
            this.selectCaseVisible = false;
        },
        deleteCollectionCase(index){
            this.collectionForm.collectionCases.splice(index, 1);
            for(let i=0; i<this.collectionForm.collectionCases.length; i++){
                this.collectionForm.collectionCases[i].index = i+1;
            }
        },
        getVersion(){
            let url = "/autotest/version/list/" + this.$store.state.projectId;
            this.$get(url, response => {
                this.versionList = response.data;
            });
        },
        getDetail(param){
            if (param.collectionId){
                let url = "/autotest/collection/detail/" + param.collectionId;
                this.$get(url, response => {
                    this.collectionForm = response.data;
                });
            }
        },
        cancelAdd(){
            this.$router.push({path: '/planManage/testCollection'})
        },
        saveAdd(){
            this.$refs["collectionForm"].validate(valid => {
                if (valid) {
                    this.collectionForm.projectId = this.$store.state.projectId;
                    for(let i=0; i<this.collectionForm.collectionCases.length; i++){
                        this.collectionForm.collectionCases[i].index = i+1;
                    }
                    let url = '/autotest/collection/save';
                    this.$post(url, this.collectionForm, response =>{
                        this.$message.success("保存成功");
                        this.$router.push({path: '/planManage/testCollection'});
                    });
                }else{
                    return false;
                }
            });
            
        },
    }
}
</script>

<style scoped>
</style>