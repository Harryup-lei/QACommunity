import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 恢复登录状态
  const savedToken = localStorage.getItem('token')
  const savedUser = localStorage.getItem('user')

  const user = ref(savedUser ? JSON.parse(savedUser) : null)
  const token = ref(savedToken || null)

  const setUser = (userData) => {
    user.value = userData
    if (userData) {
      localStorage.setItem('user', JSON.stringify(userData))
    } else {
      localStorage.removeItem('user')
    }
  }

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('userId')
  }

  return {
    user,
    token,
    setUser,
    setToken,
    logout
  }
})
