<template>
  <div class="profile-page">
    <div class="page-container">
      <div class="profile-header">
        <div class="header-cover"></div>
        <div class="header-content">
          <img :src="user?.avatar || '/default-avatar.png'" class="profile-avatar"/>
          <div class="profile-info">
            <h1 class="profile-name">{{ user?.username || '用户' }}</h1>
            <p class="profile-bio">{{ user?.bio || '这个人很懒，什么都没写' }}</p>
            <div class="profile-meta">
              <span>注册于 {{ user?.createdAt || '未知' }}</span>
            </div>
          </div>
          <button v-if="isOwn" class="edit-btn">编辑资料</button>
        </div>
      </div>

      <div class="profile-tabs">
        <button 
          :class="['tab-item', { active: currentTab === 'questions' }]"
          @click="currentTab = 'questions'"
        >
          我的提问
          <span class="tab-count">{{ questions.length }}</span>
        </button>
        <button 
          :class="['tab-item', { active: currentTab === 'answers' }]"
          @click="currentTab = 'answers'"
        >
          我的回答
          <span class="tab-count">{{ answers.length }}</span>
        </button>
        <button 
          :class="['tab-item', { active: currentTab === 'votes' }]"
          @click="currentTab = 'votes'"
        >
          我点赞的
          <span class="tab-count">{{ votes.length }}</span>
        </button>
      </div>

      <div class="profile-content">
        <!-- 我的提问 -->
        <div v-if="currentTab === 'questions'" class="content-list">
          <div 
            v-for="q in questions" 
            :key="q.id"
            class="content-item"
          >
            <div class="item-votes">
              <span class="vote-count">{{ q.votes }}</span>
              <span class="vote-label">票</span>
            </div>
            <div class="item-content">
              <router-link :to="`/question/${q.id}`" class="item-title">
                {{ q.title }}
              </router-link>
              <div class="item-meta">
                <span>{{ q.answersCount }} 回答</span>
                <span>{{ formatTime(q.createdAt) }}</span>
              </div>
            </div>
          </div>
          <div v-if="questions.length === 0" class="empty-list">
            <p>暂无提问</p>
          </div>
        </div>

        <!-- 我的回答 -->
        <div v-if="currentTab === 'answers'" class="content-list">
          <div 
            v-for="a in answers" 
            :key="a.id"
            class="content-item"
          >
            <div class="item-votes">
              <span class="vote-count">{{ a.votes }}</span>
              <span class="vote-label">票</span>
            </div>
            <div class="item-content">
              <router-link :to="`/question/${a.questionId}`" class="item-title">
                {{ a.questionTitle }}
              </router-link>
              <div class="item-excerpt">{{ truncateText(a.content, 100) }}</div>
              <div class="item-meta">
                <span v-if="a.isAccepted" class="accepted-badge">已采纳</span>
                <span>{{ formatTime(a.createdAt) }}</span>
              </div>
            </div>
          </div>
          <div v-if="answers.length === 0" class="empty-list">
            <p>暂无回答</p>
          </div>
        </div>

        <!-- 我点赞的 -->
        <div v-if="currentTab === 'votes'" class="content-list">
          <div 
            v-for="v in votes" 
            :key="v.id"
            class="content-item"
          >
            <div class="item-votes voted">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
              </svg>
            </div>
            <div class="item-content">
              <router-link :to="`/question/${v.id}`" class="item-title">
                {{ v.title }}
              </router-link>
            </div>
          </div>
          <div v-if="votes.length === 0" class="empty-list">
            <p>暂无点赞</p>
          </div>
        </div>
      </div>

      <aside class="sidebar">
        <div class="sidebar-card">
          <div class="card-header">统计</div>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-value">{{ stats.questions }}</span>
              <span class="stat-label">提问</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.answers }}</span>
              <span class="stat-label">回答</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.accepted }}</span>
              <span class="stat-label">采纳</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.points }}</span>
              <span class="stat-label">积分</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const currentTab = ref('questions')
