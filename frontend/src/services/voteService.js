import api from '@/utils/api'

const voteService = {
  vote(targetType, targetId) {
    return api.post('/votes', null, {
      params: { targetType, targetId }
    })
  },

  unvote(targetType, targetId) {
    return api.delete('/votes', {
      params: { targetType, targetId }
    })
  },

  checkVote(targetType, targetId) {
    return api.get('/votes/check', {
      params: { targetType, targetId }
    })
  },

  getVoteCount(targetType, targetId) {
    return api.get('/votes/count', {
      params: { targetType, targetId }
    })
  }
}

export default voteService
