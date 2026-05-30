<template>
  <div class="care-page">
    <header class="header">
      <div class="container header-content">
        <h1 class="logo">智竞云图</h1>
        <nav class="nav">
          <router-link to="/home" class="nav-link">首页</router-link>
          <router-link to="/user/profile" class="nav-link">个人中心</router-link>
        </nav>
        <div class="user-actions">
          <span class="username">{{ username }}</span>
          <button @click="handleLogout" class="btn btn-text">退出</button>
        </div>
      </div>
    </header>

    <main class="main container">
      <div class="page-header">
        <h2 class="page-title">我的关注</h2>
        <button
          v-if="selectedList.length > 0"
          @click="handleBatchDelete"
          class="btn btn-danger"
        >
          批量取消关注
        </button>
      </div>

      <div class="care-list" v-if="pageResult.records?.length">
        <div
          v-for="item in pageResult.records"
          :key="item.id"
          class="care-item card"
          :class="{ selected: selectedList.includes(item.id) }"
          @click="toggleSelect(item.id)"
        >
          <div class="care-checkbox">
            <input
              type="checkbox"
              :checked="selectedList.includes(item.id)"
              @click.stop
              @change="toggleSelect(item.id)"
            />
          </div>
          <div class="care-content" @click.stop="goToDetail(item.competitionId)">
            <h3 class="care-title">{{ item.competitionName }}</h3>
            <div class="care-info">
              <span>截止时间：{{ item.deadline }}</span>
              <span>状态：{{ item.status }}</span>
            </div>
          </div>
          <div class="care-actions">
            <button @click.stop="handleDelete([item.id])" class="btn btn-text">
              取消关注
            </button>
          </div>
        </div>
      </div>
      <div v-else class="empty-tip">
        <p>暂无关注的比赛</p>
        <router-link to="/home" class="btn btn-primary">去首页看看</router-link>
      </div>

      <div class="pagination" v-if="pageResult.total > pageResult.size">
        <button
          @click="handlePageChange(pageResult.current - 1)"
          :disabled="pageResult.current === 1"
          class="btn btn-text"
        >
          上一页
        </button>
        <span class="page-info">{{ pageResult.current }} / {{ totalPages }}</span>
        <button
          @click="handlePageChange(pageResult.current + 1)"
          :disabled="pageResult.current >= totalPages"
          class="btn btn-text"
        >
          下一页
        </button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCareListAPI, deleteCareAPI } from '@/api/care'
import { logoutAPI } from '@/api/login'

const router = useRouter()
const pageResult = reactive({
  records: [],
  current: 1,
  size: 10,
  total: 0
})
const selectedList = ref([])

const username = computed(() => {
  const user = JSON.parse(sessionStorage.getItem('user') || '{}')
  return user.username || ''
})

const totalPages = computed(() => Math.ceil(pageResult.total / pageResult.size))

const loadData = async () => {
  try {
    const res = await getCareListAPI({
      current: pageResult.current,
      size: pageResult.size
    })
    Object.assign(pageResult, res.data)
  } catch (error) {
    console.error(error)
  }
}

const toggleSelect = (id) => {
  const index = selectedList.value.indexOf(id)
  if (index === -1) {
    selectedList.value.push(id)
  } else {
    selectedList.value.splice(index, 1)
  }
}

const handleDelete = async (ids) => {
  try {
    await deleteCareAPI(ids)
    ElMessage.success('取消成功')
    selectedList.value = []
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要取消关注所选比赛吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await handleDelete(selectedList.value)
  } catch {
  }
}

const handlePageChange = (page) => {
  pageResult.current = page
  loadData()
}

const goToDetail = (id) => {
  router.push(`/competition/${id}`)
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
.care-page {
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
}

.care-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.care-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.care-item.selected {
  background-color: #f0f0f0;
}

.care-checkbox {
  flex-shrink: 0;
}

.care-content {
  flex: 1;
  cursor: pointer;
}

.care-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.care-info {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 13px;
}

.care-actions {
  flex-shrink: 0;
}

.empty-tip {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 10px;
}

.empty-tip p {
  color: #999;
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-info {
  color: #666;
}
</style>