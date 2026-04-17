<template>
  <div class="tags-page">
    <div class="page-container">
      <div class="main-section">
        <div class="page-header">
          <div class="page-title-section">
            <h1 class="page-title">
              <svg class="title-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M7 7h10l-4 4H9z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="7" cy="7" r="2" stroke-width="2"/>
              </svg>
              标签
            </h1>
            <p class="page-subtitle">按标签探索问题</p>
          </div>
          <div class="search-box">
            <input 
              v-model="searchKeyword"
              type="text"
              placeholder="搜索标签..."
              @input="handleSearch"
              class="search-input"
            />
          </div>
        </div>

        <div class="tags-grid">
          <div 
            v-for="tag in filteredTags" 
            :key="tag.name"
            class="tag-card"
          >
            <div class="tag-header">
              <span class="tag-name">{{ tag.name }}</span>
              <span class="tag-count">{{ tag.count }}</span>
            </div>
            <p class="tag-desc">{{ tag.description }}</p>
            <div class="tag-footer">
              <span class="tag-questions">{{ tag.questionsCount }} 问题</span>
              <span class="tag-answers">{{ tag.answersCount }} 回答</span>
            </div>
          </div>
        </div>

        <div v-if="filteredTags.length === 0" class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M7 7h10l-4 4H9z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="7" cy="7" r="2" stroke-width="2"/>
          </svg>
          <h3>暂无标签</h3>
          <p>没有找到匹配的标签</p>
        </div>
      </div>

      <aside class="sidebar">
        <div class="sidebar-card">
          <div class="card-header">
            <svg class="card-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <circle cx="12" cy="12" r="10" stroke-width="2"/>
              <path d="M12 6v6l4 2" stroke-width="2"/>
            </svg>
            <span>标签说明</span>
          </div>
          <div class="card-body">
            <ul class="说明-list">
              <li>每个问题最多添加 3 个标签</li>
              <li>选择准确有助于快速获得回答</li>
              <li>热门标签会显示在前排</li>
            </ul>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const searchKeyword = ref('')
const tags = ref([
  { name: 'Java', count: 234, description: 'Java 编程语言相关', questionsCount: 180, answersCount: 450 },
  { name: 'Vue.js', count: 189, description: 'Vue.js 框架相关', questionsCount: 150, answersCount: 320 },
  { name: 'Spring Boot', count: 156, description: 'Spring Boot 框架', questionsCount: 120, answersCount: 280 },
  { name: 'MySQL', count: 134, description: 'MySQL 数据库', questionsCount: 100, answersCount: 240 },
  { name: '前端', count: 112, description: '前端开发技术', questionsCount: 90, answersCount: 180 },
  { name: 'API', count: 89, description: 'API 设计与开发', questionsCount: 70, answersCount: 150 },
  { name: 'Python', count: 78, description: 'Python 编程', questionsCount: 60, answersCount: 120 },
  { name: 'React', count: 67, description: 'React 框架', questionsCount: 50, answersCount: 100 },
  { name: '数据库', count: 56, description: '数据库技术', questionsCount: 45, answersCount: 90 },
  { name: '后端', count: 45, description: '后端开发', questionsCount: 35, answersCount: 70 },
])

const filteredTags = computed(() => {
  if (!searchKeyword.value) return tags.value
  return tags.value.filter(tag => 
    tag.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const handleSearch = () => {
  // 实时搜索
}
</script>

<style scoped>
.tags-page {
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
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.search-box {
  width: 240px;
}

.search-input {
  width: 100%;
  padding: 10px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  font-size: 14px;
  background: var(--zh-paper);
  transition: all 0.2s;
  box-sizing: border-box;
}

.search-input:focus {
  outline: none;
  border-color: var(--zh-red);
  background: white;
}

.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.tag-card {
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 20px;
  transition: all 0.2s;
}

.tag-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.tag-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.tag-name {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  color: var(--zh-ink);
}

.tag-count {
  background: var(--zh-red);
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.tag-desc {
  font-size: 13px;
  color: var(--zh-ink-light);
  margin-bottom: 12px;
  line-height: 1.5;
}

.tag-footer {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--zh-ink-lighter);
  padding-top: 12px;
  border-top: 1px solid var(--zh-paper-dark);
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
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .search-box {
    width: 100%;
  }
}
</style>