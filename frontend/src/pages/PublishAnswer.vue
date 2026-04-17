<template>
  <div class="publish-answer-page">
    <div class="page-container">
      <div class="publish-card">
        <div class="card-header">
          <router-link :to="`/question/${questionId}`" class="back-link">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M19 12H5M12 19l-7-7 7-7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            返回问题
          </router-link>
          <h1 class="page-title">回答</h1>
          <p class="page-subtitle">分享你的知识，帮助他人成长</p>
        </div>
        
        <div class="card-body">
          <div class="form-group">
            <label class="form-label">回答内容</label>
            <textarea 
              v-model="form.content" 
              class="form-textarea"
              placeholder="请输入你的回答：

1. 先给出结论
2. 再详细解释原因
3. 附上代码示例（如果适用）
4. 说明注意事项

建议使用 Markdown 格式，代码用 ``` 包裹"
              rows="12"
            ></textarea>
          </div>

          <div class="form-actions">
            <button class="zh-btn zh-btn-outline" @click="onCancel">取消</button>
            <button class="zh-btn zh-btn-primary" :loading="loading" @click="onSubmit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              发布回答
            </button>
          </div>
        </div>

        <div class="card-footer">
          <div class="writing-tips">
            <h3>回答指南</h3>
            <ul>
              <li>结论优先：先给出明确的答案，再详细解释</li>
              <li>代码示例：提供完整的、可运行的代码示例</li>
              <li>图文并茂：适当使用代码块格式</li>
              <li>说明来源：注明参考文档或经验的来源</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import answerService from '@/services/answerService'

const route = useRoute()
const router = useRouter()
const questionId = ref(route.params.id)
const loading = ref(false)

const form = ref({
  content: ''
})

const onSubmit = async () => {
  if (!form.value.content.trim()) {
    ElMessage.error('请输入回答内容')
    return
  }

  loading.value = true
  try {
    await answerService.publishAnswer(questionId.value, form.value.content)
    ElMessage.success('回答发布成功')
    router.push(`/question/${questionId.value}`)
  } catch (error) {
    ElMessage.error('发布回答失败')
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
.publish-answer-page {
  max-width: 800px;
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
  background: linear-gradient(135deg, var(--zh-blue) 0%, var(--zh-blue-dark) 100%);
  padding: 32px;
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

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 16px;
  transition: color 0.2s;
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
  font-size: 28px;
  color: white;
  font-weight: 600;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
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
  color: var(--zh-ink);
  margin-bottom: 10px;
  font-weight: 500;
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--zh-paper-dark);
  border-radius: 2px;
  font-size: 15px;
  background: var(--zh-paper);
  transition: all 0.2s;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
  box-sizing: border-box;
}

.form-textarea:focus {
  outline: none;
  border-color: var(--zh-blue);
  background: white;
  box-shadow: 0 0 0 3px rgba(61,90,128,0.1);
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
  border: 1px solid var(--zh-paper-darker);
  color: var(--zh-ink-light);
}

.zh-btn-outline:hover {
  border-color: var(--zh-ink);
  color: var(--zh-ink);
}

.zh-btn-primary {
  background: linear-gradient(135deg, var(--zh-blue) 0%, var(--zh-blue-dark) 100%);
  border: none;
  color: white;
}

.zh-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(61,90,128,0.3);
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