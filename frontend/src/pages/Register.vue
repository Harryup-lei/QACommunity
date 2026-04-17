<template>
  <div class="register-page">
    <div class="ink-bg">
      <div class="ink-pattern"></div>
    </div>
    <div class="register-container">
      <div class="register-card">
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
            <label>邮箱</label>
            <input 
              v-model="form.email" 
              type="email" 
              placeholder="请输入邮箱"
              @keyup.enter="onSubmit"
            />
          </div>
          <div class="input-group">
            <label>密码</label>
            <input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码"
              @keyup.enter="onSubmit"
            />
          </div>
          <div class="input-group">
            <label>确认密码</label>
            <input 
              v-model="form.confirmPassword" 
              type="password" 
              placeholder="请再次输入密码"
              @keyup.enter="onSubmit"
            />
          </div>
          <button class="submit-btn" :loading="loading" @click="onSubmit">
            注 册
          </button>
          <div class="link-section">
            <span>已有账号？</span>
            <router-link to="/login" class="highlight">立即登录</router-link>
          </div>
        </div>
      </div>
      <div class="decorative-poem">
        <p>纸上得来终觉浅</p>
        <p>绝知此事要躬行</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import userService from '@/services/userService'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const onSubmit = async () => {
  if (!form.value.username.trim()) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!form.value.email.trim()) {
    ElMessage.error('请输入邮箱')
    return
  }
  if (!form.value.password.trim()) {
    ElMessage.error('请输入密码')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.error('密码长度不能少于6位')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    await userService.register(
      form.value.username,
      form.value.email,
      form.value.password
    )
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('Register error:', error)
    ElMessage.error('注册失败：用户名或邮箱已存在')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
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
  left: -200px;
  bottom: -200px;
  background: radial-gradient(circle, rgba(196,30,58,0.05) 0%, transparent 70%);
  border-radius: 50%;
}

.register-container {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 60px;
}

.register-card {
  width: 380px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, var(--zh-red) 0%, var(--zh-red-dark) 100%);
  padding: 40px 32px;
  text-align: center;
  position: relative;
}

.card-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 4px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 4'%3E%3Cpath d='M0 4 Q25 0 50 4 T100 4' fill='none' stroke='white' stroke-width='2'/%3E%3C/svg%3E") repeat-x;
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
  background: rgba(255,255,255,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255,255,255,0.3);
  text-decoration: none;
}

.logo-icon span {
  font-family: var(--font-serif);
  font-size: 24px;
  color: white;
  font-weight: 600;
}

.logo-title {
  font-family: var(--font-serif);
  font-size: 28px;
  color: white;
  font-weight: 600;
}

.logo-subtitle {
  font-family: var(--font-serif);
  font-size: 14px;
  color: rgba(255,255,255,0.7);
  letter-spacing: 4px;
}

.form-section {
  padding: 32px;
}

.input-group {
  margin-bottom: 16px;
}

.input-group label {
  display: block;
  font-size: 14px;
  color: var(--zh-ink-light);
  margin-bottom: 8px;
  font-family: var(--font-serif);
}

.input-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  font-size: 15px;
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
  background: linear-gradient(135deg, var(--zh-red) 0%, var(--zh-red-dark) 100%);
  color: white;
  border: none;
  border-radius: 2px;
  font-size: 16px;
  font-family: var(--font-serif);
  letter-spacing: 4px;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 8px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(196,30,58,0.3);
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
  color: var(--zh-red);
  cursor: pointer;
  text-decoration: underline;
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
  .register-container {
    flex-direction: column;
    gap: 32px;
  }
  
  .decorative-poem {
    writing-mode: horizontal-tb;
    border-left: none;
    border-top: 1px solid var(--zh-paper-dark);
    padding: 20px 0 0;
  }
  
  .register-card {
    width: 100%;
    max-width: 380px;
    margin: 0 16px;
  }
}
</style>