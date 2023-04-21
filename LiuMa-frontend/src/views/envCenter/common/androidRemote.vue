/**
* 安卓远程控制
*/
<template>
    <div>
        <div class="disabled-view" v-if="device.status==='testing'"/>
        <el-row :gutter="10">
            <el-col :span="6">
                <div class="screen-header">
                    <div style="float:left; margin: 12px 4px" class="long-text">
                        <el-tooltip :content="device.serial" placement="bottom">
                            <span>{{device.name}}</span>
                        </el-tooltip>
                    </div>
                    <div style="float:right; margin: 7px 3px">
                        <el-button type="danger" size="mini" @click="stopUsing" round :disabled="device.serial===null">停用</el-button>
                    </div>
                </div>
                <div class="screen-body" :style="'height: '+ screenHeight +'px'">
                    <video id="screen-player" class="scrcpy-screen" muted autoplay></video>
                    <span class="finger finger-0" style="transform: translate3d(200px, 100px, 0px)"></span>
                    <span class="finger finger-1" style="transform: translate3d(200px, 100px, 0px)"></span>
                </div>
                <div class="screen-footer">
                    <el-button size="mini" type="text" :style="'width:'+ (screenWidth-20)/3 +'px;margin:0px !important'" @click="runKeyevent('MENU')">
                        <i class="el-icon-menu device-sys-btn"/>
                    </el-button>
                    <el-button size="mini" type="text" :style="'width:'+ (screenWidth-20)/3 +'px;margin:0px !important'" @click="runKeyevent('HOME')">
                        <i class="el-icon-s-home device-sys-btn"/>
                    </el-button>
                    <el-button size="mini" type="text" :style="'width:'+ (screenWidth-20)/3 +'px;margin:0px !important'" @click="runKeyevent('BACK')">
                        <i class="el-icon-caret-left device-sys-btn"/>
                    </el-button>
                </div>
            </el-col>
            <el-col :span="18">
                <el-tabs v-model="activeName" @tab-click="handleTabClick">
                    <el-tab-pane label="常用功能" name="common">
                        <div class="card-columns" ref="commonContainer">
                            <div class="card">
                                <div class="card-header">
                                    输入框
                                    <span style="float:right">
                                        <el-button type="text" size="mini" @click="fixInputMethod(false)">修复输入法</el-button>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <el-input size="small" :autosize="{ minRows: 3}" type="textarea" ref="whatsinput"
                                        :disabled="whatsinput.disabled" clearable placeholder="请输入..." v-model="whatsinput.text"
                                        @keydown.tab.native="sendInputKey('tab')" @keydown.enter.native="sendInputKey('enter')"
                                        @input="sendInputText"/>

                                    <span style="text-align: right; font-size: 0.74em; color: gray;">
                                        <code>Shift+Enter</code> to start a new line, <code>Enter</code> to
                                        send</span>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">常用地址</div>
                                <div class="card-body">
                                    <dl>
                                        <dt>ATX-AGENT地址: </dt>
                                        <dd><code v-text="device.sources.atxAgentAddress"></code></dd>
                                    </dl>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">当前应用</div>
                                <div class="card-body">
                                    <div class="form-group">
                                        <el-input v-model="topApp.packageName" size="small" placeholder="PackageName"/>
                                    </div>
                                    <div class="form-group">
                                        <el-input v-model="topApp.activity" size="small" placeholder="Activity"/>
                                    </div>
                                    <el-button type="primary" size="small" @click="refreshTopApp">刷新</el-button>
                                    <el-button type="primary" size="small" :disabled="!topApp.packageName" @click='runShell("am force-stop "+topApp.packageName)'> Kill </el-button>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">应用安装</div>
                                <div class="card-body">
                                    <el-upload class="upload-demo" accept=".apk" drag action :limit="1" :http-request="uploadAPK" :before-upload="verifyFile">
                                        <i class="el-icon-upload"></i>
                                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                                        <div class="el-upload__tip" slot="tip">暂时只支持apk的上传</div>
                                    </el-upload>
                                    <div class="form-group" style="margin-top: 20px">
                                        <span>URL</span>
                                        <el-input style="margin-top:5px" size="small" placeholder="http://..." v-model="app.installUrl"/>
                                    </div>
                                    <el-button style="margin-top:5px" type="primary" size="small" @click="appInstall" :disabled="!app.finished || !app.installUrl">安裝</el-button>
                                    <p>
                                        <pre v-show="!!app.message" v-text="app.message"></pre>
                                    </p>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">截图下载</div>
                                <div class="card-body">
                                    <a :href="'http://' + device.sources.atxAgentAddress + '/screenshot/0?download=screenshot.jpg'" download>下载截图</a>
                                </div>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="控件元素" name="control">
                        <control-view v-show="activeName==='control'" system="android" :device="device" :loading="controlLoading" :imageHeight="screenHeight*7/8"/>
                    </el-tab-pane>
                    <el-tab-pane label="测试用例" name="testcase">
                        <app-case-list v-show="activeName==='testcase'" system="android" :deviceId="deviceId" :drawerWidth="drawerWidth"/>
                    </el-tab-pane>
                </el-tabs>
            </el-col>
        </el-row>
    </div>
