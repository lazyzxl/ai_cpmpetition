<template>
  <div class="message-container">
    <div class="message-header">
      <span class="back-arrow" @click="$router.push('/home')">←</span>
      <h2>我的消息</h2>
      <div class="header-actions">
        <span class="unread-badge" v-if="unreadCount > 0">{{ unreadCount }} 条未读</span>
        <button 
          v-if="unreadCount > 0" 
          class="mark-all-btn" 
          @click="handleMarkAllRead"
        >
          全部已读
        </button>
      </div>
    </div>
    
    <div class="message-list" v-if="messages.length > 0">
      <div 
        class="message-item" 
        v-for="message in messages" 
        :key="message.id"
        :class="{ unread: message.isRead === 0 }"
        @click="handleMessageClick(message)"
      >
        <div class="message-icon">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
        </div>
        <div class="message-content">
          <div class="message-title">{{ message.title }}</div>
          <div class="message-body">{{ message.content }}</div>
          <div class="message-time">{{ formatTime(message.createTime) }}</div>
        </div>
        <div class="message-status" v-if="message.isRead === 0">
          <span class="dot"></span>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-else>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
      </svg>
      <p>暂无消息</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyMessagesAPI, markMessageAsReadAPI, markAllAsReadAPI, getUnreadCountAPI } from '@/api/user/message'

const messages = ref([])
const unreadCount = ref(0)

const loadMessages = async () => {
  try {
    const res = await getMyMessagesAPI()
    messages.value = res.data
    await loadUnreadCount()
  } catch (error) {
    console.error(error)
  }
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCountAPI()
    unreadCount.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleMessageClick = async (message) => {
  if (message.isRead === 0) {
    try {
      await markMessageAsReadAPI(message.id)
      message.isRead = 1
      unreadCount.value--
    } catch (error) {
      console.error(error)
    }
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllAsReadAPI()
    messages.value.forEach(msg => {
      msg.isRead = 1
    })
    unreadCount.value = 0
  } catch (error) {
    console.error(error)
  }
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.message-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.message-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.back-arrow {
  font-size: 24px;
  cursor: pointer;
  color: #666;
  margin-right: 12px;
  transition: color 0.2s;
}

.back-arrow:hover {
  color: #667eea;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.unread-badge {
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 10px;
}

.mark-all-btn {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  color: #667eea;
  background: #f0f2ff;
  border: 1px solid #d6d9ff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.mark-all-btn:hover {
  background: #667eea;
  color: #fff;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.2s ease;
}

.message-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.message-item.unread {
  background-color: #fff7ed;
  border-left: 3px solid #fa8c16;
}

.message-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 50%;
  margin-right: 12px;
  flex-shrink: 0;
}

.message-icon svg {
  width: 20px;
  height: 20px;
  color: #1890ff;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.message-body {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-status {
  margin-left: 12px;
}

.message-status .dot {
  display: block;
  width: 8px;
  height: 8px;
  background-color: #ff4d4f;
  border-radius: 50%;
  margin-top: 8px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 14px;
}
</style>
