<template>
  <div class="answer-list">
    <el-table :data="answers" style="width: 100%;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="内容" width="400">
        <template #default="{ row }">
          <div v-html="markdownToHtml(row.content.substring(0, 100))"></div>
        </template>
      </el-table-column>
      <el-table-column prop="author.username" label="回答者" width="120" />
      <el-table-column prop="votes" label="点赞" width="80" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.userVoted ? 'primary' : 'default'"
            @click="onVote(row)"
          >
            {{ row.userVoted ? '已赞' : '点赞' }}
          </el-button>
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { marked } from 'marked'
import answerService from '@/services/answerService'
import voteService from '@/services/voteService'

const props = defineProps({
  questionId: Number
})

const answers = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const markdownToHtml = (md) => {
  return marked(md)
}

const loadAnswers = async () => {
  try {
    const response = await answerService.getAnswerList(
      props.questionId,
      currentPage.value,
      pageSize.value
    )
    answers.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('Failed to load answers:', error)
  }
}

const onVote = async (answer) => {
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
  }
}

const viewDetail = (answer) => {
  console.log('View answer detail:', answer)
}

const onPageChange = () => {
  loadAnswers()
}

onMounted(() => {
  loadAnswers()
})
</script>

<style scoped>
.answer-list {
  padding: 20px;
}
</style>
