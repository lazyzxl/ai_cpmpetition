<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1 class="logo">管理后台</h1>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin/dashboard" class="nav-item">
          <span class="nav-icon">📊</span>
          <span>数据概览</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item">
          <span class="nav-icon">👥</span>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/competitions" class="nav-item">
          <span class="nav-icon">🏆</span>
          <span>比赛管理</span>
        </router-link>
        <router-link to="/admin/reports" class="nav-item">
          <span class="nav-icon">🚨</span>
          <span>举报管理</span>
        </router-link>
        <router-link to="/admin/messages" class="nav-item">
          <span class="nav-icon">📧</span>
          <span>消息管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <router-link to="/home" class="nav-item">
          <span class="nav-icon">🏠</span>
          <span>返回首页</span>
        </router-link>
        <button @click="handleLogout" class="nav-item logout">
          <span class="nav-icon">🚪</span>
          <span>退出登录</span>
        </button>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { logoutAPI } from '@/api/login'

const router = useRouter()

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
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: #2c3e50;
  color: #fff;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 18px;
  font-weight: 600;
}

.sidebar-nav {
  flex: 1;
  padding: 10px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s;
  cursor: pointer;
}

.nav-item:hover,
.nav-item.router-link-active {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-icon {
  margin-right: 10px;
  font-size: 16px;
}

.sidebar-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 10px 0;
}

.logout {
  background: none;
  border: none;
  width: 100%;
  text-align: left;
}

.main-content {
  flex: 1;
  margin-left: 240px;
  background: #f5f5f5;
  min-height: 100vh;
  padding: 20px;
}
</style>