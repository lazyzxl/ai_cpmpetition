<template>
  <div class="post-list-page">
    <main class="main container">
      <div class="page-header">
        <h2 class="page-title">📝 讨论帖子</h2>
        <button v-if="isLoggedIn" @click="showCreateModal = true" class="btn btn-primary">
          <span class="btn-icon">✏️</span>
          发布帖子
        </button>
      </div>

      <div class="post-list">
        <div
          v-for="post in posts"
          :key="post.id"
          class="post-card"
          @click="goToDetail(post.id)"
        >
          <div class="post-header">
            <div class="author-info">
              <div class="avatar">
                {{ post.authorName?.charAt(0).toUpperCase() || '?' }}
              </div>
              <div class="author-detail">
                <span class="author-name">{{ post.authorName || '匿名用户' }}</span>
                <span class="post-time">{{ post.createTime }}</span>
              </div>
            </div>
            <button
              v-if="isLoggedIn && post.userId !== currentUserId"
              @click.stop="toggleFollow(post.userId)"
              class="follow-btn"
              :class="{ followed: followedUsers.includes(post.userId) }"
            >
              {{ followedUsers.includes(post.userId) ? '已关注' : '+ 关注' }}
            </button>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-content">{{ post.content }}</p>
          <div class="post-footer">
            <span class="post-tag">{{ post.tag || '综合讨论' }}</span>
            <div class="post-stats">
              <span class="stat-item">👁️ {{ post.viewCount || 0 }}</span>
              <span class="stat-item">💬 {{ post.commentCount || 0 }}</span>
              <span class="stat-item">❤️ {{ post.likeCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="posts.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无帖子，快来发布第一个吧！</p>
      </div>
    </main>

    <!-- 发布帖子弹窗 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布帖子</h3>
          <button @click="showCreateModal = false" class="modal-close">×</button>
        </div>
        <form @submit.prevent="handleCreate" class="modal-form">
          <div class="form-item">
            <label class="form-label">标题</label>
            <input
              v-model="postData.title"
              type="text"
              class="form-input"
              placeholder="请输入帖子标题"
            />
          </div>
          <div class="form-item">
            <label class="form-label">分类</label>
            <select v-model="postData.tag" class="form-input">
              <option value="综合讨论">综合讨论</option>
              <option value="比赛交流">比赛交流</option>
              <option value="经验分享">经验分享</option>
              <option value="求助答疑">求助答疑</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">内容</label>
            <textarea
              v-model="postData.content"
              class="form-textarea"
              rows="5"
              placeholder="请输入帖子内容..."
            ></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" @click="showCreateModal = false" class="btn btn-cancel">取消</button>
            <button type="submit" class="btn btn-primary">发布</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostListAPI, createPostAPI } from '@/api/post'
import { logoutAPI } from '@/api/login'
import { followUserAPI, unfollowUserAPI, getFollowListAPI } from '@/api/follow'

const router = useRouter()
const posts = ref([])
const showCreateModal = ref(false)

const postData = reactive({
  title: '',
  content: '',
  tag: '综合讨论'
})

const followedUsers = ref([])

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))
const isAdmin = computed(() => sessionStorage.getItem('role') === 'admin')
const username = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.username || ''
})
const currentUserId = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.id || null
})

const loadPosts = async () => {
  try {
    const res = await getPostListAPI({ page: 1, pageSize: 20 })
    posts.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadFollowingList = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await getFollowListAPI()
    followedUsers.value = res.data?.map(f => f.followUserId) || []
  } catch (error) {
    console.error(error)
  }
}

const toggleFollow = async (userId) => {
  const index = followedUsers.value.indexOf(userId)
  if (index > -1) {
    // 取消关注
    try {
      await unfollowUserAPI(userId)
      followedUsers.value.splice(index, 1)
      ElMessage.success('已取消关注')
    } catch (error) {
      console.error(error)
      ElMessage.error('取消关注失败')
    }
  } else {
    // 关注
    try {
      await followUserAPI(userId)
      followedUsers.value.push(userId)
      ElMessage.success('关注成功')
    } catch (error) {
      console.error(error)
      ElMessage.error('关注失败')
    }
  }
}

const goToDetail = (id) => {
  router.push(`/post/${id}`)
}

const handleCreate = async () => {
  if (!postData.title.trim()) {
    ElMessage.error('请输入标题')
    return
  }
  if (!postData.content.trim()) {
    ElMessage.error('请输入内容')
    return
  }
  
  try {
    await createPostAPI(postData)
    ElMessage.success('发布成功')
    showCreateModal.value = false
    postData.title = ''
    postData.content = ''
    postData.tag = '综合讨论'
    loadPosts()
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
  loadPosts()
  loadFollowingList()
})
</script>

<style scoped>
.post-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 头部样式 */
.header {
  background: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
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

/* 主内容区域 */
.main {
  padding: 30px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

/* 按钮样式 */
.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.btn-text {
  background: transparent;
  color: #667eea;
}

.btn-text:hover {
  background: rgba(102, 126, 234, 0.1);
}

.btn-cancel {
  background: #f0f0f0;
  color: #666;
}

.btn-icon {
  font-size: 14px;
}

/* 帖子列表 */
.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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

.author-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.4;
}

.post-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-tag {
  padding: 4px 12px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 20px;
  font-size: 12px;
}

.post-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  font-size: 13px;
  color: #999;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  color: #999;
  font-size: 15px;
}

/* 弹窗样式 */
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
  border-radius: 16px;
  width: 90%;
  max-width: 550px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.modal-form {
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 10px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  transition: border-color 0.3s;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 25px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav {
    gap: 15px;
  }
  
  .nav-link {
    font-size: 13px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>