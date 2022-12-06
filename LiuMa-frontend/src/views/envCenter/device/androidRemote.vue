/**
* 安卓远程控制
*/
<template>
    <div>
        <el-row :gutter="10">
            <el-col :xs="8" :md="6" :xl="4">
                <div class="screen-header">
                    <div style="float:left; margin: 10px 5px">
                        <span style="margin-left:5px;margin-top:11px">{{device.serial}}</span>
                    </div>
                    <div style="float:right; margin: 4px 5px">
                        <el-button type="danger" size="mini" @click="stopUsing" >停用</el-button>
                    </div>
                </div>
                <div class="screen-body">
                    <video id="screen-player" class="scrcpy-fg" muted autoplay></video>
                    <span class="finger finger-0" style="transform: translate3d(200px, 100px, 0px)"></span>
                    <span class="finger finger-1" style="transform: translate3d(200px, 100px, 0px)"></span>
                </div>
                <div class="screen-footer">
                    <el-button size="mini" type="text" style="width:30%" @click="runKeyevent('MENU')" icon="el-icon-menu"></el-button>
                    <el-button size="mini" type="text" style="width:30%" @click="runKeyevent('HOME')" icon="el-icon-s-home"></el-button>
                    <el-button size="mini" type="text" style="width:30%" @click="runKeyevent('BACK')" icon="el-icon-caret-left"></el-button>
                </div>
            </el-col>
            <el-col :xs="16" :md="18" :xl="20">
                <el-tabs v-model="activeName" @tab-click="handleTabClick">
                    <el-tab-pane label="常用功能" name="common">
                        <div class="card-columns" ref="commonContainer">
                            <div class="card">
                                <div class="card-header">
                                    输入框
                                    <span style="float:right">
                                        <el-tooltip effect="dark" content="修复输入法" placement="top-start">
                                            <i @click="fixInputMethod"> 修复输入法</i>
                                        </el-tooltip>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <el-input size="small" :autosize="{ minRows: 3}" type="textarea"
                                        :disabled="whatsinput.disabled" clearable placeholder="请输入..." v-model="whatsinput.text"
                                        @keydown.tab.exact.prevent="sendInputKey('tab')" @keydown.enter.exact.prevent="sendInputKey('enter')"/>

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
                                        <dd><code v-text="deviceUrl"></code></dd>
                                        <dt>ADB远程连接: </dt>
                                        <dd><code v-text="remoteConnectAddr"></code></dd>
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
                                    <el-upload  ref="upload" class="upload-demo" drag action :http-request="uploadApk" multiple>
                                        <i class="el-icon-upload"></i>
                                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                                        <div class="el-upload__tip" slot="tip">暂时只支持apk的上传</div>
                                    </el-upload>
                                    <div class="form-group" style="margin-top: 20px">
                                        <span>URL</span>
                                        <el-input style="margin-top:5px" size="small" placeholder="http://..." v-model="app.installUrl"/>
                                    </div>
                                    <div>
                                        <el-checkbox v-model="app.launch">安装完成后启动应用</el-checkbox>
                                    </div>
                                    <el-button style="margin-top:5px" type="primary" size="small" @click="appInstall" :disabled="!app.finished || !app.installUrl">安裝</el-button>
                                    <p>
                                        <pre v-show="!!app.message" v-text="app.message"></pre>
                                        <small><code v-text="app.packageName"></code></small>
                                    </p>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">截图下载</div>
                                <div class="card-body">
                                    <a :href="screenshotUrl+'?download=screenshot.jpg'" download>下载截图</a>
                                </div>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="控件元素" name="control">

                    </el-tab-pane>
                    <el-tab-pane label="测试用例" name="testcase">

                    </el-tab-pane>
                </el-tabs>
            </el-col>
        </el-row>
    </div>
