import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/assignment',
    name: 'assignment',
    component: () => import('../views/AssignmentView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/trailer',
    name: 'trailer',
    component: () => import('../views/TrailerView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/shipper',
    name: 'shipper',
    component: () => import('../views/ShipperView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/employee',
    name: 'employee',
    component: () => import('../views/EmployeeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router