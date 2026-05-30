<template>
  <div class="profile-page">
    <header class="header">
      <div class="container header-content">
        <h1 class="logo">智竞云图</h1>
        <nav class="nav">
          <router-link to="/home" class="nav-link">首页</router-link>
          <router-link to="/user/care" class="nav-link">关注比赛</router-link>
          <router-link to="/user/follow" class="nav-link">关注用户</router-link>
          <router-link to="/user/chat" class="nav-link">聊天</router-link>
        </nav>
        <div class="user-actions">
          <span class="username">{{ user.username }}</span>
          <button @click="handleLogout" class="btn btn-text">退出</button>
        </div>
      </div>
    </header>

    <main class="main container">
      <div class="profile-content">
        <!-- 用户信息卡片 -->
        <div class="profile-card">
          <div class="profile-header">
            <div class="avatar-wrapper">
              <div class="avatar">
                {{ user.username?.charAt(0).toUpperCase() }}
              </div>
              <div class="role-badge" :class="user.role === 'admin' ? 'admin' : 'user'">
                {{ user.role === 'admin' ? '管理员' : '普通用户' }}
              </div>
            </div>
            <div class="user-info">
              <h2 class="username">{{ user.username }}</h2>
              <p class="user-id">ID: {{ user.id }}</p>
              <div class="stats-row">
                <div class="stat-item">
                  <span class="stat-value">{{ careCount }}</span>
                  <span class="stat-label">关注比赛</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <span class="stat-value">0</span>
                  <span class="stat-label">参与活动</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <span class="stat-value">0</span>
                  <span class="stat-label">获得荣誉</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 个人信息展示卡片 -->
        <div class="content-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="icon">👤</span>
              个人信息
            </h3>
            <button @click="showEditModal = true" class="btn btn-primary btn-sm">
              <span class="btn-icon">✏️</span>
              修改信息
            </button>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ user.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">{{ user.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">专业</span>
              <span class="info-value">{{ user.profession || '未设置' }}</span>
            </div>
          </div>
        </div>

        <!-- 修改密码入口 -->
        <div class="content-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="icon">🔐</span>
              账户安全
            </h3>
            <button @click="showPasswordModal = true" class="btn btn-secondary btn-sm">
              <span class="btn-icon">🔑</span>
              修改密码
            </button>
          </div>
          <div class="security-info">
            <p>为了您的账户安全，建议定期修改密码</p>
          </div>
        </div>

        <!-- 快捷操作卡片 -->
        <div class="content-card quick-actions">
          <div class="card-header">
            <h3 class="card-title">
              <span class="icon">⚡</span>
              快捷操作
            </h3>
          </div>
          <div class="action-grid">
            <button class="action-btn" @click="navigateTo('/user/care')">
              <span class="action-icon">❤️</span>
              <span class="action-text">关注比赛</span>
            </button>
            <button class="action-btn" @click="navigateTo('/user/follow')">
              <span class="action-icon">👥</span>
              <span class="action-text">关注用户</span>
            </button>
            <button class="action-btn" @click="navigateTo('/user/chat')">
              <span class="action-icon">💬</span>
              <span class="action-text">聊天</span>
            </button>
            <button class="action-btn" @click="navigateTo('/home')">
              <span class="action-icon">🏆</span>
              <span class="action-text">浏览比赛</span>
            </button>
            <button class="action-btn" v-if="user.role === 'admin'" @click="navigateTo('/admin/dashboard')">
              <span class="action-icon">🛠️</span>
              <span class="action-text">管理后台</span>
            </button>
            <button class="action-btn danger" @click="handleLogout">
              <span class="action-icon">🚪</span>
              <span class="action-text">安全退出</span>
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>© 2026 智竞云图 - 高校校园平台</p>
      </div>
    </footer>

    <!-- 修改信息弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>修改个人信息</h3>
          <button @click="showEditModal = false" class="modal-close">×</button>
        </div>
        <form @submit.prevent="handleUpdate" class="modal-form">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <input
              v-model="formData.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-item">
            <label class="form-label">手机号</label>
            <input
              v-model="formData.phone"
              type="tel"
              class="form-input"
              placeholder="请输入手机号"
            />
          </div>
          <div class="form-item">
            <label class="form-label">专业</label>
            <input
              v-model="formData.profession"
              type="text"
              class="form-input"
              placeholder="请输入专业"
            />
          </div>
          <div class="modal-actions">
            <button type="button" @click="showEditModal = false" class="btn btn-cancel">取消</button>
            <button type="submit" class="btn btn-primary">保存修改</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="showPasswordModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>修改密码</h3>
          <button @click="showPasswordModal = false" class="modal-close">×</button>
        </div>
        <form @submit.prevent="handlePasswordUpdate" class="modal-form">
          <div class="form-item">
            <label class="form-label">原密码</label>
            <input
              v-model="passwordData.oldPassword"
              type="password"
              class="form-input"
              placeholder="请输入原密码"
            />
          </div>
          <div class="form-item">
            <label class="form-label">新密码</label>
            <input
              v-model="passwordData.newPassword"
              type="password"
              class="form-input"
              placeholder="请输入新密码"
            />
          </div>
          <div class="form-item">
            <label class="form-label">确认新密码</label>
            <input
              v-model="passwordData.checkPassword"
              type="password"
              class="form-input"
              placeholder="请再次输入新密码"
            />
          </div>
          <div class="modal-actions">
            <button type="button" @click="showPasswordModal = false" class="btn btn-cancel">取消</button>
            <button type="submit" class="btn btn-secondary">修改密码</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserInfoAPI, updateUserInfoAPI, updatePasswordAPI } from '@/api/user'