</template>
<script>
import AppCaseList from '../common/appCaseList';
import controlView from '../common/controlView'
import $ from 'jquery';
const JMuxer = require('jmuxer');
let elementResizeDetectorMaker = require('element-resize-detector');
export default {
    name: 'AndroidRemote',
    components:{
        AppCaseList, controlView
    },
    props:{
        deviceId: String,
    },
    data() {
        return{
            device: {
                name:null,
                serial: null,
                status: 'using',
                sources: {
                    url:null,
                    atxAgentAddress: null
                }
            },
            screenWidth: 300,
            screenHeight: 500,
            activeName: 'common',
            websockets: {
                screen:null,
                touch:null,
                winput: null,
            },
            whatsinput: {
              text: "",
              disabled: true,
            },
            topApp: {
              packageName: '',
              activity: '',
              pid: '',
            },
            app: {
              installUrl: "",
              finished: true,
              message: ""
            },
            drawerWidth: 900,
            controlLoading: false
        }
    },
    mounted: function () {
        let erd = elementResizeDetectorMaker();
        let that = this;
        erd.listenTo(document.getElementsByClassName('screen-body'), function(element) {
            that.getScreenHeight(that.device.size);
        });
        // 获取设备信息
        this.getDevice(this.deviceId)
    },
    methods: {
        getDevice(deviceId) {
            let url = '/autotest/device/detail/' + deviceId;
            this.$get(url, response =>{
                let data = response.data;
                // 判断当前操作人是否是设备占用者
                if(data.status==='using' & data.user!==this.$store.state.userInfo.id){
                    this.$message.warning("当前设备已被他人占用 请稍后再试");
                    return;
                }
                if(data.status!=='testing' & data.user!==this.$store.state.userInfo.id){
                    this.$message.warning("当前设备不可用 请稍后再试");
                    return;
                }
                if(data.status==='testing'){
                    this.$alert("当前设备执行测试中 禁用操作", '禁用提示', {
                        confirmButtonText: '关闭页面',
                        type: 'warning'
                    }).then(() => {
                        window.close();
                    });
                }
                data.sources = JSON.parse(data.sources);
                this.device = data;
                // 动态计算屏幕区域高度
                this.getScreenHeight(this.device.size);
                // 等待渲染完成
                this.$nextTick(() => {
                    // 开启投屏
                    this.mirrorDisplay();
                    // 开启操作
                    this.syncTouchpad();
                    // 唤醒屏幕
                    this.runKeyevent("WAKEUP");
                    // 加载whatsinput输入法
                    this.loadWhatsinput();
                    // 当设备不使用时自动退出
                    this.closeWindowWhenReleased(5000);
                    // Disable WhatsInputMethod to prevent influence UIAutomation
                    this.fixInputMethod(true);
                });
            });
        },
        getScreenHeight(size){
            this.screenWidth = document.getElementsByClassName("screen-body")[0].offsetWidth;
            this.drawerWidth = parseInt(this.screenWidth*3)+40;
            if(!size){
                return;
            }
            const s = size.split("x");
            this.screenHeight = s[1]/s[0]*(this.screenWidth-4)-10;
        },
        verifyFile(file) {
            let s = file.name.split(".");
            if(s[s.length-1] !== "apk"){
                this.$message.warning("文件格式不正确");
                return false;
            }
        },
        uploadAPK(option) {
            let url = '/autotest/file/package/upload';
            let data = {
                name: option.file.name
            };
            let file = option.file;
            this.$fileUpload(url, file, null, data, response =>{
                this.$message.success("上传成功");
                this.app.installUrl = window.location.origin + response.data;
            });
        },
        appInstall() {
            this.app.finished = false;
            this.app.message = "安裝中 ...";

            this.$axios.post(this.device.sources.url + "/app/install",
            JSON.stringify({
                serial: this.device.serial,
                url: this.app.installUrl
            })).then(ret => {
                this.app.message = ret.data.message;
            }).finally(() => {
                this.app.finished = true;
            })
        },
        fixInputMethod(quite) {
            const inputMethod = "com.buscode.whatsinput/.WifiInputMethod";
            return this.runShell("ime enable " + inputMethod).then(() => {
                return this.runShell("ime set " + inputMethod);
            }).then(() => {
                this.$refs.whatsinput.focus();
                return this.loadWhatsinput();
            }).then(() => {
                if (!quite) {
                    this.$message.success({ message: "输入法修复完成" });
                }
            }, (ev) => {
                if (!quite) {
                    this.$message.success({ message: "输入法修复失败，F12查看详情" });
                }
            })
        },
        loadWhatsinput(callback) {
            let defer = $.Deferred()
            let ws = new WebSocket("ws://" + this.device.sources.whatsInputAddress)
            this.websockets.winput = ws;
            ws.onopen = (ev) => {
                defer.resolve();
            }
            ws.onmessage = (ev) => {
                let data = JSON.parse(ev.data);
                switch (data.type) {
                    case "InputStart":
                        this.whatsinput.text = data.text;
                        this.whatsinput.disabled = false;
                        setTimeout(() => {
                            this.$refs.whatsinput.focus();
                            this.$refs.whatsinput.select();
                        }, 1);
                        break;
                    case "InputFinish":
                        this.whatsinput.disabled = true;
                        break;
                    case "InputChange":
                        this.whatsinput.text = data.text;
                        break;
                }
            }
            ws.onerror = (ev) => {
                defer.reject();
            }
            ws.onclose = (ev) => {
                if (ws === this.websockets.winput) {
                    this.websockets.winput = null;
                }
            }
            return defer;
        },
        sendInputText() {
            let ws = this.websockets.winput;
            ws.send(JSON.stringify({
                type: "InputEdit",
                text: this.whatsinput.text,
            }));
        },
        sendInputKey(key) {
            let code = { "enter": 66, "tab": 61 }[key] || key;
            let ws = this.websockets.winput;
            ws.send(JSON.stringify({
                type: "InputKey",
                code: "" + code,
            }));
        },
        closeWindowWhenReleased(interval) {
            setTimeout(() => {
                if (!document.hidden) { // 设备在操作 刷新超时时间
                    let url = '/autotest/device/active/' + this.deviceId;
                    this.$post(url, null, response =>{
                        if(response.data){  // 更新成功
                            this.closeWindowWhenReleased(interval) 
                        }else{  // 设备可能已经离线
                            this.$message.warning('设备已经被释放了!');
                            this.device.serial = null;
                        }
                    });
                } else {
                    this.closeWindowWhenReleased(5000)
                }
            }, interval);
        },
        stopUsing() {
            let url = '/autotest/device/stop/' + this.device.id;
            this.$post(url, null, response => {
                window.close();
            });
        },
        runShell(command) {
            return this.$axios.post("http://" + this.device.sources.atxAgentAddress + "/shell?command="+ command)
            .then(ret => {
                return ret.data;
            })
        },
        runKeyevent(key) {
            return this.runShell("input keyevent " + key.toUpperCase());
        },
        handleTabClick(tab) {
            if(tab.name==='control'){
                this.controlLoading = true;
            }else{
                this.controlLoading = false;
            }
        },
        mirrorDisplay() {
            let jmu = new JMuxer({
                node: 'screen-player',
                mode: 'video',
                flushingTime: 0,
                fps: 25,
                onError: function(data) {
                    if (/Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor)) {
                        jmu.reset();
                    }
                },
                debug: false
            });
            var ws = new WebSocket("ws://" + this.device.sources.scrcpyServerAddress + '/screen');
            this.websockets.screen = ws;
            ws.binaryType = 'arraybuffer';
            ws.onmessage = (event) => {
                jmu.feed({
                    video: new Uint8Array(event.data)
                });
            };
            ws.onclose = (event) => {
                if(this.websockets.screen === ws){
                    this.$message.error('设备屏幕同步结束, 请刷新页面');
                    this.websockets.screen = null;
                }
            };
        },
        syncTouchpad() {
            let element = document.getElementById('screen-player');
            var ws = new WebSocket("ws://" + this.device.sources.scrcpyServerAddress + '/touch');
            this.websockets.touch = ws;
            ws.onclose = (event) => {
                if(this.websockets.touch === ws){
                    this.$message.error('设备操作异常, 请刷新页面');
                    this.websockets.touch = null;
                }
                
            };

            // touch事件
            function inject_touch_event(pix_data, action){
                let msg = {
                    msg_type: 2,
                    action: action,
                    x: pix_data[0],
                    y: pix_data[1],
                };
                ws.send(JSON.stringify(msg));
            }

            // scroll事件
            function inject_scroll_event(pix_data){
                let msg = {
                    msg_type: 3,
                    x: pix_data[0],
                    y: pix_data[1],
                    distance_x: pix_data[2],
                    distance_y: pix_data[3],
                };
                ws.send(JSON.stringify(msg));
            }

            // swipe
            function swipe(pix_data, delay=0, unit=13){
                delay = parseFloat(delay.toFixed(2));
                if (delay <= 3 && delay >=0){
                    let msg = {
                        msg_type: 30,
                        x: pix_data[0],
                        y: pix_data[1],
                        end_x: pix_data[2],
                        end_y: pix_data[3],
                        unit: unit,
                        delay: delay,
                    };
                    ws.send(JSON.stringify(msg));
                }
            }

            // 节流函数
            function throttle(fn,during) {
                let t = null;
                return function(e){
                    if(!t){
                        t = setTimeout(()=>{
                            fn.call(this,e);
                            t = null;
                        },during);
                    }
                }
            }

            // 获取鼠标在元素内的坐标
            function get_pointer_position(event, ele){
                let x = parseInt(event.layerX);
                x = Math.min(x, ele.offsetWidth);
                x = Math.max(x, 0);
                let y = parseInt(event.layerY);
                y = Math.min(y, ele.offsetHeight);
                y = Math.max(y, 0);
                return [x/ele.offsetWidth, y/ele.offsetHeight];
            }

            // canvas鼠标移动事件处理函数
            function canvas_mouse_move(event) {
                let pix_data = get_pointer_position(event, this);
                inject_touch_event(pix_data, 2);
            }

            // touch事件
            function add_canvas_touch_event(ele){
                // 在window对象记录touch开始
                window.touch_start = null;
                // 节流的mouse_move
                let efficient_canvas_mouse_move = throttle(canvas_mouse_move, 30);
                // 1.mousedown
                ele.addEventListener('mousedown', function (event) {
                    if(event.buttons == 1){
                        window.touch_start = true;
                        this.removeEventListener("mousemove", efficient_canvas_mouse_move);
                        let pix_data = get_pointer_position(event, this);
                        inject_touch_event(pix_data, 0);
                        this.addEventListener('mousemove', efficient_canvas_mouse_move);
                    }
                })
                // 2.mouseup
                ele.addEventListener('mouseup', function (event) {
                    if (window.touch_start){
                        window.touch_start = false;
                        let pix_data = get_pointer_position(event, this);
                        inject_touch_event(pix_data, 1);
                        this.removeEventListener("mousemove", efficient_canvas_mouse_move);
                    }
                })
                // 3.mouseout
                ele.addEventListener('mouseout', function (event) {
                    if (window.touch_start){
                        window.touch_start = false;
                        let pix_data = get_pointer_position(event, this);
                        inject_touch_event(pix_data, 1);
                        this.removeEventListener("mousemove", efficient_canvas_mouse_move);
                    }
                })
            }

            // swipe事件
            function add_canvas_swipe_event(ele){
                window.swipe_start = null;
                window.swipe_start_pix_data = null;
                // 1.mousedown
                ele.addEventListener('mousedown', function (event) {
                    if(event.buttons == 4){
                        window.swipe_start = Date.now();
                        window.swipe_start_pix_data = get_pointer_position(event, this);
                    }
                })
                // 2.mouseup
                ele.addEventListener('mouseup', function (event) {
                    if (window.swipe_start){
                        let swipe_end = Date.now();
                        let delay = (swipe_end - window.swipe_start)/1000;
                        window.swipe_start = null;
                        let swipe_end_pix_data = get_pointer_position(event, this);
                        let pix_data = window.swipe_start_pix_data.concat(swipe_end_pix_data);
                        window.swipe_start_pix_data = null;
                        swipe(pix_data, delay);
                    }
                })
                // 3.mouseout
                ele.addEventListener('mouseout', function (event) {
                    if (window.swipe_start){
                        let swipe_end = Date.now();
                        let delay = (swipe_end - window.swipe_start)/1000;
                        window.swipe_start = null;
                        let swipe_end_pix_data = get_pointer_position(event, this);
                        let pix_data = window.swipe_start_pix_data.concat(swipe_end_pix_data);
                        window.swipe_start_pix_data = null;
                        swipe(pix_data, delay);
                    }
                })
            }

            // 处理canvas mouse scroll
            function canvas_mouse_scroll(event) {
                let pix_data = get_pointer_position(event, this);
                let distance_x = 1;
                if (event.deltaX >0){
                    distance_x = -1;
                }
                pix_data[2] = distance_x;
                let distance_y = 1;
                if (event.deltaY >0){
                    distance_y = -1;
                }
                pix_data[3] = distance_y;
                inject_scroll_event(pix_data);
            }

            // scroll事件
            function add_canvas_scroll_event(ele){
                let efficient_canvas_mouse_scroll = throttle(canvas_mouse_scroll, 100);
                ele.addEventListener("wheel", efficient_canvas_mouse_scroll);
            }

            add_canvas_touch_event(element);
            add_canvas_swipe_event(element);
            add_canvas_scroll_event(element);
        },
        refreshTopApp() {
            this.runShell("dumpsys activity top").then(ret => {
                const reActivity = String.raw`\s*ACTIVITY ([A-Za-z0-9_.]+)\/([A-Za-z0-9_.]+) \w+ pid=(\d+)`;
                let matches = ret.output.match(new RegExp(reActivity, "g"));
                if (matches.length > 0) {
                    let m = matches.pop().match(new RegExp(reActivity));
                    this.topApp.packageName = m[1];
                    this.topApp.activity = m[2];
                    this.topApp.pid = m[3];
                }
            })
        }
    },
}
</script>
<style scoped>
.disabled-view{
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.05);
    margin: 42px 20px 20px 68px;
    z-index: 1500;
}

