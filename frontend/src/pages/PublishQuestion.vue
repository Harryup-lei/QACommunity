<template>
  <div class="publish-question-page">
    <div class="page-container">
      <div class="publish-card">
        <div class="card-header">
          <button type="button" @click="goBack" class="back-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M19 12H5M12 19l-7-7 7-7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            返回问题列表
          </button>
          <h1 class="page-title">提问</h1>
          <p class="page-subtitle">提出你的问题，与社区共同探讨</p>
        </div>
        
        <div class="card-body">
          <div class="form-group">
            <label class="form-label">问题标题</label>
            <input 
              v-model="form.title" 
              type="text" 
              class="form-input"
              placeholder="请输入问题标题，简洁明确地描述你的问题"
            />
            <span class="form-hint">建议使用疑问句结尾，如"如何..."、"怎么..."</span>
          </div>

          <div class="form-group">
            <label class="form-label">详细描述</label>
            <textarea 
              v-model="form.content" 
              class="form-textarea"
              placeholder="请详细描述你的问题，包括：
1. 你想要实现什么功能
2. 你遇到了什么问题
3. 你尝试过的解决方法
4. 相关的代码片段（如有）"
              rows="10"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">标签</label>
            <div class="tags-selector">
              <button 
                v-for="tag in availableTags" 
                :key="tag"
                :class="['tag-option', { selected: form.tags.includes(tag) }]"
                @click="toggleTag(tag)"
              >
                {{ tag }}
              </button>
            </div>
            <span class="form-hint">选择 1-3 个相关标签</span>
          </div>

          <div class="form-actions">
            <button class="zh-btn zh-btn-outline" @click="onCancel">取消</button>
            <button class="zh-btn zh-btn-primary" :loading="loading" @click="onSubmit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M12 5v14M5 12h14" stroke-width="2" stroke-linecap="round"/>
              </svg>
              发布问题
            </button>
          </div>
        </div>

        <div class="card-footer">
          <div class="writing-tips">
            <h3>提问指南</h3>
            <ul>
              <li>描述清晰：说明你想要实现什么以及遇到了什么具体问题</li>
              <li>提供背景：说明你的环境、版本和相关配置</li>
              <li>展示尝试：说明你已经尝试过哪些方法</li>
              <li>代码片段：附上相关代码有助于他人理解</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import questionService from '@/services/questionService'

const router = useRouter()
const loading = ref(false)

const goBack = () => {
  router.back()
}

const availableTags = [
  'Java', 'Spring Boot', 'MySQL', 'Vue.js', 
  'API', '数据库', '前端', '后端', 'Python'
]

const form = ref({
  title: '',
  content: '',
  tags: []
})

const toggleTag = (tag) => {
  const index = form.value.tags.indexOf(tag)
  if (index > -1) {
    form.value.tags.splice(index, 1)
  } else {
    if (form.value.tags.length >= 3) {
      ElMessage.warning('最多选择 3 个标签')
      return
    }
    form.value.tags.push(tag)
  }
}

const onSubmit = async () => {
  if (!form.value.title.trim()) {
    ElMessage.error('请输入问题标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.error('请输入问题内容')
    return
  }
  if (form.value.tags.length === 0) {
    ElMessage.warning('请至少选择一个标签')
    return
  }

  loading.value = true
  try {
    const tagsJson = JSON.stringify(form.value.tags)
    await questionService.publishQuestion(
      form.value.title,
      form.value.content,
      tagsJson
    )
    ElMessage.success('问题发布成功')
    router.push('/questions')
  } catch (error) {
    ElMessage.error('发布问题失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const onCancel = () => {
  router.back()
}
</script>

<style scoped>
.publish-question-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 16px;
}

.publish-card {
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, #8B0000 0%, #5C0000 100%);
  padding: 32px;
  position: relative;
  border-bottom: 3px solid #D4AF37;
  z-index: 1;
}

/* 装饰花纹 */
.card-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Ccircle cx='50' cy='50' r='45' fill='none' stroke='rgba(255,255,255,0.03)' stroke-width='1'/%3E%3C/svg%3E");
  opacity: 0.5;
}

.card-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  z-index: -1;
  right: 0;
  height: 4px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 4'%3E%3Cpath d='M0 4 Q25 0 50 4 T100 4' fill='none' stroke='%23D4AF37' stroke-width='2'/%3E%3C/svg%3E") repeat-x;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #D4AF37;
  font-size: 14px;
  margin-bottom: 16px;
  transition: color 0.2s;
  background: transparent;
  border: none;
  cursor: pointer;
  font-family: var(--font-serif);
  position: relative;
  z-index: 10;
  padding: 0;
}

