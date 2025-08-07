<template>
  <div id="app">
    
    <header class="mobile-header" v-if="isMobile">
      <div class="mobile-header-top">
        <img src="/NMShipping.png" alt="NM Shipping Logo" class="mobile-logo">
        <h1 class="mobile-title">Dock Management</h1>
        <button class="mobile-menu-btn" @click="toggleMobileMenu">
          ☰
        </button>
      </div>
      <nav class="mobile-nav" :class="{ 'mobile-nav-open': mobileMenuOpen }">
        <ul>
          <li><router-link to="/assignments" @click="closeMenu">Assignments</router-link></li>
          <li><router-link to="/orders" @click="closeMenu">Orders</router-link></li>
          <li><router-link to="/customers" @click="closeMenu">Customers</router-link></li>
          <li><router-link to="/trailers" @click="closeMenu">Trailers</router-link></li>
          <li><router-link to="/shippers" @click="closeMenu">Shippers</router-link></li>
          <li><router-link to="/unloaders" @click="closeMenu">Employees</router-link></li>
        </ul>
      </nav>
    </header>

  
    <header class="desktop-header" v-if="!isMobile">
      <div class="logo-container">
        <img src="/NMShipping.png" alt="NM Shipping Logo">
      </div>
      <div class="header-top">
        <h1>Dock Management System</h1>
      </div>
      
      <nav class="desktop-nav">
        <ul>
          <li><router-link to="/assignments" class="nav-btn">Assignment Manager</router-link></li>
          <li><router-link to="/orders" class="nav-btn">Order Management</router-link></li>
          <li><router-link to="/customers" class="nav-btn">Customer Management</router-link></li>
          <li><router-link to="/trailers" class="nav-btn">Trailer Management</router-link></li>
          <li><router-link to="/shippers" class="nav-btn">Shipper Management</router-link></li>
          <li><router-link to="/unloaders" class="nav-btn">Employee Management</router-link></li>
        </ul>
      </nav>
    </header>

    <main>
      <nav class="auth-nav">
        <router-link v-bind:to="{ name: 'logout' }" v-if="$store.state.token" class="auth-btn">
          Logout
        </router-link>
        <router-link v-bind:to="{ name: 'login' }" v-else class="auth-btn">Login</router-link>
      </nav>
      <router-view />
    </main>

    <footer>
      <p>&copy; 2025 U197 Designs</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      mobileMenuOpen: false,
      isMobile: false
    }
  },
  methods: {
    toggleMobileMenu() {
      this.mobileMenuOpen = !this.mobileMenuOpen;
    },
    closeMenu() {
      this.mobileMenuOpen = false;
    },
    checkScreenSize() {
      this.isMobile = window.innerWidth <= 425;
    }
  },
  created() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.checkScreenSize);
  }
}
</script>

<style scoped>

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
  padding: 20px;
}

.auth-nav {
  text-align: right;
  margin-bottom: 20px;
}

.auth-btn {
  background-color: #1E90FF;
  color: white;
  text-decoration: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: bold;
  transition: background-color 0.3s;
  display: inline-block;
}

.auth-btn:hover {
  background-color: #0066CC;
}

footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  padding: 1rem;
  margin-top: auto;
}


.mobile-header {
  background-color: #228B22;
  color: white;
}

.mobile-header-top {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  justify-content: space-between;
}

.mobile-logo {
  height: 40px;
}

.mobile-title {
  font-size: 1.2rem;
  margin: 0;
  flex-grow: 1;
  text-align: center;
}

.mobile-menu-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0 0.5rem;
}

.mobile-nav {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease-out;
  background-color: #1a7a1a;
}

.mobile-nav-open {
  max-height: 500px;
}

.mobile-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mobile-nav li {
  border-bottom: 1px solid #2c8b2c;
}

.mobile-nav a {
  display: block;
  padding: 0.8rem 1rem;
  color: white;
  text-decoration: none;
  transition: background-color 0.3s;
}

.mobile-nav a:hover {
  background-color: #2c8b2c;
}


.desktop-header {
  background-color: #228B22;
  color: white;
  padding: 0;
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
  margin: 0;
  padding: 0;
}

.nav-btn {
  background-color: #1E90FF;
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


@media (max-width: 425px) {
  main {
    padding: 0.5rem;
  }
  
  .auth-nav {
    margin-bottom: 0.5rem;
  }
  
  .auth-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
  
  footer {
    padding: 0.5rem;
    font-size: 0.8rem;
  }
  
  .desktop-header {
    display: none;
  }
}

@media (min-width: 426px) {
  .mobile-header {
    display: none;
  }
}
</style>