/**
* 控件元素页面
*/ 
<template>
    <div>
        <el-row :gutter="10">
            <el-col :span="7">
                <div v-loading="loading" class="image-body" :style="'height: '+ imageHeight +'px'">
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
                <div class="tree-body" :style="'height: '+ (imageHeight-78) +'px'">
                    <el-tree class="filter-tree" :data="treeData" :props="defaultProps" 
                        @node-click="clickNode" :filter-node-method="filterNode"/>
                </div>
            </el-col>
            <el-col :span="7">
                <el-table size="mini" :height='imageHeight-29' :data="propData" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="35">
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
import $ from 'jquery';
export default {
    name: 'ControlView',
    components:{
        controlAppend
    },
    props:{
        system: String,
        device: String,
        url: String,
        imageHeight: Number,
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
            loading: false,
            canvasStyle: {
                opacity: 1,
                width: 'unset',
                height: 'unset',
                maxHeight: "unset",
            },
            filterText: "",
            treeData: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            },
            propData: [],
            selections: [],
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
        }
    },
    created(){
        this.imagePool = new ImagePool(100);
    },
    watch: {
        'device.serial'(newVal, oldVal){
            this.init();
        },
        filterText(val) {
            this.$refs.tree.filter(val);
        }
    },
    mounted: function () {
        this.canvas.bg = document.getElementById('bgCanvas')
        this.canvas.fg = document.getElementById('fgCanvas')

        window.c = this.canvas.bg;
        var ctx = c.getContext('2d')
    },
    methods: {
        // 获取数据方法
        init() {
            
            this.imagePool = new ImagePool(100);
        
        },
        dumpHierarchy() {
            this.$axios.get(this.device.sources.url + "/device/hierarchy?serial=" + this.device.serial)
            .then(ret => {
                this.activity = ret.data.activity; // only for android
                this.drawAllNodeFromSource(ret.data.jsonHierarchy);
                this.nodeSelected = null;
            })
            .always(() => {
            this.dumping = false
            })
        },
        screenRefresh() {
            this.loading = true;
            this.$axios.get(this.device.sources.url + "/device/screenshot?serial=" + this.device.serial)
            .then(ret => {
                var blob = b64toBlob(ret.data.data, 'image/' + ret.data.type);
                this.drawBlobImageToScreen(blob);
                this.loading = false;
            });
        },
        drawAllNodeFromSource(source) {
            let jstreeData = this.sourceToJstree(source);
            let jstree = this.$jstree.jstree(true);
            jstree.settings.core.data = jstreeData;
            jstree.refresh();

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
            this.originNodes = sourceToNodes(source) //ret.nodes;
            this.drawAllNode();
            this.loading = false;
            this.canvasStyle.opacity = 1.0;
        },
        drawBlobImageToScreen(blob) {
            // Support jQuery Promise
            var dtd = $.Deferred();
            var bgcanvas = this.canvas.bg,
                fgcanvas = this.canvas.fg,
                ctx = bgcanvas.getContext('2d'),
                self = this,
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
        drawAllNode() {
            var self = this;
            var canvas = self.canvas.fg;
            var ctx = canvas.getContext('2d');
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            self.nodes.forEach(function (node) {
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

            let activeNodes = this.nodes.filter(function (node) {
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
        // 过滤节点
        filterNode(value, data) {
            if (!value) return true;
            return data.name.indexOf(value) !== -1;
        },
        clickNode(){

        },
        handleSelectionChange(){

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
    background-color: rgb(120, 120, 120);
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

.tree-body{
    overflow: hidden;
    overflow-x: auto;
    overflow-y: auto;
    white-space: nowrap;
}

/* .prop-footer{
    height: 36px; 
    display: flex;
}

.by-select{
    float: left;
    margin-top: 2px;
    width: 30%;
}

.control-add{
    float: right;
    width: 27px;
    padding: 5px 5px;
    height: 27px;
    margin-top: 2px;
} */
</style>