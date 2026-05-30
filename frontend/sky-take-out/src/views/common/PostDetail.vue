<template>
  <div class="detail-page">
    <main class="main container" v-if="post">
      <div class="post-header">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <div class="author-info">
            <div class="avatar">{{ post.authorName?.charAt(0).toUpperCase() || '?' }}</div>
            <div class="author-detail">
              <span class="author-name">{{ post.authorName || '匿名用户' }}</span>
              <span class="post-time">{{ post.createTime }}</span>
            </div>
          </div>
          <button
            v-if="isLoggedIn && post.userId !== currentUserId"
            @click="toggleFollow(post.userId)"
            class="follow-btn"
            :class="{ followed: isFollowing }"
          >
            {{ isFollowing ? '已关注' : '+ 关注' }}
          </button>
          <div class="post-stats">
            <span>👁️ {{ post.viewCount || 0 }}</span>
            <span>💬 {{ post.commentCount || 0 }}</span>
            <span>❤️ {{ post.likeCount || 0 }}</span>
          </div>
        </div>
      </div>

      <div class="post-content card">
        <p>{{ post.content }}</p>
      </div>

      <div class="comment-section card mt-20">
        <h3 class="section-title">评论 ({{ comments.length }})</h3>

        <div class="comment-form" v-if="isLoggedIn">
          <textarea
            v-model="commentContent"
            class="comment-input"
            placeholder="写下你的评论..."
            rows="3"
          ></textarea>
          <button @click="submitComment" class="btn btn-primary">发表评论</button>
        </div>
        <div v-else class="login-tip">
          <router-link to="/login">登录</router-link>后参与评论
        </div>

        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="comment-author">{{ comment.userName }}</span>
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
            <div class="comment-actions">
              <button @click="openReportModal(comment.id)" class="report-btn">举报</button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <div v-else class="loading">加载中...</div>

    <!-- 举报弹窗 -->
    <div v-if="showReportModal" class="modal-overlay" @click.self="closeReportModal">
      <div class="modal-content report-modal">
        <div class="modal-header">
          <h3>举报评论</h3>
          <button @click="closeReportModal" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <p class="report-tip">请选择举报原因：</p>
          <div class="report-reasons">
            <label 
              v-for="reason in reportReasons" 
              :key="reason.value"
              class="reason-item"
            >
              <input 
                type="radio" 
                v-model="selectedReason" 
                :value="reason.value"
              />
              <span class="reason-icon">{{ reason.icon }}</span>
              <span class="reason-label">{{ reason.label }}</span>
            </label>
          </div>
          <div class="report-custom">
            <textarea
              v-model="customReason"
              class="form-textarea"
              rows="3"
              placeholder="请补充说明（选填）"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeReportModal" class="btn btn-cancel">取消</button>
          <button @click="submitReport" class="btn btn-danger">确认举报</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostByIdAPI, saveCommentAPI, reportPostAPI } from '@/api/post'
import { logoutAPI } from '@/api/login'
import { followUserAPI, unfollowUserAPI, checkFollowAPI } from '@/api/follow'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const comments = ref([])
const commentContent = ref('')
const isFollowing = ref(false)

// 举报相关
const showReportModal = ref(false)
const reportedCommentId = ref(null)
const selectedReason = ref('')
const customReason = ref('')

const reportReasons = [
  { value: 'spam', label: '垃圾信息/广告', icon: '📢' },
  { value: 'abuse', label: '辱骂/人身攻击', icon: '💢' },
  { value: 'porn', label: '色情低俗内容', icon: '🔞' },
  { value: 'politics', label: '政治敏感内容', icon: '⚠️' },
  { value: 'other', label: '其他', icon: '📝' }
]

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))
const username = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.username || ''
})
const currentUserId = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.id || null
})

