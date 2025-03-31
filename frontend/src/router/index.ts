import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
        history: createWebHashHistory(),
        routes: [{
            path: '/',
            redirect: '/index',
        }, {
            path: '/index',
            component: () => import('../pages/index.vue'),
            meta: { title: '首页' }
        }, {
            path: '/cart',
            component: () => import('../pages/CartPage.vue'),
            meta: { title: '购物车' }
        }, {
            path: '/warehouse',
            component: () => import('../pages/WarehousePage.vue'),
            meta: { title: '库存管理' }
        }, {
            path: '/register',
            component: () => import('../pages/account/Register.vue'),
            meta: { title: '注册' }
        }, {
            path: '/login',
            component: () => import('../pages/account/Login.vue'),
            meta: { title: '登入' }
        }, {
            path: '/dashboard',
            component: () => import('../pages/account/Dashboard.vue'),
            meta: { title: '个人信息' }
        }
    ]
})


export {router};
