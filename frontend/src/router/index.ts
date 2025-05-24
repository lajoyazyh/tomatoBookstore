import { createRouter, createWebHashHistory } from 'vue-router';

const routeCreator = (path: string, component: any, meta: { title: string }) => ({
    path,
    component,
    meta,
});

// 基础路由
const baseRoutes = [
    {
        path: '/',
        redirect: '/index', // 重定向
    },
    routeCreator('/index', () => import('../pages/index.vue'), { title: '首页' }),
];

// 产品路由
const productRoutes = [
    routeCreator('/warehouse', () => import('../pages/product/WarehousePage.vue'), { title: '商品列表' }),
    routeCreator('/productDetail/:productId', () => import('../pages/product/Detail.vue'), { title: '商品详情' }),
    routeCreator('/collection', () => import('../pages/product/Collection.vue'), { title: '我的收藏' }),
    routeCreator('/createProduct', () => import('../pages/product/Create.vue'), { title: '创建商品' }),
];

// 账户路由
const accountRoutes = [
    routeCreator('/register', () => import('../pages/account/Register.vue'), { title: '注册' }),
    routeCreator('/login', () => import('../pages/account/Login.vue'), { title: '登入' }),
    routeCreator('/dashboard', () => import('../pages/account/Dashboard.vue'), { title: '个人信息' }),
];

// 优惠券路由
const couponRoutes = [
    routeCreator('/manageCoupon', () => import('../pages/coupon/Manage.vue'), { title: '优惠券列表' }),
    routeCreator('/createCoupon', () => import('../pages/coupon/Create.vue'), { title: '创建优惠券' }),
    routeCreator('/couponDetail/:couponId', () => import('../pages/coupon/Detail.vue'), { title: '优惠券详情' }),
]

// 订单路由
const orderRoutes = [
    routeCreator('/cart', () => import('../pages/cart/CartPage.vue'), { title: '购物车' }),
    routeCreator('/orders', () => import('../pages/cart/Order.vue'), { title: '订单' }),
]

// 广告路由
const advertisingRoutes = [
    routeCreator('/advertisement', () => import('../pages/advertising/Advertisement.vue'), { title: '广告' }),
    routeCreator('/createAdvertisement', () => import('../pages/advertising/CreateAdvertisement.vue'), { title: '创建广告' }),
];

// 黑名单路由
const blockRoutes = [
    routeCreator('/blocks', () => import('../pages/block/block.vue'), { title: '广告' })
];

// 未定义的路径路由
const notFoundRoute = [
    routeCreator('/:pathMatch(.*)', () => import('../pages/default/404.vue'), { title: '404-页面未找到' }),
];

// 路由实例
const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        ...baseRoutes,
        ...productRoutes,
        ...accountRoutes,
        ...couponRoutes,
        ...orderRoutes,
        ...advertisingRoutes,
        ...blockRoutes,
        ...notFoundRoute,
    ],
});

export { router };