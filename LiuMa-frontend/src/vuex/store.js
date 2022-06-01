import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);
// 登录验证
export default new Vuex.Store({
    state: {
        userInfo: null,
        token: null,
        projectId: null,
        permissions: []
    },
    mutations: {
        // 登录
        set_userInfo(state, user) {
            state.userInfo = user;
        },
        // 退出
        del_userInfo(state) {
            state.userInfo = null;
            localStorage.removeItem("userInfo");
        },
        set_token(state, token) {
            state.token = token;
        },
        del_token(state) {
            state.token = null;
            localStorage.removeItem('token');
        },
        set_project(state, projectId){
            state.projectId = projectId;
        },
        set_permission(state, permissions){
            state.permissions = permissions;
        }
    }
})