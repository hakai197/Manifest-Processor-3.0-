<template>
  <div class="container assignment-container">
    <router-link to="/" class="btn btn-secondary home-button">
      <i class="bi bi-house-door"></i> Home
    </router-link>
    <h1 class="mb-4">Assignment Management</h1>
    
    <div class="controls mb-4">
      <button class="btn btn-primary" @click="refreshAssignments">
        <i class="bi bi-arrow-clockwise"></i> Refresh
      </button>
      <button 
        class="btn btn-success" 
        @click="showCreateForm"
        v-if="!showForm"
      >
        <i class="bi bi-plus-lg"></i> Add Assignment
      </button>
    </div>

    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary"></div>
      <p>Loading assignments...</p>
    </div>

    <AssignmentList 
      :assignments="assignments"
      @edit="handleEdit"
      @delete="handleDelete"
    />

    <div v-if="showForm" class="form-container mt-4">
      <AssignmentForm 
        :assignment="currentAssignment"
        :employees="employees"
        :trailers="trailers"
        @save="handleSave"
        @cancel="hideForm"
      />
    </div>
      <ConfirmModal
    :show="showDeleteModal"
    title="Confirm Delete"
    message="Are you sure you want to delete this assignment?"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />
  </div>
</template>

<script>
import AssignmentService from '../services/AssignmentService';
import AssignmentList from '../components/AssignmentList.vue';
import AssignmentForm from '../components/AssignmentForm.vue';
import ConfirmModal from '../components/ConfirmModal.vue';
import axios from 'axios';

export default {
  components: {
    AssignmentList,
    AssignmentForm,
    ConfirmModal
  },
  data() {
    return {
      assignments: [],
      employees: [],
      trailers: [],
      currentAssignment: null,
      showForm: false,
      loading: false,
      showDeleteModal: false,
      assignmentToDelete: null, 
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    loadData() {
      this.loading = true;
      
      Promise.all([
        AssignmentService.getAll().then(res => this.assignments = res.data),
        axios.get(import.meta.env.VITE_REMOTE_API + '/api/unloaders').then(res => this.employees = res.data),
        axios.get(import.meta.env.VITE_REMOTE_API + '/api/trailers').then(res => this.trailers = res.data)
      ]).finally(() => {
        this.loading = false;
      });
    },
    refreshAssignments() {
      this.loading = true;
      AssignmentService.getAll()
        .then(res => this.assignments = res.data)
        .finally(() => this.loading = false);
    },
    showCreateForm() {
      this.currentAssignment = {
        assignmentId: null,
        employeeId: null,
        trailerId: null,
        doorNumber: '',
        status: 'Pending'
      };
      this.showForm = true;
    },
    handleEdit(assignment) {
      this.currentAssignment = { ...assignment };
      this.showForm = true;
    },
    hideForm() {
      this.showForm = false;
      this.currentAssignment = null;
    },
    handleSave(assignment) {
      this.loading = true;
      const operation = assignment.assignmentId 
        ? AssignmentService.update(assignment.assignmentId, assignment)
        : AssignmentService.create(assignment);
      
      operation.finally(() => {
        this.hideForm();
        this.refreshAssignments();
      });
    },
 handleDelete(id) {
      this.assignmentToDelete = id;
      this.showDeleteModal = true;
    },
    confirmDelete() {
      this.loading = true;
      AssignmentService.delete(this.assignmentToDelete)
        .then(() => {
          this.$store.dispatch('showSuccess', 'Assignment deleted successfully');
          this.refreshAssignments();
        })
        .catch(error => {
          this.handleApiError(error, 'deleting assignment');
        })
        .finally(() => {
          this.loading = false;
          this.showDeleteModal = false;
          this.assignmentToDelete = null;
        });
    },
    cancelDelete() {
      this.showDeleteModal = false;
      this.assignmentToDelete = null;
    }
  }
};
</script>

<style scoped>
.assignment-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.home-button {
  margin-bottom: 20px;
}

.controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.form-container {
  background: #f8f9fa;
  border-radius: 5px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.mt-4 {
  margin-top: 1.5rem;
}

.text-center {
  text-align: center;
}

@media (max-width: 425px) {
  .col-md-4.text-right, .col-md-8 {
    margin-top: 10px;
  }
}
</style>