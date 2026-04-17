<template>
  <div class="app">
    <!-- 头部导航 -->
    <header class="zh-header" :class="{ 'header-scrolled': isScrolled }">
      <div class="header-container">
        <!-- Logo -->
        <router-link to="/" class="logo">
          <div class="logo-icon">
            <span class="logo-char">问</span>
          </div>
          <span class="logo-text">QA 社区</span>
        </router-link>

        <!-- 桌面端导航 -->
        <nav class="desktop-nav">
          <router-link to="/questions" class="nav-item">
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M8 10h8M8 14h4M4 4h16v16H4z" stroke-width="2" stroke-linecap="round"/>
            </svg>
            问题
          </router-link>
          <router-link to="/tags" class="nav-item">
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M7 7h10l-4 4H9z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="7" cy="7" r="2" stroke-width="2"/>
            </svg>
            标签
          </router-link>
          <router-link to="/ranking" class="nav-item">
            <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M12 3v18M3 12h18" stroke-width="2" stroke-linecap="round"/>
            </svg>
            排行榜
          </router-link>
        </nav>

        <!-- 用户区域 -->
        <div class="header-actions">
          <!-- 通知按钮 -->
          <button v-if="userStore.user" class="icon-btn" @click="toggleNotifications">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke-width="2" stroke-linecap="round"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
          </button>

          <!-- 发布按钮 -->
          <router-link v-if="userStore.user" to="/publish-question" class="zh-btn zh-btn-primary">
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M12 5v14M5 12h14" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span class="btn-text">提问</span>
          </router-link>

          <!-- 用户菜单 -->
          <div v-if="userStore.user" class="user-menu" @click="toggleUserMenu">
            <img :src="userStore.user.avatar || '/default-avatar.png'" alt="avatar" class="avatar"/>
            <span class="username">{{ userStore.user.username }}</span>
            <svg class="dropdown-icon" :class="{ 'open': showUserMenu }" viewBox="0 0 24 24" fill="none">
              <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>

            <!-- 用户下拉菜单 -->
            <transition name="dropdown">
              <div v-show="showUserMenu" class="user-dropdown">
                <router-link to="/profile" class="dropdown-item">
                  <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <circle cx="12" cy="8" r="4" stroke-width="2"/>
                    <path d="M6 20v-2a6 6 0 0 1 12 0v2" stroke-width="2"/>
                  </svg>
                  个人中心
                </router-link>
                <router-link to="/profile?tab=questions" class="dropdown-item">
                  <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke-width="2"/>
                    <path d="M14 2v6h6" stroke-width="2"/>
                  </svg>
                  我的提问
                </router-link>
                <router-link to="/profile?tab=answers" class="dropdown-item">
                  <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
                  </svg>
                  我的回答
                </router-link>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item danger" @click="onLogout">
                  <svg class="item-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke-width="2"/>
                    <path d="M16 17l5-5-5-5M21 12H9" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                  退出登录
                </button>
              </div>
            </transition>
          </div>

          <!-- 登录注册按钮 -->
          <template v-else>
            <router-link to="/login" class="zh-btn zh-btn-outline">登录</router-link>
            <router-link to="/register" class="zh-btn zh-btn-primary">注册</router-link>
          </template>

          <!-- 移动端菜单按钮 -->
          <button class="mobile-menu-btn" @click="toggleMobileMenu">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path v-if="!showMobileMenu" d="M4 6h16M4 12h16M4 18h16" stroke-width="2" stroke-linecap="round"/>
              <path v-else d="M6 6l12 12M6 18L18 6" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- 移动端导航 -->
      <transition name="slide">
        <div v-show="showMobileMenu" class="mobile-nav">
          <nav class="mobile-nav-list">
            <router-link to="/questions" class="mobile-nav-item">问题</router-link>
            <router-link to="/tags" class="mobile-nav-item">标签</router-link>
            <router-link to="/ranking" class="mobile-nav-item">排行榜</router-link>
            <router-link to="/profile" class="mobile-nav-item">个人中心</router-link>
          </nav>
        </div>
      </transition>
    </header>

    <!-- 通知面板 -->
    <transition name="slide-fade">
      <div v-if="showNotifications" class="notification-panel" @click.self="toggleNotifications">
        <div class="notification-content">
          <div class="notification-header">
            <h3>通知</h3>
            <button class="mark-read" @click="markAllAsRead">全部已读</button>
          </div>
          <div class="notification-list">
            <div v-if="notifications.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke-width="2"/>
              </svg>
              <p>暂无通知</p>
            </div>
            <div v-for="notif in notifications" :key="notif.id" class="notification-item" :class="{ unread: !notif.read }">
              <div class="notif-icon" :class="notif.type">
                <svg v-if="notif.type === 'answer'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
                </svg>
                <svg v-else-if="notif.type === 'vote'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
                </svg>
                <svg v-else-if="notif.type === 'comment'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke-width="2"/>
                </svg>
              </div>
              <div class="notif-content">
                <p class="notif-text">{{ notif.message }}</p>
                <span class="notif-time">{{ formatTime(notif.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="zh-footer">
      <div class="footer-container">
        <div class="footer-section">
          <div class="footer-logo">
            <span class="footer-char">问</span>
          </div>
          <p class="footer-desc">分享知识，共同成长</p>
        </div>
        <div class="footer-section">
          <h4>社区</h4>
          <router-link to="/questions">问题</router-link>
          <router-link to="/tags">标签</router-link>
          <router-link to="/ranking">排行榜</router-link>
        </div>
        <div class="footer-section">
          <h4>关于</h4>
          <a href="#">关于我们</a>
          <a href="#">使用条款</a>
          <a href="#">隐私政策</a>
        </div>
        <div class="footer-section">
          <h4>链接</h4>
          <a href="https://github.com/Harryup-lei" target="_blank">GitHub</a>
          <a href="#" target="_blank">反馈</a>
          <a href="#" target="_blank">API</a>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 QA 社区。All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useWindowScroll } from '@vueuse/core'

const router = useRouter()
const userStore = useUserStore()
const { y } = useWindowScroll()

// 状态
const isScrolled = ref(false)
const showUserMenu = ref(false)
const showMobileMenu = ref(false)
const showNotifications = ref(false)
const unreadCount = ref(0)
const notifications = ref([])

// 滚动监听
const checkScroll = () => {
  isScrolled.value = y.value > 20
}

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
  showNotifications.value = false
}

// 切换通知面板
const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
  showUserMenu.value = false
}

