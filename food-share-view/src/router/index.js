/**
 * Vue Router配置文件
 * 功能：定义应用路由、路由守卫、权限控制
 */
import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import { getToken, clearToken } from "@/utils/storage.js";
import echarts from 'echarts';

// 全局配置
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

/**
 * 路由配置
 * 包含：登录、注册、用户端、管理端等所有页面路由
 */
const routes = [
  // 404重定向到首页
  {
    path: "*",
    redirect: "/home"
  },
  // 根路径重定向到首页
  {
    path: "/",
    redirect: "/home"
  },
  // 登录页
  {
    path: "/login",
    component: () => import(`@/views/login/Login.vue`)
  },
  // 注册页
  {
    path: "/register",
    component: () => import(`@/views/register/Register.vue`)
  },
  // 创建美食
  {
    path: "/createGourmet",
    component: () => import(`@/views/user/CreateGourmet.vue`)
  },
  // 分享详情
  {
    path: "/shareDetail",
    component: () => import(`@/views/user/ShareDetail.vue`)
  },
  // 编辑美食
  {
    path: "/editGourmet",
    component: () => import(`@/views/user/EditGourmet.vue`)
  },
  // 查看美食详情（管理端）
  {
    path: "/seeGourmetDetail",
    component: () => import(`@/views/admin/SeeGourmetDetail.vue`)
  },
  // 管理端路由
  {
    path: "/admin",
    component: () => import(`@/views/admin/Main.vue`),
    meta: {
      requireAuth: true,
    },
    children: [
      // 仪表盘
      {
        path: "/admin/home",
        name: '仪表盘',
        icon: 'el-icon-pie-chart',
        component: () => import(`@/views/admin/Home.vue`),
        meta: { requireAuth: true },
      },
      // 用户管理
      {
        path: "/userManage",
        name: '用户管理',
        icon: 'el-icon-user',
        component: () => import(`@/views/admin/UserManage.vue`),
        meta: { requireAuth: true },
      },
      // 美食类别管理
      {
        path: "/categoryManage",
        name: '美食类别管理',
        icon: 'el-icon-paperclip',
        component: () => import(`@/views/admin/CategoryManage.vue`),
        meta: { requireAuth: true },
      },
      // 美食做法管理
      {
        path: "/gourmetManage",
        name: '美食做法管理',
        icon: 'el-icon-food',
        component: () => import(`@/views/admin/GourmetManage.vue`),
        meta: { requireAuth: true },
      },
      // 食谱管理
      {
        path: "/cookbookManage",
        name: '食谱管理',
        icon: 'el-icon-tickets',
        component: () => import(`@/views/admin/CookbookManage.vue`),
        meta: { requireAuth: true },
      },
      // 营养素管理
      {
        path: "/nutrimentManage",
        name: '营养素管理',
        icon: 'el-icon-suitcase-1',
        component: () => import(`@/views/admin/NutrimentManage.vue`),
        meta: { requireAuth: true },
      },
      // 用户饮食管理
      {
        path: "/dietHistoryManage",
        name: '用户饮食管理',
        icon: 'el-icon-dish',
        component: () => import(`@/views/admin/DietHistoryManage.vue`),
        meta: { requireAuth: true },
      },
      // 评论管理
      {
        path: "/evaluationsManage",
        name: '评论管理',
        icon: 'el-icon-chat-dot-round',
        component: () => import(`@/views/admin/EvaluationsManage.vue`),
        meta: { requireAuth: true },
      },
    ]
  },
  // 用户端路由
  {
    path: "/user",
    component: () => import(`@/views/user/Main.vue`),
    meta: {
      requireAuth: true,
    },
    redirect: "/home",
    children: [
      // 首页
      {
        path: "/home",
        name: '首页',
        component: () => import(`@/views/user/Home.vue`),
        meta: { requireAuth: true },
      },
      // 分类页
      {
        path: "/category",
        name: '分类',
        component: () => import(`@/views/user/Category.vue`),
        meta: { requireAuth: true },
      },
      // 消息页
      {
        path: "/message",
        name: '消息',
        component: () => import(`@/views/user/Message.vue`),
        meta: { requireAuth: true },
      },
      // 个人中心
      {
        path: "/mine",
        name: '我的',
        component: () => import(`@/views/user/Mine.vue`),
        meta: { requireAuth: true },
      },
      // 美食做法列表
      {
        path: "/gourmet",
        name: '美食做法',
        component: () => import(`@/views/user/Gourmet.vue`),
        meta: { requireAuth: true },
      },
      // 食谱
      {
        path: "/cookbook",
        name: '我的食谱',
        component: () => import(`@/views/user/Cookbook.vue`),
        meta: { requireAuth: true },
      },
      // 饮食记录
      {
        path: "/dietHistory",
        name: '饮食记录',
        component: () => import(`@/views/user/DietHistory.vue`),
        meta: { requireAuth: true },
      },
      // 我的食谱操作
      {
        path: "/myCookbook",
        name: '我的食谱',
        component: () => import(`@/views/user/CookbookOperation.vue`),
        meta: { requireAuth: true },
      },
      // 美食详情
      {
        path: "/gourmetDetail",
        name: '美食做法详情',
        component: () => import(`@/views/user/GourmetDetail.vue`),
        meta: { requireAuth: true },
      },
      // 食谱详情
      {
        path: "/cookbookDetail",
        name: '食谱详情',
        component: () => import(`@/views/user/CookbookDetail.vue`),
        meta: { requireAuth: true },
      },
      // 营养摄入
      {
        path: "/nutriment",
        name: '营养摄入',
        component: () => import(`@/views/user/Nutriment.vue`),
        meta: { requireAuth: true },
      },
      // 服务中心
      {
        path: "/service",
        name: '服务中心',
        component: () => import(`@/views/user/Service.vue`),
        meta: { requireAuth: true },
      },
      // 个人中心（旧版）
      {
        path: "/self",
        name: '个人中心',
        component: () => import(`@/views/user/Self.vue`),
        meta: { requireAuth: true },
      },
      // 重置密码
      {
        path: "/resetPwd",
        name: '重置密码',
        component: () => import(`@/views/user/ResetPwd.vue`),
        meta: { requireAuth: true },
      },
      // 我的收藏
      {
        path: "/save",
        name: '我的收藏',
        component: () => import(`@/views/user/Save.vue`),
        meta: { requireAuth: true },
      },
      // 我的发布
      {
        path: "/myPublish",
        name: '我的发布',
        component: () => import(`@/views/user/MyPublish.vue`),
        meta: { requireAuth: true },
      },
      // 草稿箱
      {
        path: "/draft",
        name: '草稿箱',
        component: () => import(`@/views/user/Draft.vue`),
        meta: { requireAuth: true },
      },
      // 账号设置
      {
        path: "/setting",
        name: '账号设置',
        component: () => import(`@/views/user/Setting.vue`),
        meta: { requireAuth: true },
      },
      // 收到的赞
      {
        path: "/whoLikedMe",
        name: '收到的赞',
        component: () => import(`@/views/user/WhoLikedMe.vue`),
        meta: { requireAuth: true },
      },
    ]
  }
];

// 创建路由实例
const router = new VueRouter({
  routes,
  mode: 'history'
});

/**
 * 全局路由守卫
 * 功能：登录验证、权限控制
 */
router.beforeEach((to, from, next) => {
  // 放行登录页和注册页
  if (to.path === '/login' || to.path === '/register') {
    return next();
  }

  // 检查需要认证的路由
  if (to.matched.some(record => record.meta.requireAuth)) {
    const token = getToken();

    // 未登录情况处理
    if (!token) {
      return next({
        path: '/login',
        query: { redirect: to.fullPath } // 记录目标路由
      });
    }

    // 已登录时的权限检查
    try {
      const role = parseInt(sessionStorage.getItem('role'));

      // 管理员路径检查（role=1为管理员）
      if (to.matched[0].path === '/admin' && role !== 1) {
        clearToken();
        return next("/login");
      }

      // 用户路径检查（role=2为普通用户）
      if (to.matched[0].path === '/user' && role !== 2) {
        clearToken();
        return next("/login");
      }

      return next();
    } catch (error) {
      console.error('权限检查失败:', error);
      return next('/login');
    }
  }

  // 普通页面直接放行
  next();
});

export default router;
