import api from '@/utils/api'

const userService = {
  register(username, email, password) {
    return api.post('/users/register', { username, email, password })
  },

  login(username, password) {
    return api.post('/users/login', { username, password })
  },

  getUserInfo(id) {
    return api.get(`/users/${id}`)
  },

  updateUserInfo(id, avatar, bio) {
    return api.put(`/users/${id}`, { avatar, bio })
  },

  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/users/upload-avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getUserPoints(id) {
    return api.get(`/users/${id}/points`)
  },

  getPointsRanking(limit = 100) {
    return api.get('/users/ranking', {
      params: { limit }
    })
  }
}

export default userService