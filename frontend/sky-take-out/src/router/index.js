import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/common/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/common/Register.vue')
  },
  {
    path: '/forget-password',
    name: 'ForgetPassword',
    component: () => import('@/views/common/ForgetPassword.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/common/Home.vue')
  },
  {
    path: '/competition/:id',
    name: 'CompetitionDetail',
    component: () => import('@/views/common/CompetitionDetail.vue')
  },
  {
    path: '/posts',
    name: 'PostList',
    component: () => import('@/views/common/PostList.vue')
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/common/PostDetail.vue')
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/user/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/care',
    name: 'UserCare',
    component: () => import('@/views/user/UserCare.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/messages',
    name: 'MyMessages',
    component: () => import('@/views/user/MyMessages.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/follow',
    name: 'UserFollow',
    component: () => import('@/views/user/UserFollow.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/chat',
    name: 'Chat',
    component: () => import('@/views/user/Chat.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/chat/:userId',
    name: 'ChatDetail',
    component: () => import('@/views/user/ChatDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue')
      },
      {
        path: 'competitions',
        name: 'CompetitionManage',
        component: () => import('@/views/admin/CompetitionManage.vue')
      },
      {
        path: 'reports',
        name: 'ReportManage',
        component: () => import('@/views/admin/ReportManage.vue')
      },
      {
        path: 'messages',
        name: 'MessageManage',
        component: () => import('@/views/admin/MessageManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')
  const userRole = sessionStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userRole !== 'admin') {
    next('/home')
  } else {
    next()
  }
})

export default router