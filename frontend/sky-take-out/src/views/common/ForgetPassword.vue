<template>
  <div class="forget-container">
    <div class="forget-box">
      <h1 class="title">找回密码</h1>
      <p class="subtitle">请输入您的注册信息</p>
      <form @submit.prevent="handleForget">
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
            v-model="formData.phone"
            type="tel"
            class="form-input"
            placeholder="请输入手机号"
            required
          />
        </div>
        <div class="form-item">
          <input
            v-model="formData.password"
            type="password"
            class="form-input"
            placeholder="请输入新密码"
            required
          />
        </div>
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '处理中...' : '重置密码' }}
        </button>
      </form>
      <div class="back-link">
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { forgetPasswordAPI } from '@/api/login'

const router = useRouter()
const loading = ref(false)
const formData = ref({
  username: '',
  phone: '',
  password: ''
})

const handleForget = async () => {
  loading.value = true
  try {
    await forgetPasswordAPI(formData.value)
    ElMessage.success('密码重置成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forget-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.forget-box {
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

.back-link {
  text-align: center;
  margin-top: 20px;
}

.back-link a {
  color: #667eea;
}
</style>