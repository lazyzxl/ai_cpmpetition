<template>
  <div class="global-message-btn" v-if="isLoggedIn">
    <button @click="goToMessages" class="message-float-btn">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
      </svg>
      <span class="badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUnreadCountAPI } from '@/api/user'

const router = useRouter()
const unreadCount = ref(0)

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))

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

let timer = null

onMounted(() => {
  loadUnreadCount()
  // 每30秒刷新一次未读消息数量
  timer = setInterval(loadUnreadCount, 30000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.global-message-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

.message-float-btn {
  position: relative;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.message-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.message-float-btn svg {
  width: 24px;
  height: 24px;
  color: #fff;
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #f5576c;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 5px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  line-height: 14px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}
</style>
