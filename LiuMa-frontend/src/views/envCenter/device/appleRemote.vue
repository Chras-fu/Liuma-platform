/**
* 苹果远程控制
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :xs="8" :md="6" :xl="4" class="left-screen">
                <div class="screen-header">
                    <div style="float:left; margin:11px 4px">
                        <el-tooltip :content="device.serial" placement="bottom">
                            <span class="long-text">{{device.name}}</span>
                        </el-tooltip>
                    </div>
                    <div style="float:right; margin: 4px 3px">
                        <el-button type="danger" size="mini" @click="stopUsing" >停用</el-button>
                    </div>
                </div>
                <div class="screen-body" :style="'height: '+screenHeight+'px'">
                    <canvas ref="bgCanvas" class="canvas-bg" v-bind:style="canvasStyle"></canvas>
                    <span class="finger finger-0" style="transform: translate3d(200px, 100px, 0px)"></span>
                    <span class="finger finger-1" style="transform: translate3d(200px, 100px, 0px)"></span>
                </div>
                <div class="screen-footer">
                    <el-button size="mini" type="text" style="width:100%" @click="pressHome">
                        <i class="el-icon-s-home device-sys-btn"/>
                    </el-button>
                </div>
            </el-col>
            <el-col :xs="16" :md="18" :xl="20">
                <el-tabs v-model="activeName" @tab-click="handleTabClick">
                    <el-tab-pane label="常用功能" name="common">
                        <div class="card-columns" ref="commonContainer">
                            <div class="card">
                                <div class="card-header">
                                    常用操作
                                    <span style="font-size: 0.7em; padding-top: 5px; color: gray">注: iOS的弹窗不能通过屏幕点击来选择</span>
                                </div>
                                <div class="card-body">
                                    <el-button size="small" :loading="alert.loading" @click="chooseAlertButtons">选择弹框按钮</el-button>
                                    <el-dialog title="弹窗选择" :visible.sync="alert.visible">
                                        <span style="padding: 5px" v-for="(v, index) in alert.buttons" :key="index">
                                            <el-button round size="small" @click="alertAccept(v); alert.visible=false">{{v}}</el-button>
                                        </span>
                                    </el-dialog>
                                </div>
                            </div>
                
                            <div class="card">
                                <div class="card-header">常用信息</div>
                                <div class="card-body">
                                    <dl>
                                        <dt>SessionID</dt>
                                        <dd><code v-text="session.id"></code></dd>
                                        <dt>WDA URL</dt>
                                        <dd><code v-text="device.sources.wdaUrl"></code></dd>
                                    </dl>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">截图下载</div>
                                <div class="card-body">
                                    <el-button type="text" size="small" @click="screenshot">下载截图</el-button>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header">应用安装</div>
                                <div class="card-body">
                                    <el-upload ref="upload" accept=".ipa" drag action :http-request="uploadIPA">
                                        <i class="el-icon-upload"></i>
                                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                                        <div class="el-upload__tip" slot="tip">只能上传ipa文件，且不超过2G</div>
                                    </el-upload>
                                    <div class="form-group" style="margin-top: 20px">
                                        <span>URL</span>
                                        <el-input style="margin-top:5px" size="small" placeholder="http://..." v-model="app.url"/>
                                    </div>
                                    <el-button style="margin-top:5px" type="primary" size="small" @click="appInstall" :disabled="!app.finished || !app.url">安裝</el-button>
                                    <p>
                                        <pre v-show="!!app.message" v-text="app.message"></pre>
                                    </p>
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
import {ImagePool} from "@/utils/imagepool";
let elementResizeDetectorMaker = require('element-resize-detector');
import $ from 'jquery';
export default {
    name: 'AppleRemote',
    props:{
        serial: String,
    },
    data() {
        return{
            device: {
                name:null,
                serial: null,
                sources: {
                    wdaUrl: null
                }
            },
            screenWidth: 300,
            screenHeight: 550,
            activeName: 'common',
            canvas: {
                bg: null
            },
            canvasStyle: {
                opacity: 1,
                width: 'unset',
                height: 'unset',
                maxHeight: "unset",
            },
            session: {
                id: '',
                frameCount: 0,
            },
            display: {
                width: 0,
                height: 0,
                ws: null,
            },
            alert: {
                buttons: [],
                loading: false,
                visible: false,
            },
            app: {
                message: "",
                finished: true,
            },
            imagePool: new ImagePool(100),
        }
    },
    mounted: function () {
        let erd = elementResizeDetectorMaker();
        let that = this;
        erd.listenTo(document.getElementsByClassName('left-screen'), function(element) {
            that.getScreenHeight(that.device.size);
        });
        // 获取设备信息
        this.getDevice(this.serial);
    },
    watch: {
        screenWidth: function (n, o) {
            this.getScreenHeight(this.device.size);
        }
    },
    methods: {
        getDevice(serial) {
            let url = '/autotest/device/detail/' + serial;
            this.$get(url, response =>{
                let data = response.data;
                data.sources = JSON.parse(data.sources);
                this.device = data;
                // 等待渲染完成
                this.$nextTick(() => {
                    // 动态计算屏幕区域高度
                    this.getScreenHeight(data.size);
                    // 初始化canvas
                    this.canvas.bg = this.$refs.bgCanvas;
                    // 开启投屏
                    this.mirrorDisplay();
                    // 开启操作
                    this.syncTouchpad();
                    // 初始化session
                    this.initSession();
                    // 防止设备被自动释放
                    this.closeWindowWhenReleased(5000);
                });
            });
        },
        getScreenHeight(size){
            if(!size){
                return;
            }
            this.screenWidth = document.getElementsByClassName("screen-body")[0].offsetWidth;
            const s = size.split("*");
            this.screenHeight = parseInt(s[1]/s[0]*this.screenWidth);
            this.display.width = s[0];
            this.display.height = s[1];
        },
        screenshot() {
            this.$axios.get(this.path2url("/screenshot")).then(ret => {
                if (window.navigator.msSaveOrOpenBlob) {
                    alert("IE is not supported !!!")
                    return
                }
                var a = document.createElement("a");
                a.href = "data:image/jpeg;base64," + ret.data.value;
                a.download = "screen-" + new Date().getTime() + ".jpg";
                a.click();
                setTimeout(function () {
                    document.body.removeChild(a);
                }, 0);
            })
        },
        chooseAlertButtons() {
            this.alert.buttons = [];
            this.alert.loading = true;
            return this.$axios.get(this.path2url("/session/" + this.session.id + "/wda/alert/buttons")
            ).then(ret => {
                this.alert.buttons = ret.data.value;
                this.$nextTick(() => {
                    this.alert.visible = true;
                })
            }).finally(() => {
                this.alert.loading = false;
            })
        },
        alertAccept(name) {
            let data = null;
            if (typeof name === 'string' || name instanceof String) {
                data = JSON.stringify({ name: name });
            }
            return this.$axios.post(this.path2url("/session/" + this.session.id + "/alert/accept"), data);
        },
        uploadIPA(resp, file, files) {
            if (!resp.success) {
                this.$message({
                    message: resp.description,
                    type: "error",
                })
                return;
            }
            this.app.url = resp.data.url;
            return this.appInstall();
        },
        appInstall() {
            const url = this.app.url
            this.app.finished = false
            this.app.message = "安装中 ..."
            this.$axios.post(this.device.sources.url + "/app/install?serial=" + this.serial,
            JSON.stringify({url: url})).then(ret => {
                if(ret.data.status === 10000){
                    this.app.message = "安装成功";
                }else{
                    this.app.message = ret.data.message;
                }
            }).error(err => {
                this.app.message = err.responseJSON.message;
            }).always(() => {
                this.app.finished = true;
            });
        },
        closeWindowWhenReleased(interval) {
            setTimeout(() => {
                if (!document.hidden) { // 设备在操作 刷新超时时间
                    let url = '/autotest/device/active/' + this.serial;
                    this.$post(url, null, response =>{
                        if(response.data){  // 更新成功
                            this.closeWindowWhenReleased(interval) 
                        }else{  // 设备可能已经离线
                            this.$message.warning('设备可能被释放了');
                        }
                    });
                } else {
                    this.closeWindowWhenReleased(5000)
                }
            }, interval);
        },
        pressHome() {
            return this.$axios.post(this.path2url("/wda/homescreen")).then(() => {
                setTimeout(() => {
                    this.initSession();
                }, 500);
            });
        },
        getSessionId() {
            this.$axios.post(this.path2url("/session"),
            JSON.stringify({"capabilities": {}})).then(ret => {
                this.session.id = ret.data.sessionId;
                this.setFps();
                return ret.data.sessionId;
            });
        },
        initSession() {
            return this.$axios.get(this.path2url("/status")).then(ret => {
                if (ret.data.sessionId) {
                    this.session.id = ret.data.sessionId;
                    this.setFps();
                }else{
                    this.getSessionId();
                }
            })
        },
        setFps() {
            // 设定帧率为30
            this.$axios.post(this.path2url("/session/" + this.session.id + "/appium/settings"),
                JSON.stringify({"settings": {"mjpegServerFramerate": 30}})); 
        },
        stopUsing() {
            let url = '/autotest/device/stop/' + this.device.serial;
            this.$post(url, null, response => {
                window.close();
            });
        },
        handleTabClick(tab, event) {
            console.log(tab.name)
        },
        path2url(pathname) {
            return "http://" + this.device.sources.wdaUrl + pathname
        },
        disableTouch() {
            let element = this.canvas.bg;
            element.style.cursor = 'not-allowed'; // set el.style is not working
            element.style.pointerEvents = "none";
        },
        enableTouch() {
            let element = this.canvas.bg;
            element.style.cursor = '';
            element.style.pointerEvents = "";
        },
        mirrorDisplay() {
            let ws = new WebSocket("ws://" + this.device.sources.wdaUrl + "/screen");
            this.display.ws = ws;

            ws.onopen = () => {
                this.canvasStyle.opacity = 1;
                this.enableTouch();
            }
            ws.onmessage = (message) => {
                if (message.data instanceof Blob) {
                    this.drawBlobImageToCanvas(message.data, this.canvas.bg, this.display.width > this.display.height);
                }
            }
            ws.onclose = () => {
                if (this.display.ws === ws) {
                    this.display.ws = null;
                    this.$message.error("设备屏幕同步中断");
                    this.canvasStyle.opacity = 0.5;
                    this.disableTouch();
                }
            }
        },
        closeMirrorDisplay() {
            this.canvasStyle.opacity = 0.5;
            if (this.display.ws) {
                let ws = this.display.ws;
                this.display.ws = null;
                ws.close();
            }
        },
        syncTouchpad() {
            let bounds = {};
            let element = this.canvas.bg;

            function calculateBounds() {
                var el = element;
                bounds.w = el.offsetWidth;
                bounds.h = el.offsetHeight;
                bounds.x = 0;
                bounds.y = 0;

                while (el.offsetParent) {
                    bounds.x += el.offsetLeft;
                    bounds.y += el.offsetTop;
                    el = el.offsetParent;
                }
            }

            let coords = (e) => {
                let x = e.pageX - bounds.x;
                let y = e.pageY - bounds.y;
                x = Math.max(0, Math.min(bounds.w, x));
                y = Math.max(0, Math.min(bounds.h, y));

                return {
                    fingerX: x + element.offsetLeft,
                    fingerY: y + element.offsetTop,
                    x: Math.floor(x / bounds.w * this.display.width),
                    y: Math.floor(y / bounds.h * this.display.height)
                };
            }


            let mousePos = {
                beganAt: null,
                down: null,
                up: null,
            };

            let wdaTouch = (x, y) => {
                return this.$axios.post(this.path2url("/session/" + this.session.id + "/wda/tap/0"), 
                JSON.stringify({ x, y}));
            }

            let wdaSwipe = (fromX, fromY, toX, toY, duration) => {
            return this.$axios.post(this.path2url("/session/" + this.session.id + "/wda/touch/perform"),
                JSON.stringify({
                    actions: [
                        {action: "press", options: {x: fromX, y: fromY}},
                        {action: "wait", options: {ms: duration > 17 ? duration : 100 }},
                        {action: "moveTo", options: {x: toX, y: toY}},// 必须大于17ms，否则 wda 会没反应。日常使用一般100毫秒比较正常
                        {action:"release", options: {}}
                    ]
                }));
            }

            let mouseUpOperate = (x, y) => {
                let duration = new Date() - mousePos.beganAt; // milliseconds

                const
                    startX = mousePos.down.x,
                    startY = mousePos.down.y,
                    moveX = Math.abs(startX - x),
                    moveY = Math.abs(startY - y);

            if (moveX == 0 && moveY == 0) {
                if (duration < 200) { // click
                // click
                return wdaTouch(x, y);
                } else {
                // long click
                return wdaSwipe(startX, startY, x, y, duration);
                }
            } else {
                return wdaSwipe(startX, startY, x, y, 100);
            }
            }

            const mouseDownListener = (event) => {
                let e = event;
                if (e.originalEvent) {
                    e = e.originalEvent;
                }
                e.preventDefault();

                // Middle click equals HOME
                if (e.which === 2) {
                    this.pressHome();
                    return
                }
                
                calculateBounds()

                let { fingerX, fingerY, x, y } = coords(e)

                activeFinger(0, fingerX, fingerY);
                mousePos.beganAt = new Date();
                mousePos.down = { x, y };

                document.addEventListener('mousemove', mouseMoveListener);
                document.addEventListener('mouseup', mouseUpListener);
            }

            function mouseMoveListener(event) {
                var e = event;
                if (e.originalEvent) {
                    e = e.originalEvent;
                }
                e.preventDefault();

                let { fingerX, fingerY } = coords(e);
                var pressure = 0.5;
                activeFinger(1, fingerX, fingerY, pressure);
            }

            function mouseUpListener(event) {
                var e = event;
                if (e.originalEvent) {
                    e = e.originalEvent;
                }
                e.preventDefault();

                element.style.cursor = "not-allowed"; // not working
                element.style.pointerEvents = "none";

                let { fingerX, fingerY, x, y } = coords(e);

                activeFinger(1, fingerX, fingerY);

                stopMousing();
                element.removeEventListener("mousedown", mouseDownListener);

                mouseUpOperate(x, y).then(() => {
                    recoverFingersAndMouse()
            });
            }

            function stopMousing() {
                document.removeEventListener('mousemove', mouseMoveListener);
                document.removeEventListener('mouseup', mouseUpListener);
            }

            function activeFinger(index, x, y, pressure) {
                var scale = 0.5 + (pressure || 0.5);
                $(".finger-" + index)
                    .addClass("active")
                    .css("transform", 'translate3d(' + x + 'px,' + y + 'px,0)')
            }

            function deactiveFinger(index) {
                $(".finger-" + index).removeClass("active")
            }

            function recoverFingersAndMouse() {
                deactiveFinger(0)
                deactiveFinger(1)
                element.style.cursor = ""
                element.style.pointerEvents = ""
                element.addEventListener('mousedown', mouseDownListener);
            }

            function preventHandler(event) {
                event.preventDefault()
            }

            /* bind listeners */
            element.addEventListener('mousedown', mouseDownListener);
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
        drawBlobImageToCanvas(blob, canvas, landscape) {
            // Support jQuery Promise
            var dtd = $.Deferred();
            var ctx = canvas.getContext('2d'),
            URL = window.URL || window.webkitURL,
            BLANK_IMG ='data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',
            img = this.imagePool.next();

            img.onload = () => {
                canvas.width = img.width
                canvas.height = img.height

                ctx.drawImage(img, 0, 0, img.width, img.height);

                if (landscape) {
                    // 顺时针旋转 270°
                    canvas.width = img.height
                    canvas.height = img.width

                    ctx.save()
                    ctx.translate(canvas.width / 2, canvas.height / 2);
                    ctx.rotate(1.5 * Math.PI);
                    ctx.drawImage(img,
                    -img.width / 2,
                    -img.height / 2);
                    ctx.restore();
                }
                this.fitCanvas(canvas);

                // Try to forcefully clean everything to get rid of memory
                // leaks. Note self despite this effort, Chrome will still
                // leak huge amounts of memory when the developer tools are
                // open, probably to save the resources for inspection. When
                // the developer tools are closed no memory is leaked.
                img.onload = img.onerror = null;
                img.src = BLANK_IMG;
                img = null;
                blob = null;

                URL.revokeObjectURL(url);
                url = null;
                dtd.resolve();
            }

            img.onerror = function () {
                // Happily ignore. I suppose this shouldn't happen, but
                // sometimes it does, presumably when we're loading images
                // too quickly.

                // Do the same cleanup here as in onload.
                img.onload = img.onerror = null;
                img.src = BLANK_IMG;
                img = null;
                blob = null;

                URL.revokeObjectURL(url);
                url = null;
                dtd.reject();
            }

            var url = URL.createObjectURL(blob);
            img.src = url;
            return dtd;
        }
    }
}
</script>
<style scoped>
.screen-header{
    height: 36px;
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
    height: 32px;
    position: relative;
    border-radius: 0px 0px 12px 12px;
    border-bottom:2px solid rgb(219, 219, 219);
    border-left: 2px solid rgb(219, 219, 219);
    border-right: 2px solid rgb(219, 219, 219);
    background-color: rgb(44, 44, 44);
}


.long-text {
    width: 50px;
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

.canvas-bg {
    z-index: 20;
    max-width: 100%;
    height: auto;
}

.finger {
    position: absolute;
    border-style: solid;
    border-radius: 50%;
    border-color: white;
    border-width: 1mm;
    width: 6mm;
    height: 6mm;
    top: -3mm;
    left: -3mm;
    opacity: 0.7;
    pointer-events: none;
    background: red;
    z-index: 200;
    display: none;
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
