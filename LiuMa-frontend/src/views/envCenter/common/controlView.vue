/**
* 控件元素页面
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="7">
                <div v-loading="imageLoading" class="image-body" id="screen" :style="'height: '+ imageHeight +'px'">
                    <canvas id="fgCanvas" class="canvas-fg" v-bind:style="canvasStyle"></canvas>
                    <canvas id="bgCanvas" class="canvas-bg" v-bind:style="canvasStyle"></canvas>
                    <span class="finger finger-0" style="transform: translate3d(200px, 100px, 0px)"></span>
                </div>
            </el-col>
            <el-col :span="10">
                <div class="tree-header">
                    <div class="tree-title">控件元素树</div>
                    <el-button class="tree-add" type="text" size="mini" icon="el-icon-refresh" @click="init">刷新控件</el-button>
                </div>
                <div class="tree-input">
                    <el-input size="mini" placeholder="输入关键字过滤" v-model="filterText"/>
                </div>
                <div v-loading="treeLoading" class="tree-body" :style="'height: '+ (imageHeight-78) +'px'">
                    <el-tree class="filter-tree" :data="treeData" :current-node-key="currentNode" check-on-click-node
                    :props="defaultProps" node-key="id" ref="tree" :filter-node-method="filterNode"
                    @node-click="clickNode" highlight-current :default-expanded-keys="expandedKeys">
                        <span class="custom-tree-node" slot-scope="{ node }">
                            <span class="tree-label">{{node.label}}</span>
                        </span>
                    </el-tree>
                </div>
            </el-col>
            <el-col :span="7">
                <el-table size="mini" :height='imageHeight-29' :data="propData" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" :selectable="selectable" width="45">
                    </el-table-column>
                    <el-table-column prop="prop" label="属性名">
                    </el-table-column>
                    <el-table-column prop="value" label="属性值" min-width="100">
                    </el-table-column>
                </el-table>
                <el-button size="mini" type="text" @click="addControl('PROP')" icon="el-icon-plus">属性定位元素</el-button>
                <el-button size="mini" type="text" @click="addControl('XPATH')" icon="el-icon-plus">XPATH定位元素</el-button>
            </el-col>
        </el-row>
        <!-- 添加控件弹框 -->
        <control-append :controlVisible="controlVisible" :controlForm="controlForm" @closeDialog="controlVisible=false" @submitControlForm="submitControlForm"/>
    </div>
</template>
<script>
import controlAppend from '../../caseCenter/common/control/controlAppend';
import {ImagePool} from "@/utils/imagepool";
import {b64toBlob} from "@/utils/base64"
import $ from 'jquery';
export default {
    name: 'ControlView',
    components:{
        controlAppend
    },
    props:{
        system: String,
        device: Object,
        imageHeight: Number,
        loading: Boolean
    },
    data() {
        return{
            canvas: {
                bg: null,
                fg: null,
            },
            canvasStyle: {
                opacity: 0.5,
                width: 'inherit',
                height: 'inherit'
            },
            lastScreenSize: {
                screen: {},
                canvas: {
                    width: 1,
                    height: 1
                }
            },
            imageLoading: false,
            treeLoading: false,
            filterText: "",
            treeData: [],
            cursor: {},
            currentNode: null,
            nodeSelected: null,
            nodeHovered: null,
            nodeHoveredList: [],
            originNodes: [],
            originNodeMaps: {},
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            expandedKeys:[],
            mapAttrCount: {},
            propData: [],
            selections: [],
            xpathExpr: null,
            controlVisible: false,
            controlForm: {
                id:"",
                name:"",
                system: "",
                by: "",
                expression: "XPATH",
                expressionList: [],
                moduleId: "",
                moduleName:"",
                description: ""
            },
            imagePool: null,
            activity: null,
            packageName: null
        }
    },
    created(){
        this.imagePool = new ImagePool(100);
    },
    watch: {
        loading(val){
            if(val===true){
                this.init();
            }
        },
        filterText(val) {
            this.$refs.tree.filter(val);
        }
    },
    mounted: function () {
        this.canvas.bg = document.getElementById('bgCanvas');
        this.canvas.fg = document.getElementById('fgCanvas');
        this.activeMouseControl();

    },
    methods: {
        // 获取数据方法
        init() {
            this.getScreen();
            this.dumpHierarchy();
        },
        dumpHierarchy() {
            this.treeLoading = true;
            this.$axios.get(this.device.sources.url + "/device/hierarchy?serial=" + this.device.serial)
            .then(ret => {
                this.packageName = ret.data.data.packageName;
                this.activity = ret.data.data.activity;
                this.drawAllNodeFromSource(ret.data.data.jsonHierarchy);
                this.nodeSelected = null;
                this.treeLoading = false;
            });
        },
        getScreen() {
            this.imageLoading = true;
            this.$axios.get(this.device.sources.url + "/device/screenshot?serial=" + this.device.serial)
            .then(ret => {
                var blob = b64toBlob(ret.data.data.data, 'image/' + ret.data.data.type);
                this.drawBlobImageToScreen(blob);
                this.imageLoading = false;
            });
        },
        drawAllNodeFromSource(source) {
            let tree = [];
            let that = this;
            if(this.system === 'android'){
                source._type = this.packageName;
                source.name = this.activity;
                tree.push(this.sourceToTree(source));
            }else{
                source.forEach(function(s){
                    tree.push(that.sourceToTree(s));
                });
            }
            this.treeData = tree;

            let nodeMaps = this.originNodeMaps = {}

            function sourceToNodes(source) {
                let node = Object.assign({}, source); //, { children: undefined });
                nodeMaps[node._id] = node;
                let nodes = [node];
                if (source.children) {
                    source.children.forEach(function (s) {
                        s._parentId = node._id;
                        nodes = nodes.concat(sourceToNodes(s))
                    })
                }
                return nodes;
            }
            let nodes = [];
            if(this.system === 'android'){
                nodes = sourceToNodes(source);
            }else{
                source.forEach(function(s){
                    nodes = nodes.concat(sourceToNodes(s));
                });
            }
            this.originNodes = nodes;
            this.drawAllNode();
            this.canvasStyle.opacity = 1.0;
        },
        drawBlobImageToScreen(blob) {
            // Support jQuery Promise
            var dtd = $.Deferred();
            var bgcanvas = this.canvas.bg,
                fgcanvas = this.canvas.fg,
                self = this,
                ctx = bgcanvas.getContext('2d'),
                URL = window.URL || window.webkitURL,
                BLANK_IMG = 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',
                img = this.imagePool.next();
                
            img.onload = function () {
                fgcanvas.width = bgcanvas.width = img.width
                fgcanvas.height = bgcanvas.height = img.height

                ctx.drawImage(img, 0, 0, img.width, img.height);
                self.resizeScreen(img);

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
        resizeScreen(img) {
            // check if need update
            if (img) {
                if (this.lastScreenSize.canvas.width == img.width &&
                    this.lastScreenSize.canvas.height == img.height) {
                    return;
                }
            } else {
                img = this.lastScreenSize.canvas;
                if (!img) {
                    return;
                }
            }
            var screenDiv = document.getElementById('screen');
            this.lastScreenSize = {
                canvas: {
                    width: img.width,
                    height: img.height
                },
                screen: {
                    width: screenDiv.clientWidth,
                    height: screenDiv.clientHeight,
                }
            }
            var canvasRatio = img.width / img.height;
            var screenRatio = screenDiv.clientWidth / screenDiv.clientHeight;
            if (canvasRatio > screenRatio) {
                Object.assign(this.canvasStyle, {
                    width: Math.floor(screenDiv.clientWidth) + 'px', //'100%',
                    height: Math.floor(screenDiv.clientWidth / canvasRatio) + 'px', // 'inherit',
                })
            } else {
                Object.assign(this.canvasStyle, {
                    width: Math.floor(screenDiv.clientHeight * canvasRatio) + 'px', //'inherit',
                    height: Math.floor(screenDiv.clientHeight) + 'px', //'100%',
                })
            }
        },
        drawRefresh: function () {
            this.drawAllNode();
            if (this.nodeHovered) {
                this.drawNode(this.nodeHovered, "blue");
            }
            if (this.nodeSelected) {
                this.drawNode(this.nodeSelected, "red");
            }
        },
        drawAllNode() {
            var self = this;
            var canvas = self.canvas.fg;
            var ctx = canvas.getContext('2d');
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            self.originNodes.forEach(function (node) {
                // ignore some types
                if (['Layout'].includes(node.type)) {
                    return;
                }
                self.drawNode(node, 'black', true);
            })
            },
        drawNode: function (node, color, dashed) {
            if (!node || !node.rect) {
                return;
            }
            var x = node.rect.x,
                y = node.rect.y,
                w = node.rect.width,
                h = node.rect.height;
            color = color || 'black';
            var ctx = this.canvas.fg.getContext('2d');
            var rectangle = new Path2D();
            rectangle.rect(x, y, w, h);
            if (dashed) {
                ctx.lineWidth = 1;
                ctx.setLineDash([8, 10]);
            } else {
                ctx.lineWidth = 5;
                ctx.setLineDash([]);
            }
            ctx.strokeStyle = color;
            ctx.stroke(rectangle);
        },
        findNodesByPosition(pos) {
            function isInside(node, x, y) {
                if (!node.rect) {
                return false;
                }
                var lx = node.rect.x,
                ly = node.rect.y,
                rx = node.rect.width + lx,
                ry = node.rect.height + ly;
                return lx < x && x < rx && ly < y && y < ry;
            }

            function nodeArea(node) {
                return node.rect.width * node.rect.height;
            }

            let activeNodes = this.originNodes.filter(function (node) {
                if (!isInside(node, pos.x, pos.y)) {
                return false;
                }
                // skip some types
                if (['Layout', 'Sprite'].includes(node.type)) {
                return false;
                }
                return true;
            })

            activeNodes.sort((node1, node2) => {
                return nodeArea(node1) - nodeArea(node2)
            })
            return activeNodes;
        },
        sourceToTree(source) {
            var n = {}
            n.id = source._id;
            n.label = source._type
            if (source.name) {
                n.label += " - " + source.name;
            }
            if (source.resourceId) {
                n.label += " - " + source.resourceId;
            }
            if (source.children) {
                n.children = []
                let that = this;
                source.children.forEach(function (s) {
                    n.children.push(that.sourceToTree(s));
                }.bind(this));
            }
            return n;
        },
        // 过滤节点
        filterNode(val, data) {
            if (!val) return true;
            return data.label.indexOf(val) !== -1;
        },
        clickNode(val){
            // 点击后图片高亮显示被选中元素
            let node = this.originNodeMaps[val.id]
            this.drawAllNode();
            this.drawNode(node, "red");
            // 右侧列表显示被选中元素
            this.getPropData(node);
        },
        handleSelectionChange(rows){
            this.selections.splice(0, this.selections.length);
            for(let i=0;i<rows.length;i++){
                this.selections.push(rows[i]);
            }
        },
        activeMouseControl() {
            var self = this;
            var element = this.canvas.fg;

            var screen = {
                bounds: {}
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

            function activeFinger(index, x, y, pressure) {
                var scale = 0.5 + pressure
                $(".finger-" + index)
                .addClass("active")
                .css("transform", 'translate3d(' + x + 'px,' + y + 'px,0)')
            }

            function deactiveFinger(index) {
                $(".finger-" + index).removeClass("active")
            }

            function mouseMoveListener(event) {
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
                // that.touchMove(0, x / screen.bounds.w, y / screen.bounds.h, pressure);
            }

            function mouseHoverLeaveListener(event) {
                self.nodeHovered = null;
                self.drawRefresh()
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

                var pos = coord(e);
                // change precision
                pos.px = Math.floor(pos.px * 1000) / 1000;
                pos.py = Math.floor(pos.py * 1000) / 1000;
                pos.x = Math.floor(pos.px * element.width);
                pos.y = Math.floor(pos.py * element.height);
                self.cursor = pos;

                self.nodeHovered = null;
                markPosition(self.cursor)

                stopMousing()
            }

            function stopMousing() {
                element.removeEventListener('mousemove', mouseMoveListener);
                element.addEventListener('mousemove', mouseHoverListener);
                element.addEventListener('mouseleave', mouseHoverLeaveListener);
                document.removeEventListener('mouseup', mouseUpListener);
                deactiveFinger(0);
            }

            function coord(event) {
                var e = event;
                if (e.originalEvent) {
                e = e.originalEvent
                }
                calculateBounds()
                var x = e.pageX - screen.bounds.x
                var y = e.pageY - screen.bounds.y
                var px = x / screen.bounds.w;
                var py = y / screen.bounds.h;
                return {
                px: px,
                py: py,
                x: Math.floor(px * element.width),
                y: Math.floor(py * element.height),
                }
            }

            function mouseHoverListener(event) {
                var e = event;
                if (e.originalEvent) {
                e = e.originalEvent
                }
                // Skip secondary click
                if (e.which === 3) {
                return
                }
                e.preventDefault()
                // startMousing()

                var x = e.pageX - screen.bounds.x
                var y = e.pageY - screen.bounds.y
                var pos = coord(event);

                self.nodeHoveredList = self.findNodesByPosition(pos);
                self.nodeHovered = self.nodeHoveredList[0];
                self.drawRefresh()

                if (self.cursor.px) {
                    markPosition(self.cursor)
                }
            }

            function mouseDownListener(event) {
                var e = event;
                if (e.originalEvent) {
                e = e.originalEvent
                }
                // Skip secondary click
                if (e.which === 3) {

                return
                }
                e.preventDefault()

                let fakePinch = e.altKey
                calculateBounds()
                // startMousing()

                var x = e.pageX - screen.bounds.x
                var y = e.pageY - screen.bounds.y
                var pressure = 0.5
                activeFinger(0, e.pageX, e.pageY, pressure);

                if (self.nodeHovered) {
                    self.nodeSelected = self.nodeHovered;
                    self.drawAllNode();
                    self.expandedKeys = [self.nodeSelected._id];
                    self.currentNode = self.nodeSelected._id;
                    self.$refs.tree.setCurrentKey(self.currentNode);
                    self.drawNode(self.nodeSelected, "red");
                    self.getPropData(self.nodeSelected);
                }

                element.removeEventListener('mouseleave', mouseHoverLeaveListener);
                element.removeEventListener('mousemove', mouseHoverListener);
                element.addEventListener('mousemove', mouseMoveListener);
                document.addEventListener('mouseup', mouseUpListener);
            }

            function markPosition(pos) {
                var ctx = self.canvas.fg.getContext("2d");
                ctx.fillStyle = '#ff0000'; // red
                ctx.beginPath()
                ctx.arc(pos.x, pos.y, 12, 0, 2 * Math.PI)
                ctx.closePath()
                ctx.fill()

                ctx.fillStyle = "#fff"; // white
                ctx.beginPath()
                ctx.arc(pos.x, pos.y, 8, 0, 2 * Math.PI)
                ctx.closePath()
                ctx.fill();
            }

            /* bind listeners */
            element.addEventListener('mousedown', mouseDownListener);
            element.addEventListener('mousemove', mouseHoverListener);
            element.addEventListener('mouseleave', mouseHoverLeaveListener);
        },
        getPropData(node){
            // 当前元素的xpath定位推荐
            this.xpathExpr = this.elemXPathLite(node);
            let keys = this.filterAttributeKeys(node);
            let data = [
                {prop: "Xpath", value: this.xpathExpr},
                {prop: "坐标", value: '('+ this.cursor.px +','+ this.cursor.py+')'},
                {prop: "className", value: this.system==='android'?node._type:'XCUIElementType' + node._type},
                {prop: "rect", value: JSON.stringify(node.rect)}
            ];
            keys.forEach((k) =>{
                data.push({prop: k, value: node[k]})
            });
            this.propData = data;
        },
        filterAttributeKeys(node) {
            return Object.keys(node).filter(k => {
                if (['children', 'rect'].includes(k)) {
                    return false;
                }
                return !k.startsWith("_");
            })
        },
        incrAttrCount(collectionKey, key) {
            if (!this.mapAttrCount.hasOwnProperty(collectionKey)) {
                this.mapAttrCount[collectionKey] = {}
            }
            let count = this.mapAttrCount[collectionKey][key] || 0;
            this.mapAttrCount[collectionKey][key] = count + 1;
        },
        getAttrCount(collectionKey, key) {
            let mapCount = this.mapAttrCount[collectionKey];
            if (!mapCount) {
                return 0;
            }
            return mapCount[key] || 0;
        },
        elemXPathLite(node) {
            // scan nodes
            this.mapAttrCount = {}
            let that = this;
            this.originNodes.forEach((n) => {
                that.incrAttrCount("label", n.label)
                that.incrAttrCount("resourceId", n.resourceId)
                that.incrAttrCount("text", n.text)
                that.incrAttrCount("_type", n._type)
                that.incrAttrCount("description", n.description)
            })

            const array = [];
            while (node && node._parentId) {
                const parent = this.originNodeMaps[node._parentId]
                if (this.getAttrCount("label", node.label) === 1) {
                    array.push(`*[@label="${node.label}"]`)
                    break
                } else if (this.getAttrCount("resourceId", node.resourceId) === 1) {
                    array.push(`*[@resource-id="${node.resourceId}"]`)
                    break
                } else if (this.getAttrCount("text", node.text) === 1) {
                    array.push(`*[@text="${node.text}"]`)
                    break
                } else if (this.getAttrCount("description", node.description) === 1) {
                    array.push(`*[@content-desc="${node.description}"]`)
                    break
                } else if (this.getAttrCount("_type", node._type) === 1) {
                    array.push(`${node._type}`)
                    break
                } else if (!parent) {
                    array.push(`${node._type}`)
                } else {
                    let index = 0;
                    parent.children.some((n) => {
                        if (n._type == node._type) {
                        index++
                        }
                        return n._id == node._id
                    })
                    array.push(`${node._type}[${index}]`)
                }
                node = parent;
            }
            return `//${array.reverse().join("/")}`
        },
        selectable(row, index){
            if(['label', 'resourceId', 'name', 'text', 'type', 'tag', 'description', 'className'].includes(row.prop)){
                return true;
            }
        },
        // 新增控件
        addControl(val){
            if(val==='PROP' & this.selections.length===0){
                this.$message.warning("属性定位请至少选择一条属性");
                return;
            }
            this.controlForm = {
                system: this.system,
                id:"",
                name:"",
                by: val,
                expression: "",
                expressionList: [],
                moduleId: "",
                moduleName:"",
                description: ""
            };
            if(val === 'XPATH'){
                this.controlForm.expression = this.xpathExpr;
            }else{
                let elist = []
                this.selections.forEach((s) => {
                    elist.push({propName: s.prop,propValue: s.value});
                });
                this.controlForm.expressionList = elist;
            }
            this.controlVisible = true;
        },
        // 提交控件
        submitControlForm() {
            this.controlVisible = false;
        }
    }
}
</script>
<style scoped>
.image-body{
    background-color: white;
}

.canvas-fg {
    z-index: 1;
    position: absolute;
    max-width: 100%;
    height: auto;
    margin-top: 2px;
}

.canvas-bg {
    z-index: 0;
    position: absolute;
    max-width: 100%;
    height: auto;
    margin-top: 2px;
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

.tree-header{
    height: 35px; 
    padding-right: 5px; 
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.tree-title {
  float: left; 
  padding: 6px 12px; 
  font-weight: bold; 
  font-size: 12px;
}

.tree-input{
  height: 30px;
  padding: 5px 5px;
}

.filter-tree >>> .el-tree-node__children{
    overflow: visible !important;
}

.tree-body{
    overflow: hidden;
    overflow-x: auto;
    overflow-y: auto;
    white-space: nowrap;
}

</style>