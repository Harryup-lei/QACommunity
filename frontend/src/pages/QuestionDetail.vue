<template>
  <div class="question-detail-page">
    <div class="page-container">
      <!-- 左侧主内容 -->
      <div class="main-section">
        <!-- 返回按钮 -->
        <router-link to="/questions" class="back-link">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M19 12H5M12 19l-7-7 7-7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          返回列表
        </router-link>

        <!-- 问题卡片 -->
        <div v-if="question" class="question-card zh-card zh-border-decorative">
          <div class="question-header">
            <h1 class="question-title">{{ question.title }}</h1>
            <div class="question-status">
              <span v-if="question.isSolved" class="solved-tag">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke-width="2"/>
                  <polyline points="22 4 12 14.01 9 11.01" stroke-width="2"/>
                </svg>
                已解决
              </span>
              <span v-else class="unsolved-tag">未解决</span>
            </div>
          </div>

          <!-- 元信息 -->
          <div class="question-meta">
            <div class="author-info">
              <router-link :to="`/profile?user=${question.author.id}`" class="author-link">
                <img :src="question.author.avatar || '/default-avatar.png'" alt="avatar" class="author-avatar"/>
                <span class="author-name">{{ question.author.username }}</span>
                <span v-if="question.author.status === 'ONLINE'" class="status-dot status-online"></span>
              </router-link>
              <span class="meta-time">{{ formatTime(question.createdAt) }}</span>
            </div>
            <div class="question-stats">
              <span class="stat-item" title="浏览数">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke-width="2"/>
                  <circle cx="12" cy="12" r="3" stroke-width="2"/>
                </svg>
                {{ question.views }}
              </span>
              <span class="stat-item" title="回答数">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
                </svg>
                {{ question.answersCount }}
              </span>
              <span class="stat-item" title="点赞数">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
                </svg>
                {{ question.votes }}
              </span>
            </div>
          </div>

          <!-- 问题内容 -->
          <div class="question-content zh-code" v-html="renderMarkdown(question.content)"></div>

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

          <!-- 操作按钮 -->
          <div class="question-actions">
            <button
              :class="['zh-btn', question.userVoted ? 'zh-btn-primary' : 'zh-btn-outline']"
              @click="handleVote"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
              </svg>
              {{ question.userVoted ? '已点赞' : '点赞' }} ({{ question.votes }})
            </button>

            <button class="zh-btn zh-btn-secondary" @click="onPublishAnswer">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
              </svg>
              写回答
            </button>

            <button v-if="canEdit" class="zh-btn zh-btn-ghost" @click="onEdit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke-width="2"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke-width="2"/>
              </svg>
              编辑
            </button>

            <button v-if="canEdit" class="zh-btn zh-btn-ghost danger" @click="onDelete">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <polyline points="3 6 5 6 21 6" stroke-width="2"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" stroke-width="2"/>
              </svg>
              删除
            </button>
          </div>
        </div>

        <!-- 回答区域 -->
        <div class="answers-section">
          <div class="answers-header">
            <h2 class="section-title">
              <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
              </svg>
              回答 ({{ answers.length }})
            </h2>
            <div class="answer-sort">
              <span class="sort-label">排序：</span>
              <select v-model="answerSort" @change="loadAnswers" class="sort-select">
                <option value="time">按时间</option>
                <option value="votes">按票数</option>
              </select>
            </div>
          </div>

          <!-- 回答列表 -->
          <div class="answers-list">
            <!-- 被采纳的回答 -->
            <div
              v-if="acceptedAnswer"
              class="answer-card zh-card accepted"
            >
              <div class="accepted-badge">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke-width="2"/>
                  <polyline points="22 4 12 14.01 9 11.01" stroke-width="2"/>
                </svg>
                采纳答案
              </div>
              <div class="answer-content" v-html="renderMarkdown(acceptedAnswer.content)"></div>
              <div class="answer-footer">
                <div class="answer-author">
                  <img :src="acceptedAnswer.author.avatar || '/default-avatar.png'" alt="avatar" class="author-avatar"/>
                  <span class="author-name">{{ acceptedAnswer.author.username }}</span>
                  <span class="answer-time">{{ formatTime(acceptedAnswer.createdAt) }}</span>
                </div>
                <div class="answer-actions">
                  <button
                    :class="['vote-btn', { voted: acceptedAnswer.userVoted }]"
                    @click="handleAnswerVote(acceptedAnswer)"
                  >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
                    </svg>
                    {{ acceptedAnswer.votes }}
                  </button>
                </div>
              </div>
            </div>

            <!-- 其他回答 -->
            <div
              v-for="answer in answers"
              :key="answer.id"
              class="answer-card zh-card"
              :class="{ 'is-accepted': answer.isAccepted }"
            >
              <div class="answer-content zh-code" v-html="renderMarkdown(answer.content)"></div>
              <div class="answer-footer">
                <div class="answer-author">
                  <img :src="answer.author.avatar || '/default-avatar.png'" alt="avatar" class="author-avatar"/>
                  <span class="author-name">{{ answer.author.username }}</span>
                  <span class="answer-time">{{ formatTime(answer.createdAt) }}</span>
                </div>
                <div class="answer-actions">
                  <button
                    :class="['vote-btn', { voted: answer.userVoted }]"
                    @click="handleAnswerVote(answer)"
                  >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" stroke-width="2"/>
                    </svg>
                    {{ answer.votes }}
                  </button>
                  <button
                    v-if="isQuestionOwner"
                    class="accept-btn"
                    :class="{ accepted: answer.isAccepted }"
                    @click="handleAccept(answer)"
                  >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <polyline points="20 6 9 17 4 12" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ answer.isAccepted ? '已采纳' : '采纳' }}
                  </button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="answers.length === 0" class="empty-answers">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke-width="2"/>
              </svg>
              <p>暂无回答，快来写下第一个回答吧！</p>
              <button class="zh-btn zh-btn-primary" @click="onPublishAnswer">
                写回答
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <aside class="sidebar">
        <!-- 问题作者信息 -->
        <div v-if="question" class="sidebar-card zh-card">
          <div class="card-header">
            <span>提问者</span>
          </div>
          <div class="author-card">
            <img :src="question.author.avatar || '/default-avatar.png'" alt="avatar" class="author-avatar-lg"/>
            <div class="author-details">
              <span class="author-name-lg">{{ question.author.username }}</span>
              <span class="author-bio">{{ question.author.bio || '这个人很懒，什么都没写' }}</span>
            </div>
          </div>
        </div>

        <!-- 相关问题 -->
        <div class="sidebar-card zh-card">
          <div class="card-header">
            <span>相关问题</span>
          </div>
          <div class="related-list">
            <router-link
              v-for="q in relatedQuestions"
              :key="q.id"
              :to="`/question/${q.id}`"
              class="related-item"
            >
              {{ q.title }}
            </router-link>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import hljs from 'highlight.js'
