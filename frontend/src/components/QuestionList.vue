<template>
  <div class="question-list">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>问题列表</span>
          <el-button type="primary" @click="onPublishQuestion">发布问题</el-button>
        </div>
      </template>

      <!-- 排序和筛选 -->
      <div class="filters">
        <el-radio-group v-model="sortBy" @change="onSortChange">
          <el-radio label="latest">最新</el-radio>
          <el-radio label="hot">热门</el-radio>
          <el-radio label="unsolved">未解决</el-radio>
        </el-radio-group>

        <el-input
          v-model="searchKeyword"
          placeholder="搜索问题..."
          @keyup.enter="onSearch"
          style="width: 200px; margin-left: 20px;"
        />
        <el-button @click="onSearch">搜索</el-button>
      </div>

      <!-- 问题列表 -->
      <el-table :data="questions" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="title" label="标题" width="400">
          <template #default="{ row }">
            <router-link :to="`/question/${row.id}`" class="title-link">
              {{ row.title }}
            </router-link>
            <span v-if="row.isSolved" class="solved-tag">已解决</span>
          </template>
        </el-table-column>
        <el-table-column prop="answersCount" label="回答" width="80" />
        <el-table-column prop="views" label="浏览" width="80" />
        <el-table-column prop="votes" label="点赞" width="80" />
        <el-table-column prop="author.username" label="提问者" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        @current-change="onPageChange"
        style="margin-top: 20px; text-align: center;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import questionService from '@/services/questionService'

const router = useRouter()
const questions = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const sortBy = ref('latest')
const searchKeyword = ref('')

const loadQuestions = async () => {
  try {
    const response = await questionService.getQuestionList(
      sortBy.value,
      null,
      null,
      currentPage.value,
      pageSize.value
    )
    questions.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('Failed to load questions:', error)
  }
}

const onSortChange = () => {
  currentPage.value = 1
  loadQuestions()
}

const onSearch = async () => {
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
    questions.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('Failed to search questions:', error)
  }
}

const onPageChange = () => {
  loadQuestions()
}

const onPublishQuestion = () => {
  router.push('/publish-question')
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.question-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.title-link {
  color: #409eff;
  text-decoration: none;
}

.title-link:hover {
  text-decoration: underline;
}

.solved-tag {
  background-color: #67c23a;
  color: white;
  padding: 2px 8px;
  border-radius: 3px;
  margin-left: 10px;
  font-size: 12px;
}
</style>
