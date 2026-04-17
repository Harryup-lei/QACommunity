import api from '@/utils/api'

const answerService = {
  publishAnswer(questionId, content) {
    return api.post('/answers', null, {
      params: { questionId, content }
    })
  },

  getAnswerList(questionId, pageNum = 1, pageSize = 20) {
    return api.get('/answers', {
      params: { questionId, pageNum, pageSize }
    })
  },

  getUserAnswers(userId, pageNum = 1, pageSize = 20) {
    return api.get(`/answers/user/${userId}`, {
      params: { pageNum, pageSize }
    })
  },

  updateAnswer(id, content) {
    return api.put(`/answers/${id}`, null, {
      params: { content }
    })
  },

  deleteAnswer(id) {
    return api.delete(`/answers/${id}`)
  }
}

export default answerService
