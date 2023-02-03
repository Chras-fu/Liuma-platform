/**
* 响应断言
*/ 
<template>
    <div>
        <el-tabs style="width: 100%" v-model="activeTab">
            <el-tab-pane label="请求头" name="header">
                <request-param-rule :ruleForm="paramRuleForm.header" style="width: 100%"/>
            </el-tab-pane>
            <el-tab-pane label="请求体" name="body">
                <request-param-rule :ruleForm="paramRuleForm.body" style="width: 100%"/>
            </el-tab-pane>
            <el-tab-pane label="QUERY参数" name="query">
                <request-param-rule :ruleForm="paramRuleForm.query" style="width: 100%"/>
            </el-tab-pane>
            <el-tab-pane label="REST参数" name="rest">
                <request-param-rule :ruleForm="paramRuleForm.rest" style="width: 100%"/>
            </el-tab-pane>
            <el-tab-pane label="正向断言" name="positive">
                <assertion :assertion="paramRuleForm.positiveAssertion" :apiId="paramRuleForm.apiId" style="width: 100%"/>
            </el-tab-pane>
            <el-tab-pane label="逆向断言" name="opposite">
                <assertion :assertion="paramRuleForm.oppositeAssertion" :apiId="paramRuleForm.apiId" style="width: 100%"/>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>
<script>
import RequestParamRule from '../api/requestParamRule'
import Assertion from './assertion'
import {toJsonPath} from '@/utils/jsonPath'
export default {
    name: 'Autocase',
    props:{
        paramRuleForm: Object
    },
    components: { RequestParamRule, Assertion },
    data() {
        return{
            activeTab: "body"
        }
    },
    created() {
        this.getApiData(this.paramRuleForm.apiId);
    },
    methods: {
        getApiData(id){
            let url = '/autotest/api/detail/' + id;
            this.$get(url, response =>{
                let data = response.data;
                if(data.header){
                    data.header = JSON.parse(data.header);
                }
                if(data.body){
                    data.body = JSON.parse(data.body);
                }
                if(data.query){
                    data.query = JSON.parse(data.query);
                }
                if(data.rest){
                    data.rest = JSON.parse(data.rest);
                }
                if(data.body.type === 'json' & data.body.json !== ''){  //仅支持form格式以及json格式的请求体
                    let arr = [];
                    toJsonPath(arr, JSON.parse(data.body.json), "$");
                    this.filterPath(arr, this.paramRuleForm.body);
                }else if(data.body.type === 'form-data' | data.body.type === 'form-urlencoded'){
                    this.toRuleForm(data.body.form, this.paramRuleForm.body);
                }
                // 处理数据
                this.toRuleForm(data.header, this.paramRuleForm.header);
                this.toRuleForm(data.query, this.paramRuleForm.query);
                this.toRuleForm(data.rest, this.paramRuleForm.rest);
            });
        },
        toRuleForm(oldArr, newArr){
            oldArr.forEach((item, index) => {
                let newItem = {
                    name: item.name,
                    type: item.type,
                    required: item.required,
                    random: "",
                    value: item.value,
                    isNull: false
                }
                if(!item.type){
                    newItem.type = "String";
                }
                if(item.required === true){
                    newItem.required = "must";
                }else if(item.required === false){
                    newItem.required = "lost";
                }else if(!item.required){
                    newItem.required = "must";
                }

                newArr.push(newItem);
            });
        },
        filterPath(oldArr, newArr){
            oldArr.forEach((item, index) => {
                if(item.type !== 'array' & item.type !== 'object'){
                    let newItem = {
                        name: item.path,
                        type: item.type,
                        required: "must",
                        random: "",
                        value: item.childValue,
                        isNull: false
                    };
                    if(item.type === 'error'){
                        newItem.type = 'String';
                        newItem.value = '';
                    }
                    if(item.type === 'null'){
                        newItem.type = 'String';
                        newItem.value = null;
                        newItem.isNull = true;
                    }
                    newArr.push(newItem);
                }else{
                    this.filterPath(item.children, newArr);
                }
            });
        }
    }
}
</script>
<style scoped>

</style>