</template>
<script>
const JMuxer = require('jmuxer');
export default {
    name: 'AndroidRemote',
    props:{
        device: Object,
    },
    data() {
        return{
            activeName: 'common',
            websockets: {
              remote: null,
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
              packageName: "",
              message: "",
              launch: true,
            }
        }
    },
    mounted: function () {
        // 开启投屏
        this.mirrorDisplay()

        // 开启操作
        this.syncTouchpad()

        // 唤醒屏幕
        this.runKeyevent("WAKEUP")

        // 加载whatsinput输入法
        this.loadWhatsinput()

        // 当设备不使用时自动退出
        this.closeWindowWhenReleased(5000)

        // Disable WhatsInputMethod to prevent influence UIAutomation
        this.fixInputMethod(true)
    },
    methods: {
        uploadApk(option) {
            let formData = new FormData();
            formData.append('file', option.file);
            $.ajax({
            method: "post",
            url: urlPrefix + "/uploads",
            data: formData,
            processData: false,
            contentType: false,
            }).done(ret => {
            this.app.installUrl = ret.data.url;
            this.$notify.success("上传成功");
            }).fail(err => {
            this.$notify.error("上传失败");
            })
        },
        appInstall() {
            this.app.packageName = ""
            this.app.finished = false
            this.app.message = "安裝中 ..."

            $.ajax({
            method: "post",
            url: this.address + "/app/install?udid=" + this.udid,
            data: {
                url: this.app.installUrl,
                launch: this.app.launch,
                secret: this.source.secret,
            }
            }).done(ret => {
            this.app.message = ret.output;
            this.app.packageName = ret.packageName;
            }).fail(err => {
            if (err.status == 400) {
                this.app.message = err.responseJSON.description;
            } else {
                this.app.message = err.responseText;
            }
            }).always(() => {
            this.app.finished = true
            })
        },
        fixInputMethod(quite) {
            if (!quite) {
            this.$notify.info({
                title: "输入法",
                message: "修复中",
            })
            }
            const inputMethod = "com.buscode.whatsinput/.WifiInputMethod"
            return this.runShell("ime enable " + inputMethod)
            .then(() => {
                return this.runShell("ime set " + inputMethod)
            })
            .then($.delay(1000))
            .then(() => {
                this.$refs.whatsinput.focus()
                return this.loadWhatsinput()
            })
            .then(() => {
                if (!quite) {
                this.$notify.success({ message: "输入法修复完成" })
                }
            }, () => {
                if (!quite) {
                this.$notify.success({ message: "输入法修复失败，F12查看详情" })
                }
            })
        },
        loadWhatsinput(callback) {
            console.log(this.whatsInputUrl)
            let defer = $.Deferred()
            let ws = new WebSocket(this.whatsInputUrl)
            this.websockets.winput = ws;
            ws.onopen = (ev) => {
            defer.resolve()
            console.log("whatsinput connected")
            }
            ws.onmessage = (ev) => {
            console.log("winput recv", ev)
            let data = JSON.parse(ev.data)
            switch (data.type) {
                case "InputStart":
                this.whatsinput.text = data.text;
                this.whatsinput.disabled = false
                setTimeout(() => {
                    this.$refs.whatsinput.focus()
                    this.$refs.whatsinput.select()
                }, 1)
                break;
                case "InputFinish":
                this.whatsinput.disabled = true
                break
                case "InputChange":
                this.whatsinput.text = data.text;
                break;
            }
            }
            ws.onerror = (ev) => {
            console.error(ev)
            defer.reject()
            }
            ws.onclose = (ev) => {
            console.log("winput closed")
            if (ws === this.websockets.winput) {
                this.websockets.winput = null;
            }
            }
            return defer;
        },
        sendInputText() {
            console.log("sync", this.whatsinput.text)
            let ws = this.websockets.winput;
            ws.send(JSON.stringify({
            type: "InputEdit",
            text: this.whatsinput.text,
            }))
        },
        sendInputKey(key) {
            console.log("Sync key", key)
            let code = { "enter": 66, "tab": 61 }[key] || key;
            let ws = this.websockets.winput;
            ws.send(JSON.stringify({
            type: "InputKey",
            code: "" + code,
            }))
        },
        stopUsing() {
            $.ajax({
            method: "delete",
            url: urlPrefix + "/api/v1/user/devices/" + this.udid,
            dataType: "json"
            }).always(() => {
            window.close()
            })
        },
        runShell(command) {
            return $.ajax({
            method: "get",
            url: this.deviceUrl + "/shell",
            data: {
                "command": command,
            },
            dataType: "json"
            }).then(ret => {
            console.log("runShell", command, ret)
            return ret;
            })
        },
        runKeyevent(key) {
            return this.runShell("input keyevent " + key.toUpperCase())
        },
        handleTabClick(tab, event) {
            if (tab.name == "control") {

            }
        },
        mirrorDisplay() {
            jmu = new JMuxer({
                node: 'screen-player',
                mode: 'video',
                flushingTime: 0,
                fps: 30,
                onError: function(data) {
                    if (/Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor)) {
                        jmu.reset();
                    }
                },
                debug: false
            });
            var ws = new WebSocket(this.deviceServerUrl + '/scrcpy/screen');
            this.websockets.remote = ws;
            ws.binaryType = 'arraybuffer';
            ws.onopen = (ev) => {};
            ws.onmessage = (ev) => {
                if(typeof(event.data) === "string"){
                  this.$message({
                    showClose: true,
                    message: event.data,
                    type: 'error',
                  });
                  ws.close()
                }else{
                  jmu.feed({
                    video: new Uint8Array(event.data)
                  });
                }
            };
            ws.onclose = (ev) => {
                if (this.websockets.screen === ws) {
                    this.websockets.screen = null;
                    this.$message({
                        showClose: true,
                        message: '设备屏幕同步中断',
                        type: 'error',
                    });
                }
            };
            ws.onerror = function (ev) {
                console.log("screen websocket error")
            };
        },
        syncTouchpad() {
            let element = document.getElementById('screen-player');
            let ws = this.websockets.remote;

            // touch事件
            function inject_touch_event(pix_data, action){
                msg = {
                msg_type: 2,
                action: action,
                x: pix_data[0],
                y: pix_data[1],
                }
                ws.send(JSON.stringify(msg))
            }

            // scroll事件
            function inject_scroll_event(pix_data){
                msg = {
                msg_type: 3,
                x: pix_data[0],
                y: pix_data[1],
                distance_x: pix_data[2],
                distance_y: pix_data[3],
                }
                ws.send(JSON.stringify(msg))
            }

            // swipe
            function swipe(pix_data, delay=0, unit=13){
                delay = parseFloat(delay.toFixed(2))
                if (delay <= 3 && delay >=0){
                msg = {
                    msg_type: 30,
                    x: pix_data[0],
                    y: pix_data[1],
                    end_x: pix_data[2],
                    end_y: pix_data[3],
                    unit: unit,
                    delay: delay,
                }
                ws.send(JSON.stringify(msg))
                }
            }

            // 节流函数
            function throttle(fn,during) {
                let t = null
                return function(e){
                    if(!t){
                        t = setTimeout(()=>{
                            fn.call(this,e)
                            t = null
                        },during)
                    }
                }
            }

            // 获取鼠标在元素内的坐标
            function get_pointer_position(event, ele){
                x = event.layerX;
                x = parseInt(x);
                x = Math.min(x, ele.offsetWidth);
                x = Math.max(x, 0);
                y = event.layerY;
                y = parseInt(y);
                y = Math.min(y, ele.offsetHeight);
                y = Math.max(y, 0);
                return [x/ele.offsetWidth, y/ele.offsetHeight]
            }

            // canvas鼠标移动事件处理函数
            function canvas_mouse_move(event) {
                pix_data = get_pointer_position(event, this)
                inject_touch_event(pix_data, 2)
            }

            // touch事件
            function add_canvas_touch_event(ele){
                // 在window对象记录touch开始
                window.touch_start = null
                // 节流的mouse_move
                efficient_canvas_mouse_move = throttle(canvas_mouse_move, 30);
                // 1.mousedown
                ele.addEventListener('mousedown', function (event) {
                    if(event.buttons == 1){
                    window.touch_start = true
                    this.removeEventListener("mousemove", efficient_canvas_mouse_move)
                    pix_data = get_pointer_position(event, this)
                    inject_touch_event(pix_data, 0)
                    this.addEventListener('mousemove', efficient_canvas_mouse_move)
                    }
                })
                // 2.mouseup
                ele.addEventListener('mouseup', function (event) {
                    if (window.touch_start){
                    window.touch_start = false
                    pix_data = get_pointer_position(event, this)
                    inject_touch_event(pix_data, 1)
                    this.removeEventListener("mousemove", efficient_canvas_mouse_move)
                    }
                })
                // 3.mouseout
                ele.addEventListener('mouseout', function (event) {
                    if (window.touch_start){
                    window.touch_start = false
                    pix_data = get_pointer_position(event, this)
                    inject_touch_event(pix_data, 1)
                    this.removeEventListener("mousemove", efficient_canvas_mouse_move)
                    }
                })
            }

            // swipe事件
            function add_canvas_swipe_event(ele){
                window.swipe_start = null
                window.swipe_start_pix_data = null
                // 1.mousedown
                ele.addEventListener('mousedown', function (event) {
                    if(event.buttons == 4){
                    window.swipe_start = Date.now()
                    window.swipe_start_pix_data = get_pointer_position(event, this)
                    }
                })
                // 2.mouseup
                ele.addEventListener('mouseup', function (event) {
                    if (window.swipe_start){
                    swipe_end = Date.now()
                    delay = (swipe_end - window.swipe_start)/1000
                    window.swipe_start = null
                    swipe_end_pix_data = get_pointer_position(event, this)
                    pix_data = window.swipe_start_pix_data.concat(swipe_end_pix_data)
                    window.swipe_start_pix_data = null
                    swipe(pix_data, delay)
                    }
                })
                // 3.mouseout
                ele.addEventListener('mouseout', function (event) {
                    if (window.swipe_start){
                    swipe_end = Date.now()
                    delay = (swipe_end - window.swipe_start)/1000
                    window.swipe_start = null
                    swipe_end_pix_data = get_pointer_position(event, this)
                    pix_data = window.swipe_start_pix_data.concat(swipe_end_pix_data)
                    window.swipe_start_pix_data = null
                    swipe(pix_data, delay)
                    }
                })
            }

            // 处理canvas mouse scroll
            function canvas_mouse_scroll(event) {
                pix_data = get_pointer_position(event, this)
                if (event.deltaX >0){
                distance_x = -1
                } else{
                distance_x = 1
                }
                pix_data[2] = distance_x
                if (event.deltaY >0){
                distance_y = -1
                } else{
                distance_y = 1
                }
                pix_data[3] = distance_y
                inject_scroll_event(pix_data)
            }

            // scroll事件
            function add_canvas_scroll_event(ele){
                efficient_canvas_mouse_scroll = throttle(canvas_mouse_scroll, 100);
                ele.addEventListener("wheel", efficient_canvas_mouse_scroll)
            }

            add_canvas_touch_event(element);
            add_canvas_swipe_event(element);
            add_canvas_scroll_event(element);
        },
        closeWindowWhenReleased(interval) {
            setTimeout(() => {
            if (document.hidden) {
                $.getJSON(urlPrefix + "/api/v1/user/devices/" + udid)
                .done(ret => {
                    this.closeWindowWhenReleased(5000)
                })
                .fail(ret => {
                    let content = '设备' + this.idleTimeout + "秒内没有操作，设备自动释放，点击刷新重新占用该设备"
                    this.$alert(content, '设备超时提示', {
                    confirmButtonText: '刷新',
                    type: 'warning'
                    }).then(() => {
                    location.reload()
                    }).catch(() => {
                    window.close()
                    })
                })
            } else {
                $.getJSON(urlPrefix + "/api/v1/user/devices/" + udid + "/active?email=" + userEmail)
                .done((ret) => {
                    this.closeWindowWhenReleased(interval)
                })
                .fail(function (ret) {
                    console.log(ret)
                    alert("设备可能被释放了，Press F12 to debug")
                })
            }
            }, interval)
        },
        refreshTopApp() {
            this.runShell("dumpsys activity top").then(ret => {
            const reActivity = String.raw`\s*ACTIVITY ([A-Za-z0-9_.]+)\/([A-Za-z0-9_.]+) \w+ pid=(\d+)`
            let matches = ret.output.match(new RegExp(reActivity, "g"))
            if (matches.length > 0) {
                let m = matches.pop().match(new RegExp(reActivity))
                this.topApp.packageName = m[1];
                this.topApp.activity = m[2]
                this.topApp.pid = m[3]
            }
            })
        }
    },
    computed: {
        address() {
            return this.device.sources.url;
        },
        deviceUrl() {
            return "http://" + this.device.sources.atxAgentAddress;
        },
        screenshotUrl() {
            return  this.deviceUrl + "/screenshot/0";
        },
        remoteConnectAddr() {
            return "adb connect " + this.device.sources.remoteConnectAddress;
        },
        whatsInputUrl() {
            return "ws://" + this.device.sources.whatsInputAddress;
        },
        deviceServerUrl() {
            return "http://" + this.device.sources.deviceServerAddress;
        }
    }

}
</script>
<style scoped>
.screen-header{
    height: 36px;
    position: relative;
    border:1px solid rgb(219, 219, 219);
}

.screen-body{
    min-height: 575px;
    background-color: gray;
    border:1px solid rgb(219, 219, 219);
}

.screen-footer{
    height: 32px;
    position: relative;
    border:1px solid rgb(219, 219, 219);
}

.cursor-pointer {
    cursor: pointer;
}

.scrcpy-fg {
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
    margin-bottom: 0.5rem;
    margin-left: 0;
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
