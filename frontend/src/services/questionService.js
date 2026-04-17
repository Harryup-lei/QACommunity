import api from '@/utils/api'

const questionService = {
  publishQuestion(title, content, tags) {
    return api.post('/questions', null, {
      params: { title, content, tags }
    })
  },

  getQuestionDetail(id) {
    return api.get(`/questions/${id}`)
  },

  getQuestionList(sortBy = 'latest', tags = null, solved = null, pageNum = 1, pageSize = 20) {
    return api.get('/questions', {
      params: { sortBy, tags, solved, pageNum, pageSize }
    })
  },

  searchQuestions(keyword, pageNum = 1, pageSize = 20) {
    return api.get('/questions/search', {
      params: { keyword, pageNum, pageSize }
    })
  },

  getUserQuestions(userId, pageNum = 1, pageSize = 20) {
    return api.get(`/questions/user/${userId}`, {
      params: { pageNum, pageSize }
    })
  },

  updateQuestion(id, title, content, tags) {
    return api.put(`/questions/${id}`, null, {
      params: { title, content, tags }
    })
  },

  deleteQuestion(id) {
    return api.delete(`/questions/${id}`)
  },

  acceptAnswer(questionId, answerId) {
    return api.post(`/questions/${questionId}/accept/${answerId}`)
  }
}

export default questionService
