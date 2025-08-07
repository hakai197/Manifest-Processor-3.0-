<template>
  <div class="card">
    <div class="card-header">
      <h2>Employee List</h2>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Employee #</th>
            <th>Shift</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="unloader in unloaders" :key="unloader.employeeId">
            <td>{{ unloader.employeeId }}</td>
            <td>{{ unloader.name }}</td>
            <td>{{ unloader.employeeNumber }}</td>
            <td>{{ unloader.shift }}</td>
            <td>
              <button 
                class="btn btn-primary btn-sm mr-2" 
                @click="handleEditClick(unloader)"
              >
                Edit
              </button>
              <button 
                class="btn btn-danger btn-sm" 
                @click="handleDeleteClick(unloader.employeeId)"
                :disabled="!isAdmin"
                :title="isAdmin ? '' : 'Only administrators can delete employees'"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UnloaderList',
  props: {
    unloaders: {
      type: Array,
      required: true
    }
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  methods: {
    handleEditClick(unloader) {
      this.$emit('edit', unloader);
    },
    handleDeleteClick(employeeId) {
      this.$emit('delete', employeeId);
    }
  }
};
</script>

<style scoped>
.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
.mr-2 {
  margin-right: 0.5rem;
}
.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>