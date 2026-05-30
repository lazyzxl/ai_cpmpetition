<template>
  <div class="chat-detail-page">
    <header class="header">
      <div class="container header-content">
        <button class="back-btn" @click="goBack">
          <span class="back-icon">←</span>
        </button>
        <div class="chat-title">
          <span class="chat-name">{{ chatUser?.username || '聊天' }}</span>
          <span class="chat-profession">{{ chatUser?.profession || '' }}</span>
        </div>
        <div class="placeholder"></div>
      </div>
    </header>

    <main class="main" ref="messageContainer">
      <div class="messages-container">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="messages.length === 0" class="empty">
          <p>暂无聊天记录，开始发送消息吧！</p>
        </div>
        <div v-else>
          <div 
            v-for="msg in messages" 
            :key="msg.id" 
            class="message-item"
            :class="{ mine: msg.senderId === currentUserId, other: msg.senderId !== currentUserId }"
          >
            <div class="message-avatar" v-if="msg.senderId !== currentUserId">
              {{ (msg.senderUsername || 'U').charAt(0).toUpperCase() }}
            </div>
            <div class="message-content">
              <div class="message-bubble">
                {{ msg.content }}
              </div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <footer class="footer">
      <div class="input-container">
        <input 
          v-model="messageText" 
          type="text" 
          class="message-input" 
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
        />
        <button class="send-btn" @click="sendMessage" :disabled="!messageText.trim()">
          发送
        </button>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getChatHistoryAPI, sendMessageAPI, markAsReadAPI } from '@/api/chat'
import { getUserInfoAPI } from '@/api/user'

const router = useRouter()
const route = useRoute()
const currentUserId = parseInt(sessionStorage.getItem('userId'))
const targetUserId = parseInt(route.params.userId)

const messages = ref([])
const messageText = ref('')
const loading = ref(true)
const chatUser = ref(null)
const messageContainer = ref(null)
let refreshTimer = null
let initialLoaded = false

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  }
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const loadChatUser = async () => {
  try {
    const res = await getUserInfoAPI(targetUserId)
    chatUser.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadMessages = async () => {
  try {
    const res = await getChatHistoryAPI(targetUserId)
    messages.value = res.data || []
    if (!initialLoaded) {
      loading.value = false
      initialLoaded = true
    }
    // 标记消息为已读
    try {
      await markAsReadAPI(targetUserId)
    } catch (e) {
      console.error(e)
    }
    scrollToBottom()
  } catch (error) {
    console.error(error)
    if (!initialLoaded) {
      loading.value = false
    }
  }
}

const sendMessage = async () => {
  const content = messageText.value.trim()
  if (!content) return
  
  try {
    await sendMessageAPI(targetUserId, content)
    messageText.value = ''
    loadMessages()
  } catch (error) {
    console.error(error)
    ElMessage.error('发送失败')
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

const goBack = () => {
  router.push('/user/chat')
}

onMounted(() => {
  loadChatUser()
  loadMessages()
  // 每5秒刷新一次消息
  refreshTimer = setInterval(loadMessages, 5000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.chat-detail-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 15px;
}

.back-btn {
  background: transparent;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #fff;
}

.back-icon {
  font-size: 24px;
}

.chat-title {
  text-align: center;
}

.chat-name {
  display: block;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.chat-profession {
  display: block;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

.placeholder {
  width: 40px;
}

.main {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.messages-container {
  max-width: 600px;
  margin: 0 auto;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
}

.message-item.mine {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
}

.message-item.other .message-bubble {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-item.mine .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 5px;
  padding: 0 5px;
}

.message-item.mine .message-time {
  text-align: right;
}

.footer {
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  padding: 10px 15px;
}

.input-container {
  display: flex;
  gap: 10px;
  max-width: 600px;
  margin: 0 auto;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 24px;
  font-size: 15px;
  outline: none;
  transition: all 0.3s ease;
}

.message-input:focus {
  border-color: #667eea;
}

.send-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
