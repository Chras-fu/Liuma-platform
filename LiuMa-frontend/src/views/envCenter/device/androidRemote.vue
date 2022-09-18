/**
* 响应断言
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="6" min-width="400px">
                <div class="screen-header">
                    <span>
                        {{!device.serial}}
                    </span>
                    <span>
                        <i v-if="displayLinked" @click="closeDisplayTouchpad" class="fas fa-link" style="color: green"></i>
                        <i v-else @click="openDisplayTouchpad" class="fas fa-unlink" style="color: red"></i>
                    </span>
                    <span>
                        <i v-if="websockets.touchpad != null" @click="closeSyncTouchpad" class="fas fa-mouse-pointer"
                        style="color: green"></i>
                        <i v-else @click="syncTouchpad" class="fas fa-mouse-pointer" style="color: red"></i>
                    </span>
                    <span>
                        <i v-if="!websockets.winput" class="fas fa-keyboard" style="color: red"></i>
                        <i v-else-if="!whatsinput.disabled" class="fas fa-keyboard" style="color: green"></i>
                        <i v-else class="fas fa-keyboard" style="color:gray"></i>
                    </span>
                    <span @click="hotfix" class="cursor-pointer">
                        <i title="hotfix" class="fas fa-hammer"></i>
                    </span>
                </div>
                <div class="screen-body">
                    <canvas ref="bgCanvas" class="canvas-bg" v-bind:style="canvasStyle"/>
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
                            <el-card style="margin-bottom: 10px">
                                <div class="card-header">
                                <i class="fas fa-keyboard"></i> 输入框
                                <span class="float-right">
                                    <el-tooltip effect="dark" content="修复输入法" placement="top-start">
                                    <i class="fas fa-tools float-right cursor-pointer" @click="fixInputMethod"> 修复输入法</i>
                                    </el-tooltip>
                                </span>
                                </div>
                                <div class="card-body">
                                <textarea ref="whatsinput" :disabled="whatsinput.disabled"
                                    :placeholder="whatsinput.disabled ? 'Input disabled' : 'Input something ...'" class="form-control"
                                    v-model="whatsinput.text" @input="sendInputText" @keydown.tab.exact.prevent="sendInputKey('tab')"
                                    @keydown.enter.exact.prevent="sendInputKey('enter')"></textarea>
                                <span style="text-align: right; font-size: 0.74em; color: gray; margin-bottom: 0px">
                                    <code>Shift+Enter</code> to start a new line, <code>Enter</code> to
                                    send</span>
                                </div>
                            </el-card>
                
                            <el-card style="margin-bottom: 10px">
                                <div class="card-header">
                                <i class="far fa-address-card"></i> 常用地址
                                </div>
                                <div class="card-body">
                                <dl>
                                    <dt>ATX-AGENT地址</dt>
                                    <dd><code v-text="deviceUrl"></code>
                                    <i :data-clipboard-text="deviceUrl" class="far fa-copy clipboard-copy cursor-pointer"></i>
                                    </dd>
                                    <dt>ADB远程连接</dt>
                                    <dd><code v-text="remoteConnectAddr"></code>
                                    <i :data-clipboard-text="remoteConnectAddr" class="far fa-copy clipboard-copy cursor-pointer"></i>
                                    </dd>
                                </dl>
                                </div>
                            </el-card>
                            
                            <el-card style="margin-bottom: 10px">
                                <div class="card-header">
                                <i class="fa fa-globe"></i>
                                浏览器打开URL
                                </div>
                                <div class="card-body">
                                <textarea class="form-control" placeholder="在这里输入URL" rows=2 v-model="browserUrl"
                                    @keyup.enter="openBrowser(browserUrl)"></textarea>
                                <div style="padding-top: 5px">
                                    <button type="button" class="btn btn-primary btn-sm float-right"
                                    @click='openBrowser(browserUrl)'>打开</button>
                                </div>
                                </div>
                            </el-card>
                
                            <el-card style="margin-bottom: 10px">
                                <div class="card-header">
                                <i class="fas fa-blind"></i>
                                快捷操作
                                </div>
                                <div class="card-body">
                                <button class="btn btn-sm btn-sm btn-outline-primary"
                                    @click="runShell('am start -a android.settings.SETTINGS')">
                                    <i class="fas fa-cog"></i>
                                    Settings
                                </button>
                                <button class="btn btn-sm btn-outline-primary"
                                    @click="runShell('am start -a com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS')">
                                    <i class="fas fa-wrench"></i>
                                    开发者
                                </button>
                                <button class="btn btn-sm btn-outline-primary"
                                    @click="runShell('am start -a android.settings.WIRELESS_SETTINGS')">
                                    <i class="fas fa-wifi"></i>
                                    WIFI
                                </button>
                                </div>
                            </el-card>
                
                            <el-card style="margin-bottom: 10px">
                                <div class="card-header">
                                <i class="fab fa-app-store-ios"></i>
                                当前应用
                                </div>
                                <div class="card-body">
                                <div class="form-group">
                                    <input v-model="topApp.packageName" class="form-control form-control-sm" placeholder="PackageName">
                                </div>
                                <div class="form-group">
                                    <input v-model="topApp.activity" class="form-control form-control-sm" placeholder="Activity">
                                </div>
                                <div class="btn-group">
                                    <button type="button" class="btn btn-sm btn-outline-primary" @click="refreshTopApp"><i
                                        class="fas fa-sync"></i>
                                    刷新</button>
                                    <button type="button" :disabled="!topApp.packageName" class="btn btn-sm btn-outline-primary"
                                    @click='runShell("am force-stop "+topApp.packageName)'><i class="fas fa-stop-circle"></i>
                                    Kill</button>
                                    <button type="button" :disabled="!topApp.packageName" class="btn btn-sm btn-outline-primary"
                                    @click="addTopAppToShortcut"><i class="fas fa-blind"></i> 添加到快捷入口</button>
                                </div>
                                </div>
                            </el-card>

                            <el-card style="margin-bottom: 10px">
                                <el-upload ref="upload" class="upload-demo" drag action :http-request="uploadApk" multiple>
                                <i class="el-icon-upload"></i>
                                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                                <div class="el-upload__tip" slot="tip">暂时只支持apk的上传</div>
                                </el-upload>
                                <div class="form-group">
                                <label>URL</label>
                                <input type="text" class="form-control form-control-sm" placeholder="http://..." v-model="app.installUrl">
                                </div>
                                <div>
                                <el-checkbox v-model="app.launch">安装完成后启动应用</el-checkbox>
                                </div>
                                <button class="btn btn-outline-primary btn-sm" @click="appInstall"
                                :disabled="!app.finished || !app.installUrl">安裝</button>
                                <p>
                                <pre v-show="!!app.message" v-text="app.message"></pre>
                                <small><code v-text="app.packageName"></code></small>
                                </p>
                            </el-card>

                            <el-card style="margin-bottom: 10px">
                                <a :href="screenshotUrl+'?download=screenshot.jpg'" download>下载截图</a>
                            </el-card>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="Terminal" name="terminal">
                        <p><code>Ctrl+Ins</code>复制，<code>Shift+Ins</code>粘贴</p>
                        <div ref="xterm" class="xterm-wrapper"></div>
                        <div>
                        <strong>常用命令</strong>
                        <ul>
                            <li>查看IP地址
                            <term-snippet :term="term" command="ifconfig | grep Mask" />
                            </li>
                            <li>查看前台应用
                            <term-snippet :term="term" command="dumpsys activity activities | grep mFocusedActivity" />
                            </li>
                            <li>列出第三方应用
                            <term-snippet :term="term" command="pm list packages -3" />
                            </li>
                            <li>屏幕分辨率
                            <term-snippet :term="term" command="wm size" />
                            </li>
                            <li>点亮屏幕
                            <term-snippet :term="term" command="input keyevent 224"></term-snippet>
                            熄灭屏幕
                            <term-snippet :term="term" command="input keyevent 223"></term-snippet>
                            </li>
                            <li>
                            minicap
                            <term-snippet :term="term" command="LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -i">
                            </term-snippet>
                            </li>
                            <li>更多参考 <a href="https://github.com/mzlogin/awesome-adb" target="_blank">awesome-adb</a>
                            </li>
                        </ul>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="文件管理" name="filemanager">
                        <div class="filemanagerDiv" :style="canvasStyle" style="overflow-y:auto">
                        <el-button size="mini" type="primary" @click="getPhoneFile('/sdcard')">返回 sdcard</el-button>
                        <el-button size="mini" type="primary" @click="getPhoneFile('..')">返回上层</el-button>
                        <el-input size="mini" v-model="phoneDir" placeholder="目标目录" :disabled="true">
                            <template slot="prepend">当前目录：</template>
                        </el-input>
                        <el-table size="mini" :data="phoneFiles">
                            <el-table-column label="文件名（点击选择文件夹）">
                            <template slot-scope="scope">
                                <div width="100%" @dblclick="getPhoneFile(`${scope.row}`)">
                                <span v-text="scope.row"></span>
                                </div>
                            </template>
                            </el-table-column>
                        </el-table>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="应用管理" name="packagemanager">
                        <el-button icon="el-icon-refresh" size="small" @click="loadPackages">刷新</el-button>
                        <el-table :data="packages" :default-sort="{prop: 'name'}">
                        <el-table-column sortable prop="label" label="应用">
                            <template slot-scope="scope">
                            <img :src="`${deviceUrl}/packages/${scope.row.name}/icon`" onerror="this.onerror='';this.src='/static/images/transparent.png'"
                                style="position: absolute; top: 50%; margin: -20px; height: 40px; left: 20px;" alt="icon" />
                            <span v-text=" scope.row.label" style="padding-left: 45px"></span>
                            </template>
                        </el-table-column>
                        <el-table-column sortable prop="name" label="包名"></el-table-column>
                        <el-table-column sortable label="版本号">
                            <template slot-scope="scope" v-if="scope.row.versionName">
                            <span v-text="scope.row.versionName"></span>(<span v-text="scope.row.versionCode"></span>)
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                            <a class="color-red" href="javascript:void(0)" @click="removePackage(scope.row.name)">卸载</a>
                            <a href="javascript:void(0)" @click="appLaunch(scope.row.name)">启动</a>
                            </template>
                        </el-table-column>
                        </el-table>
                    </el-tab-pane>
                </el-tabs>
            </el-col>
        </el-row>
    </div>
</template>
<script>
import {ImagePool} from '@/utils/imagepool.js'
export default {
    name: 'AndroidRemote',
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
              height: '200px',
              maxHeight: "unset",
            },
            rotation: 0,
            term: null, // Terminal object
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
            // imagePool: new ImagePool(100),
            browserUrl: "",
            app: {
              installUrl: "",
              finished: true,
              packageName: "",
              message: "",
              launch: true,
            },
            packages: [],
            userSettings: Object.assign({
              inputName: '',
              inputCommand: '',
              visible: false,
              shortcuts: [{
                command: "input keyevent POWER",
                name: '删除',
              }]
            }, {}),
            phoneFiles: [],
            phoneDir: "",
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
        appLaunch(packageName) {
            $.ajax({
            url: `${this.deviceUrl}/session/${packageName}`,
            method: "post"
            }).then(ret => {
            console.log(ret)
            })
        },
        addTopAppToShortcut() {
            this.$prompt('给 <code>' + this.topApp.packageName + '</code> 起个名字', '快捷方式添加', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            dangerouslyUseHTMLString: true,
            }).then(({ value }) => {
            let command = ["monkey", "-p", this.topApp.packageName, "-c", "android.intent.category.LAUNCHER", "1"].join(" ")
            this.addShortcut(value, command)
            }).catch(() => {
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
        loadUserSettings() {
            return $.getJSON(urlPrefix + "/api/v1/user/settings?email=" + userEmail).then(ret => {
            this.userSettings.shortcuts = ret.shortcuts || []
            })
        },
        updateUserSettings() {
            return $.ajax({
            method: "put",
            url: urlPrefix + "/api/v1/user/settings",
            dataType: "json",
            data: JSON.stringify({
                "shortcuts": this.userSettings.shortcuts,
                "email": userEmail
            })
            }).then(ret => {
            console.log("设置已更新")
            })
        },
        triggerShortcut(v) {
            return this.runShell(v.command).done(ret => {
            this.$notify({
                dangerouslyUseHTMLString: true,
                message: "CMD: <code>" + v.command + "</code><br>Output: <code>" + ret.output + "</code>",
            })
            console.log("Shell", v.command, "output", ret)
            })
        },
        addShortcut(name, command) {
            if (!name || !command) {
            return
            }
            let s = this.userSettings;
            s.shortcuts.push({ name, command })
            s.inputName = ""
            s.inputCommand = ""
            s.visible = false;
            return this.updateUserSettings()
        },
        removeShortcut(c) {
            this.userSettings.shortcuts = this.userSettings.shortcuts.filter(v => {
            return v.name != c.name
            })
            return this.updateUserSettings()
        },
        hotfix() {
            // 修复屏幕旋转问题
            // this.$notify({
            //   message: "Hotfixing",
            //   position: 'top-left',
            //   duration: 1000,
            // })
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
        openBrowser(url) {
            if (!/^https?:\/\//.test(url)) {
            url = "http://" + url;
            }
            this.browserUrl = ""
            return this.runShell("am start -a android.intent.action.VIEW -d " + url);
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
            console.log(tab.name)
            if (tab.name == "terminal") {
            if (!this.term) {
                this.loadTerminal()
            }
            }
            if (tab.name == "filemanager") {
            this.getPhoneFile();
            }
            if (tab.name == "packagemanager") {
            if (!this.packages.length) {
                this.loadPackages()
            }
            }
        },
        loadTerminal() {
            let term;
            let ws = new WebSocket(this.deviceUrl.replace(/^https?/, this.scheme) + "/term");
            ws.binaryType = "arraybuffer"

            function ab2str(buf) {
            return String.fromCharCode.apply(null, new Uint8Array(buf));
            }

            ws.onopen = (evt) => {
            term = new Terminal({
                screenKeys: true,
                useStyle: true,
                cursorBlink: true
            })
            term.on('data', data => {
                ws.send(new TextEncoder().encode("\x00" + data))
            })

            term.on("resize", evt => {
                ws.send(new TextEncoder().encode("\x01" + JSON.stringify({
                cols: evt.cols,
                rows: evt.rows,
                })))
            })

            term.on("title", title => {
                console.log("title", title)
            })

            term.open(this.$refs.xterm, { focus: true });
            term.fit()
            this.term = term;

            new ResizeSensor(this.$refs.xterm, function (e) {
                console.log("Resize", e)
                term.resize()
                term.fit()
            })
            }

            ws.onmessage = (evt) => {
            if (evt.data instanceof ArrayBuffer) {
                term.write(ab2str(evt.data))
            } else {
                alert(evt.data)
            }
            }

            ws.onclose = (evt) => {
            term.write("Session terminated");
            term.destroy()
            }

            ws.onerror = (evt) => {
            console.log(evt)
            }
        },
        getPhoneFile(dir) {
            if (!dir || dir == "/sdcard") {
            this.phoneDir = "/sdcard";
            } else if (dir == "..") {
            if (this.phoneDir.indexOf("/") == this.phoneDir.lastIndexOf("/")){
                this.phoneDir = "/sdcard";
            } else {
                this.phoneDir = this.phoneDir.substr(0, this.phoneDir.lastIndexOf("/"));
            }
            } else if (dir == this.phoneDir) {
            this.phoneDir = dir;
            } else {
            this.phoneDir = this.phoneDir + "/" + dir;
            }
            this.runShell('ls \"' + this.phoneDir + '/\"')
            .done(ret => {
            this.phoneFiles = ret.output.split("\n");
            this.phoneFiles.pop();
            if (this.phoneFiles[0].search("^ls.*Not a directory$") == 0) {
                this.$message({
                showClose: true,
                message: "不是文件夹",
                type: "error"
                });
                this.getPhoneFile('..');
            }
            });
        },
        loadPackages() {
            return this.runShell("pm list packages -3").then(ret => {
            let packages = []
            ret.output.split(/\r?\n/).forEach(v => {
                if (v.startsWith("package:")) {
                const packageName = v.substr("package:".length)
                let item = {
                    key: packageName,
                    name: packageName,
                    label: "",
                    versionName: "",
                    versionCode: "",
                }
                $.getJSON(`${this.deviceUrl}/packages/${packageName}/info`).then(ret => {
                    if (ret.success && ret.data) {
                    item.label = ret.data.label
                    item.versionName = ret.data.versionName
                    item.versionCode = ret.data.versionCode
                    console.log(packageName, ret.data)
                    }
                })
                packages.push(item)
                }
            })
            this.packages = packages;
            })
        },
        removePackage(packageName) {
            if (!confirm(`确认卸载 ${packageName} ?`)) {
            return
            }
            return this.runShell(`pm uninstall ${packageName}`).then(() => {
            const index = this.packages.findIndex((v) => {
                return v.name == packageName
            })
            this.packages.splice(index, 1)
            })
        },
        openDisplayTouchpad() {
            this.mirrorDisplay()
            this.syncTouchpad()
        },
        closeDisplayTouchpad() {
            this.closeMirrorDisplay()
            this.closeSyncTouchpad()
        },
        mirrorDisplay() {
            // 安卓10以下使用minicap投屏
            let ws = new WebSocket(this.deviceUrl.replace(/^https?/, this.scheme)+ '/minicap');
            this.websockets.screen = ws;

            ws.onopen = (ev) => {
                console.log('minicap connected')
                this.canvasStyle.opacity = 1;
            };
            ws.onmessage = (message) => {
                if (message.data instanceof Blob) {
                this.drawBlobImageToCanvas(message.data, this.canvas.bg)
                } else if (/data size: /.test(message.data)) {
                // console.log("receive message:", message.data)
                } else if (/^rotation/.test(message.data)) {
                this.rotation = parseInt(message.data.substr('rotation '.length), 10);
                console.log("rotation:", this.rotation)
                } else {
                console.log("receive message:", message.data)
                }
            }
            ws.onclose = (ev) => {
                if (this.websockets.screen === ws) {
                console.log("minicap closed")
                this.websockets.screen = null;
                this.$message({
                    showClose: true,
                    message: '设备屏幕同步中断',
                    type: 'error',
                });
                this.canvasStyle.opacity = 0.5;
                }
            }
            ws.onerror = function (ev) {
                console.log("screen websocket error")
            }
        },
        closeMirrorDisplay() {
            this.canvasStyle.opacity = 0.5;
            let ws = this.websockets.screen;
            this.websockets.screen = null;
            ws.close()
        },
        syncTouchpad() {
            let element = this.canvas.bg; // maybe fg is better
            let ws = new WebSocket(this.deviceUrl.replace(/^https?/, this.scheme) + "/minitouch")

            let screen = {
            bounds: {}
            }

            this.websockets.touchpad = ws

            ws.onopen = (ret) => {
            console.log("touch connected")
            touchReset() // fix when device is out of control
            
            }
            ws.onmessage = (message) => {
            // console.log("minitouch recv", message)
            }
            ws.onclose = () => {
            if (this.websockets.touchpad === ws) {
                console.log("touch closed")
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
                ws.send(JSON.stringify({
                operation: 'u',
                index: index,
                }));
            }

            function touchWait(millseconds) {
            ws.send(JSON.stringify({
                operation: 'w',
                milliseconds: millseconds,
            }))
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
                touchUp(0);
                touchCommit()
            stopMousing()
            }

            function stopMousing() {
            element.removeEventListener('mousemove', mouseMoveListener);
            // element.addEventListener('mousemove', mouseHoverListener);
            document.removeEventListener('mouseup', mouseUpListener);
            deactiveFinger(0);
            }

            function activeFinger(index, x, y, pressure) {
                var scale = 0.5 + pressure
                $(".finger-" + index)
                .addClass("active")
                .css("transform", 'translate3d(' + x + 'px,' + y + 'px,0)')
            
            }

            function deactiveFinger(index) {
                $(".finger-" + index).removeClass("active")
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
            let e = event
            if (e.originalEvent) {
                e = e.originalEvent
            }

            calculateBounds()
            let x = e.pageX - screen.bounds.x
            let y = e.pageY - screen.bounds.y
            let pressure = 0.5

            if (wheel.key === null) { // mouse down
                wheel.mouseY = y;
                touchDown(1, x, y, pressure)
                touchCommit()
            } else {
                clearTimeout(wheel.key)
            }

            // 从 wheel.mouseY --> targetY 分10步移动完
            wheel.count += 1
            const stepCount = 10; // 10 steps
            const direction = e.deltaY > 0 ? -1 : 1
            const offsetY = wheel.count * direction * 0.2 * screen.bounds.h
            const targetY = Math.max(0, Math.min(y + offsetY, screen.bounds.h))

            let mouseY = wheel.mouseY;
            const stepY = (targetY - mouseY) / stepCount;
            for (let i = 0; i < stepCount; i += 1) {
                mouseY += stepY;
                touchWait(10); // 间隔10ms
                touchMove(1, x, mouseY, pressure)
                touchCommit()
            }
            wheel.mouseY = targetY; // 记录当前点的位置

            wheel.key = setTimeout(() => { // wheel stopped do mouse up
                touchUp()
                touchCommit()
                wheel.key = null;
                wheel.count = 0;
            }, 100)
            }

            /* bind listeners */
            element.addEventListener('mousedown', mouseDownListener);
            element.addEventListener('mousewheel', mouseWheelListener);
            // element.addEventListener('mousemove', mouseHoverListener);
        },
        closeSyncTouchpad() {
            if (this.websockets.touchpad) {
            this.websockets.touchpad.close()
            }
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
        drawBlobImageToCanvas(blob, canvas) {
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
    height: 750px;
    background-color: gray;
    border:1px solid rgb(219, 219, 219);
}

.screen-footer{
    height: 38px;
    position: relative;
    border:1px solid rgb(219, 219, 219);
}

.flex-reset {
    min-height: 0%;
  }

  .height100 {
    height: 100%;
  }

  .height-auto-fill {
    height: -webkit-fill-available;
  }

  .grow-0 {
    flex-grow: 0;
  }

  .grow-1 {
    flex-grow: 1;
  }

  .nopadding {
    padding: 0px;
  }

  .color-red {
    color: red;
  }

  .color-blue {
    color: blue;
  }

  .cursor-pointer {
    cursor: pointer;
  }

  .debugarea {
    border: 1px solid rgb(106, 106, 106);
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

  .canvas-fg {
    z-index: 20;
    position: absolute;
  }

  .canvas-bg {
    z-index: 10;
  }

  .xterm-wrapper {
    line-height: 1.2;
    font-size: 12px;
    font-family: 'Courier New', Courier, monospace;
    height: 30em;
  }

  .terminal {
    border: 5px solid black;
  }

  .bottom-gutter {
    margin-bottom: 10px;
  }

  .card-columns {
    column-count: 2;
  }
</style>