.back-btn:hover {
  color: white;
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #D4AF37;
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 16px;
  transition: color 0.2s;
  font-family: var(--font-serif);
}

.back-link:hover {
  color: white;
}

.back-link svg {
  width: 18px;
  height: 18px;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 32px;
  color: #D4AF37;
  font-weight: 600;
  margin-bottom: 8px;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
  font-family: var(--font-serif);
  letter-spacing: 2px;
}

.card-body {
  padding: 32px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-family: var(--font-serif);
  font-size: 15px;
  color: #5C0000;
  margin-bottom: 10px;
  font-weight: 600;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  font-size: 15px;
  background: #FDFCFB;
  transition: all 0.2s;
  box-sizing: border-box;
  color: #2C2C2C;
}

.form-input:focus {
  outline: none;
  border-color: #5C0000;
  background: white;
  box-shadow: 0 0 0 3px rgba(92,0,0,0.1);
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  font-size: 15px;
  background: #FDFCFB;
  transition: all 0.2s;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
  box-sizing: border-box;
  color: #2C2C2C;
}

.form-textarea:focus {
  outline: none;
  border-color: #5C0000;
  background: white;
  box-shadow: 0 0 0 3px rgba(92,0,0,0.1);
}

.form-hint {
  display: block;
  font-size: 12px;
  color: var(--zh-ink-lighter);
  margin-top: 6px;
}

.tags-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-option {
  padding: 8px 16px;
  border: 1px solid #D4AF37;
  border-radius: 2px;
  background: transparent;
  font-size: 14px;
  color: #D4AF37;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-serif);
}

.tag-option:hover {
  border-color: #8B0000;
  color: #8B0000;
  background: rgba(139,0,0,0.05);
}

.tag-option.selected {
  background: linear-gradient(135deg, #8B0000 0%, #5C0000 100%);
  border-color: #D4AF37;
  color: #D4AF37;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--zh-paper-dark);
}

.card-footer {
  padding: 24px 32px;
  background: var(--zh-paper);
  border-top: 1px solid var(--zh-paper-dark);
}

.writing-tips h3 {
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--zh-ink);
  margin-bottom: 12px;
}

.writing-tips ul {
  margin: 0;
  padding-left: 20px;
}

.writing-tips li {
  font-size: 13px;
  color: var(--zh-ink-light);
  line-height: 1.8;
}

.zh-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 2px;
  font-size: 14px;
  font-family: var(--font-serif);
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
}

.zh-btn svg {
  width: 16px;
  height: 16px;
}

.zh-btn-outline {
  background: transparent;
  border: 1px solid #5C0000;
  color: #5C0000;
}

.zh-btn-outline:hover {
  border-color: #8B0000;
  color: #8B0000;
  background: rgba(92,0,0,0.05);
}

.zh-btn-primary {
  background: linear-gradient(135deg, #8B0000 0%, #5C0000 100%);
  border: 2px solid #D4AF37;
  color: #D4AF37;
}

.zh-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212,175,55,0.4);
  background: #D4AF37;
  color: #5C0000;
}

@media (max-width: 768px) {
  .card-header,
  .card-body {
    padding: 24px 16px;
  }
  
  .card-footer {
    padding: 24px 16px;
  }
}
</style>