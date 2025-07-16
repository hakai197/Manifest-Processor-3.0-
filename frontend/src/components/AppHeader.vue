<template>
  <header>
    <div class="logo-container">
      <img src="../assets/img/NMShipping.png" alt="NM Shipping Logo">
    </div>
    <div class="header-top">
      <h1>{{ currentRouteTitle }}</h1>
      <div class="user-info" v-if="authStore.user">
        <i class="fas fa-user"></i> {{ authStore.user.username }} ({{ authStore.user.role }})
      </div>
      <button @click="logout" class="logout-btn">
        <i class="fas fa-sign-out-alt"></i> Logout
      </button>
    </div>
    
    <nav class="desktop-nav">
      <ul>
        <li><router-link to="/" class="nav-btn">Dashboard</router-link></li>
        <li><router-link to="/assignment" class="nav-btn">Assignment Manager</router-link></li>
        <li><router-link to="/trailer" class="nav-btn">Trailer Management</router-link></li>
        <li><router-link to="/shipper" class="nav-btn">Shipper Management</router-link></li>
        <li><router-link to="/employee" class="nav-btn">Employee Management</router-link></li>
      </ul>
    </nav>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const currentRouteTitle = computed(() => {
  const routeMap = {
    '/': 'Dock Management System',
    '/assignment': 'Assignment Manager',
    '/trailer': 'Trailer Management',
    '/shipper': 'Shipper Management',
    '/employee': 'Employee Management'
  }
  return routeMap[route.path] || 'Dock Management System'
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
header {
  background-color: var(--primary-color);
  color: white;
  position: relative;
}

.header-top {
  padding: 0.5rem 1rem;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  position: relative;
  min-height: 70px;
  padding-left: 80px;
}

.header-top h1 {
  font-size: 2rem;
  flex-grow: 0;
  text-align: left;
  margin-left: 1rem;
}

.logo-container {
  position: absolute;
  left: 1rem;
  top: 0.5rem;
  padding: 0;
}

.logo-container img {
  height: 60px;
  width: auto;
  max-width: 100%;
}

.desktop-nav {
  background-color: white;
  padding: 1rem;
  border-top: 1px solid #ddd;
}

.desktop-nav ul {
  display: flex;
  justify-content: center;
  list-style: none;
  gap: 1rem;
  flex-wrap: wrap;
}

.nav-btn {
  background-color: var(--secondary-color);
  color: white;
  text-decoration: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: bold;
  transition: background-color 0.3s;
  display: inline-block;
}

.nav-btn:hover {
  background-color: #0066CC;
}

.user-info {
  margin-left: auto;
  background: rgba(255, 255, 255, 0.2);
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.logout-btn {
  margin-left: 1rem;
  padding: 0.5rem 1rem;
  background-color: var(--accent-color);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #c0392b;
}

.router-link-active {
  background-color: var(--dark-color);
}
</style>