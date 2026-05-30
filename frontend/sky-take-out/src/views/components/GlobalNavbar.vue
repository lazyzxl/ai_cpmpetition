<template>
  <!-- 顶部导航 -->
  <header class="header">
    <div class="header-content">
      <h1 class="logo">
        <span class="logo-icon">🏆</span>
        智竞云图
      </h1>
      <nav class="nav">
        <router-link to="/home" class="nav-link" :class="{ active: $route.path === '/home' }">首页</router-link>
        <router-link to="/posts" class="nav-link" :class="{ active: $route.path === '/posts' }">讨论帖子</router-link>
        <router-link v-if="isLoggedIn" to="/user/care" class="nav-link" :class="{ active: $route.path === '/user/care' }">我的关注</router-link>
        <router-link v-if="isLoggedIn" to="/user/follow" class="nav-link" :class="{ active: $route.path === '/user/follow' }">关注用户</router-link>
        <router-link v-if="isLoggedIn" to="/user/chat" class="nav-link" :class="{ active: $route.path.startsWith('/user/chat') }">聊天</router-link>
        <router-link v-if="isAdmin" to="/admin/dashboard" class="nav-link" :class="{ active: $route.path.startsWith('/admin') }">管理后台</router-link>
      </nav>
      <div class="user-actions">
        <template v-if="isLoggedIn">
          <span class="username">{{ username || 'admin' }}</span>
          <button @click="handleLogout" class="logout-btn">退出</button>
        </template>
        <template v-else>
          <router-link to="/login" class="login-btn">登录</router-link>
          <router-link to="/register" class="register-btn">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { logoutAPI } from '@/api/login'
import { getUnreadCountAPI } from '@/api/user'

const router = useRouter()
const unreadCount = ref(0)

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))
const isAdmin = computed(() => sessionStorage.getItem('role') === 'admin')
const username = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.username || ''
})

const loadUnreadCount = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await getUnreadCountAPI()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error(error)
  }
}

const goToMessages = () => {
  router.push('/user/messages')
}

const handleLogout = async () => {
  try {
    await logoutAPI()
  } catch (error) {
    console.error(error)
  }
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('user')
  sessionStorage.removeItem('role')
  sessionStorage.removeItem('userId')
  router.push('/login')
}

onMounted(() => {
  loadUnreadCount()
})

onUnmounted(() => {
})
</script>

<style scoped>
.header {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  font-size: 24px;
  font-weight: 600;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.logo-icon {
  font-size: 24px;
}

.nav {
  display: flex;
  gap: 35px;
  align-items: center;
}

.nav-link {
  color: #666;
  font-size: 19px;
  font-weight: 500;
  text-decoration: none;
  padding: 18px 0;
  transition: all 0.2s ease;
  position: relative;
}

.nav-link:hover {
  color: #667eea;
}

.nav-link:hover::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: #667eea;
  border-radius: 2px;
}

.nav-link.active,
.nav-link.router-link-active {
  color: #667eea;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: #667eea;
  border-radius: 2px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #667eea;
  font-size: 18px;
  font-weight: 500;
}

.login-btn,
.register-btn,
.logout-btn {
  font-size: 18px;
  padding: 6px 16px;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
}

.login-btn {
  color: #667eea;
  border: 1px solid #667eea;
  background: transparent;
}

.login-btn:hover {
  background: #f0f0ff;
}

.register-btn {
  color: #fff;
  background: #667eea;
  border: none;
}

.register-btn:hover {
  background: #5a6fd6;
}

.logout-btn {
  color: #666;
  border: none;
  background: transparent;
}

.logout-btn:hover {
  color: #333;
}

@media (max-width: 768px) {
  .nav {
    gap: 15px;
  }
  
  .nav-link {
    font-size: 17px;
  }
}
</style>
