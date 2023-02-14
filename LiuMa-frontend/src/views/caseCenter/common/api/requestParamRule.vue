/**
* 请求参数规则设置
*/
<template>
    <div>
        <el-table :data="ruleForm">
            <el-table-column label="参数名称" prop="name" width="100px"/>
            <el-table-column label="参数类型" prop="type" width="150px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 95%" v-model="ruleForm[scope.$index].type">
                        <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="必填项" prop="required" width="150px">
                <template slot-scope="scope">
                    <el-select size="small" style="width: 95%" v-model="ruleForm[scope.$index].required">
                        <el-option v-for="item in requires" :key="item.value" :label="item.label" :value="item.value" :disabled="item.disabled"/>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="参数范围" prop="random" min-width="120px">
                <template slot-scope="scope">
                    <el-input size="small" style="width: 85%" placeholder="例如[1, 3)" v-model="ruleForm[scope.$index].random" :disabled="ruleForm[scope.$index].type==='Boolean' | ruleForm[scope.$index].type==='None'"/>
                    <el-tooltip content="数字型输入大小上下限, 字符串输入长度上下限, []表示包含边界值, ()表示不包含边界值." placement="bottom">
                        <i class="el-icon-info"></i>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column label="默认值" prop="value" width="240px">
                <template slot-scope="scope">
                    <el-radio-group v-if="ruleForm[scope.$index].type==='Boolean'" style="width: 64%" v-model="ruleForm[scope.$index].value" :disabled="ruleForm[scope.$index].isNull">
                        <el-radio :label="true">True</el-radio>
                        <el-radio :label="false">False</el-radio>
                    </el-radio-group>
                    <el-input v-else size="small" style="width: 65%" placeholder="参数默认值" v-model="ruleForm[scope.$index].value" :disabled="ruleForm[scope.$index].isNull"/>
                    <el-switch size="small" style="width: 33%" v-model="ruleForm[scope.$index].isNull" active-text="null" @change="changeValue($event, ruleForm[scope.$index])"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="60px">
                <template slot-scope="scope">
                    <el-button size="small" type="text" @click="remove(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script>
export default {
    name: 'RequestParamRule',
    props:{
        type: String,
        ruleForm:Array
    },
    data() {
        return{
            requires: [
                {label: '字段必填', value: 'must'},
                {label: '字段可为空', value: 'empty'},
                {label: '字段可为null', value: 'null'},
                {label: '字段可缺失', value: 'lost', disabled: this.type === 'rest'},
                {label: '不校验', value: 'None'}
            ],
            types: [
                {label: '整数型', value: 'Int'},
                {label: '浮点型', value: 'Float'},
                {label: '布尔型', value: 'Boolean'},
                {label: '字符串(普通)', value: 'String'},
                {label: '字符串(特殊)', value: 'SpecialStr'},
                {label: '不校验', value: 'None'}
            ]
        }
        },
        created() {
        },
        methods: {
            remove(index){
                this.ruleForm.splice(index, 1);
            },
            changeValue(val, form){
                if(val){
                    form.value = null;
                }else{
                    form.value = "";
                }
            }
        }
    }
</script>
<style scoped>

</style>
