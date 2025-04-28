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
            component: () => import('../pages/Cart/CartPage.vue'),
            meta: { title: '购物车' }
        }, {
            path: '/orders',
            component: () => import('../pages/Cart/Order.vue'),
            meta: { title: '订单' }
        }, {
            path: '/warehouse',
            component: () => import('../pages/product/WarehousePage.vue'),
            meta: { title: '库存管理' }
        }, {
            path: '/productDetail/:productId',
            component: () => import('../pages/product/Detail.vue'),
            meta: { title: '商品详情' }
        }, {
            path: '/createProduct',
            component: () => import('../pages/product/Create.vue'),
            meta: { title: '创建商品' }
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
        }, {
            path: '/advertisement',
            component: () => import('../pages/advertising/Advertisement.vue'),
            meta: { title: '广告' }
        }, {
            path: '/createAdvertisement',
            component: () => import('../pages/advertising/CreateAdvertisement.vue'),
            meta: { title: '广告' }
        }
    ]
})


export {router};
