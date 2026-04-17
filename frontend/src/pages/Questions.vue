<template>
  <div class="questions-page">
    <div class="page-container">
      <!-- 左侧主内容 -->
      <div class="main-section">
        <!-- 页面头部 -->
        <div class="page-header zh-card">
          <div class="page-title-section">
            <h1 class="page-title">
              <svg class="title-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M8 10h8M8 14h4M4 4h16v16H4z" stroke-width="2" stroke-linecap="round"/>
              </svg>
              问题列表
            </h1>
            <p class="page-subtitle">发现问题，分享知识</p>
          </div>
          <router-link to="/publish-question" class="zh-btn zh-btn-primary">
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M12 5v14M5 12h14" stroke-width="2" stroke-linecap="round"/>
            </svg>
            发布问题
          </router-link>
        </div>

        <!-- 筛选和排序 -->
        <div class="filter-bar zh-card">
          <div class="filter-tabs">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              :class="['filter-tab', { active: currentTab === tab.value }]"
              @click="selectTab(tab.value)"
            >
              {{ tab.label }}
              <span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
            </button>
          </div>

          <div class="filter-actions">
            <!-- 排序选择 -->
            <div class="sort-group">
              <span class="sort-label">排序：</span>
              <select v-model="sortBy" @change="loadQuestions" class="sort-select">
                <option value="latest">最新</option>
                <option value="hot">热门</option>
                <option value="votes">票数</option>
                <option value="answers">回答数</option>
              </select>
            </div>

            <!-- 搜索框 -->
            <div class="search-box">
              <input
                v-model="searchKeyword"
                type="text"
                placeholder="搜索问题..."
                @keyup.enter="handleSearch"
                class="search-input"
              />
              <button @click="handleSearch" class="search-btn">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <circle cx="11" cy="11" r="8" stroke-width="2"/>
                  <path d="M21 21l-4.35-4.35" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 问题列表 -->
        <div class="question-list">
          <!-- 加载中 -->
          <div v-if="loading" class="loading-state">
            <div class="question-card zh-card skeleton">
              <div class="question-content">
                <div class="skeleton-line skeleton-title"></div>
                <div class="skeleton-line skeleton-excerpt"></div>
                <div class="question-meta">
                  <div class="skeleton-avatar"></div>
                  <div class="skeleton-line skeleton-name"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="questions.length === 0" class="empty-state">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <circle cx="12" cy="12" r="10" stroke-width="2"/>
              <path d="M8 14s1.5 2 4 2 4-2 4-2" stroke-width="2" stroke-linecap="round"/>
              <line x1="9" y1="9" x2="9.01" y2="9" stroke-width="2" stroke-linecap="round"/>
              <line x1="15" y1="9" x2="15.01" y2="9" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <h3>暂无问题</h3>
            <p>成为第一个提问者吧！</p>
            <router-link to="/publish-question" class="zh-btn zh-btn-primary">
              发布问题
            </router-link>
          </div>

          <!-- 问题卡片列表 -->
          <template v-else>
            <div
              v-for="question in questions"
              :key="question.id"
              class="question-card zh-card"
            >
              <!-- 投票区 -->
              <div class="question-votes">
                <button
                  :class="['vote-btn', { voted: question.userVoted }]"
                  @click="handleVote(question)"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
                  </svg>
                </button>
                <span class="vote-count" :class="{ 'has-votes': question.votes > 0 }">
                  {{ question.votes }}
                </span>
                <div class="answer-count" :class="{ 'has-answers': question.answersCount > 0 }">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
                  </svg>
                  {{ question.answersCount }}
                </div>
              </div>

              <!-- 问题内容 -->
              <div class="question-content">
                <div class="question-header">
                  <h2 class="question-title">
                    <router-link :to="`/question/${question.id}`" class="title-link">
                      {{ question.title }}
                    </router-link>
                    <span v-if="question.isSolved" class="solved-badge" title="已解决">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke-width="2" stroke-linecap="round"/>
                        <polyline points="22 4 12 14.01 9 11.01" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </span>
                  </h2>
                </div>

                <p class="question-excerpt">{{ truncateText(question.content, 150) }}</p>

                <!-- 标签 -->
                <div class="question-tags">
                  <router-link
                    v-for="tag in parseTags(question.tags)"
                    :key="tag"
                    :to="`/tags?tag=${encodeURIComponent(tag)}`"
                    class="zh-tag zh-tag-secondary"
                  >
                    # {{ tag }}
                  </router-link>
                </div>

                <!-- 元信息 -->
                <div class="question-meta">
                  <router-link :to="`/profile?user=${question.author.id}`" class="author-info">
                    <img :src="question.author.avatar || '/default-avatar.png'" alt="avatar" class="author-avatar"/>
                    <span class="author-name">{{ question.author.username }}</span>
                  </router-link>
                  <span class="meta-divider">•</span>
                  <span class="question-time">{{ formatTime(question.createdAt) }}</span>
                  <span class="meta-divider">•</span>
                  <span class="question-views">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" width="14" height="14">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke-width="2"/>
                      <circle cx="12" cy="12" r="3" stroke-width="2"/>
                    </svg>
                    {{ question.views }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- 分页 -->
        <div v-if="totalPages > 1" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :pager-count="5"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </div>

      <!-- 右侧边栏 -->
      <aside class="sidebar">
        <!-- 社区统计 -->
        <div class="sidebar-card zh-card">
          <div class="card-header">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M18 20V10M12 20V4M6 20v-6" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span>社区统计</span>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-value">{{ stats.questions }}</span>
              <span class="stat-label">问题</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.answers }}</span>
              <span class="stat-label">回答</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ stats.users }}</span>
              <span class="stat-label">用户</span>
            </div>
          </div>
        </div>

        <!-- 热门标签 -->
        <div class="sidebar-card zh-card">
          <div class="card-header">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M7 7h10l-4 4H9z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="7" cy="7" r="2" stroke-width="2"/>
            </svg>
            <span>热门标签</span>
          </div>
          <div class="tags-cloud">
            <router-link
              v-for="tag in hotTags"
              :key="tag.name"
              :to="`/tags?tag=${encodeURIComponent(tag.name)}`"
              class="tag-item"
              :style="{ '--tag-size': tag.size }"
            >
              {{ tag.name }}
              <span class="tag-count">{{ tag.count }}</span>
            </router-link>
          </div>
        </div>

        <!-- 贡献者榜单 -->
        <div class="sidebar-card zh-card">
          <div class="card-header">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <circle cx="12" cy="8" r="4" stroke-width="2"/>
              <path d="M6 20v-2a6 6 0 0 1 12 0v2" stroke-width="2"/>
            </svg>
            <span>本周贡献</span>
          </div>
          <div class="contributors-list">
            <div
              v-for="(user, index) in contributors"
              :key="user.id"
              class="contributor-item"
            >
              <span class="rank" :class="getRankClass(index)">{{ index + 1 }}</span>
              <img :src="user.avatar || '/default-avatar.png'" alt="avatar" class="contributor-avatar"/>
              <div class="contributor-info">
                <span class="contributor-name">{{ user.username }}</span>
                <span class="contributor-score">+{{ user.score }} 积分</span>
              </div>
            </div>
          </div>
          <router-link to="/ranking" class="view-all">
            查看更多
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M9 18l6-6-6-6" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </router-link>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import questionService from '@/services/questionService'
import voteService from '@/services/voteService'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 状态
const loading = ref(false)
const questions = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const currentTab = ref('latest')
const sortBy = ref('latest')
const searchKeyword = ref('')

