<template>
  <div class="chat-page">
    <main class="main container">
      <div class="chat-content">
        <div class="chat-header">
          <div class="header-left">
            <span class="back-arrow" @click="$router.push('/home')">←</span>
            <h2>消息中心</h2>
          </div>
          <span class="unread-badge" v-if="totalUnread > 0">{{ totalUnread }} 条未读</span>
        </div>

        <div class="session-list" v-if="!loading">
          <div v-if="sessions.length === 0" class="empty">
            <div class="empty-icon">💬</div>
            <p>暂无聊天记录</p>
            <p class="empty-tip">去关注用户，开始聊天吧！</p>
            <router-link to="/user/follow" class="btn btn-primary">发现用户</router-link>
          </div>
          <div 
            v-else
            v-for="session in sessions" 
            :key="session.userId" 
            class="session-item"
            @click="goToChat(session)"
          >
            <div class="session-avatar">
              {{ session.username?.charAt(0).toUpperCase() || 'U' }}
            </div>
            <div class="session-info">
              <div class="session-top">
                <span class="session-name">{{ session.username }}</span>
                <span class="session-time" v-if="session.lastMessageTime">{{ formatTime(session.lastMessageTime) }}</span>
              </div>
              <div class="session-bottom">
                <span class="session-preview">{{ session.lastMessage || '暂无消息' }}</span>
                <span class="unread-count" v-if="session.unreadCount > 0">{{ session.unreadCount }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="loading">加载中...</div>
      </div>
    </main>

    <footer class="footer">
      <div class="container">
        <p>© 2026 智竞云图 - 高校校园平台</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getChatSessionsAPI, getUnreadCountAPI } from '@/api/chat'

const router = useRouter()
const loading = ref(true)
const sessions = ref([])
const totalUnread = ref(0)
const username = ref(sessionStorage.getItem('user') || '用户')
let refreshTimer = null
let initialLoaded = false

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const loadSessions = async () => {
  try {
    const res = await getChatSessionsAPI()
    sessions.value = res.data || []
    if (!initialLoaded) {
      loading.value = false
      initialLoaded = true
    }
  } catch (error) {
    console.error(error)
    if (!initialLoaded) {
      loading.value = false
    }
  }
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCountAPI()
    totalUnread.value = res.data || 0
  } catch (error) {
    console.error(error)
  }
}

const goToChat = (session) => {
  router.push(`/user/chat/${session.userId}`)
}

onMounted(() => {
  loadSessions()
  loadUnreadCount()
  // 每30秒刷新一次
  refreshTimer = setInterval(() => {
    loadSessions()
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 0%);
  padding-bottom: 80px;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav {
  display: flex;
  gap: 35px;
}

.nav-link {
  color: #555;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link:hover,
.nav-link.active,
.nav-link.router-link-active {
  color: #667eea;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username-link {
  color: #667eea;
  font-weight: 600;
  font-size: 15px;
}

.main {
  padding: 40px 20px;
}

.chat-content {
  max-width: 600px;
  margin: 0 auto;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.back-arrow {
  font-size: 32px;
  font-weight: 700;
  cursor: pointer;
  color: rgba(255,255,255,0.8);
  margin-right: 12px;
  transition: color 0.2s;
}

.back-arrow:hover {
  color: #fff;
}

.chat-header h2 {
  color: #fff;
  font-size: 28px;
  font-weight: 700;
}

.unread-badge {
  background: #ef4444;
  color: #fff;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.session-list {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.empty {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty p {
  color: #666;
  font-size: 16px;
  margin-bottom: 10px;
}

.empty-tip {
  color: #999;
  font-size: 14px;
  margin-bottom: 30px;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #fff;
}

.session-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  gap: 15px;
}

.session-item:hover {
  background: #f8fafc;
}

.session-item:last-child {
  border-bottom: none;
}

.session-avatar {
  width: 55px;
  height: 55px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 5px;
}

.session-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.session-time {
  font-size: 12px;
  color: #999;
}

.session-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.session-preview {
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 280px;
}

.unread-count {
  background: #ef4444;
  color: #fff;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  flex-shrink: 0;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  text-decoration: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-text {
  background: transparent;
  color: #667eea;
  padding: 8px 16px;
  font-weight: 500;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.btn-text:hover {
  background: rgba(102, 126, 234, 0.1);
}

.footer {
  background: rgba(0, 0, 0, 0.1);
  padding: 20px 0;
  text-align: center;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}

.footer p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

@media (max-width: 768px) {
  .session-preview {
    max-width: 180px;
  }
  
  .nav {
    gap: 15px;
    font-size: 14px;
  }
}
</style>
