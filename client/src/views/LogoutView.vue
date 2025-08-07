<template>
  <main class="auth-view">
    <div class="auth-card">
      <div class="card-header">Logging Out</div>
      <div class="card-content">
        <p class="logout-message">You are being logged out...</p>
      </div>
    </div>
  </main>
</template>

<script>
export default {
  created() {
    this.$store.commit("LOGOUT");
    this.$router.push("/login");
  }
};
</script>

<style scoped>
.auth-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 2rem;
}

.auth-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.card-header {
  background-color: #1E90FF;
  color: white;
  padding: 1rem;
  font-weight: bold;
  font-size: 1.2rem;
  text-align: center;
  border-radius: 8px 8px 0 0;
}

.card-content {
  padding: 2rem;
  text-align: center;
}

.logout-message {
  font-size: 1.1rem;
  color: #2c3e50;
}
</style>