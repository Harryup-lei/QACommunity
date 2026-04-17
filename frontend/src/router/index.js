import { createRouter, createWebHistory } from 'vue-router'
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
    component: Questions
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
    component: PublishQuestion
  },
  {
    path: '/publish-answer/:id',
    name: 'PublishAnswer',
    component: PublishAnswer
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
    component: Profile
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