// 切换移动端菜单
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

// 登出
const onLogout = () => {
  userStore.logout()
  router.push('/')
  showUserMenu.value = false
}

// 标记全部已读
const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true)
  unreadCount.value = 0
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 7) return `${days} 天前`
  return date.toLocaleDateString('zh-CN')
}

// 点击外部关闭菜单
const handleClickOutside = (e) => {
  if (!e.target.closest('.user-menu')) {
    showUserMenu.value = false
  }
  if (!e.target.closest('.notification-panel')) {
    showNotifications.value = false
  }
}

onMounted(() => {
  checkScroll()
  window.addEventListener('click', handleClickOutside)

  // 模拟通知数据
  notifications.value = [
    { id: 1, type: 'answer', message: '有人回答了你的问题', read: false, createdAt: new Date() },
    { id: 2, type: 'vote', message: '你的回答获得了点赞', read: false, createdAt: new Date(Date.now() - 3600000) },
  ]
  unreadCount.value = notifications.value.filter(n => !n.read).length
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 头部样式 */
.zh-header {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  background: rgba(245, 243, 232, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
}

.header-scrolled {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: var(--shadow-sm);
  border-bottom-color: var(--zh-paper-dark);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 24px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: var(--zh-ink);
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--zh-red) 0%, var(--zh-red-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(196, 30, 58, 0.3);
}

.logo-char {
  font-family: var(--font-serif);
  font-size: 20px;
  color: white;
  font-weight: 600;
}

.logo-text {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  color: var(--zh-ink);
}

/* 桌面导航 */
.desktop-nav {
  display: flex;
  gap: 8px;
  flex: 1;
  margin-left: 20px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius-md);
  color: var(--zh-ink-light);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: var(--zh-paper-dark);
  color: var(--zh-ink);
}

.nav-item.router-link-active {
  background: var(--info-light);
  color: var(--zh-blue);
}

.nav-icon {
  width: 18px;
  height: 18px;
}

/* 头部操作区 */
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-btn {
  position: relative;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.icon-btn:hover {
  background: var(--zh-paper-dark);
}

.icon-btn .icon {
  width: 22px;
  height: 22px;
  color: var(--zh-ink-light);
}

.icon-btn .badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  border-radius: 9px;
  background: var(--zh-red);
  color: white;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 用户菜单 */
.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px 6px 6px;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: background 0.2s ease;
}

.user-menu:hover {
  background: var(--zh-paper-dark);
}

.user-menu .avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--zh-paper-dark);
}