const user = computed(() => userStore.user)
const isOwn = ref(true)

const questions = ref([
  { id: 1, title: '如何在 Spring Boot 中配置多数据源？', votes: 15, answersCount: 3, createdAt: '2024-01-15' },
])

const answers = ref([
  { id: 1, questionId: 1, questionTitle: '如何在 Spring Boot 中配置多数据源？', content: '可以通过 `@Primary` 注解指定主数据源...', votes: 8, isAccepted: true, createdAt: '2024-01-16' },
])

const votes = ref([
  { id: 1, title: 'Vue 3 的 Composition API 有什么优势？' },
])

const stats = ref({
  questions: 5,
  answers: 12,
  accepted: 3,
  points: 180
})

const formatTime = (time) => {
  return new Date(time).toLocaleDateString('zh-CN')
}

const truncateText = (text, length) => {
  return text.length > length ? text.slice(0, length) + '...' : text
}
</script>

<style scoped>
.profile-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 16px 24px;
}

.profile-header {
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  overflow: hidden;
  margin-bottom: 16px;
}

.header-cover {
  height: 120px;
  background: linear-gradient(135deg, var(--zh-red) 0%, var(--zh-red-dark) 100%);
}

.header-content {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  padding: 0 24px 24px;
  margin-top: -48px;
  position: relative;
}

.profile-avatar {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  background: white;
}

.profile-info {
  flex: 1;
  padding-top: 48px;
}

.profile-name {
  font-family: var(--font-serif);
  font-size: 24px;
  color: var(--zh-ink);
  margin-bottom: 4px;
}

.profile-bio {
  font-size: 14px;
  color: var(--zh-ink-light);
  margin-bottom: 8px;
}

.profile-meta {
  font-size: 13px;
  color: var(--zh-ink-lighter);
}

.edit-btn {
  padding: 8px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  background: white;
  font-size: 14px;
  color: var(--zh-ink-light);
  cursor: pointer;
  transition: all 0.2s;
  position: absolute;
  right: 24px;
  top: 80px;
}

.edit-btn:hover {
  border-color: var(--zh-red);
  color: var(--zh-red);
}

.profile-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tab-item {
  padding: 12px 20px;
  border: none;
  background: white;
  border-radius: 2px;
  font-size: 14px;
  font-family: var(--font-serif);
  color: var(--zh-ink-light);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-item:hover {
  background: var(--zh-paper-dark);
}

.tab-item.active {
  background: var(--zh-red);
  color: white;
}

.tab-count {
  font-size: 12px;
  opacity: 0.7;
}

.profile-content {
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 24px;
  margin-bottom: 16px;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-item {
  display: flex;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--zh-paper-dark);
}

.content-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.item-votes {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 48px;
}

.item-votes.voted {
  color: var(--zh-red);
}

.item-votes svg {
  width: 20px;
  height: 20px;
}

.vote-count {
  font-size: 18px;
  font-weight: 600;
  color: var(--zh-ink);
}

.vote-label {
  font-size: 12px;
  color: var(--zh-ink-lighter);
}

.item-content {
  flex: 1;
}

.item-title {
  display: block;
  font-family: var(--font-serif);
  font-size: 16px;
  color: var(--zh-ink);
  text-decoration: none;
  margin-bottom: 8px;
}

.item-title:hover {
  color: var(--zh-red);
}

.item-excerpt {
  font-size: 14px;
  color: var(--zh-ink-light);
  margin-bottom: 8px;
}

.item-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--zh-ink-lighter);
}

.accepted-badge {
  color: var(--zh-jade);
  font-weight: 500;
}

.empty-list {
  text-align: center;
  padding: 40px;
  color: var(--zh-ink-lighter);
}

.sidebar {
  display: none;
}

@media (min-width: 768px) {
  .sidebar {
    display: block;
  }
  
  .profile-page {
    display: grid;
    grid-template-columns: 1fr 240px;
    gap: 24px;
  }
  
  .profile-content {
    grid-column: 1;
  }
}
</style>