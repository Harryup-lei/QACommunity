<template>
  <div class="login-page">
    <div class="ink-bg">
      <div class="ink-pattern"></div>
    </div>
    <div class="login-container">
      <div class="login-card">
        <div class="card-header">
          <div class="logo-section">
            <router-link to="/" class="logo-icon">
              <span>问</span>
            </router-link>
            <h1 class="logo-title">QA 社区</h1>
          </div>
          <p class="logo-subtitle">墨韵 · 问道</p>
        </div>
        <div class="form-section">
          <div class="input-group">
            <label>用户名</label>
            <input 
              v-model="form.username" 
              type="text" 
              placeholder="请输入用户名"
              @keyup.enter="onSubmit"
            />
          </div>
          <div class="input-group">
            <label>密码</label>
            <input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码"
              show-password
              @keyup.enter="onSubmit"
            />
          </div>
          <button class="submit-btn" :loading="loading" @click="onSubmit">
            登录
          </button>
          <div class="link-section">
            <span>还没有账号？</span>
            <router-link to="/register" class="highlight">立即注册</router-link>
          </div>
        </div>
      </div>
      <div class="decorative-poem">
        <p>书山有路勤为径</p>
        <p>学海无涯苦作舟</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import userService from '@/services/userService'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const onSubmit = async () => {
  if (!form.value.username.trim()) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!form.value.password.trim()) {
    ElMessage.error('请输入密码')
    return
  }

  loading.value = true
  try {
    // Result 包装格式: { code: 0, message: "success", data: "token-string" }
    const result = await userService.login(form.value.username, form.value.password)

    // 安全检查：确保返回的是有效对象
    if (!result || result.code !== 0 || !result.data) {
      throw new Error(result?.message || '登录失败')
    }

    const token = result.data

    // 解析 JWT payload 获取 userId (第二部分)
    const payload = JSON.parse(atob(token.split('.')[1]))
    const userId = payload.sub

    localStorage.setItem('token', token)
    localStorage.setItem('userId', userId)

    userStore.setToken(token)

    const user = await userService.getUserInfo(userId)
    userStore.setUser(user)

    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error(error.message || '登录失败：用户名或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--zh-paper);
  position: relative;
  overflow: hidden;
}

.ink-bg {
  position: absolute;
  inset: 0;
  opacity: 0.4;
  pointer-events: none;
}

.ink-pattern {
  position: absolute;
  width: 600px;
  height: 600px;
  right: -200px;
  top: -200px;
  background: radial-gradient(circle, rgba(196,30,58,0.05) 0%, transparent 70%);
  border-radius: 50%;
}

.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 60px;
}

.login-card {
  width: 380px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, #8B0000 0%, #5C0000 100%);
  padding: 40px 32px;
  text-align: center;
  position: relative;
  border-bottom: 3px solid #D4AF37;
}

.card-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Ccircle cx='50' cy='50' r='45' fill='none' stroke='rgba(255,255,255,0.03)' stroke-width='1'/%3E%3C/svg%3E");
  opacity: 0.5;
  pointer-events: none;
}

.card-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 4px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 4'%3E%3Cpath d='M0 4 Q25 0 50 4 T100 4' fill='none' stroke='%23D4AF37' stroke-width='2'/%3E%3C/svg%3E") repeat-x;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #D4AF37 0%, #B8962E 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid #D4AF37;
  box-shadow: 0 2px 8px rgba(212, 175, 55, 0.4);
  text-decoration: none;
}

.logo-icon span {
  font-family: var(--font-serif);
  font-size: 24px;
  color: #5C0000;
  font-weight: 600;
}

.logo-title {
  font-family: var(--font-serif);
  font-size: 28px;
  color: #D4AF37;
  font-weight: 600;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

.logo-subtitle {
  font-family: var(--font-serif);
  font-size: 14px;
  color: #D4AF37;
  letter-spacing: 4px;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

.form-section {
  padding: 32px;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  font-size: 14px;
  color: #5C0000;
  margin-bottom: 8px;
  font-family: var(--font-serif);
  font-weight: 600;
}

.input-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  font-size: 15px;
  color: #2C2C2C;
  background: #FDFCFB;
  transition: all 0.2s;
  box-sizing: border-box;
}

.input-group input:focus {
  outline: none;
  border-color: var(--zh-red);
  background: white;
  box-shadow: 0 0 0 3px rgba(196,30,58,0.1);
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #8B0000 0%, #5C0000 100%);
  color: #D4AF37;
  border: 2px solid #D4AF37;
  border-radius: 4px;
  font-size: 16px;
  font-family: var(--font-serif);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 8px;
}

.submit-btn:hover {
  background: #D4AF37;
  color: #5C0000;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.link-section {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--zh-paper-dark);
  font-size: 14px;
  color: var(--zh-ink-lighter);
}

.link-section .highlight {
  color: #D4AF37;
  cursor: pointer;
  text-decoration: underline;
  font-weight: 600;
  margin-left: 4px;
}

.decorative-poem {
  writing-mode: vertical-rl;
  text-orientation: mixed;
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--zh-ink);
  letter-spacing: 6px;
  line-height: 2;
  opacity: 0.6;
  padding: 20px;
  border-left: 1px solid var(--zh-paper-dark);
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    gap: 32px;
  }
  
  .decorative-poem {
    writing-mode: horizontal-tb;
    border-left: none;
    border-top: 1px solid var(--zh-paper-dark);
    padding: 20px 0 0;
  }
  
  .login-card {
    width: 100%;
    max-width: 380px;
    margin: 0 16px;
  }
}
</style>