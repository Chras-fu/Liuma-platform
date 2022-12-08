/**
* 苹果远程控制
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="6" min-width="400px">
                <div class="screen-header">
                    <span>
                        {{device.serial}}
                    </span>
                    <div style="float:right">
                        <el-button type="danger" size="small" @click="stopUsing" >停用</el-button>
                    </div>
                </div>
                <div class="screen-body">
                    <canvas ref="bgCanvas" class="canvas-bg" v-bind:style="canvasStyle"></canvas>
                    <span class="finger finger-0" style="transform: translate3d(200px, 100px, 0px)"></span>
                    <span class="finger finger-1" style="transform: translate3d(200px, 100px, 0px)"></span>
                </div>
                <div class="screen-footer">
                    <el-button size="mini" type="text" style="width:100%" @click="pressHome" icon="el-icon-s-home"></el-button>
                </div>
            </el-col>
            <el-col :span="18">
                <el-tabs v-model="activeName" @tab-click="handleTabClick">
                    <el-tab-pane label="常用功能" name="common">
                        <div class="card-columns" ref="commonContainer">
                            <div class="card">
                                <div class="card-header">
                                    常用操作
                                    <span style="font-size: 0.7em; padding-top: 5px; color: gray">注: iOS的弹窗不能通过屏幕点击来选择</span>
                                </div>
                                <div class="card-body">
                                    <el-button size="small" @click="hotfix">修复旋转</el-button>
                                    <el-button size="small" :loading="alert.loading" @click="chooseAlertButtons">选择弹框按钮</el-button>
                                    <el-dialog title="弹窗选择" :visible.sync="alert.visible">
                                        <span style="padding: 5px" v-for="(v, index) in alert.buttons" :key="index">
                                            <el-button round size="small" @click="alertAccept(v); alert.visible=false">{{!v}}</el-button>
                                        </span>
                                    </el-dialog>
                                </div>
                            </div>
                
                            <div class="card">
                                <div class="card-header">常用信息</div>
                                <div class="card-body">
                                    <dl>
                                        <dt>Display</dt>
                                        <dd><code>{{!display.width}}x{{!display.height}}</code></dd>
                                        <dt>SessionID</dt>
                                        <dd><code v-text="session.id"></code></dd>
                                        <dt>FPS/Frames</dt>
                                        <dd>
                                        <code v-text="display.fps"></code>
                                        /
                                        <small><code v-text="session.frameCount"></code></small>
                                        </dd>
                                        <dt>WDA URL</dt>
                                        <dd>
                                        <code v-text="sources.wdaUrl"></code>
                                        <i :data-clipboard-text="sources.wdaUrl" class="far fa-copy clipboard-copy cursor-pointer"></i>
                                        </dd>
                                    </dl>
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

                            <div class="card">
                                <div class="card-header">截图下载</div>
                                <div class="card-body">
                                    <a @click="screenshot">下载截图</a>
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
export default {
    name: 'AppleRemote',
    props:{
        device: Object,
    },
    data() {
        return{
            activeName: 'common',
            canvas: {
                bg: null,
                fg: null,
            },
            canvasStyle: {
                opacity: 1,
                width: '400px',
                height: 'unset',
                maxHeight: "unset",
            },
            rotation: 0,
            session: {
                id: '',
                frameCount: 0,
            },
            display: {
                width: 0,
                height: 0,
                fps: 0,
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
            pageHidden: false,
            imagePool: new ImagePool(100),
        }
    },
    mounted: function () {
        this.canvas.bg = this.$refs.bgCanvas;

        // save the bandwidth
        document.addEventListener("visibilitychange", () => {
            this.pageHidden = document.hidden;
            if (this.pageHidden) {
            this.closeMirrorDisplay()
            } else {
            this.mirrorDisplay()
            }
        })

        this.mirrorDisplay()
        this.syncTouchpad()
        this.hotfix()

        // show fps
        this.showFPS()

        // 防止设备被自动释放
        this.closeWindowWhenReleased(10000)

        // 更新使用时间
        setInterval(() => {
            let duration = moment.now() - moment(this.usingBeganAt)
            this.$refs.usingTime.innerHTML = moment.utc(duration).format("HH:mm:ss")
        }, 1000)

        // 初始化剪贴板
        this.initClipboardJS()
    },
    methods: {
        screenshot() {
            $.ajax({
            url: this.path2url("/screenshot"),
            dataType: "json",
            }).then(ret => {
            if (window.navigator.msSaveOrOpenBlob) {
                alert("IE is not supported !!!")
                return
            }
            var a = document.createElement("a");
            a.href = "data:image/jpeg;base64," + ret.value;
            a.download = "screen-" + new Date().getTime() + ".jpg";
            a.click();
            setTimeout(function () {
                document.body.removeChild(a);
            }, 0);
            })
        },
        appOpen(bundleId) {
            return $.ajax({
            method: "post",
            url: this.path2url("/session"),
            data: JSON.stringify({
                capabilities: {
                alwaysMatch: {
                    bundleId: bundleId,
                    shouldWaitForQuiescence: true,
                }
                }
            })
            }).then(ret => {
            this.session.id = ret.sessionId
            })
        },
        chooseAlertButtons() {
            this.alert.buttons = []
            this.alert.loading = true
            return $.ajax({
            method: "get",
            url: this.path2url("/session/" + this.session.id + "/wda/alert/buttons")
            }).then(ret => {
            this.alert.buttons = ret.value
            this.$nextTick(() => {
                this.alert.visible = true
            })
            // this.$alert("未检测到系统弹窗")
            // this.$alert("未知异常，打开开发者选项查看问题
            }).always(() => {
            this.alert.loading = false
            })
        },
        alertAccept(name) {
            let data = null;
            if (typeof name === 'string' || name instanceof String) {
            data = JSON.stringify({ name: name })
            }
            return $.ajax({
            method: "post",
            url: this.path2url("/session/" + this.session.id + "/alert/accept"),
            data: data,
            })
        },
        initClipboardJS() {
            var clipboard = new ClipboardJS(".clipboard-copy")

            clipboard.on('success', function (e) {
            e.clearSelection()
            showTooltip(e.trigger, "Copied!")
            })

            document.querySelectorAll(".clipboard-copy").forEach(e => {
            e.addEventListener('mouseleave', clearTooltip);
            e.addEventListener('blur', clearTooltip);
            })

            function clearTooltip(e) {
            const target = e.currentTarget;
            setTimeout(() => {
                target.innerHTML = ""
            }, 200)
            }

            function showTooltip(elem, msg) {
            elem.innerHTML = "&nbsp;<small>" + msg + "</small>"
            }
        },
        onUploadSelect(file) {
            this.app.url = file.response.data.url;
        },
        onUpload(resp, file, files) {
            if (!resp.success) {
            this.$message({
                message: resp.description,
                type: "error",
            })
            return
            }
            this.app.url = resp.data.url;
            return this.appInstall()
        },
        appInstall() {
            const url = this.app.url

            this.app.finished = false
            this.app.message = "安装中 ..."
            $.ajax({
            method: "post",
            url: this.source.url + "/app/install?udid=" + this.udid,
            data: {
                url: url,
            }
            }).done(ret => {
            if(ret.code === 10000){
                this.app.message = "安装成功";
            }else{
                this.app.message = ret.message;
            }
            }).fail(err => {
            this.app.message = err.responseJSON.message;
            }).always(() => {
            this.app.finished = true
            })
        },
        showFPS() {
            let frame = this.session.frameCount;
            setInterval(() => {
            this.display.fps = this.session.frameCount - frame
            frame = this.session.frameCount;
            }, 1000);
        },
        closeWindowWhenReleased(interval) {
            setTimeout(() => {
            if (document.hidden) {
                $.getJSON(urlPrefix + "/api/v1/user/devices/" + udid)
                .done(ret => {
                    this.closeWindowWhenReleased(5000)
                })
                .fail(ret => {
                    console.log(ret)
                    if (ret.status === 403) { // forbidden
                    window.close()
                    return
                    }
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
        pressHome() {
            return $.ajax({
            url: this.path2url("/wda/homescreen"),
            method: "POST",
            }).then(() => {
            setTimeout(() => {
                this.hotfix()
            }, 500)
            })
        },
        getSessionId() {
            return $.ajax({
            method: "post",
            url: this.path2url("/session"),
            data: '{"capabilities": {}}',
            }).then(ret => {
            return ret.sessionId;
            })
        },
        hotfix() {
            return $.ajax({
            url: this.path2url("/status")
            }).then(ret => {
            if (ret.sessionId) {
                return ret.sessionId
            }
            return this.getSessionId()
            }).then((sessionId) => {
            this.session.id = sessionId;
            return $.ajax({
                url: this.path2url("/session/" + this.session.id + "/window/size")
            })
            }).then(ret => {
            if (ret.value.width && ret.value.height) { // width and height might be 0
                this.display.width = ret.value.width;
                this.display.height = ret.value.height;
            }

            // 设定帧率为30
            $.ajax({
                method: "post",
                url: this.path2url("/session/" + this.session.id + "/appium/settings"),
                data: '{"settings": {"mjpegServerFramerate": 30}}',
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
        handleTabClick(tab, event) {
            console.log(tab.name)
        },
        path2url(pathname) {
            return this.source.wdaUrl + pathname
        },
        disableTouch() {
            let element = this.canvas.bg;
            element.style.cursor = 'not-allowed' // set el.style is not working
            element.style.pointerEvents = "none"
        },
        enableTouch() {
            let element = this.canvas.bg;
            element.style.cursor = ''
            element.style.pointerEvents = ""
        },
        mirrorDisplay() {
            let ws = new WebSocket(this.displayWSUrl)
            this.display.ws = ws;

            ws.onopen = () => {
            console.log("screen connected")
            this.canvasStyle.opacity = 1
            this.enableTouch()
            }
            ws.onmessage = (message) => {
            if (message.data instanceof Blob) {
                this.session.frameCount += 1
                this.drawBlobImageToCanvas(message.data, this.canvas.bg, this.landscape)
            }
            }
            ws.onclose = () => {
            if (this.display.ws === ws) {
                this.display.ws = null;
                this.$message({
                showClose: true,
                message: "设备屏幕同步中断",
                type: "error",
                })
                this.canvasStyle.opacity = 0.5
                this.disableTouch()
            }
            }
        },
        closeMirrorDisplay() {
            this.canvasStyle.opacity = 0.5
            if (this.display.ws) {
            let ws = this.display.ws;
            this.display.ws = null;
            ws.close()
            }
        },
        syncTouchpad() {
            let bounds = {}
            let element = this.canvas.bg;

            function calculateBounds() {
            var el = element;
            bounds.w = el.offsetWidth
            bounds.h = el.offsetHeight
            bounds.x = 0
            bounds.y = 0

            while (el.offsetParent) {
                bounds.x += el.offsetLeft
                bounds.y += el.offsetTop
                el = el.offsetParent
            }
            }

            let coords = (e) => {
            let x = e.pageX - bounds.x
            let y = e.pageY - bounds.y
            x = Math.max(0, Math.min(bounds.w, x))
            y = Math.max(0, Math.min(bounds.h, y))

            return {
                fingerX: x + element.offsetLeft,
                fingerY: y + element.offsetTop,
                x: Math.floor(x / bounds.w * this.display.width),
                y: Math.floor(y / bounds.h * this.display.height)
            }
            }


            let mousePos = {
            beganAt: null,
            down: null,
            up: null,
            }

            let wdaTouch = (x, y) => {
            return $.ajax({
                method: "POST",
                url: this.path2url("/session/" + this.session.id + "/wda/tap/0"),
                data: JSON.stringify({ x, y })
            })
            }

            let wdaSwipe = (fromX, fromY, toX, toY, duration) => {
            return $.ajax({ // 经过测试，相同移动轨迹，wda/touch/perform 比 wda/dragfromtoforduration 响应速度快不少，整体操作iPhoneX上从1.3s+缩短到0.5s内
                url: this.path2url("/session/" + this.session.id + "/wda/touch/perform"),
                method: "POST",
                data: JSON.stringify({
                actions: [
                    {action: "press", options: {x: fromX, y: fromY}},
                    {action: "wait", options: {ms: duration > 17 ? duration : 100 }},// 必须大于17ms，否则 wda 会没反应。日常使用一般100毫秒比较正常
                    {action: "moveTo", options: {x: toX, y: toY}},
                    {action:"release", options: {}}
                ]
                })
            })
            }

            let mouseUpOperate = (x, y) => {
            let duration = new Date() - mousePos.beganAt; // milliseconds
            console.log("hold duration", duration);

            const
                startX = mousePos.down.x,
                startY = mousePos.down.y,
                moveX = Math.abs(startX - x),
                moveY = Math.abs(startY - y);

            if (moveX == 0 && moveY == 0) {
                if (duration < 200) { // click
                // click
                console.log("touch", x, y)
                return wdaTouch(x, y)
                } else {
                // long click
                console.log("touchHold", x, y)
                return wdaSwipe(startX, startY, x, y, duration)
                }
            } else {
                console.log("swipe:", mousePos.down, "to", { x, y }, duration)
                return wdaSwipe(startX, startY, x, y, 100)
            }
            }

            const mouseDownListener = (event) => {
            let e = event;
            if (e.originalEvent) {
                e = e.originalEvent
            }
            e.preventDefault()

            // Middle click equals HOME
            if (e.which === 2) {
                this.pressHome()
                return
            }

            fakePinch = e.altKey
            calculateBounds()

            let { fingerX, fingerY, x, y } = coords(e)

            activeFinger(0, fingerX, fingerY);
            mousePos.beganAt = new Date()
            mousePos.down = { x, y }

            document.addEventListener('mousemove', mouseMoveListener);
            document.addEventListener('mouseup', mouseUpListener);
            }

            function mouseMoveListener(event) {
            var e = event
            if (e.originalEvent) {
                e = e.originalEvent
            }
            e.preventDefault()

            let { fingerX, fingerY } = coords(e)
            var pressure = 0.5
            activeFinger(1, fingerX, fingerY, pressure);
            }

            function mouseUpListener(event) {
            var e = event
            if (e.originalEvent) {
                e = e.originalEvent
            }
            e.preventDefault()

            element.style.cursor = "not-allowed" // not working
            element.style.pointerEvents = "none"

            let { fingerX, fingerY, x, y } = coords(e)

            activeFinger(1, fingerX, fingerY);

            stopMousing()
            element.removeEventListener("mousedown", mouseDownListener)

            mouseUpOperate(x, y).then(() => {
                recoverFingersAndMouse()
            })
            }

            function stopMousing() {
            document.removeEventListener('mousemove', mouseMoveListener);
            document.removeEventListener('mouseup', mouseUpListener);
            }

            function activeFinger(index, x, y, pressure) {
            var scale = 0.5 + (pressure || 0.5)
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
            this.canvasStyle.height = "auto"
            this.canvasStyle.width = canvas.parentElement.clientHeight + "px"
            } else {
            this.canvasStyle.maxHeight = "unset"
            this.canvasStyle.height = canvas.parentElement.clientHeight + "px"
            this.canvasStyle.width = "auto"
            }
        },
        drawBlobImageToCanvas(blob, canvas, landscape) {
            // Support jQuery Promise
            var dtd = $.Deferred();
            var ctx = canvas.getContext('2d'),
            URL = window.URL || window.webkitURL,
            BLANK_IMG =
                'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',
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
            this.fitCanvas(canvas)

            // Try to forcefully clean everything to get rid of memory
            // leaks. Note self despite this effort, Chrome will still
            // leak huge amounts of memory when the developer tools are
            // open, probably to save the resources for inspection. When
            // the developer tools are closed no memory is leaked.
            img.onload = img.onerror = null
            img.src = BLANK_IMG
            img = null
            blob = null

            URL.revokeObjectURL(url)
            url = null
            dtd.resolve();
            }

            img.onerror = function () {
            // Happily ignore. I suppose this shouldn't happen, but
            // sometimes it does, presumably when we're loading images
            // too quickly.

            // Do the same cleanup here as in onload.
            img.onload = img.onerror = null
            img.src = BLANK_IMG
            img = null
            blob = null

            URL.revokeObjectURL(url)
            url = null
            dtd.reject();
            }

            var url = URL.createObjectURL(blob)
            img.src = url;
            return dtd;
        },
    },
    computed: {
        landscape() {
            return this.display.width > this.display.height;
        },
        displayLinked() {
            return this.display.ws !== null
        },
        displayWSUrl() {
            let url = this.source.wdaUrl.replace(/^https?:\/\//, "ws://") + "/screen"
            return url
        }
    },

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