.user-menu .username {
  font-size: 14px;
  font-weight: 500;
  color: var(--zh-ink);
}

.user-menu .dropdown-icon {
  width: 16px;
  height: 16px;
  color: var(--zh-ink-light);
  transition: transform 0.3s ease;
}

.user-menu .dropdown-icon.open {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 200px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--zh-paper-dark);
  overflow: hidden;
  z-index: var(--z-dropdown);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  color: var(--zh-ink);
  text-decoration: none;
  transition: background 0.2s ease;
  border: none;
  background: transparent;
  width: 100%;
  cursor: pointer;
  font-size: 14px;
}

.dropdown-item:hover {
  background: var(--zh-paper-dark);
}

.dropdown-item.danger {
  color: var(--zh-red);
}

.dropdown-item .item-icon {
  width: 18px;
  height: 18px;
  color: currentColor;
}

.dropdown-divider {
  height: 1px;
  background: var(--zh-paper-dark);
  margin: 4px 0;
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  align-items: center;
  justify-content: center;
}

.mobile-menu-btn .icon {
  width: 24px;
  height: 24px;
  color: var(--zh-ink);
}

/* 移动端导航 */
.mobile-nav {
  position: absolute;
  top: 64px;
  left: 0;
  right: 0;
  background: white;
  border-bottom: 1px solid var(--zh-paper-dark);
  box-shadow: var(--shadow-md);
}

.mobile-nav-list {
  display: flex;
  flex-direction: column;
  padding: 12px 20px;
}

.mobile-nav-item {
  padding: 12px 16px;
  border-radius: var(--radius-md);
  color: var(--zh-ink);
  text-decoration: none;
  font-weight: 500;
  transition: background 0.2s ease;
}

.mobile-nav-item:hover {
  background: var(--zh-paper-dark);
}

/* 通知面板 */
.notification-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: calc(var(--z-modal) - 1);
  display: flex;
  justify-content: flex-end;
}

.notification-content {
  width: 100%;
  max-width: 400px;
  background: white;
  box-shadow: var(--shadow-xl);
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--zh-paper-dark);
}

.notification-header h3 {
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--zh-ink);
}

.mark-read {
  background: transparent;
  border: none;
  color: var(--zh-blue);
  cursor: pointer;
  font-size: 13px;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--zh-paper-dark);
  transition: background 0.2s ease;
}

.notification-item:hover {
  background: var(--zh-paper-dark);
}

.notification-item.unread {
  background: var(--info-light);
}

.notif-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notif-icon.answer {
  background: var(--zh-blue-light);
  color: white;
}

.notif-icon.vote {
  background: var(--zh-jade);
  color: white;
}

.notif-icon.comment {
  background: var(--zh-gold);
  color: white;
}

.notif-icon svg {
  width: 18px;
  height: 18px;
}

.notif-content {
  flex: 1;
}

.notif-text {
  font-size: 14px;
  color: var(--zh-ink);
  margin-bottom: 4px;
}

.notif-time {
  font-size: 12px;
  color: var(--zh-ink-lighter);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--zh-ink-lighter);
}

.empty-state svg {
  width: 48px;
  height: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

/* 主内容区 */
.main-content {
  min-height: calc(100vh - 64px - 200px);
  padding: 24px 20px;
}

/* 页脚 */
.zh-footer {
  background: var(--zh-ink);
  color: rgba(255, 255, 255, 0.8);
  padding: 40px 20px 20px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-logo {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--zh-red);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.footer-char {
  font-family: var(--font-serif);
  font-size: 24px;
  color: white;
}

.footer-desc {
  font-size: 14px;
  opacity: 0.8;
}

.footer-section h4 {
  color: white;
  font-family: var(--font-serif);
  font-size: 16px;
  margin-bottom: 16px;
}

.footer-section a,
.footer-section router-link {
  display: block;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  padding: 6px 0;
  font-size: 14px;
  transition: color 0.2s ease;
}

.footer-section a:hover,
.footer-section router-link:hover {
  color: white;
}

.footer-bottom {
  max-width: 1400px;
  margin: 32px auto 0;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
  font-size: 13px;
  opacity: 0.6;
}

/* 动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式 */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .user-menu .username {
    display: none;
  }

  .btn-text {
    display: none;
  }

  .footer-container {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .main-content {
    padding: 16px 12px;
  }
}
</style>