// 标签页
const tabs = reactive([
  { label: '最新', value: 'latest' },
  { label: '热门', value: 'hot' },
  { label: '未解决', value: 'unsolved' },
])

// 社区统计
const stats = reactive({
  questions: 1234,
  answers: 5678,
  users: 890
})

// 热门标签
const hotTags = reactive([
  { name: 'Java', count: 234, size: 1.5 },
  { name: 'Vue.js', count: 189, size: 1.3 },
  { name: 'Spring Boot', count: 156, size: 1.2 },
  { name: 'MySQL', count: 134, size: 1.1 },
  { name: '前端', count: 112, size: 1.0 },
  { name: 'API', count: 89, size: 0.9 },
])

// 贡献者
const contributors = reactive([
  { id: 1, username: '技术达人', avatar: null, score: 1250 },
  { id: 2, username: '代码高手', avatar: null, score: 980 },
  { id: 3, username: '问答专家', avatar: null, score: 750 },
  { id: 4, username: '社区之星', avatar: null, score: 620 },
  { id: 5, username: '热心用户', avatar: null, score: 540 },
])

// 计算总页数
const totalPages = ref(0)

// 加载问题列表
const loadQuestions = async () => {
  loading.value = true
  try {
    const response = await questionService.getQuestionList(
      sortBy.value,
      null,
      currentTab.value === 'unsolved' ? true : null,
      currentPage.value,
      pageSize.value
    )
    questions.value = response.data.records || []
    total.value = response.data.total || 0
    totalPages.value = Math.ceil(total.value / pageSize.value)
  } catch (error) {
    console.error('Failed to load questions:', error)
    ElMessage.error('加载问题列表失败')
  } finally {
    loading.value = false
  }
}

