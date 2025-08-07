<template>
  <main class="auth-view">
    <div class="auth-card">
      <div class="card-header">Create Account</div>
      <div class="card-content">
        <form v-on:submit.prevent="register" class="auth-form">
          <div class="form-group">
            <label for="username">Username</label>
            <input
              type="text"
              id="username"
              placeholder="Username"
              v-model="user.username"
              required
              autofocus
            />
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input
              type="password"
              id="password"
              placeholder="Password"
              v-model="user.password"
              required
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              placeholder="Confirm Password"
              v-model="user.confirmPassword"
              required
            />
          </div>
          <div class="form-group">
            <label for="role">Role</label>
            <select id="role" v-model="user.role" required>
              <option value="ROLE_USER">User</option>
              <option value="ROLE_ADMIN">Admin</option>
            </select>
          </div>
          <button type="submit" class="submit-btn">Register</button>
        </form>
        <div class="auth-footer">
          <p>Already have an account? <router-link v-bind:to="{ name: 'login' }">Sign In!</router-link></p>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import authService from "../services/AuthService";

export default {
  data() {
    return {
      user: {
        username: "",
        password: "",
        confirmPassword: "",
        role: "ROLE_USER",
      },
    };
  },
  methods: {
    register() {
      if (this.user.password !== this.user.confirmPassword) {
        alert("Passwords do not match!");
        return;
      }
      authService
        .register(this.user)
        .then((response) => {
          if (response.status == 201) {
            this.$router.push("/login");
          }
        })
        .catch((error) => {
          const response = error.response;
          if (!response) {
            alert(error);
          } else if (response.status === 400) {
            alert("Validation error: " + response.data.message);
          } else {
            alert(response.message);
          }
        });
    },
  },
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
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #2c3e50;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #228B22;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #1e7b1e;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
  color: #666;
}

.auth-footer a {
  color: #1E90FF;
  text-decoration: none;
}

.auth-footer a:hover {
  text-decoration: underline;
}
@media (max-width: 425px) {
  .col-md-4.text-right, .col-md-8 {
    margin-top: 10px;
  }
}
</style>
