<template>
  <div class="detail-page">

    <main class="main container" v-if="competition">
      <div class="detail-header">
        <div class="detail-info">
          <h1 class="detail-title">{{ competition.name }}</h1>
          <div class="detail-tags">
            <span class="tag" v-if="competition.title">{{ competition.title }}</span>
          </div>
        </div>
        <div class="detail-actions">
          <button
            v-if="isLoggedIn"
            @click="toggleCare"
            class="btn"
            :class="isCared ? 'btn-default' : 'btn-primary'"
          >
            {{ isCared ? '已关注' : '关注比赛' }}
          </button>
        </div>
      </div>

      <div class="detail-content">
        <div class="content-main">
          <div class="card image-card">
            <div class="competition-image-wrapper">
              <img 
                v-if="competition.photo || competition.photourl || competition.photoUrl" 
                :src="competition.photo || competition.photourl || competition.photoUrl" 
                :alt="competition.name"
                class="competition-img"
              />
              <div v-else class="placeholder-image">
                <span class="placeholder-icon">📷</span>
              </div>
            </div>
          </div>

          <div class="card mt-20">
            <h3 class="card-title">比赛描述</h3>
            <p class="content-text">{{ competition.description }}</p>
          </div>

          <div class="card mt-20">
            <h3 class="card-title">比赛详情</h3>
            <div class="detail-list">
              <div class="detail-item">
                <span class="label">比赛类别：</span>
                <span
                  v-if="competition.title"
                  class="value link category-link"
                  @click="goToCategory(competition.title)"
                >{{ competition.title }}</span>
                <span v-else class="value">未知</span>
              </div>
              <div class="detail-item">
                <span class="label">主办方：</span>
                <span class="value">{{ competition.organizer || '未知' }}</span>
              </div>
              <div class="detail-item">
                <span class="label">比赛时间：</span>
                <span class="value">{{ competition.date || competition.competitionTime }}</span>
              </div>
              <div class="detail-item">
                <span class="label">参赛人数：</span>
                <span class="value">{{ competition.participantLimit || 0 }}</span>
              </div>
              <div class="detail-item" v-if="competition.url">
                <span class="label">官网地址：</span>
                <a :href="competition.url" target="_blank" class="value link">{{ competition.url }}</a>
              </div>
            </div>
          </div>
        </div>

        <div class="content-side">
          <div class="card">
            <h3 class="card-title">比赛奖励</h3>
            <p class="content-text">{{ competition.prize || '暂无奖励信息' }}</p>
          </div>

          <div class="card mt-20">
            <h3 class="card-title">联系方式</h3>
            <p class="content-text">{{ competition.contact || '暂无联系方式' }}</p>
          </div>

          <div class="card mt-20">
            <h3 class="card-title">AI 智能助手</h3>
            <div class="ai-section">
              <div class="ai-summary">
                <h4 class="ai-subtitle">AI 总结</h4>
                <p class="ai-content" v-if="aiSummary">{{ aiSummary }}</p>
                <p class="ai-placeholder" v-else>点击下方按钮生成比赛总结</p>
                <button @click="getAISummary" class="btn btn-primary btn-small" :disabled="isAiLoading">
                  {{ isAiLoading ? '生成中...' : '生成总结' }}
                </button>
              </div>
              
              <div class="ai-chat">
                <h4 class="ai-subtitle">AI 问答</h4>
                <div class="chat-history">
                  <div v-for="(msg, index) in chatHistory" :key="index" class="chat-message" :class="msg.role">
                    <div class="chat-content">{{ msg.content }}</div>
                  </div>
                </div>
                <div class="chat-input">
                  <input
                    v-model="chatQuestion"
                    type="text"
                    class="form-input"
                    placeholder="向AI提问..."
                    @keyup.enter="sendChatMessage"
                    :disabled="isAiLoading"
                  />
                  <button @click="sendChatMessage" class="btn btn-primary btn-small" :disabled="isAiLoading">
                    发送
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCompetitionByIdAPI, careCompetitionAPI, uncareCompetitionAPI } from '@/api/competition'
import { recordViewAPI } from '@/api/home'
import { logoutAPI } from '@/api/login'
import { checkCareAPI } from '@/api/care'
import { getAISummaryAPI, chatWithAiAPI } from '@/api/ai'

const route = useRoute()
const router = useRouter()
const competition = ref(null)
const isCared = ref(false)
const aiSummary = ref('')
const chatQuestion = ref('')
const chatHistory = ref([])
const isAiLoading = ref(false)

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))
const username = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.username || ''
})

const loadData = async () => {
  try {
    const id = route.params.id
    const res = await getCompetitionByIdAPI(id)
    competition.value = res.data

    recordViewAPI(id).catch(() => {})
    
    // 检查是否已关注
    if (isLoggedIn.value) {
      try {
        const careRes = await checkCareAPI(id)
        isCared.value = careRes.data
      } catch (error) {
        console.error('检查关注状态失败:', error)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载失败')
  }
}

const toggleCare = async () => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }

  try {
    const id = competition.value.id
    if (isCared.value) {
      await uncareCompetitionAPI(id)
      isCared.value = false
      ElMessage.success('已取消关注')
    } else {
      await careCompetitionAPI(id)
      isCared.value = true
      ElMessage.success('关注成功')
    }
  } catch (error) {
    console.error(error)
    // 显示后端返回的错误信息
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('操作失败')
    }
  }
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

