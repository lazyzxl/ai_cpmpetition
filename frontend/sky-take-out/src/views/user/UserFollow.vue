<template>
  <div class="follow-page">
    <header class="header">
      <div class="container header-content">
        <h1 class="logo">智竞云图</h1>
        <nav class="nav">
          <router-link to="/home" class="nav-link">首页</router-link>
          <router-link to="/user/profile" class="nav-link">个人中心</router-link>
        </nav>
        <div class="user-actions">
          <span class="username-text">{{ username }}</span>
        </div>
      </div>
    </header>

    <main class="main container">
      <div class="follow-content">
        <!-- 统计卡片 -->
        <div class="stats-card">
          <div class="stat-item" @click="activeTab = 'follow'">
            <span class="stat-value">{{ followCount }}</span>
            <span class="stat-label">关注</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item" @click="activeTab = 'fans'">
            <span class="stat-value">{{ fansCount }}</span>
            <span class="stat-label">粉丝</span>
          </div>
        </div>

        <!-- 标签切换 -->
        <div class="tabs">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'follow' }"
            @click="activeTab = 'follow'"
          >
            我的关注
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'fans' }"
            @click="activeTab = 'fans'"
          >
            我的粉丝
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'users' }"
            @click="activeTab = 'users'"
          >
            发现用户
          </div>
        </div>

        <!-- 用户列表 -->
        <div class="user-list">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="currentList.length === 0" class="empty">
            {{ activeTab === 'follow' ? '暂未关注任何用户' : activeTab === 'fans' ? '暂无粉丝' : '暂无其他用户' }}
          </div>
          <div v-else>
            <div v-for="item in currentList" :key="item.id" class="user-card">
              <div class="user-avatar">
                {{ item.followUsername?.charAt(0).toUpperCase() || 'U' }}
              </div>
              <div class="user-info">
                <div class="username">{{ item.followUsername || item.username }}</div>
                <div class="profession">{{ item.followUserProfession || item.profession || '未设置专业' }}</div>
                <div class="time">关注时间: {{ formatTime(item.createTime) }}</div>
              </div>
              <div class="user-actions">
                <button 
                  v-if="activeTab === 'follow' || activeTab === 'users'"
                  class="btn-action"
                  :class="{ followed: isFollowed(item.followUserId || item.id) }"
                  @click="handleFollow(item)"
                >
                  {{ isFollowed(item.followUserId || item.id) ? '已关注' : '关注' }}
                </button>
                <button 
                  v-if="activeTab === 'follow' || activeTab === 'fans'"
                  class="btn-chat"
                  @click="goToChat(item.followUserId || item.id)"
                >
                  发消息
                </button>
                <button 
                  v-if="activeTab === 'follow'"
                  class="btn-unfollow"
                  @click="handleUnfollow(item)"
                >
                  取消关注
                </button>
              </div>
            </div>
          </div>
        </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getFollowListAPI, 
  getFansListAPI, 
  getAllUsersAPI,
  followUserAPI, 
  unfollowUserAPI,
  checkFollowAPI,
  getFansCountAPI,
  getFollowCountAPI
} from '@/api/follow'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('follow')
const username = computed(() => {
  try {
    const user = JSON.parse(sessionStorage.getItem('user') || '{}')
    return user.username || '用户'
  } catch {
    return '用户'
  }
})

// 数据
const followList = ref([])
const fansList = ref([])
const usersList = ref([])
const followedIds = ref(new Set())
const followCount = ref(0)
const fansCount = ref(0)

const currentList = computed(() => {
  switch (activeTab.value) {
    case 'follow': return followList.value
    case 'fans': return fansList.value
    case 'users': return usersList.value
    default: return []
  }
})

