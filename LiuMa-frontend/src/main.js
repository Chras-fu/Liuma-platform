// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
// 引入element UI
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
Vue.use(ElementUI);
// 引入App
import App from './App';
// 引入路由
import router from './router';
// 引入状态管理
import store from './vuex/store';
// 引入icon
import './assets/icon/iconfont.css';
// 引入全局样式
import '../static/css/index.css';
// 引入axios
import ajax from './utils/ajax'
Vue.use(ajax);

Vue.config.productionTip = false;

// 过滤器
import * as custom from './utils/util'
Object.keys(custom).forEach(key => {
    Vue.filter(key, custom[key])
})

/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    store, //使用store vuex状态管理
    components: { App },
    template: '<App/>',
    data: {
        // 空的实例放到根组件下，所有的子组件都能调用
        Bus: new Vue()
    }
})

// 路由拦截器
router.beforeEach((to, from, next) => {
    if (to.matched.length != 0) {
        if (to.meta.requireAuth) { // 判断该路由是否需要登录权限
            if (store.state.token !== null) { // 通过state判断当前用户是否登录
                if (to.meta.requirePerm) {
                    if(store.state.permissions.includes(to.meta.requirePerm)){
                        next();
                    }else{
                        next({
                            path: '/home/dashboard'
                        });
                    }
                }else{
                    next();
                }
            } else {
                next({
                    path: '/login',
                    query: { redirect: to.fullPath } // 将跳转的路由path作为参数，登录成功后跳转到该路由
                });
            }
        } else {
            next();
        }
    } else {
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
    }
})