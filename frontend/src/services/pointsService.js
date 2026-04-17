import api from '@/utils/api'

const pointsService = {
  getPointsRanking(pageNum = 1, pageSize = 20) {
    return api.get('/points/ranking', {
      params: { pageNum, pageSize }
    })
  },

  getUserPointsHistory(userId, pageNum = 1, pageSize = 20) {
    return api.get(`/points/${userId}/history`, {
      params: { pageNum, pageSize }
    })
  }
}

export default pointsService