// 选择标签页
const selectTab = (value) => {
  currentTab.value = value
  currentPage.value = 1
  if (value === 'latest') sortBy.value = 'latest'
  else if (value === 'hot') sortBy.value = 'hot'
  loadQuestions()
}

// 处理搜索
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadQuestions()
    return
  }
  try {
    const response = await questionService.searchQuestions(
      searchKeyword.value,
      currentPage.value,
      pageSize.value
    )
    questions.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('Search failed:', error)
    ElMessage.error('搜索失败')
  }
}

// 处理分页
const handlePageChange = () => {
  loadQuestions()
}

// 处理点赞
const handleVote = async (question) => {
  try {
    if (question.userVoted) {
      await voteService.unvote('question', question.id)
      question.userVoted = false
      question.votes -= 1
    } else {
      await voteService.vote('question', question.id)
      question.userVoted = true
      question.votes += 1
    }
  } catch (error) {
    console.error('Vote failed:', error)
    ElMessage.error('操作失败')
  }
}

// 解析标签
const parseTags = (tags) => {
  if (!tags) return []
  try {
    return JSON.parse(tags)
  } catch {
    return [tags]
  }
}

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.slice(0, length) + '...' : text
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

// 获取排名样式
const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.questions-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-container {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-serif);
  font-size: 24px;
  color: var(--zh-ink);
}

.title-icon {
  width: 28px;
  height: 28px;
  color: var(--zh-red);
}

.page-subtitle {
  color: var(--zh-ink-light);
  font-size: 14px;
}