.screen-header{
    height: 42px;
    position: relative;
    border-radius: 12px 12px 0px 0px;
    border-top:2px solid rgb(219, 219, 219);
    border-left: 2px solid rgb(219, 219, 219);
    border-right: 2px solid rgb(219, 219, 219);
    background-color: rgb(44, 44, 44);
}

.screen-body{
    background-color: black;
    border-left: 2px solid rgb(219, 219, 219);
    border-right: 2px solid rgb(219, 219, 219);
}

.screen-footer{
    height: 36px;
    position: relative;
    border-radius: 0px 0px 12px 12px;
    border-bottom:2px solid rgb(219, 219, 219);
    border-left: 2px solid rgb(219, 219, 219);
    border-right: 2px solid rgb(219, 219, 219);
    background-color: rgb(44, 44, 44);
}

.long-text {
    width: 100px;
    overflow-x: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: white;
}

.device-sys-btn{
    width: 100%;
    font-size: 18px;
}

.cursor-pointer {
    cursor: pointer;
}

.scrcpy-screen {
    z-index: 20;
    max-width: 100%;
    height: auto;
}

.card-columns {
    column-count: 2;
}

.card {
    position: relative;
    display: -ms-flexbox;
    display: flex;
    -ms-flex-direction: column;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-color: #fff;
    background-clip: border-box;
    border: 1px solid rgba(0,0,0,.125);
    border-radius: 0.25rem;
    margin-bottom: 15px;
}

.card-header {
    padding: 0.75rem 1.25rem;
    margin-bottom: 0;
    color: inherit;
    background-color: rgba(0,0,0,.03);
    border-bottom: 1px solid rgba(0,0,0,.125);
}

.card-body {
    -ms-flex: 1 1 auto;
    flex: 1 1 auto;
    padding: 1.25rem;
}

.form-group {
    margin-bottom: 10px;
}

dt {
    font-weight: 700;
    display: block;
}

dd {
    margin: 0.25rem 0px;
    display: block;
    margin-inline-start: 10px;
}

dl {
    display: block;
    margin-block-start: 1em;
    margin-block-end: 1em;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
}
</style>