import { logoutAPI } from '@/api/login'
import { getCareListAPI } from '@/api/care'

const router = useRouter()
const user = ref({})
const careCount = ref(0)
const showEditModal = ref(false)
const showPasswordModal = ref(false)

const formData = reactive({
  username: '',
  phone: '',
  profession: ''
})

const passwordData = reactive({
  oldPassword: '',
  newPassword: '',
  checkPassword: ''
})

const loadUserInfo = async () => {
  try {
    const userId = sessionStorage.getItem('userId')
    if (!userId) {
      router.push('/login')
      return
    }
    const res = await getUserInfoAPI(userId)
    user.value = res.data
    Object.assign(formData, {
      username: res.data.username,
      phone: res.data.phone || '',
      profession: res.data.profession || ''
    })
  } catch (error) {
    console.error(error)
  }
}

const loadCareCount = async () => {
  try {
    const res = await getCareListAPI({ page: 1, pageSize: 1 })
    careCount.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const handleUpdate = async () => {
  try {
    await updateUserInfoAPI({
      id: user.value.id,
      ...formData
    })
    ElMessage.success('修改成功')
    showEditModal.value = false
    loadUserInfo()
  } catch (error) {
    console.error(error)
  }
}

const handlePasswordUpdate = async () => {
  if (!passwordData.oldPassword) {
    ElMessage.error('请输入原密码')
    return
  }
  if (!passwordData.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordData.newPassword !== passwordData.checkPassword) {
    ElMessage.error('两次密码输入不一致')
    return
  }
  try {
    await updatePasswordAPI({
      id: user.value.id,
      oldPassword: passwordData.oldPassword,
      newPassword: passwordData.newPassword
    })
    ElMessage.success('密码修改成功')
    showPasswordModal.value = false
    passwordData.oldPassword = ''
    passwordData.newPassword = ''
    passwordData.checkPassword = ''
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

const navigateTo = (path) => {
  router.push(path)
}

onMounted(() => {
  loadUserInfo()
  loadCareCount()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 0%);
  padding-bottom: 80px;
}

/* 头部样式 */
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
.nav-link.router-link-active,
.nav-link.router-link-exact-active {
  color: #667eea;
  font-size: 18px;
  font-weight: 600;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.nav-link:hover::after {
  width: 100%;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-actions .username {
  color: #667eea;
  font-weight: 600;
  font-size: 15px;
}

/* 主内容区域 */
.main {
  padding: 40px 20px;
}

.profile-content {
  max-width: 800px;
  margin: 0 auto;
}

/* 用户信息卡片 */
.profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  margin-bottom: 25px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 48px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.5);
}

.role-badge {
  position: absolute;
  bottom: 5px;
  right: 5px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.role-badge.user {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.role-badge.admin {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.user-info {
  flex: 1;
}

.user-info .username {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 5px;
}

.user-id {
  color: #999;
  font-size: 14px;
  margin-bottom: 20px;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 3px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e0e0e0;
}

/* 内容卡片 */
.content-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.content-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 25px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon {
  font-size: 22px;
}

/* 信息展示网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.info-item {
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
}

.info-label {
  display: block;
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 安全信息 */
.security-info {
  color: #666;
  font-size: 14px;
}

/* 按钮样式 */
.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-sm {
  padding: 8px 16px;
  font-size: 13px;
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

.btn-secondary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.4);
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.5);
}

.btn-text {
  background: transparent;
  color: #667eea;
  padding: 8px 16px;
  font-weight: 500;
}

.btn-text:hover {
  color: #764ba2;
  background: rgba(102, 126, 234, 0.1);
}

.btn-cancel {
  background: #f1f5f9;
  color: #666;
  border: 1px solid #e2e8f0;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-icon {
  font-size: 16px;
}

/* 快捷操作卡片 */
.quick-actions .action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 25px 15px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
}

.action-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.action-btn:hover .action-icon,
.action-btn:hover .action-text {
  color: #fff;
}

.action-icon {
  font-size: 32px;
  transition: all 0.3s ease;
}

.action-text {
  font-size: 13px;
  font-weight: 500;
  color: #555;
  transition: all 0.3s ease;
}

.action-btn.danger:hover {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 10px 30px rgba(239, 68, 68, 0.3);
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
  backdrop-filter: blur(5px);
}

.modal-content {
  background: #fff;
  border-radius: 20px;
  width: 90%;
  max-width: 500px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalIn 0.3s ease;
}

@keyframes modalIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
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
  padding: 0;
  line-height: 1;
}

.modal-close:hover {
  color: #666;
}

.modal-form {
  padding: 24px;
}

.modal-form .form-item {
  margin-bottom: 20px;
}

.modal-form .form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  margin-bottom: 10px;
}

.modal-form .form-input {
  width: 100%;
  padding: 14px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.modal-form .form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 25px;
}

/* 页脚 */
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

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .stats-row {
    justify-content: center;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .nav {
    gap: 20px;
  }

  .card-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>