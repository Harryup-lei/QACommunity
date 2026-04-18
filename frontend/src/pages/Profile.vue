<template>
  <div class="profile-page">
    <div class="page-container">
      <div class="profile-header">
        <div class="header-cover"></div>
        <div class="header-content">
          <!-- 隐藏的文件输入，用于头像上传 -->
          <input
            ref="avatarInputRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
          />

          <img
            v-if="isOwn"
            :src="user?.avatar || '/default-avatar.png'"
            class="profile-avatar clickable"
            @click="handleAvatarClick"
            title="点击更换头像"
          />
          <img
            v-else
            :src="user?.avatar || '/default-avatar.png'"
            class="profile-avatar"
          />
          <div class="profile-info">
            <h1 class="profile-name">{{ user?.username || '用户' }}</h1>
            <p class="profile-bio">{{ user?.bio || '这个人很懒，什么都没写' }}</p>
            <div class="profile-meta">
              <span>注册于 {{ user?.createdAt || '未知' }}</span>
            </div>
          </div>
          <button v-if="isOwn" class="edit-btn" @click="handleEditClick">编辑资料</button>

<!-- 弹窗对话框 -->
<div v-if="showDialog" class="dialog-overlay" @click.self="showDialog = false">
  <div class="edit-dialog">
    <div class="dialog-header">
      <h3>编辑资料</h3>
      <button class="close-btn" @click="showDialog = false">&times;</button>
    </div>
    <div class="dialog-body">
      <div class="form-item">
        <label>头像</label>
        <div class="avatar-preview">
          <img :src="editForm.avatar || '/default-avatar.png'" class="preview-img" />
          <button class="change-avatar-btn" @click="handleAvatarClick">更换头像</button>
        </div>
      </div>
      <div class="form-item">
        <label>个人简介</label>
        <textarea
          v-model="editForm.bio"
          class="bio-input"
          placeholder="介绍一下你自己..."
          maxlength="500"
        ></textarea>
        <span class="word-count">{{ editForm.bio?.length || 0 }}/500</span>
      </div>
    </div>
    <div class="dialog-footer">
      <button class="cancel-btn" @click="showDialog = false">取消</button>
      <button class="save-btn" @click="handleSaveProfile">保存</button>
    </div>
  </div>
</div>
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
          <div v-for="q in questions" :key="q.id" class="content-item">
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
          <div v-for="a in answers" :key="a.id" class="content-item">
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
          <div v-for="v in votes" :key="v.id" class="content-item">
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
import userService from '@/services/userService'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()

const currentTab = ref('questions')
const user = computed(() => userStore.user)
const isOwn = ref(true)

// 弹窗状态
const showDialog = ref(false)
const editForm = ref({
  avatar: '',
  bio: ''
})

const handleEditClick = () => {
  editForm.value = {
    avatar: user.value?.avatar || '',
    bio: user.value?.bio || ''
  }
  showDialog.value = true
}

const handleSaveProfile = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }

    const currentAvatar = user.value?.avatar || ''
    await userService.updateUserInfo(parseInt(userId), currentAvatar, editForm.value.bio)

    userStore.setUser({
      ...user.value,
      bio: editForm.value.bio
    })

    ElMessage.success({
      message: '资料更新成功'
    })
    showDialog.value = false
  } catch (error) {
    console.error('Update profile error:', error)
    this.$message.error({
      message: '资料更新失败',
      duration: 3000
    })
  }
}

// 头像上传
const avatarInputRef = ref(null)
const handleAvatarClick = () => {
  avatarInputRef.value?.click()
}

const handleAvatarChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG、PNG、GIF 或 WebP 格式的图片')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  try {
    // 上传到后端
    const result = await userService.uploadAvatar(file)

    if (result && result.code === 0 && result.data) {
      // 保存到数据库
      const userId = localStorage.getItem('userId')
      if (userId) {
        await userService.updateUserInfo(parseInt(userId), result.data, editForm.value.bio)
      }

      // 更新用户头像
      userStore.setUser({
        ...user.value,
        avatar: result.data
      })
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result?.message || '头像上传失败')
    }
  } catch (error) {
    console.error('Upload avatar error:', error)
}
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }

  try {
    // 上传到后端
    const result = await userService.uploadAvatar(file)

    if (result && result.code === 0 && result.data) {
      // 更新用户头像
      userStore.setUser({
        ...user.value,
        avatar: result.data
      })
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result?.message || '头像上传失败')
    }
  } catch (error) {
    console.error('Upload avatar error:', error)
    ElMessage.error({
      message: '头像上传失败',
      duration: 3000
    })
  }
}

// Mock 数据
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

.profile-avatar.clickable {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.profile-avatar.clickable:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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

/* 简洁编辑模式 */
.inline-edit {
  background: white;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.inline-edit .edit-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.inline-edit .edit-row label {
  font-size: 14px;
  font-weight: 500;
  color: var(--zh-ink);
}

.inline-edit .bio-input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

.inline-edit .bio-input:focus {
  outline: none;
  border-color: var(--zh-red);
}

.inline-edit .word-count {
  font-size: 12px;
  color: var(--zh-ink-lighter);
  text-align: right;
}

.inline-edit .save-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background: var(--zh-red);
  font-size: 14px;
  color: white;
  cursor: pointer;
}

.inline-edit .save-btn:hover {
  background: var(--zh-red-dark);
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

/* 编辑表单样式 */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.edit-form .form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.edit-form .form-item label {
  font-size: 14px;
  font-weight: 500;
  color: var(--zh-ink);
}

.avatar-preview {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview .preview-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--zh-paper-dark);
}

.change-avatar-btn {
  padding: 8px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 4px;
  background: white;
  font-size: 14px;
  color: var(--zh-ink-light);
  cursor: pointer;
  transition: all 0.2s;
}

.change-avatar-btn:hover {
  border-color: var(--zh-red);
  color: var(--zh-red);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 自定义对话框样式 */
.dialog-overlay {
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
}

.edit-dialog {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--zh-paper-dark);
}

.dialog-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--zh-ink);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--zh-ink-light);
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: var(--zh-ink);
}

.dialog-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-body .form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dialog-body .form-item label {
  font-size: 14px;
  font-weight: 500;
  color: var(--zh-ink);
}

.dialog-body .avatar-preview {
  display: flex;
  align-items: center;
  gap: 16px;
}

.dialog-body .preview-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--zh-paper-dark);
}

.dialog-body .change-avatar-btn {
  padding: 8px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 4px;
  background: white;
  font-size: 14px;
  color: var(--zh-ink-light);
  cursor: pointer;
  transition: all 0.2s;
}

.dialog-body .change-avatar-btn:hover {
  border-color: var(--zh-red);
  color: var(--zh-red);
}

.dialog-body .bio-input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.dialog-body .bio-input:focus {
  outline: none;
  border-color: var(--zh-red);
}

.dialog-body .word-count {
  font-size: 12px;
  color: var(--zh-ink-lighter);
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--zh-paper-dark);
}

.dialog-footer .cancel-btn {
  padding: 10px 20px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 4px;
  background: white;
  font-size: 14px;
  color: var(--zh-ink-light);
  cursor: pointer;
  transition: all 0.2s;
}

.dialog-footer .cancel-btn:hover {
  border-color: var(--zh-ink-light);
}

.dialog-footer .save-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  background: var(--zh-red);
  font-size: 14px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.dialog-footer .save-btn:hover {
  background: var(--zh-red-dark);
}
</style>