const isFollowed = (userId) => {
  return followedIds.value.has(userId)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const loadFollowList = async () => {
  try {
    const res = await getFollowListAPI()
    followList.value = res.data || []
    res.data?.forEach(item => followedIds.value.add(item.followUserId))
  } catch (error) {
    console.error(error)
  }
}

const loadFansList = async () => {
  try {
    const res = await getFansListAPI()
    fansList.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadUsersList = async () => {
  loading.value = true
  try {
    const res = await getAllUsersAPI()
    usersList.value = res.data || []
    // 检查每个用户是否已被关注
    for (const user of res.data || []) {
      try {
        const checkRes = await checkFollowAPI(user.id || user.followUserId)
        if (checkRes.data) {
          followedIds.value.add(user.id || user.followUserId)
        }
      } catch (e) {
        console.error(e)
      }
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCounts = async () => {
  try {
    const [followRes, fansRes] = await Promise.all([
      getFollowCountAPI(),
      getFansCountAPI()
    ])
    followCount.value = followRes.data || 0
    fansCount.value = fansRes.data || 0
  } catch (error) {
    console.error(error)
  }
}

const handleFollow = async (item) => {
  const userId = item.followUserId || item.id
  try {
    if (isFollowed(userId)) {
      await ElMessageBox.confirm('确定要取消关注吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await unfollowUserAPI(userId)
      followedIds.value.delete(userId)
      ElMessage.success('已取消关注')
      loadFollowList()
      loadCounts()
    } else {
      await followUserAPI(userId)
      followedIds.value.add(userId)
      ElMessage.success('关注成功')
      loadFollowList()
      loadCounts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('操作失败')
    }
  }
}

const handleUnfollow = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消关注吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unfollowUserAPI(item.followUserId)
    followedIds.value.delete(item.followUserId)
    ElMessage.success('已取消关注')
    loadFollowList()
    loadCounts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const goToChat = (userId) => {
  router.push(`/user/chat/${userId}`)
}

onMounted(() => {
  loadFollowList()
  loadFansList()
  loadCounts()
})

// 切换标签时加载数据
import { watch } from 'vue'
watch(activeTab, (newVal) => {
  if (newVal === 'users' && usersList.value.length === 0) {
    loadUsersList()
  }
})
</script>

<style scoped>
.follow-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 0%);
  padding-bottom: 80px;
}

.header {
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
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
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
  margin: 0;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: #666;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.3s ease;
  text-decoration: none;
}

.nav-link:hover,
.nav-link.active,
.nav-link.router-link-active {
  color: #667eea;
}

.user-actions {
  display: flex;
  align-items: center;
}

.username-text {
  color: #667eea;
  font-weight: 600;
  font-size: 14px;
}

.main {
  padding: 40px 20px;
}

.follow-content {
  max-width: 800px;
  margin: 0 auto;
}

.stats-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 60px;
}

.stat-item {
  text-align: center;
  cursor: pointer;
  padding: 10px 20px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(102, 126, 234, 0.1);
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.stat-divider {
  width: 1px;
  height: 50px;
  background: #e0e0e0;
}

.tabs {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 10px;
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 15px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
}

.tab-item:hover {
  background: rgba(102, 126, 234, 0.1);
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 600;
}

.user-list {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.user-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  gap: 20px;
}

.user-card:last-child {
  border-bottom: none;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.profession {
  font-size: 14px;
  color: #666;
  margin-bottom: 3px;
}

.time {
  font-size: 12px;
  color: #999;
}

.user-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.btn-action {
  padding: 10px 20px;
  border-radius: 20px;
  border: 2px solid #667eea;
  background: transparent;
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-action:hover {
  background: #667eea;
  color: #fff;
}

.btn-action.followed {
  background: #667eea;
  color: #fff;
}

.btn-chat {
  padding: 10px 20px;
  border-radius: 20px;
  border: 2px solid #10b981;
  background: transparent;
  color: #10b981;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-chat:hover {
  background: #10b981;
  color: #fff;
}

.btn-unfollow {
  padding: 10px 20px;
  border-radius: 20px;
  border: 2px solid #ef4444;
  background: transparent;
  color: #ef4444;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-unfollow:hover {
  background: #ef4444;
  color: #fff;
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
  .user-card {
    flex-direction: column;
    text-align: center;
  }
  
  .user-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .stats-card {
    gap: 30px;
  }
  
  .nav {
    gap: 15px;
  }
}
</style>