const goToCategory = (title) => {
  router.push({ path: '/home', query: { category: title } })
}

onMounted(() => {
  loadData()
})

const getAISummary = async () => {
  if (!competition.value) return
  
  try {
    isAiLoading.value = true
    const res = await getAISummaryAPI({
      resourceType: 'competition',
      resourceId: competition.value.id
    })
    aiSummary.value = res.data || res
    ElMessage.success('AI总结生成成功')
  } catch (error) {
    console.error('AI总结失败:', error)
    ElMessage.error('AI总结生成失败')
  } finally {
    isAiLoading.value = false
  }
}

const sendChatMessage = async () => {
  if (!chatQuestion.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  
  if (!competition.value) return
  
  try {
    isAiLoading.value = true
    const userMessage = chatQuestion.value
    chatHistory.value.push({ role: 'user', content: userMessage })
    chatQuestion.value = ''
    
    const res = await chatWithAiAPI({
      resourceType: 'competition',
      resourceId: competition.value.id,
      question: userMessage
    })
    
    const aiAnswer = res.data || res
    chatHistory.value.push({ role: 'ai', content: aiAnswer })
  } catch (error) {
    console.error('AI问答失败:', error)
    ElMessage.error('AI问答失败')
  } finally {
    isAiLoading.value = false
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 68px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  font-weight: 500;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #fff;
  font-weight: 600;
  font-size: 15px;
}

.main {
  padding: 30px 20px;
  height: calc(100vh - 68px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.detail-header {
  background: #fff;
  border-radius: 10px;
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.detail-title {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.detail-tags {
  display: flex;
  gap: 10px;
}

.tag {
  padding: 4px 12px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 13px;
  color: #666;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 24px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  text-decoration: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.btn-default {
  background: #e0e0e0;
  color: #666;
}

.btn-default:hover {
  background: #d0d0d0;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 20px;
  flex: 1;
  overflow: hidden;
}

.content-main,
.content-side {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding-right: 4px;
}

.content-main::-webkit-scrollbar,
.content-side::-webkit-scrollbar {
  width: 6px;
}

.content-main::-webkit-scrollbar-thumb,
.content-side::-webkit-scrollbar-thumb {
  background: #d0d0d0;
  border-radius: 3px;
}

.content-main::-webkit-scrollbar-track,
.content-side::-webkit-scrollbar-track {
  background: transparent;
}

.card {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.content-text {
  color: #666;
  line-height: 1.8;
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
}

.label {
  color: #999;
  width: 100px;
}

.value {
  color: #333;
  font-weight: 500;
}

.value.link {
  color: #667eea;
  text-decoration: none;
}

.value.link:hover {
  text-decoration: underline;
}

.category-link {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 4px;
  transition: all 0.2s ease;
}

.category-link:hover {
  background: rgba(102, 126, 234, 0.2);
  text-decoration: none !important;
}

.image-card {
  padding: 0;
  overflow: hidden;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.competition-image-wrapper {
  width: 100%;
  overflow: hidden;
  background: #f8f9fa;
}

.competition-img {
  width: 100%;
  height: auto;
  display: block;
  transition: transform 0.3s;
}

.competition-image-wrapper:hover .competition-img {
  transform: scale(1.05);
}

.placeholder-image {
  width: 100%;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.placeholder-icon {
  font-size: 64px;
  opacity: 0.5;
}

.loading {
  text-align: center;
  padding: 100px;
  color: #999;
}

.ai-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ai-subtitle {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.ai-summary {
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.ai-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
  word-break: break-all;
  overflow-wrap: break-word;
}

.ai-placeholder {
  color: #999;
  font-size: 13px;
  margin-bottom: 10px;
}

.ai-chat {
  padding-top: 15px;
}

.chat-history {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 10px;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 8px;
}

.chat-message {
  margin-bottom: 10px;
}

.chat-message.user {
  text-align: right;
}

.chat-message.ai {
  text-align: left;
}

.chat-content {
  display: inline-block;
  max-width: 80%;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.5;
}

.chat-message.user .chat-content {
  background: #667eea;
  color: #fff;
}

.chat-message.ai .chat-content {
  background: #fff;
  color: #333;
  border: 1px solid #e0e0e0;
}

.chat-input {
  display: flex;
  gap: 8px;
}

.btn-small {
  padding: 6px 12px;
  font-size: 13px;
}

@media (max-width: 768px) {
  .main {
    height: auto;
    overflow: visible;
  }

  .detail-content {
    grid-template-columns: 1fr;
    overflow: visible;
  }

  .content-main,
  .content-side {
    overflow-y: visible;
  }

  .detail-header {
    flex-direction: column;
    gap: 20px;
  }
}
</style>