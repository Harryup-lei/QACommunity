import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Questions from '@/pages/Questions.vue'
import QuestionDetail from '@/pages/QuestionDetail.vue'
import PublishQuestion from '@/pages/PublishQuestion.vue'
import PublishAnswer from '@/pages/PublishAnswer.vue'
import Login from '@/pages/Login.vue'
import Register from '@/pages/Register.vue'
import Ranking from '@/pages/Ranking.vue'
import Tags from '@/pages/Tags.vue'
import Profile from '@/pages/Profile.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: '/questions'
  },
  {
    path: '/questions',
    name: 'Questions',
    component: Questions
  },
  {
    path: '/question/:id',
    name: 'QuestionDetail',
    component: QuestionDetail
  },
  {
    path: '/publish-question',
    name: 'PublishQuestion',
    component: PublishQuestion,
    meta: { requiresAuth: true }
  },
  {
    path: '/publish-answer/:id',
    name: 'PublishAnswer',
    component: PublishAnswer,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/ranking',
    name: 'Ranking',
    component: Ranking
  },
  {
    path: '/tags',
    name: 'Tags',
    component: Tags
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查登录状态
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 检查是否需要登录
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 如果没有 token，重定向到登录页
    if (!userStore.token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
