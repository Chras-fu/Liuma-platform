/**
 * 公共组件  文件管理
 */
<template>
  <div>
    <!-- 搜索筛选 -->
    <el-form :inline="true" :model="searchForm">
        <el-form-item label="">
            <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入文件uuid、名称"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button size="small" type="primary" @click="search">搜索</el-button>
            <el-button size="small" @click="reset">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right">
            <el-button size="small" type="primary" icon="el-icon-plus" @click="addFile">上传文件</el-button>
        </el-form-item>
    </el-form>
    <!--列表-->
    <el-table size="small" :data="fileData" v-loading="loading">
        <el-table-column prop="id" label="uuid" width="250px"/>
        <el-table-column prop="name" label="文件名称" min-width="200px"/>
        <el-table-column prop="description" label="文件描述" min-width="240px"/>
        <el-table-column prop="username" label="创建人"/>
        <el-table-column prop="createTime" label="上传时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" v-if="scope.row.createUser===currentUser" size="mini" @click="deleteFile(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"/>
    <!-- 上传文件界面 -->
    <el-dialog title="上传文件" :visible.sync="uploadFileVisible" width="600px" destroy-on-close>
        <el-form label-width="120px" style="padding-right: 30px;" :model="uploadFileForm" :rules="rules" ref="uploadFileForm">
            <el-form-item label="文件名称" prop="name">
                <el-input size="small" style="width: 90%" v-model="uploadFileForm.name" placeholder="请输入文件名称"/>
            </el-form-item>
            <el-form-item label="文件描述" prop="description">
                <el-input size="small" style="width: 90%" v-model="uploadFileForm.description" :autosize="{ minRows: 3}"
                maxlength="200" show-word-limit type="textarea" clearable placeholder="请输入文件描述"/>
            </el-form-item>
            <el-form-item label="选择文件" prop="fileList">
                <el-upload class="upload-demo" :file-list="uploadFileForm.fileList" :before-upload="beforeUpload" :http-request="uploadFile"
                        :on-remove="removeFile" :on-exceed="handleExceed" drag action :limit="1" ref="upload">
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                    <div class="el-upload__tip" slot="tip">只能上传单个文件，且不超过50M</div>
                </el-upload>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="uploadFileVisible=false">取消</el-button>
            <el-button size="small" type="primary" @click="submitFileForm('uploadFileForm', uploadFileForm)">上传</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '../common/components/pagination'
import {timestampToTime} from '@/utils/util'
export default {
    components: {
        Pagination
    },
    data() {
        return{
            loading:false,
            uploadFileVisible: false,
            searchForm: {
                page: 1,
                limit: 10,
                condition: ""
            },
            fileData: [],
            pageparam: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            uploadFileForm: {},
            rules:{
                name: [{ required: true, message: '文件名称不能为空', trigger: 'blur' }],
                fileList: [{ required: true, message: '文件不能为空', trigger: 'blur' }]
            },
            currentUser: ""
        }
    },
    created() {
        this.$root.Bus.$emit('initBread', ["公共组件", "文件管理"]);
        this.currentUser = this.$store.state.userInfo.id;
        this.getdata(this.searchForm);
    },
    methods: {
        // 获取列表数据方法
        getdata(searchParam) {
            this.loading = true
            let url = '/autotest/file/list/' + searchParam.page + '/' + searchParam.limit;
            let param = {
                condition: searchParam.condition,
                projectId: this.$store.state.projectId
            };
            this.$post(url, param, response => {
                let data = response.data;
                for(let i =0; i<data.list.length; i++){
                    data.list[i].createTime= timestampToTime(data.list[i].createTime);
                }
                this.fileData = data.list;
                this.loading = false;
                // 分页赋值
                this.pageparam.currentPage = this.searchForm.page;
                this.pageparam.pageSize = this.searchForm.limit;
                this.pageparam.total = data.total;
            });
        },
        // 分页插件事件
        callFather(parm) {
            this.searchForm.page = parm.currentPage;
            this.searchForm.limit = parm.pageSize;
            this.getdata(this.searchForm);
        },
        // 搜索按钮
        search() {
            this.getdata(this.searchForm);
        },
        // 重置按钮
        reset() {
            this.searchForm.condition = "";
            this.getdata(this.searchForm);
        },
        // 新增文件
        addFile(){
            this.uploadFileForm = {
                name: "",
                description: "",
                fileList: []
            };
            this.uploadFileVisible = true;
        },
        submitFileForm(confirm, form){
            this.$refs[confirm].validate(valid => {
              console.log("file valid: "+ valid);
              if (valid) {
                    let url = '/autotest/file/upload';
                    let data = {
                        name: form.name,
                        description: form.description,
                        projectId: this.$store.state.projectId
                    };
                    let file = form.fileList[0];
                    this.$fileUpload(url, file, null, data, response =>{
                        this.$message.success("上传成功");
                        this.uploadFileVisible = false;
                        this.getdata(this.searchForm);
                    });
                }else{
                    return false;
                }
            });
        },
        // 删除文件
        deleteFile(row){
            this.$confirm('确定要删除文件吗? 文件删除可能会导致相关用例无法执行!', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                let url = '/autotest/file/delete';
                this.$post(url, {id: row.id}, response => {
                    this.$message.success("删除成功");
                    this.getdata(this.searchForm);
                });
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
        // 上传前判断格式和大小
        beforeUpload(file) {
            if (file.size > 50 * 1024 * 1024) {
                this.$message.warning('文件大小超过50M 无法上传');
                return false;
            }
            return true;
        },
        handleExceed() {
            this.$message.warning('一次最多只能上传一个文件');
        },
        uploadFile(option) {
            window.console.log(option)
            this.uploadFileForm.fileList.push(option.file);
            this.uploadFileForm.name = option.file.name;
            this.$refs.uploadFileForm.validateField('fileList');
        },
        removeFile() {
            this.uploadFileForm.fileList = [];
        }
    }
}
</script>

<style scoped>

</style>
