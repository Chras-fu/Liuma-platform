/**
 * 环境中心  设备管理
 */
<template>
    <div class="device-list">
        <!-- 搜索 -->
        <el-form :inline="true" :model="searchForm">
            <el-form-item label="">
                <el-input size="small" v-model="searchForm.condition" prefix-icon="el-icon-search" placeholder="请输入设备名称"/>
            </el-form-item>
            <el-form-item label="">
                <el-select size="small" clearable style="width:150px" v-model="searchForm.status" placeholder="请选择状态">
                    <el-option v-for="item in statusList" :key="item.id" :label="item.name" :value="item.id"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button size="small" type="primary" @click="search">搜索</el-button>
                <el-button size="small" @click="reset">重置</el-button>
            </el-form-item>
        </el-form>
        <!-- 筛选条件 -->
        <el-table class="filter-table" stripe :row-style="{height:35+'px'}" :data="filterData">
            <el-table-column label="筛选条件" prop="name" width="150px"/>
            <el-table-column label="" prop="selectAll" width="120px">
                <template slot-scope="scope">
                    <el-checkbox v-model="scope.row.selectAll" label="全部" key="全部" :indeterminate="scope.row.isIndeterminate"
                    @change="handleCheckAllChange(scope.row)">全部</el-checkbox>
                </template>
            </el-table-column>
            <el-table-column label="" prop="conditions" min-width="1000">
                <template slot-scope="scope" style="dispaly: flex">
                    <el-checkbox-group v-model="scope.row.selected" @change="handleCheckedChange(scope.row)">
                        <el-checkbox style="width: 9.5%; float:left" v-for="condition in scope.row.conditions" :label="condition" :key="condition">{{condition}}</el-checkbox>
                    </el-checkbox-group>
                </template>
            </el-table-column>
            <el-table-column label="" align="right" prop="more" width="80px">
                <template slot-scope="scope">
                    <el-button type="text" v-if="scope.row.showMore" size="small" @click="showMore(scope.row)">{{scope.row.showBtn}} <i :class="scope.row.icon"/></el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 设备列表 -->
        <div class="device-table">
            <div v-for="(devicesRow, index) in devicesData" :key="index" class="device-box-row">
                <div v-for="(device, index) in devicesRow" :key="index" class="device-box">
                    <div class="device-box-border">
                        <div class="device-box-info">
                            <div class="box-header">
                                <span style="float: left;">
                                    <i v-if="device.status==='offline'" class="el-icon-warning-outline tpw-warning"> 已离线</i>
                                    <i v-if="device.status==='online'" class="el-icon-video-play tpw-success"> 空闲中</i>
                                    <i v-if="device.status==='using'" class="el-icon-video-pause tpw-error"> 占用中</i>
                                    <i v-if="device.status==='testing'" class="el-icon-video-pause tpw-error"> 测试中</i>
                                    <i v-if="device.status==='colding'" class="tpw-loading"><i class="el-icon-loading"/> 冷却中</i>
                                </span>
                                <el-button style="float: right" size="mini" v-if="device.status==='online'" type="primary" @click="useDevice(device)">立即使用</el-button>
                                <el-button style="float: right" size="mini" v-if="device.status==='using' && device.user===currentUser" type="danger" @click="releaseDevice(device)">停用</el-button>
                                <el-button style="float: right" size="mini" v-if="device.status==='using' && device.user!==currentUser" type="info" disabled><i class="el-icon-s-custom"> {{device.user}}</i></el-button>
                                <el-button style="float: right" size="mini" v-if="device.status==='testing'" type="primary" @click="viewDevice(device)">查看</el-button>
                            </div>
                            <div class="box-body">
                                <div style="display: flex">
                                    <div style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;margin-right:15px">
                                        <span style="font-weight:bold;font-size:16px;">{{device.name}}</span>
                                    </div>
                                    <i class="el-icon-edit-outline" style="font-size:12px;margin-left:-12px;margin-top:6px" @click="editDevice(device)"/>
                                </div>
                                <div style="display:flex; margin-top: 30px">
                                    <img class="box-img" :src="device.img" alt=""/>
                                    <div>
                                        <div class="box-info">品牌: {{device.brand}}</div>
                                        <div class="box-info">型号: {{device.model}}</div>
                                        <div class="box-info">系统: {{device.system}} {{device.version}}</div>
                                        <div class="box-info">分辨率: {{device.size}}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <el-dialog title="编辑设备名称" :visible.sync="deviceVisible" width="600px" :append-to-body="true" destroy-on-close @closed="deviceVisible=false">
            <el-form label-width="120px" style="padding-right: 30px;" :model="deviceForm" :rules="rules" ref="deviceForm">
              <el-form-item label="设备名" prop="name">
                <el-input size="small" style="width: 90%" v-model="deviceForm.name" auto-complete="off" placeholder="请输入设备名"/>
              </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="small" @click="deviceVisible=false">取消</el-button>
                <el-button size="small" type="primary" class="title" @click="submitForm('deviceForm', deviceForm)">保存</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
