<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container header-content">
        <h1 class="logo">
          <span class="logo-icon">🏆</span>
          智竞云图
        </h1>
        <nav class="nav">
          <router-link to="/home" class="nav-link active">首页</router-link>
          <router-link to="/posts" class="nav-link">讨论帖子</router-link>
          <router-link v-if="isLoggedIn" to="/user/care" class="nav-link">我的关注</router-link>
          <router-link v-if="isLoggedIn" to="/user/follow" class="nav-link">关注用户</router-link>
          <router-link v-if="isLoggedIn" to="/user/chat" class="nav-link">聊天</router-link>
          <router-link v-if="isAdmin" to="/admin/dashboard" class="nav-link">管理后台</router-link>
        </nav>
        <div class="user-actions">
          <template v-if="isLoggedIn">
            <button @click="goToMessages" class="message-btn">
              <span class="badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
              <span>🔔</span>
            </button>
            <router-link to="/user/profile" class="user-avatar">
              {{ username?.charAt(0).toUpperCase() || 'U' }}
            </router-link>
            <button @click="handleLogout" class="btn btn-outline">退出</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-primary">登录</router-link>
            <router-link to="/register" class="btn btn-outline">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="container hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            发现精彩 <span class="highlight">校园比赛</span>
          </h1>
          <p class="hero-desc">汇聚高校各类竞赛信息，找到属于你的舞台</p>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-number">{{ statistics.competitionCount }}+</span>
              <span class="stat-label">精选比赛</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ statistics.userCount }}+</span>
              <span class="stat-label">参与用户</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ statistics.categoryCount }}+</span>
              <span class="stat-label">比赛类别</span>
            </div>
          </div>
        </div>
        <div class="hero-search">
          <div class="search-box-wrapper">
            <div class="search-box">
              <span class="search-icon">🔍</span>
              <input
                v-model="searchKey"
                type="text"
                class="search-input"
                placeholder="搜索比赛名称、类型..."
                @keyup.enter="handleSearch"
                @focus="showDropdown = true"
                @blur="setTimeout(() => showDropdown = false, 150)"
                @input="showDropdown = true"
              />
              <button @click="handleSearch" class="search-btn">搜索</button>
            </div>
            <div v-if="showDropdown && searchSuggestions.length > 0" class="search-dropdown">
              <div
                v-for="item in searchSuggestions"
                :key="item.id"
                class="search-dropdown-item"
                @mousedown.prevent="handleSelectSuggestion(item.id)"
              >
                <span class="dropdown-item-name">{{ item.name || item.title }}</span>
                <span class="dropdown-item-tag">{{ item.title || item.category || '比赛' }}</span>
              </div>
              <div v-if="filteredCompetitions.length > 6" class="search-dropdown-more">
                还有 {{ filteredCompetitions.length - 6 }} 个结果
              </div>
            </div>
          </div>
          <div class="filter-tags">
            <span 
              v-for="tag in filterTags" 
              :key="tag.value"
              :class="['filter-tag', selectedTag === tag.value ? 'active' : '']"
              @click="toggleTag(tag.value)"
            >
              {{ tag.icon }} {{ tag.label }}
            </span>
          </div>
        </div>
      </div>
    </section>

    <main class="main">
      <div class="container">
        <!-- 热门比赛 - 无限滚动展示 -->
        <section class="section hot-marquee-section" v-if="hotCompetitions.length > 0">
          <div class="section-header">
            <div class="section-title-group">
              <h2 class="section-title">🔥 热门比赛</h2>
              <span class="section-subtitle">最受关注的赛事</span>
            </div>
          </div>
          <div class="marquee-container">
            <div class="marquee-track">
              <div
                v-for="item in hotCompetitions"
                :key="`a-${item.id}`"
                class="marquee-item"
                @click="goToDetail(item.id)"
              >
                <div class="competition-card">
                  <div class="card-image">
                    <img 
                      v-if="item.photourl" 
                      :src="item.photourl" 
                      :alt="item.name" 
                      class="competition-img"
                    />
                    <div v-else class="placeholder-image">
                      <span class="placeholder-icon">🎯</span>
                    </div>
                    <div class="card-badge">
                      <span class="badge-hot">🔥 热门</span>
                    </div>
                  </div>
                  <div class="card-content">
                    <div class="card-header">
                      <h3 class="card-title">{{ item.name }}</h3>
                      <span class="card-tag">{{ item.title }}</span>
                    </div>
                    <p class="card-desc">{{ item.description }}</p>
                    <div class="card-meta">
                      <span class="meta-item">
                        <span class="meta-icon">👥</span>
                        {{ item.careCount || 0 }} 人关注
                      </span>
                      <span class="meta-item">
                        <span class="meta-icon">📅</span>
                        {{ formatDate(item.date) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div
                v-for="item in hotCompetitions"
                :key="`b-${item.id}`"
                class="marquee-item"
                @click="goToDetail(item.id)"
              >
                <div class="competition-card">
                  <div class="card-image">
                    <img 
                      v-if="item.photourl" 
                      :src="item.photourl" 
                      :alt="item.name" 
                      class="competition-img"
                    />
                    <div v-else class="placeholder-image">
                      <span class="placeholder-icon">🎯</span>
                    </div>
                    <div class="card-badge">
                      <span class="badge-hot">🔥 热门</span>
                    </div>
                  </div>
                  <div class="card-content">
                    <div class="card-header">
                      <h3 class="card-title">{{ item.name }}</h3>
                      <span class="card-tag">{{ item.title }}</span>
                    </div>
                    <p class="card-desc">{{ item.description }}</p>
                    <div class="card-meta">
                      <span class="meta-item">
                        <span class="meta-icon">👥</span>
                        {{ item.careCount || 0 }} 人关注
                      </span>
                      <span class="meta-item">
                        <span class="meta-icon">📅</span>
                        {{ formatDate(item.date) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 为你推荐 -->
        <section class="section" v-if="recommendedCompetitions.length > 0">
          <div class="section-header">
            <div class="section-title-group">
              <h2 class="section-title">🎯 为你推荐</h2>
              <span class="section-subtitle">根据你的兴趣精选</span>
            </div>
          </div>
          <div class="competition-grid">
            <div
              v-for="item in recommendedCompetitions"
              :key="item.id"
              class="competition-card"
              @click="goToDetail(item.id)"
            >
              <div class="card-image">
                <img 
                  v-if="item.photourl" 
                  :src="item.photourl" 
                  :alt="item.name" 
                  class="competition-img"
                />
                <div v-else class="placeholder-image">
                  <span class="placeholder-icon">⭐</span>
                </div>
                <div class="card-badge">
                  <span class="badge-recommend">推荐</span>
                </div>
              </div>
              <div class="card-content">
                <div class="card-header">
                  <h3 class="card-title">{{ item.name }}</h3>
                  <span class="card-tag">{{ item.title }}</span>
                </div>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-meta">
                  <span class="meta-item">
                    <span class="meta-icon">👥</span>
                    {{ item.careCount || 0 }} 人关注
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    {{ formatDate(item.date) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 全部比赛 -->
        <section class="section" id="all-competitions">
          <div class="section-header">
            <div class="section-title-group">
              <h2 class="section-title">📚 全部比赛</h2>
              <span class="section-subtitle">共 {{ filteredCompetitions.length }} 个比赛</span>
            </div>

          </div>
          
          <div class="competition-grid" v-if="filteredCompetitions.length > 0">
            <div
              v-for="item in filteredCompetitions"
              :key="item.id"
              class="competition-card"
              @click="goToDetail(item.id)"
            >
              <div class="card-image">
                <img 
                  v-if="item.photourl" 
                  :src="item.photourl" 
                  :alt="item.name" 
                  class="competition-img"
                />
                <div v-else class="placeholder-image">
                  <span class="placeholder-icon">🎪</span>
                </div>
                <div class="card-overlay">
                  <span class="view-detail">查看详情</span>
                </div>
              </div>
              <div class="card-content">
                <div class="card-header">
                  <h3 class="card-title">{{ item.name }}</h3>
                  <span class="card-tag">{{ item.title }}</span>
                </div>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-meta">
                  <span class="meta-item">
                    <span class="meta-icon">👥</span>
                    {{ item.careCount || 0 }} 人关注
                  </span>
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    {{ formatDate(item.date) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="filteredCompetitions.length === 0" class="empty-state">
            <div class="empty-icon">🔍</div>
            <p class="empty-text">{{ searchKey ? '未找到匹配的比赛' : '暂无比赛数据' }}</p>
            <button v-if="searchKey" @click="clearSearch" class="btn btn-primary">清除搜索</button>
          </div>
        </section>
      </div>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="footer-logo">🏆 智竞云图</span>
            <p class="footer-desc">高校校园竞赛信息平台</p>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>平台</h4>
              <a href="#">关于我们</a>
              <a href="#">帮助中心</a>
              <a href="#">意见反馈</a>
            </div>
            <div class="link-group">
              <h4>合作</h4>
              <a href="#">入驻申请</a>
              <a href="#">联系我们</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 智竞云图. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getAllCompetitionsAPI, getHotCompetitionsAPI, getRecommendedCompetitionsAPI, getHomeStatisticsAPI } from '@/api/home'
import { logoutAPI } from '@/api/login'
import { getUnreadCountAPI } from '@/api/user'

const router = useRouter()
const route = useRoute()
const allCompetitions = ref([])
const hotCompetitions = ref([])
const recommendedCompetitions = ref([])
const unreadCount = ref(0)
const searchKey = ref('')
const selectedTag = ref('')
const sortBy = ref('default')
const statistics = ref({ userCount: 0, competitionCount: 0, categoryCount: 0 })
const showDropdown = ref(false)

const filterTags = [
  { label: '全部', value: '', icon: '🌟' },
  { label: '工科类', value: '工科类', icon: '🔧' },
  { label: '理科类', value: '理科类', icon: '🔬' },
  { label: '文学', value: '文学', icon: '📖' },
  { label: '法学', value: '法学', icon: '⚖️' },
  { label: '教育学', value: '教育学', icon: '🎓' },
]

const categories = computed(() => {
  const cats = {}
  allCompetitions.value.forEach(c => {
    const title = c.title || '其他'
    if (!cats[title]) {
      cats[title] = { name: title, count: 0, icon: getCategoryIcon(title) }
    }
    cats[title].count++
  })
  return Object.values(cats)
})

const getCategoryIcon = (name) => {
  const icons = {
    '工科类': '🔧', '理科类': '🔬', '文学': '📖', 
    '法学': '⚖️', '教育学': '🎓', '其他': '📚'
  }
  return icons[name] || '📚'
}

const totalParticipants = computed(() => {
  return allCompetitions.value.reduce((sum, c) => sum + (c.careCount || 0), 0)
})

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))
const isAdmin = computed(() => sessionStorage.getItem('role') === 'admin')
const username = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.username || ''
})

const filteredCompetitions = computed(() => {
  let result = [...allCompetitions.value]
  
  if (searchKey.value) {
    const key = searchKey.value.toLowerCase()
    result = result.filter(item => 
      (item.name && item.name.toLowerCase().includes(key)) ||
      (item.title && item.title.toLowerCase().includes(key)) ||
      (item.description && item.description.toLowerCase().includes(key))
    )
  }
  
  if (selectedTag.value) {
    const tag = selectedTag.value.trim()
    result = result.filter(item => item.title && item.title.trim() === tag)
  }
  
  if (sortBy.value === 'popular') {
    result.sort((a, b) => (b.careCount || 0) - (a.careCount || 0))
  }
  
  return result
})

const searchSuggestions = computed(() => {
  if (!searchKey.value) return []
  return filteredCompetitions.value.slice(0, 6)
})

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

const handleSearch = () => {
  showDropdown.value = false
  if (filteredCompetitions.value.length === 1) {
    goToDetail(filteredCompetitions.value[0].id)
  } else {
    const el = document.getElementById('all-competitions')
    if (el) el.scrollIntoView({ behavior: 'smooth' })
  }
}

const handleSelectSuggestion = (id) => {
  showDropdown.value = false
  searchKey.value = ''
  goToDetail(id)
}

const toggleTag = (tag) => {
  selectedTag.value = selectedTag.value === tag ? '' : tag
  // 点击分类后平滑滚动到比赛列表区域
  setTimeout(() => {
    const el = document.getElementById('all-competitions')
    if (el) el.scrollIntoView({ behavior: 'smooth' })
  }, 100)
}

const selectCategory = (name) => {
  selectedTag.value = name
  handleSearch()
}

const clearSearch = () => {
  searchKey.value = ''
  selectedTag.value = ''
}

const scrollToSection = (id) => {
  const el = document.querySelector(`.section`)
  if (el) el.scrollIntoView({ behavior: 'smooth' })
}

const loadData = async () => {
  try {
    const [allRes, hotRes, recommendRes, statsRes] = await Promise.all([
      getAllCompetitionsAPI(),
      getHotCompetitionsAPI(6),
      getRecommendedCompetitionsAPI(6),
      getHomeStatisticsAPI()
    ])
    allCompetitions.value = allRes.data || []
    hotCompetitions.value = hotRes.data || []
    recommendedCompetitions.value = recommendRes.data || []
    statistics.value = statsRes.data || { userCount: 0, competitionCount: 0, categoryCount: 0 }
  } catch (error) {
    console.error(error)
  }
}

const loadUnreadCount = async () => {
  if (!isLoggedIn.value) return
  try {
    const res = await getUnreadCountAPI()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error(error)
  }
}

const goToDetail = (id) => {
  router.push(`/competition/${id}`)
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

onMounted(async () => {
  await loadData()
  loadUnreadCount()
  const category = route.query.category
  if (category) {
    selectedTag.value = category
    setTimeout(() => {
      const el = document.getElementById('all-competitions')
      if (el) el.scrollIntoView({ behavior: 'smooth' })
    }, 300)
  }
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0f0f1a;
  color: #fff;
}

/* 导航栏 */
.header {
  background: rgba(15, 15, 26, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo {
  font-size: 30px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 32px;
}

.nav {
  display: flex;
  gap: 35px;
}

.nav-link {
  color: rgba(255, 255, 255, 0.7);
  font-size: 19px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  padding: 5px 0;
  display: inline-block;
}

.nav-link:hover,
.nav-link.active,
.nav-link.router-link-active {
  color: #fff;
}

.nav-link:hover {
  transform: scale(1.2);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  text-decoration: none;
  transition: transform 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.1);
}

.message-btn {
  position: relative;
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  transition: transform 0.3s ease;
}

.message-btn:hover {
  transform: scale(1.1);
}

.badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #f5576c;
  color: #fff;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  line-height: 14px;
  text-align: center;
}

.btn {
  padding: 8px 20px;
  border-radius: 25px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border: none;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.btn-outline {
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.btn-outline:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
  color: #fff;
}

/* Hero 区域 */
.hero {
  position: relative;
  padding: 80px 0 60px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.15) 0%, 
    rgba(118, 75, 162, 0.15) 50%,
    rgba(245, 87, 108, 0.1) 100%,
    rgba(102, 126, 234, 0.15) 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  z-index: 0;
}

.hero-bg::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 60%;
  height: 100%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, transparent 70%);
  animation: float 6s ease-in-out infinite;
}

.hero-bg::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 50%;
  height: 80%;
  background: radial-gradient(circle, rgba(118, 75, 162, 0.25) 0%, transparent 70%);
  animation: float 8s ease-in-out infinite reverse;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.hero-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.hero-text {
  margin-bottom: 40px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 15px;
  line-height: 1.2;
}

.highlight {
  background: linear-gradient(135deg, #667eea, #f5576c);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 30px;
}

.hero-stats {
  display: flex;
  gap: 50px;
  justify-content: center;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

/* 搜索框 */
.hero-search {
  width: 100%;
  max-width: 700px;
}

.search-box {
  display: flex;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50px;
  padding: 6px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.search-box:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 30px rgba(102, 126, 234, 0.3);
}

.search-icon {
  padding: 12px 15px;
  font-size: 18px;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  color: #fff;
  font-size: 16px;
  outline: none;
  padding: 10px;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.search-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 50px;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.5);
}

.search-box-wrapper {
  position: relative;
}

.search-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: #1a1a2e;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 8px;
  z-index: 100;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  max-height: 320px;
  overflow-y: auto;
}

.search-dropdown-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #fff;
}

.search-dropdown-item:hover {
  background: rgba(102, 126, 234, 0.2);
}

.dropdown-item-name {
  font-size: 14px;
  font-weight: 500;
}

.dropdown-item-tag {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.08);
  padding: 2px 8px;
  border-radius: 8px;
}

.search-dropdown-more {
  padding: 8px 14px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  text-align: center;
}

.filter-tags {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.filter-tag {
  padding: 8px 18px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.filter-tag:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.filter-tag.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-color: transparent;
}

/* 分类导航 */
.categories {
  padding: 30px 0;
  background: rgba(255, 255, 255, 0.03);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.category-grid {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 25px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  min-width: 100px;
}

.category-card:hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateY(-3px);
}

.category-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

.category-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 4px;
}

/* 主内容 */
.main {
  flex: 1;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-title-group {
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.section-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0;
}

.section-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

.more-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: rgba(255, 255, 255, 0.8);
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.more-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: #667eea;
  color: #fff;
}

.sort-select {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 8px 20px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  outline: none;
}

.sort-select option {
  background: #1a1a2e;
  color: #fff;
}

/* 比赛卡片 */
.competition-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 25px;
}