import questionService from '@/services/questionService'
import answerService from '@/services/answerService'
import voteService from '@/services/voteService'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const questionId = ref(route.params.id)
const question = ref(null)
const answers = ref([])
const acceptedAnswer = ref(null)
const answerSort = ref('time')
const relatedQuestions = ref([
  { id: 1, title: '如何在 Spring Boot 中配置多数据源？' },
  { id: 2, title: 'Vue 3 的 Composition API 有什么优势？' },
  { id: 3, title: 'MySQL 索引优化的最佳实践有哪些？' },
])

const currentUserId = computed(() => userStore.user?.id)
const isQuestionOwner = computed(() => currentUserId.value === question.value?.author.id)
const canEdit = computed(() => isQuestionOwner.value)

// 加载问题详情
const loadQuestion = async () => {
  try {
    question.value = await questionService.getQuestionDetail(questionId.value)
  } catch (error) {
    console.error('Failed to load question:', error)
    ElMessage.error('加载问题失败')
  }
}

// 加载回答列表
const loadAnswers = async () => {
  try {
    const response = await answerService.getAnswerList(
      questionId.value,
      1,
      50
    )
    const allAnswers = response.records || []

    // 分离已采纳回答和其他回答
    acceptedAnswer.value = allAnswers.find(a => a.isAccepted)
    answers.value = allAnswers
      .filter(a => !a.isAccepted)
      .sort((a, b) => {
        if (answerSort.value === 'votes') {
          return b.votes - a.votes
        }
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
  } catch (error) {
    console.error('Failed to load answers:', error)
  }
}

// 渲染 Markdown（带代码高亮）
const renderMarkdown = (content) => {
  // 简单的 Markdown 解析（实际项目中建议使用 markdown-it）
  let html = content
    .replace(/```(\w+)?\n([\s\S]*?)```/g, (match, lang, code) => {
      const language = lang || 'text'
      const highlighted = hljs.highlight(code.trim(), { language }).value
      return `<pre class="zh-code-block"><code class="language-${language}">${highlighted}</code></pre>`
    })
    .replace(/`([^`]+)`/g, '<code class="zh-code-inline">$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/\*([^*]+)\*/g, '<em>$1</em>')
    .replace(/\n/g, '<br/>')

  return html
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

// 处理点赞
const handleVote = async () => {
  try {
    if (question.value.userVoted) {
      await voteService.unvote('question', questionId.value)
      question.value.userVoted = false
      question.value.votes -= 1
    } else {
      await voteService.vote('question', questionId.value)
      question.value.userVoted = true
      question.value.votes += 1
    }
  } catch (error) {
    console.error('Vote failed:', error)
    ElMessage.error('操作失败')
  }
}

// 处理回答点赞
const handleAnswerVote = async (answer) => {
  try {
    if (answer.userVoted) {
      await voteService.unvote('answer', answer.id)
      answer.userVoted = false
      answer.votes -= 1
    } else {
      await voteService.vote('answer', answer.id)
      answer.userVoted = true
      answer.votes += 1
    }
  } catch (error) {
    console.error('Vote failed:', error)
    ElMessage.error('操作失败')
  }
}

// 处理采纳
const handleAccept = async (answer) => {
  try {
    await questionService.acceptAnswer(questionId.value, answer.id)
    answer.isAccepted = true
    acceptedAnswer.value = answer
    answers.value = answers.value.filter(a => a.id !== answer.id)
    question.value.isSolved = true
    ElMessage.success('答案已采纳')
  } catch (error) {
    console.error('Accept failed:', error)
    ElMessage.error('操作失败')
  }
}

// 写回答
const onPublishAnswer = () => {
  router.push(`/publish-answer/${questionId.value}`)
}

// 编辑问题
const onEdit = () => {
  router.push(`/edit-question/${questionId.value}`)
}

// 删除问题
const onDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个问题吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await questionService.deleteQuestion(questionId.value)
    ElMessage.success('问题已删除')
    router.push('/questions')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete failed:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadQuestion()
  loadAnswers()
})
</script>

<style scoped>
.question-detail-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-container {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}

.main-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 返回链接 */
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--zh-ink-light);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s ease;
}

.back-link:hover {
  color: var(--zh-blue);
}

.back-link svg {
  width: 18px;
  height: 18px;
}

/* 问题卡片 */
.question-card {
  padding: 24px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.question-title {
  font-family: var(--font-serif);
  font-size: 24px;
  font-weight: 600;
  color: var(--zh-ink);
  line-height: 1.4;
}

.question-status {
  display: flex;
  gap: 8px;
}

.solved-tag,
.unsolved-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 500;
}

.solved-tag {
  background: var(--success-light);
  color: var(--success);
}

.solved-tag svg {
  width: 16px;
  height: 16px;
}

.unsolved-tag {
  background: var(--warning-light);
  color: var(--warning);
}

/* 元信息 */
.question-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-top: 1px solid var(--zh-paper-dark);
  border-bottom: 1px solid var(--zh-paper-dark);
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-link {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-weight: 500;
  color: var(--zh-ink);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-online {
  background: var(--zh-jade);
  box-shadow: 0 0 8px rgba(0, 168, 107, 0.4);
}

.meta-time {
  color: var(--zh-ink-lighter);
  font-size: 13px;
}

.question-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--zh-ink-light);
  font-size: 14px;
}

.stat-item svg {
  width: 18px;
  height: 18px;
  opacity: 0.7;
}

/* 问题内容 */
.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: var(--zh-ink);
  margin-bottom: 20px;
}

.question-content :deep(img) {
  max-width: 100%;
  border-radius: var(--radius-md);
  margin: 16px 0;
}

.question-content :deep(pre) {
  margin: 16px 0;
}

/* 标签 */
.question-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

/* 操作按钮 */
.question-actions {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid var(--zh-paper-dark);
}

.question-actions .zh-btn svg {
  width: 18px;
  height: 18px;
}

.question-actions .danger {
  color: var(--zh-red);
}

.question-actions .danger:hover {
  background: var(--error-light);
}

/* 回答区域 */
.answers-section {
  margin-top: 24px;
}

.answers-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-serif);
  font-size: 20px;
  color: var(--zh-ink);
}

.section-icon {
  width: 24px;
  height: 24px;
  color: var(--zh-blue);
}

.answer-sort {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: var(--zh-ink-light);
}

.sort-select {
  padding: 6px 12px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: var(--radius-md);
  background: white;
  font-size: 14px;
  cursor: pointer;
}

/* 回答卡片 */
.answers-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.answer-card {
  padding: 20px;
}

.answer-card.accepted,
.answer-card.is-accepted {
  border: 2px solid var(--zh-jade);
  position: relative;
}

.accepted-badge {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: var(--zh-jade);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 0 0 0 var(--radius-md);
}

.accepted-badge svg {
  width: 14px;
  height: 14px;
}

.answer-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--zh-ink);
  margin-bottom: 16px;
}

.answer-content :deep(.zh-code-block) {
  margin: 16px 0;
}

.answer-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--zh-paper-dark);
}

.answer-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.answer-author .author-avatar {
  width: 32px;
  height: 32px;
}

.answer-author .author-name {
  font-weight: 500;
  color: var(--zh-ink);
}

.answer-time {
  color: var(--zh-ink-lighter);
  font-size: 13px;
  margin-left: 8px;
}

.answer-actions {
  display: flex;
  gap: 12px;
}

.vote-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: var(--radius-md);
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--zh-ink-light);
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
  width: 16px;
  height: 16px;
}

.accept-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: var(--radius-md);
  background: transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--zh-ink-light);
  font-size: 13px;
}

.accept-btn:hover {
  border-color: var(--zh-jade);
  color: var(--zh-jade);
  background: var(--success-light);
}

.accept-btn.accepted {
  background: var(--zh-jade);
  border-color: var(--zh-jade);
  color: white;
}

.accept-btn svg {
  width: 16px;
  height: 16px;
}

/* 空回答状态 */
.empty-answers {
  text-align: center;
  padding: 60px 20px;
  color: var(--zh-ink-light);
}

.empty-answers svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-answers p {
  margin-bottom: 20px;
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

.sidebar-card .card-header {
  padding: 16px;
  border-bottom: 1px solid var(--zh-paper-dark);
  font-family: var(--font-serif);
  font-weight: 600;
  color: var(--zh-ink);
  background: linear-gradient(180deg, rgba(196, 30, 58, 0.02) 0%, transparent 100%);
}

.author-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.author-avatar-lg {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 16px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name-lg {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  color: var(--zh-ink);
}

.author-bio {
  font-size: 13px;
  color: var(--zh-ink-lighter);
}

.related-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  color: var(--zh-ink);
  text-decoration: none;
  font-size: 14px;
  line-height: 1.5;
  padding: 8px;
  border-radius: var(--radius-sm);
  transition: background 0.2s ease;
}

.related-item:hover {
  background: var(--zh-paper-dark);
  color: var(--zh-blue);
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
  .question-header {
    flex-direction: column;
  }

  .question-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .question-stats {
    width: 100%;
    justify-content: space-around;
  }

  .question-actions {
    flex-wrap: wrap;
  }

  .answer-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
