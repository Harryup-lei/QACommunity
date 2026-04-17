<template>
  <div class="ranking-page">
    <div class="page-container">
      <div class="main-section">
        <div class="page-header">
          <div class="page-title-section">
            <h1 class="page-title">
              <svg class="title-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M12 3v18M3 12h18" stroke-width="2" stroke-linecap="round"/>
              </svg>
              排行榜
            </h1>
            <p class="page-subtitle">社区贡献者的荣誉殿堂</p>
          </div>
        </div>

        <div class="ranking-tabs">
          <button 
            :class="['ranking-tab', { active: currentTab === 'points' }]"
            @click="switchTab('points')"
          >
            积分榜
          </button>
          <button 
            :class="['ranking-tab', { active: currentTab === 'questions' }]"
            @click="switchTab('questions')"
          >
            提问榜
          </button>
          <button 
            :class="['ranking-tab', { active: currentTab === 'answers' }]"
            @click="switchTab('answers')"
          >
            回答榜
          </button>
        </div>

        <div class="ranking-list">
          <div 
            v-for="(user, index) in ranking" 
            :key="user.id"
            class="ranking-item"
            :class="getRankClass(index)"
          >
            <div class="rank-indicator">
              <span v-if="index < 3" class="rank-medal">
                <svg v-if="index === 0" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
                </svg>
                <span v-else>{{ index + 1 }}</span>
              </span>
              <span v-else class="rank-number">{{ index + 1 }}</span>
            </div>
            <div class="user-info">
              <img :src="user.avatar || '/default-avatar.png'" class="user-avatar"/>
              <div class="user-details">
                <span class="user-name">{{ user.username }}</span>
                <span class="user-bio">{{ user.bio || '暂无简介' }}</span>
              </div>
            </div>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-value">{{ user.points }}</span>
                <span class="stat-label">积分</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ user.questionsCount }}</span>
                <span class="stat-label">提问</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ user.answersCount }}</span>
                <span class="stat-label">回答</span>
              </div>
            </div>
          </div>

          <div v-if="ranking.length === 0" class="empty-state">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M12 3v18M3 12h18" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <h3>暂无数据</h3>
            <p>成为第一个上榜者吧！</p>
          </div>
        </div>

        <div v-if="total > pageSize" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :pager-count="5"
            layout="prev, pager, next"
            @current-change="loadRanking"
          />
        </div>
      </div>

      <aside class="sidebar">
        <div class="sidebar-card">
          <div class="card-header">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke-width="2"/>
            </svg>
            <span>榜单说明</span>
          </div>
          <div class="card-body">
            <ul class="说明-list">
              <li>积分 = 提问数 × 5 + 回答数 × 10 + 采纳数 × 20</li>
              <li>每周日 24:00 更新榜单</li>
              <li>排名前三可获得徽章</li>
            </ul>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import pointsService from '@/services/pointsService'

const ranking = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const currentTab = ref('points')

const switchTab = (tab) => {
  currentTab.value = tab
  currentPage.value = 1
  loadRanking()
}

const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

const loadRanking = async () => {
  try {
    const response = await pointsService.getPointsRanking(currentPage.value, pageSize.value)
    ranking.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('Failed to load ranking:', error)
  }
}

onMounted(() => {
  loadRanking()
})
</script>

<style scoped>
.ranking-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-container {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}

.page-header {
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 24px;
  margin-bottom: 16px;
}

.page-title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
  font-size: 14px;
  color: var(--zh-ink-light);
}

.ranking-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.ranking-tab {
  padding: 10px 20px;
  border: none;
  background: white;
  border-radius: 2px;
  font-size: 14px;
  font-family: var(--font-serif);
  color: var(--zh-ink-light);
  cursor: pointer;
  transition: all 0.2s;
}

.ranking-tab:hover {
  background: var(--zh-paper-dark);
}

.ranking-tab.active {
  background: var(--zh-red);
  color: white;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: all 0.2s;
}

.ranking-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.ranking-item.rank-gold {
  background: linear-gradient(135deg, #FFF9E6 0%, #FFF3D0 100%);
  border: 1px solid #D4AF37;
}

.ranking-item.rank-silver {
  background: linear-gradient(135deg, #F5F5F5 0%, #EBEBEB 100%);
  border: 1px solid #C0C0C0;
}

.ranking-item.rank-bronze {
  background: linear-gradient(135deg, #FFF5F0 0%, #FFE8DC 100%);
  border: 1px solid #CD7F32;
}

.rank-indicator {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.rank-medal {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #D4AF37;
}

.rank-medal svg {
  width: 100%;
  height: 100%;
}

.rank-number {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  color: var(--zh-ink-light);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--zh-paper-dark);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--zh-ink);
}

.user-bio {
  font-size: 13px;
  color: var(--zh-ink-lighter);
}

.user-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--zh-ink);
}

.stat-label {
  font-size: 12px;
  color: var(--zh-ink-lighter);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 4px;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  color: var(--zh-ink-lighter);
  opacity: 0.5;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--zh-ink);
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--zh-ink-light);
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-card {
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
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
  background: linear-gradient(180deg, rgba(196,30,58,0.02) 0%, transparent 100%);
}

.card-icon {
  width: 20px;
  height: 20px;
  color: var(--zh-red);
}

.card-body {
  padding: 16px;
}

.说明-list {
  margin: 0;
  padding-left: 20px;
}

.说明-list li {
  font-size: 13px;
  color: var(--zh-ink-light);
  line-height: 1.8;
}

@media (max-width: 1024px) {
  .page-container {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .ranking-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .user-stats {
    width: 100%;
    justify-content: space-around;
    padding-top: 12px;
    border-top: 1px solid var(--zh-paper-dark);
  }
}
</style>