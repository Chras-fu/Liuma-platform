/**
* 安卓远程控制
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="6" min-width="400px">
                <div class="screen-header">
                    <span>
                        {{device.serial}}
                    </span>
                    <span @click="hotfix" class="cursor-pointer">
                        <i title="hotfix" class="fas fa-hammer"></i>
                    </span>
                    <div style="float:right">
                        <el-button type="danger" size="small" @click="stopUsing" >停用</el-button>
                    </div>
                </div>
                <div class="screen-body">
                    <video id="screen-player" class="scrcpy-fg" muted autoplay></video>
                    <span class="finger finger-0" style="transform: translate3d(200px, 100px, 0px)"></span>
                    <span class="finger finger-1" style="transform: translate3d(200px, 100px, 0px)"></span>
                </div>
                <div class="screen-footer">
                    <el-button size="mini" type="text" @click="runKeyevent('APP_SWITCH')"></el-button>
                    <el-button size="mini" type="text" @click="runKeyevent('MENU')"></el-button>
                    <el-button size="mini" type="text" @click="runKeyevent('HOME')"></el-button>
                    <el-button size="mini" type="text" @click="runKeyevent('BACK')"></el-button>
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
                                        <el-tooltip effect="dark" content="修复输入法" placement="top-start">
                                            <i @click="fixInputMethod"> 修复输入法</i>
                                        </el-tooltip>
                                    </span>
                                </div>
                                <div class="card-body">
                                    <el-input size="small" :autosize="{ minRows: 4}" type="textarea" 
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
                                        <dt>ATX-AGENT地址</dt>
                                        <dd><code v-text="deviceUrl"></code></dd>
                                        <dt>ADB远程连接</dt>
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
            rotation: 0,
            websockets: {
              screen: null,
              touchpad: null,
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
            },
            scheme: 'ws',
        }
    },
    created() {
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
        hotfix() {
            $.ajax({
            method: "get",
            url: this.deviceUrl + "/info/rotation"
            }).done(ret => {
            this.$notify({
                message: "Rotation updated",
                position: 'bottom-left',
                duration: 1000,
            })
            })
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
            var ws = new WebSocket(this.deviceServerUrl.replace(/^https?/, this.scheme) + '/scrcpy/screen');
            this.websockets.screen = ws;
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
            let ws = new WebSocket(this.address.replace(/^https?/, this.scheme) + '/scrcpy/touch?udid='+ this.serial);

            let screen = {
                bounds: {}
            }

            this.websockets.touchpad = ws

            ws.onopen = (ret) => {
                
            }
            ws.onmessage = (message) => {
                
            }
            ws.onclose = () => {
                if (this.websockets.touchpad === ws) {
                    this.websockets.touchpad = null;
                }
                element.removeEventListener('mousedown', mouseDownListener);
                element.removeEventListener('mousewheel', mouseWheelListener);
            }

            function calculateBounds() {
            var el = element;
            screen.bounds.w = el.offsetWidth
            screen.bounds.h = el.offsetHeight
            screen.bounds.x = 0
            screen.bounds.y = 0

            while (el.offsetParent) {
                screen.bounds.x += el.offsetLeft
                screen.bounds.y += el.offsetTop
                el = el.offsetParent
            }
            }

            function coords(boundingW, boundingH, relX, relY, rotation) {
            var w, h, x, y;

            switch (rotation) {
                case 0:
                w = boundingW
                h = boundingH
                x = relX
                y = relY
                break
                case 90:
                w = boundingH
                h = boundingW
                x = boundingH - relY
                y = relX
                break
                case 180:
                w = boundingW
                h = boundingH
                x = boundingW - relX
                y = boundingH - relY
                break
                case 270:
                w = boundingH
                h = boundingW
                x = relY
                y = boundingW - relX
                break
            }

            return {
                xP: x / w,
                yP: y / h,
            }
            }

            let touchDown = (index, x, y, pressure) => {
            let scaled = coords(screen.bounds.w, screen.bounds.h, x, y, this.rotation);
            ws.send(JSON.stringify({
                operation: 'd',
                index: index,
                pressure: pressure,
                xP: scaled.xP,
                yP: scaled.yP,
            }))
            }

            let touchMove = (index, x, y, pressure) => {
            let scaled = coords(screen.bounds.w, screen.bounds.h, x, y, this.rotation);
            ws.send(JSON.stringify({
                operation: 'm',
                index: index,
                pressure: pressure,
                xP: scaled.xP,
                yP: scaled.yP,
            }))
            }

            function touchUp(index, x, y, pressure) {
                let scaled = coords(screen.bounds.w, screen.bounds.h, x, y, this.rotation);
                ws.send(JSON.stringify({
                operation: 'u',
                index: index,
                pressure: pressure,
                xP: scaled.xP,
                yP: scaled.yP,
                }));
            }

            function touchWait(millseconds) {
                ws.send(JSON.stringify({
                    operation: 'w',
                    milliseconds: millseconds,
                }));
            }

            function touchReset() {
                ws.send(JSON.stringify({
                    operation: "r",
                }))
            }

            function touchCommit() {
                ws.send(JSON.stringify({ operation: 'c' }))
            }

            let mouseDownListener = (event) => {
                var e = event;
                if (e.originalEvent) {
                    e = e.originalEvent
                }
                e.preventDefault()

                // activate whatsinput
                this.$refs.whatsinput.focus()

                // Middle click equals HOME
                if (e.which === 2) {
                    this.runKeyevent("HOME")
                    return
                }
                // Right click equals BACK
                if (e.which === 3) {
                    this.runKeyevent("BACK")
                    return
                }

                fakePinch = e.altKey
                calculateBounds()

                var x = e.pageX - screen.bounds.x
                var y = e.pageY - screen.bounds.y
                var pressure = 0.5
                // activeFinger(0, e.pageX, e.pageY, pressure);

                touchDown(0, x, y, pressure);
                touchCommit();

                // element.removeEventListener('mousemove', mouseHoverListener);
                element.addEventListener('mousemove', mouseMoveListener);
                document.addEventListener('mouseup', mouseUpListener);
            }

            let mouseMoveListener = (event) => {
                var e = event
                if (e.originalEvent) {
                    e = e.originalEvent
                }
                // Skip secondary click
                if (e.which === 3) {
                    return
                }
                e.preventDefault()

                var pressure = 0.5
                activeFinger(0, e.pageX, e.pageY, pressure);
                var x = e.pageX - screen.bounds.x
                var y = e.pageY - screen.bounds.y

                touchMove(0, x, y, pressure);
                
                touchCommit();
            }

            function mouseUpListener(event) {
                var e = event
                if (e.originalEvent) {
                    e = e.originalEvent
                }
                // Skip secondary click
                if (e.which === 3) {
                    return
                }
                e.preventDefault()
                var pressure = 0.5
                activeFinger(0, e.pageX, e.pageY, pressure);
                var x = e.pageX - screen.bounds.x
                var y = e.pageY - screen.bounds.y
                touchUp(0, x, y, pressure);
                touchCommit()
                stopMousing()
            }

            function stopMousing() {
                element.removeEventListener('mousemove', mouseMoveListener);
                document.removeEventListener('mouseup', mouseUpListener);
                deactiveFinger(0);
            }

            function activeFinger(index, x, y, pressure) {
                var scale = 0.5 + pressure
            }

            function deactiveFinger(index) {
                    if(this.screenMode === "minicap"){
                        $(".finger-" + index).removeClass("active")
                    }
            }

            function preventHandler(event) {
                event.preventDefault()
            }

            let wheel = {
                count: 0,
                key: null,
                mouseY: null,
            };

            function mouseWheelListener(event) {
                let e = event;
                if (e.originalEvent) {
                    e = e.originalEvent;
                }

                calculateBounds()
                let x = e.pageX - screen.bounds.x;
                let y = e.pageY - screen.bounds.y;
                let pressure = 0.5;

                if (wheel.key === null) { // mouse down
                    wheel.mouseY = y;
                    touchDown(1, x, y, pressure);
                    touchCommit();
                } else {
                    clearTimeout(wheel.key);
                }

                // 从 wheel.mouseY --> targetY 分10步移动完
                wheel.count += 1;
                const stepCount = 10; // 10 steps
                const direction = e.deltaY > 0 ? -1 : 1;
                const offsetY = wheel.count * direction * 0.2 * screen.bounds.h;
                const targetY = Math.max(0, Math.min(y + offsetY, screen.bounds.h));

                let mouseY = wheel.mouseY;
                const stepY = (targetY - mouseY) / stepCount;
                for (let i = 0; i < stepCount; i += 1) {
                    mouseY += stepY;
                    touchWait(10); // 间隔10ms
                    touchMove(1, x, mouseY, pressure)
                    touchCommit();
                }
                wheel.mouseY = targetY; // 记录当前点的位置

                wheel.key = setTimeout(() => { // wheel stopped do mouse up
                    touchUp();
                    touchCommit();
                    wheel.key = null;
                    wheel.count = 0;
                }, 100)
                }

                /* bind listeners */
                element.addEventListener('mousedown', mouseDownListener);
                element.addEventListener('mousewheel', mouseWheelListener);
        },
        fitCanvas(canvas) {
            if (canvas.width > canvas.height) {
                // 横屏显示，宽高相等
                this.canvasStyle.maxHeight = canvas.parentElement.clientHeight + "px";
                this.canvasStyle.height = "auto";
                this.canvasStyle.width = canvas.parentElement.clientHeight + "px";
            } else {
                this.canvasStyle.maxHeight = "unset";
                this.canvasStyle.height = canvas.parentElement.clientHeight + "px";
                this.canvasStyle.width = "auto";
            }
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
            // return "http://" + this.device.sources.atxAgentAddress;
        },
        remoteTerminal() {
            return "http://" + this.deviceUrl + "/term";
        },
        screenshotUrl() {
            return  this.deviceUrl + "/screenshot/0";
        },
        remoteConnectAddr() {
            // return "adb connect " + this.device.sources.remoteConnectAddress;
        },
        whatsInputUrl() {
            // return this.scheme + "://" + this.device.sources.whatsInputAddress;
        },
        displayLinked() {
            return this.websockets.screen !== null;
        },
        deviceServerUrl() {
            // return "http://" + this.device.sources.deviceServerAddress;
        }
    }

}
</script>
<style scoped>
.screen-header{
    height: 38px;
    position: relative;
    border:1px solid rgb(219, 219, 219);
}

.screen-body{
    height: auto;
    background-color: gray;
    border:1px solid rgb(219, 219, 219);
}

.screen-footer{
    height: 38px;
    position: relative;
    border:1px solid rgb(219, 219, 219);
}

.cursor-pointer {
    cursor: pointer;
}

.screen {
    position: relative;
    background-color: gray;
}

.scrcpy-fg {
    z-index: 20;
    max-width: 100%;
    height: 700px;
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
    margin-bottom: 1rem;
}

dt {
    font-weight: 700;
    display: block;
}

dd {
    margin-bottom: 0.5rem;
    margin-left: 0;
    display: block;
    margin-inline-start: 40px;
}

dl {
    display: block;
    margin-block-start: 1em;
    margin-block-end: 1em;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
}
</style>
