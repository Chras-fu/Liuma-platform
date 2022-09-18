// 导入组件
import Vue from 'vue';
import Router from 'vue-router';
// 登录
import login from '@/views/login';
// 首页
import index from '@/views/index';
// 主页
import Homepage from '@/views/home/dashboard';

/**
 * 公共组件
 */
// 文件管理
import FileManage from '@/views/baseCenter/fileManage';
// 公共参数
import CommonParam from '@/views/baseCenter/commonParam';
// 函数管理
import FuncManage from '@/views/baseCenter/funcManage';
// 函数编辑
import FuncEdit from '@/views/baseCenter/funcEdit';
// 操作管理
import OperationManage from '@/views/baseCenter/operationManage';
// 操作编辑
import OperationEdit from '@/views/baseCenter/operationEdit';
/**
 * 环境中心
 */
// 环境管理
import EnvManage from '@/views/envCenter/envManage';
// 引擎管理
import EngineManage from '@/views/envCenter/engineManage';
// 设备管理
import DeviceManage from '@/views/envCenter/deviceManage';
// 设备控制
import DeviceControl from '@/views/envCenter/deviceControl';
/**
 * 用例中心
 */
// 接口管理
import InterfaceManage from '@/views/caseCenter/interfaceManage';
// 接口编辑
import InterfaceEdit from '@/views/caseCenter/interfaceEdit';
// 元素管理
import ElementManage from '@/views/caseCenter/elementManage';
// 控件管理
import ControlManage from '@/views/caseCenter/controlManage';
// 用例管理
import CaseManage from '@/views/caseCenter/caseManage';
// API用例
import ApiCaseEdit from '@/views/caseCenter/apiCaseEdit';
// WEB用例
import WebCaseEdit from '@/views/caseCenter/webCaseEdit';
// APP用例
import AppCaseEdit from '@/views/caseCenter/appCaseEdit';
/**
 * 计划管理
 */
// 测试集合
import TestCollection from '@/views/planCenter/testCollection';
// 测试集合编辑
import CollectionEdit from '@/views/planCenter/collectionEdit';
// 测试计划
import TestPlan from '@/views/planCenter/testPlan';
// 测试计划编辑
import PlanEdit from '@/views/planCenter/planEdit';
/**
 * 测试追踪
 */
// 测试报告
import TestReport from '@/views/report/testReport';
// 引擎管理
import ReportDetail from '@/views/report/reportDetail';
/**
 * 配置中心
 */
// 系统配置
import SystemSetting from '@/views/setting/systemSetting';
// 个人信息
import UserSetting from '@/views/setting/userSetting';
/**
 * 系统管理
 */
// 项目管理
import projectManage from '@/views/system/project';
// 角色管理
import RoleManage from '@/views/system/role';
// 用户管理
import userManage from '@/views/system/user';


// 启用路由
Vue.use(Router);

// 导出路由
export default new Router({
    routes: [{
        path: '/',
        name: '',
        component: login,
        hidden: true,
        meta: {
            requireAuth: false
        }
    }, {
        path: '/login',
        name: '登录',
        component: login,
        hidden: true,
        meta: {
            requireAuth: false
        }
    }, {
        path: '/index',
        name: '首页',
        component: index,
        iconCls: 'el-icon-tickets',
        children: [{
            path: '/home/dashboard',
            name: '主页',
            component: Homepage,
            meta: {
                requireAuth: true
            }
        },{
            path: '/common/fileManage',
            name: '文件管理',
            component: FileManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/commonParam',
            name: '公共参数',
            component: CommonParam,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/funcManage',
            name: '函数管理',
            component: FuncManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/funcManage/edit/:functionId',
            name: '函数编辑',
            component: FuncEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/funcManage/add',
            name: '函数新增',
            component: FuncEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/operationManage',
            name: '操作管理',
            component: OperationManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/operationManage/:uiType/edit/:operationId',
            name: '操作编辑',
            component: OperationEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/common/operationManage/:uiType/add/:operationType',
            name: '操作新增',
            component: OperationEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/envCenter/envManage',
            name: '环境管理',
            component: EnvManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/envCenter/engineManage',
            name: '引擎管理',
            component: EngineManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/envCenter/deviceManage',
            name: '设备管理',
            component: DeviceManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/envCenter/deviceControl/:serial',
            name: '设备控制',
            component: DeviceControl,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/interfaceManage',
            name: '接口管理',
            component: InterfaceManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/interfaceManage/add',
            name: '接口新增',
            component: InterfaceEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/interfaceManage/edit/:apiId',
            name: '接口编辑',
            component: InterfaceEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/elementManage',
            name: '元素管理',
            component: ElementManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/controlManage',
            name: '控件管理',
            component: ControlManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage',
            name: '用例管理',
            component: CaseManage,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/apiCase/:type/:caseId',
            name: 'API用例编辑',
            component: ApiCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/apiCase/:type',
            name: 'API用例新增',
            component: ApiCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/apiCase/:type/:caseId',
            name: 'API用例复用',
            component: ApiCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/webCase/:type/:caseId',
            name: 'WEB用例编辑',
            component: WebCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/webCase/:type',
            name: 'WEB用例新增',
            component: WebCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/webCase/:type/:caseId',
            name: 'WEB用例复用',
            component: WebCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/appCase/:system/:type/:caseId',
            name: 'APP用例编辑',
            component: AppCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/appCase/:system/:type',
            name: 'APP用例新增',
            component: AppCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/caseCenter/caseManage/appCase/:system/:type/:caseId',
            name: 'APP用例复用',
            component: AppCaseEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/planManage/testCollection',
            name: '测试集合',
            component: TestCollection,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/planManage/testCollection/add',
            name: '测试集合新增',
            component: CollectionEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/planManage/testCollection/edit/:collectionId',
            name: '测试集合编辑',
            component: CollectionEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/planManage/testPlan',
            name: '测试计划',
            component: TestPlan,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/planManage/testPlan/add',
            name: '测试计划新增',
            component: PlanEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/planManage/testPlan/edit/:planId',
            name: '测试计划编辑',
            component: PlanEdit,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/report/testReport',
            name: '测试报告',
            component: TestReport,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/report/testReport/detail/:reportId',
            name: '报告详情',
            component: ReportDetail,
            meta: {
                requirePerm: "NORMAL_MENU",
                requireAuth: true
            }
        },{
            path: '/setting/systemSetting',
            name: '系统配置',
            component: SystemSetting,
            meta: {
                requirePerm: "SETTING_MENU",
                requireAuth: true
            }
        },{
            path: '/setting/userSetting',
            name: '用户信息',
            component: UserSetting,
            meta: {
                requireAuth: true
            }
        },{
            path: '/systemManage/project',
            name: '项目管理',
            component: projectManage,
            meta: {
                requirePerm: "PROJECT_MENU",
                requireAuth: true
            }
        },{
            path: '/systemManage/role',
            name: '角色管理',
            component: RoleManage,
            meta: {
                requirePerm: "ROLE_MENU",
                requireAuth: true
            }
        },{
            path: '/systemManage/user',
            name: '用户管理',
            component: userManage,
            meta: {
                requirePerm: "USER_MENU",
                requireAuth: true
            }
        }]
    }]
})