.competition-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.competition-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  border-color: rgba(102, 126, 234, 0.5);
}

.card-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.competition-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.competition-card:hover .competition-img {
  transform: scale(1.1);
}

.placeholder-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.3));
}

.placeholder-icon {
  font-size: 60px;
  opacity: 0.7;
}

.card-badge {
  position: absolute;
  top: 15px;
  left: 15px;
}

.badge-hot {
  background: linear-gradient(135deg, #f5576c, #f093fb);
  color: #fff;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.badge-recommend {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.competition-card:hover .card-overlay {
  opacity: 1;
}

.view-detail {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  padding: 10px 25px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
}

.card-content {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 10px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-tag {
  padding: 4px 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  flex-shrink: 0;
}

.card-desc {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 44px;
}

.card-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.meta-icon {
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-text {
  color: rgba(255, 255, 255, 0.6);
  font-size: 18px;
  margin-bottom: 30px;
}

/* 底部 */
.footer {
  background: rgba(0, 0, 0, 0.3);
  padding: 50px 0 20px;
  margin-top: auto;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.footer-brand {
  max-width: 300px;
}

.footer-logo {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 10px;
  display: block;
}

.footer-desc {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #fff;
  width: 100%;
}

.link-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 20px;
  align-items: center;
}

.link-group a {
  display: inline-block;
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.link-group a:hover {
  color: #667eea;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-bottom p {
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
  margin: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }
  
  .hero-stats {
    gap: 30px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .nav {
    gap: 15px;
    font-size: 13px;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 30px;
  }
  
  .footer-links {
    gap: 40px;
  }
  
  .competition-grid {
    grid-template-columns: 1fr;
  }
}

/* ========== 热门比赛无限滚动 Marquee ========== */
.hot-marquee-section {
  overflow: hidden;
}

.marquee-container {
  overflow: hidden;
  width: calc(100% + 40px);
  margin: 0 -20px;
  padding: 10px 0;
}

.marquee-track {
  display: flex;
  gap: 25px;
  width: max-content;
  animation: marquee-scroll 40s linear infinite;
}

.marquee-container:hover .marquee-track {
  animation-play-state: paused;
}

.marquee-item {
  width: 320px;
  flex-shrink: 0;
}

.marquee-item .competition-card {
  height: 100%;
}

@keyframes marquee-scroll {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

/* 响应式 marquee */
@media (max-width: 768px) {
  .marquee-item {
    width: 280px;
  }
  .marquee-track {
    gap: 15px;
    animation-duration: 25s;
  }
}
</style>