/* 筛选栏 */
.filter-bar {
  margin-bottom: 16px;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-tab {
  padding: 8px 16px;
  border: none;
  background: var(--zh-paper-dark);
  border-radius: var(--radius-md);
  color: var(--zh-ink-light);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-tab:hover {
  background: var(--zh-paper-darker);
}

.filter-tab.active {
  background: var(--zh-red);
  color: white;
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 11px;
}

.filter-actions {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.sort-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: var(--zh-ink-light);
}

.sort-select {
  padding: 8px 12px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: var(--radius-md);
  background: white;
  font-size: 14px;
  cursor: pointer;
}

.search-box {
  display: flex;
  gap: 0;
  border: 1px solid var(--zh-paper-dark);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.search-input {
  padding: 8px 12px;
  border: none;
  font-size: 14px;
  width: 200px;
}

.search-input:focus {
  outline: none;
}

.search-btn {
  padding: 8px 12px;
  border: none;
  background: var(--zh-paper-dark);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.search-btn:hover {
  background: var(--zh-blue);
}

.search-btn svg {
  width: 18px;
  height: 18px;
  color: var(--zh-ink-light);
}

/* 问题列表 */
.question-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  transition: all 0.3s ease;
}

.question-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

/* 投票区 */
.question-votes {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.vote-btn {
  width: 36px;
  height: 36px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: var(--radius-md);
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.vote-btn:hover {
  border-color: var(--zh-red);
  color: var(--zh-red);
  background: var(--error-light);
}

.vote-btn.voted {
  background: var(--zh-red);
  border-color: var(--zh-red);
  color: white;
}

.vote-btn svg {
  width: 20px;
  height: 20px;
}

.vote-count {
  font-size: 16px;
  font-weight: 600;
  color: var(--zh-ink-light);
}

.vote-count.has-votes {
  color: var(--zh-red);
}

.answer-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--zh-ink-light);
  padding: 4px 8px;
  background: var(--zh-paper-dark);
  border-radius: var(--radius-sm);
}

.answer-count.has-answers {
  color: var(--zh-blue);
  background: var(--info-light);
}

.answer-count svg {
  width: 14px;
  height: 14px;
}

/* 问题内容 */
.question-content {
  flex: 1;
  min-width: 0;
}

.question-header {
  margin-bottom: 8px;
}

.question-title {
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.title-link {
  color: var(--zh-ink);
  text-decoration: none;
  transition: color 0.2s ease;
}

.title-link:hover {
  color: var(--zh-blue);
}

.solved-badge {
  width: 20px;
  height: 20px;
  color: var(--zh-jade);
  flex-shrink: 0;
}

.solved-badge svg {
  width: 100%;
  height: 100%;
}

.question-excerpt {
  color: var(--zh-ink-light);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.question-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--zh-ink-lighter);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: var(--zh-ink);
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-weight: 500;
}

.meta-divider {
  color: var(--zh-paper-darker);
}

.question-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 骨架屏 */
.skeleton .skeleton-line {
  background: linear-gradient(90deg,
    var(--zh-paper-dark) 25%,
    var(--zh-paper) 50%,
    var(--zh-paper-dark) 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: var(--radius-sm);
}

.skeleton-title {
  height: 24px;
  width: 60%;
  margin-bottom: 12px;
}

.skeleton-excerpt {
  height: 16px;
  width: 100%;
  margin-bottom: 8px;
}

.skeleton-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  margin-right: 8px;
}

.skeleton-name {
  height: 16px;
  width: 100px;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--zh-ink-light);
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state h3 {
  font-family: var(--font-serif);
  font-size: 18px;
  margin-bottom: 8px;
  color: var(--zh-ink);
}

.empty-state p {
  margin-bottom: 20px;
}

/* 分页 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-card {
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-bottom: 1px solid var(--zh-paper-dark);
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--zh-ink);
  background: linear-gradient(180deg, rgba(196, 30, 58, 0.02) 0%, transparent 100%);
}

.card-icon {
  width: 20px;
  height: 20px;
  color: var(--zh-red);
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--zh-blue);
}

.stat-label {
  font-size: 12px;
  color: var(--zh-ink-lighter);
}

/* 标签云 */
.tags-cloud {
  padding: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  padding: 6px 12px;
  background: var(--zh-paper-dark);
  border-radius: var(--radius-full);
  text-decoration: none;
  color: var(--zh-ink);
  font-size: calc(12px * var(--tag-size));
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-item:hover {
  background: var(--zh-blue);
  color: white;
}

.tag-count {
  font-size: 11px;
  opacity: 0.7;
}

/* 贡献者列表 */
.contributors-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contributor-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  background: var(--zh-paper-dark);
  color: var(--zh-ink-light);
}

.rank-gold {
  background: var(--zh-gold);
  color: white;
}

.rank-silver {
  background: #C0C0C0;
  color: white;
}

.rank-bronze {
  background: #CD7F32;
  color: white;
}

.contributor-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.contributor-info {
  flex: 1;
}

.contributor-name {
  display: block;
  font-weight: 500;
  color: var(--zh-ink);
  margin-bottom: 2px;
}

.contributor-score {
  font-size: 12px;
  color: var(--zh-red);
}

.view-all {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px;
  border-top: 1px solid var(--zh-paper-dark);
  color: var(--zh-blue);
  text-decoration: none;
  font-size: 14px;
  transition: background 0.2s ease;
}

.view-all:hover {
  background: var(--zh-paper-dark);
}

.view-all svg {
  width: 16px;
  height: 16px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .page-container {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .filter-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .question-card {
    flex-direction: column;
  }

  .question-votes {
    flex-direction: row;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--zh-paper-dark);
    margin-bottom: 12px;
  }

  .vote-btn {
    width: 32px;
    height: 32px;
  }
}
</style>
