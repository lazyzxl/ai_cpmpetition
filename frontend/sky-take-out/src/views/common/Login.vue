<template>
  <div class="login-container">
    <div class="image-section">
      <img
        src="https://images.unsplash.com/photo-1562774053-701939374585?w=1200&h=1600&fit=crop"
        alt="校园风景"
        class="bg-image"
      />
      <div class="image-overlay">
        <div class="image-content">
          <h2>Competition</h2>
          <p>追求卓越，引领未来</p>
        </div>
      </div>
    </div>
    <div class="login-section">
      <div class="login-box">
        <div class="logo-area">
          <div class="logo-icon">🏛️</div>
          <h1 class="title">智竞云图</h1>
        </div>
        <p class="subtitle">欢迎回来，请登录您的账户</p>
        <form @submit.prevent="handleLogin">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <input
              v-model="formData.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              required
            />
          </div>
          <div class="form-item">
            <label class="form-label">密码</label>
            <input
              v-model="formData.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              required
            />
          </div>
          <div class="form-actions">
            <router-link to="/forget-password" class="forget-link">忘记密码？</router-link>
          </div>
          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </form>
        <div class="register-link">
          还没有账户？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { loginAPI } from '@/api/login'

const router = useRouter()
const loading = ref(false)
const formData = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await loginAPI(formData.value)
    sessionStorage.setItem('token', res.data.token)
    sessionStorage.setItem('user', JSON.stringify(res.data))
    sessionStorage.setItem('role', res.data.role)
    sessionStorage.setItem('userId', res.data.id)
    if (res.data.role === 'admin') {
      router.push('/admin/dashboard')
    } else {
      router.push('/home')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
}

.image-section {
  flex: 1.2;
  position: relative;
  overflow: hidden;
}

.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(33, 15, 52, 0.85) 0%, rgba(52, 28, 82, 0.7) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-content {
  text-align: center;
  color: #fff;
  transform: translateY(-110px);
}

.image-content h2 {
  font-size: 80px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.image-content p {
  font-size: 28px;
  opacity: 0.9;
  letter-spacing: 6px;
}

.login-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a0a2e 0%, #16082a 50%, #0f051a 100%);
  padding: 40px;
  padding-bottom: 200px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  padding: 50px 45px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.logo-icon {
  font-size: 52px;
  margin-bottom: 14px;
}

.title {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  letter-spacing: 2px;
}

.subtitle {
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 32px;
  font-size: 14px;
}

.form-item {
  margin-bottom: 22px;
}

.form-label {
  display: block;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 10px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 15px 18px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.form-input:focus {
  border-color: #9d4edd;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 0 3px rgba(157, 78, 221, 0.2);
  outline: none;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 26px;
}

.forget-link {
  color: #c77dff;
  font-size: 13px;
}

.forget-link:hover {
  text-decoration: underline;
  color: #9d4edd;
}

.btn-primary {
  background: linear-gradient(135deg, #9d4edd 0%, #7b2cbf 100%);
  border: none;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(157, 78, 221, 0.4);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #b770e8 0%, #9d4edd 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(157, 78, 221, 0.5);
}

.btn-primary:disabled {
  background: rgba(157, 78, 221, 0.5);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-block {
  width: 100%;
  padding: 15px;
  font-size: 15px;
  border-radius: 10px;
  transition: all 0.3s;
}

.register-link {
  text-align: center;
  margin-top: 28px;
  padding-top: 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.register-link a {
  color: #c77dff;
  margin-left: 4px;
}

.register-link a:hover {
  text-decoration: underline;
  color: #9d4edd;
}
</style>