const loadData = async () => {
  try {
    const id = route.params.id
    const res = await getPostByIdAPI(id)
    post.value = res.data
    comments.value = res.data.comments || []
    
    // 检查关注状态
    if (isLoggedIn.value && post.value.userId) {
      try {
        const followRes = await checkFollowAPI(post.value.userId)
        isFollowing.value = followRes.data
      } catch (error) {
        console.error('检查关注状态失败:', error)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加载失败')
  }
}

const toggleFollow = async (userId) => {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  
  try {
    if (isFollowing.value) {
      await unfollowUserAPI(userId)
      isFollowing.value = false
      ElMessage.success('已取消关注')
    } else {
      await followUserAPI(userId)
      isFollowing.value = true
      ElMessage.success('关注成功')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

// 举报相关方法
const openReportModal = (commentId) => {
  reportedCommentId.value = commentId
  selectedReason.value = ''
  customReason.value = ''
  showReportModal.value = true
}

const closeReportModal = () => {
  showReportModal.value = false
  reportedCommentId.value = null
  selectedReason.value = ''
  customReason.value = ''
}

const submitReport = async () => {
  if (!selectedReason.value) {
    ElMessage.warning('请选择举报原因')
    return
  }
  
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  
  try {
    let reasonText = selectedReason.value
    if (customReason.value.trim()) {
      reasonText += ' - ' + customReason.value.trim()
    }
    
    await reportPostAPI({
      targetId: reportedCommentId.value,
      reason: reasonText
    })
    
    ElMessage.success('举报提交成功，我们会尽快处理')
    closeReportModal()
  } catch (error) {
    console.error(error)
    ElMessage.error('举报失败，请重试')
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await saveCommentAPI(post.value.id, commentContent.value)
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleReport = async (commentId) => {
  try {
    await reportPostAPI({
      type: 'comment',
      targetId: commentId,
      reason: '违规内容'
    })
    ElMessage.success('举报成功')
  } catch (error) {
    console.error(error)
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: #666;
  font-size: 15px;
  transition: all 0.3s ease;
}

.nav-link:hover,
.nav-link.active,
.nav-link.router-link-active,
.nav-link.router-link-exact-active {
  color: #667eea;
  font-size: 18px;
  font-weight: 600;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  color: #667eea;
  font-weight: 500;
}

.main {
  padding: 30px 20px;
  max-width: 900px;
}

.post-header {
  background: #fff;
  border-radius: 10px;
  padding: 30px;
  margin-bottom: 20px;
}

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 15px;
}

.post-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 15px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.follow-btn {
  padding: 6px 16px;
  border: 1px solid #667eea;
  border-radius: 20px;
  background: transparent;
  color: #667eea;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.follow-btn:hover {
  background: rgba(102, 126, 234, 0.1);
}

.follow-btn.followed {
  background: #667eea;
  color: #fff;
}

.follow-btn.followed:hover {
  background: #5a6fd6;
}

.post-stats {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.post-content,
.comment-section {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  resize: vertical;
  margin-bottom: 10px;
  font-family: inherit;
}

.comment-input:focus {
  border-color: #667eea;
}

.login-tip {
  text-align: center;
  padding: 20px;
  color: #999;
  margin-bottom: 20px;
}

.login-tip a {
  color: #667eea;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #333;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  text-align: right;
}

.report-btn {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
  line-height: 1.5;
}

.report-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.report-btn:active {
  transform: scale(0.95);
}

.report-btn:active {
  transform: scale(0.95);
}

.loading {
  text-align: center;
  padding: 100px;
  color: #999;
}

/* 举报弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 12px;
  width: 90%;
  max-width: 480px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.modal-close:hover {
  background: #eee;
  color: #666;
}

.modal-body {
  padding: 24px;
}

.report-tip {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

.report-reasons {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.reason-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  gap: 10px;
}

.reason-item:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.reason-item input[type="radio"] {
  width: 16px;
  height: 16px;
  accent-color: #667eea;
}

.reason-icon {
  font-size: 20px;
}

.reason-label {
  font-size: 14px;
  color: #333;
}

.report-custom {
  margin-top: 16px;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.btn-cancel {
  padding: 10px 24px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.btn-danger {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  background: #ef4444;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-danger:hover {
  background: #dc2626;
}
</style>