<template>
  <div class="register-container">
    <div class="register-box">
      <h1 class="title">注册账户</h1>
      <p class="subtitle">加入智竞云图</p>
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <input
            v-model="formData.username"
            type="text"
            class="form-input"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="formData.password"
            type="password"
            class="form-input"
            placeholder="请输入密码"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="formData.checkPassword"
            type="password"
            class="form-input"
            placeholder="请确认密码"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="formData.phone"
            type="tel"
            class="form-input"
            placeholder="请输入手机号"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="formData.profession"
            type="text"
            class="form-input"
            placeholder="请输入专业"
          />
        </div>
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <div class="login-link">
        已有账户？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerAPI } from '@/api/login'

const router = useRouter()
const loading = ref(false)
const formData = ref({
  username: '',
  password: '',
  checkPassword: '',
  phone: '',
  profession: ''
})

const handleRegister = async () => {
  if (formData.value.password !== formData.value.checkPassword) {
    ElMessage.error('两次密码输入不一致')
    return
  }
  loading.value = true
  try {
    await registerAPI(formData.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.title {
  text-align: center;
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.subtitle {
  text-align: center;
  color: #999;
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 16px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-input:focus {
  border-color: #667eea;
}

.btn-block {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  border-radius: 6px;
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #667eea;
  margin-left: 5px;
}
</style>