let elementResizeDetectorMaker = require('element-resize-detector');
export default {
  data() {
    return {
      searchForm: {
        condition: '',
        status: '',
        filter: {
          brand: [],
          android: [],
          apple: [],
          size: []
        }
      },
      statusList: [
        {id: 'online', name: '空闲中'},
        {id: 'using', name: '占用中'},
        {id: 'testing', name: "测试中"},
        {id: 'colding', name: '冷却中'},
        {id: 'offline', name: '已离线'}
      ],
      filterData: [],
      devicesData: [],
      devicesCount: 0,
      deviceForm: {
        name: '',
        serial: ''
      },
      deviceVisible: false,
      rules: {
        name: [{ required: true, message: '设备名称不能为空', trigger: 'blur' }]
      },
      boxWidth: 250,
      rowSize: 5,
      keys: {
        'brand': '品牌',
        'android': 'Android系统',
        'apple': 'IOS系统',
        'size': '屏幕分辨率'
      },
      currentUser: ''
    }
  },
  mounted() {
    let erd = elementResizeDetectorMaker();
    let that = this;
    erd.listenTo(document.getElementsByClassName('device-list'), function(element) {
      that.getWidth();
    });
    this.getWidth();
    this.getData();
  },
  watch: {
    boxWidth: function (n, o) {
      this.getData();
    }
  },
  created() {
    this.$root.Bus.$emit('initBread', ["环境中心", "设备管理"]);
    this.currentUser = this.$store.state.userInfo.id;
    this.getFilter();
  },
  methods: {
    // 获取过滤条件
    getFilter() {
      let url = '/autotest/device/filter';
      this.$get(url, response => {
        let filter = response.data;
        this.filterData = [];
        for (let key in this.keys) {
          let filterName = this.keys[key];
          let conditions = [];
          for (let i = 0; i < filter[key].length; i++) {
            if (i < 8) {
              conditions.push(filter[key][i]);
            } else {
              break;
            }
          }
          this.filterData.push({
            name: filterName,
            conditions: conditions,
            allConditions: filter[key],
            showMore: conditions.length < filter[key].length,
            showAll: false,
            showBtn: '更多',
            icon: 'el-icon-arrow-down',
            selected: [],
            isIndeterminate: false,
            selectAll: false
          });
        }
      });
    },
    // 获取设备列表
    getData() {
      let url = '/autotest/device/list';
      this.$post(url, this.searchForm, response => {
        let devicesData = [];
        let data = response.data;
        for (let i = 0; i < data.length / this.rowSize; i++) {
          let rowData = data.slice(this.rowSize * i, this.rowSize * (i + 1));
          devicesData.push(rowData);
        }
        this.devicesCount = data.length;
        this.devicesData = devicesData;
      });
    },
    // 获取屏幕宽度
    getWidth() {
      let screenWidth = document.getElementsByClassName('device-list')[0].clientWidth + 20;
      this.rowSize = parseInt(screenWidth / 250);
      this.boxWidth = parseInt(screenWidth / this.rowSize);
    },
    // 搜索按钮
    search() {
      this.getData();
    },
    // 重置按钮
    reset() {
      this.searchForm.condition = '';
      this.searchForm.status = '';
    },
    // 筛选
    fileterDevice() {
      this.searchForm.filter = {
        brand: this.filterData[0].selected,
        android: this.filterData[1].selected,
        apple: this.filterData[2].selected,
        size: this.filterData[3].selected
      };
      this.getData();
    },
    // 全选
    handleCheckAllChange(row) {
      row.isIndeterminate = false;
      if (row.selectAll) {
        row.selected = row.conditions;
      } else {
        row.selected = [];
      }
      this.fileterDevice();
    },
    // 单选
    handleCheckedChange(row) {
      if (row.selected.length === row.conditions.length) {
        row.isIndeterminate = false;
        row.selectAll = true;
      } else if (row.selected.length === 0) {
        row.isIndeterminate = false;
        row.selectAll = false;
      } else {
        row.isIndeterminate = true;
      }
      this.fileterDevice();
    },
    // 展开筛选条件
    showMore(row) {
      row.showAll = !row.showAll;
      if (row.showAll) {
        row.conditions = row.allConditions;
        row.showBtn = '收起';
        row.icon = 'el-icon-arrow-up';
      } else {
        row.conditions = row.allConditions.slice(0, 8);
        row.showBtn = '更多';
        row.icon = 'el-icon-arrow-down';
      }
    },
    // 使用设备
    useDevice(device) {
      let url = '/autotest/device/use/' + device.serial + "/600"; // 默认十分钟超时
      this.$post(url, null, response => {
        if(response.data === true){
          let path = "/#/envCenter/deviceControl/" + device.serial;
          this.getData();
          window.open(path);
        }else{
          this.$message.error('设备使用申请失败 请刷新页面重试');
        }
      });
    },
    // 释放设备
    releaseDevice(device) {
      device.status = "colding";
      let url = '/autotest/device/stop/' + device.serial;
      this.$post(url, null, response => {
        this.getData();
      });
    },
    // 查看设备
    viewDevice(device) {
      window.open("/#/envCenter/deviceControl/" + device.serial);
    },
    editDevice(device) {
      this.deviceForm.serial = device.serial;
      this.deviceForm.name = device.name;
      this.deviceVisible = true;
    },
    submitForm(confirm, form) {
      this.$refs[confirm].validate(valid => {
        if (valid) {
          let url = '/autotest/device/update';
          let data = {
            serial: form.serial,
            name: form.name
          };
          this.result = this.$request.post(url, {apiServer: 'cloudphone', data: data}).then(response => {
            this.$message.success('保存成功');
            this.deviceVisible = false;
            // 更新列表
            this.getData();
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.device-list{
    min-width: 300px;
}
.el-table--scrollable-x .el-table__body-wrapper {
    overflow-x: hidden;
}
.filter-table >>> .el-table__cell{
    padding: 4px 0px !important;
}
.filter-table >>> tbody tr:hover>td {
    background-color: #FFF !important;
}
.filter-table >>> tbody tr.el-table__row--striped:hover>td {
    background-color: #FAFAFA !important;
}
.filter-table >>> tbody tr td{
	border: none;
}
.filter-table::before{
	height: 0px;
}
.device-table{
    margin-top: 10px;
    padding: 10px 0px;
    border-top: 1px solid rgb(219, 219, 219);
    min-height: 300px;
}
.device-box-row{
    display: flex;
    margin: 0px -10px;
}
.device-box{
    padding: 10px;
}
.device-box-border{
    width: 100%;
    padding-top: 100%;
    border: 1px solid rgb(219, 219, 219);
}
.device-box-info{
    margin-top: -100%;
}
.box-header{
    height: 40px;
    display: flex;
    justify-content:space-between;
    align-items: center;
    padding: 0px 10px;
    border-bottom: 1px solid rgb(219, 219, 219);
}
.box-body{
    padding: 20px;
}
.box-img{
    width: 72px;
    height: 108px;
    margin-right: 10px;
}
.box-info{
    font-size: 12px;
    margin-bottom: 15px;
    overflow-x: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>
