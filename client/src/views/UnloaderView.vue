<template>
  <div class="container">
    <router-link to="/" class="btn btn-secondary mb-4">Home</router-link>
    <h1 class="mb-4">Employee Management</h1>
    

    <div class="row mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <input
            type="text"
            class="form-control"
            placeholder="Search employees by name or ID..."
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          >
          <div class="input-group-append">
            <button 
              class="btn btn-primary" 
              type="button" 
              @click="handleSearch"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm"></span>
              Search
            </button>
            <button 
              class="btn btn-outline-secondary" 
              type="button" 
              @click="clearSearch"
              :disabled="loading || !searchQuery"
            >
              Clear
            </button>
          </div>
        </div>
      </div>
      <div class="col-md-4 text-right">
        <button 
          class="btn btn-success" 
          @click="handleAddClick"
          :disabled="!isAdmin"
          :title="isAdmin ? '' : 'Only administrators can add employees'"
        >
          <i class="bi bi-plus-lg"></i> Add Employee
        </button>
      </div>
    </div>
    
   
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
      </div>
    </div>
    
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    
    <div v-if="searchMode && unloaders.length === 0" class="alert alert-info">
      No employees found matching "{{ searchQuery }}"
    </div>
    
   
    <UnloaderList 
      ref="unloaderList"
      :unloaders="unloaders" 
      @edit="handleEdit" 
      @delete="handleDelete" 
      class="mb-4"
    />
    
   
    <div ref="formContainer">
      <UnloaderForm 
        v-if="showForm" 
        :unloader="currentUnloader" 
        @save="handleSave" 
        @cancel="handleCancel" 
        :is-submitting="formSubmitting"
      />
    </div>
          <ConfirmModal
    :show="showDeleteModal"
    title="Confirm Delete"
    message="Are you sure you want to delete this Employee?"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />
  </div>
</template>

<script>
import UnloaderService from '../services/UnloaderService';
import UnloaderList from '../components/UnloaderList.vue';
import UnloaderForm from '../components/UnloaderForm.vue';
import ConfirmModal from '../components/ConfirmModal.vue';

export default {
  name: 'UnloadersView',
  components: {
    UnloaderList,
    UnloaderForm,
    ConfirmModal
  },
  data() {
    return {
      unloaders: [],
      showForm: false,
      currentUnloader: null,
      error: null,
      loading: false,
      formSubmitting: false,
      searchQuery: '',
      searchMode: false,
      showDeleteModal: false,
      employeeToDelete: null
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  created() {
    this.fetchUnloaders();
  },
  methods: {
    fetchUnloaders() {
      this.loading = true;
      this.error = null;
      this.searchMode = false;
      
      UnloaderService.getAll()
        .then(response => {
          this.unloaders = response.data;
        })
        .catch(error => {
          this.handleApiError(error, 'fetching employees');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleSearch() {
      if (!this.searchQuery.trim()) {
        return;
      }
      
      this.loading = true;
      this.error = null;
      this.searchMode = true;
      
      UnloaderService.search(this.searchQuery.trim())
        .then(response => {
          this.unloaders = response.data;
        })
        .catch(error => {
          if (error.response && error.response.status === 404) {
            this.unloaders = [];
          } else {
            this.handleApiError(error, 'searching employees');
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    clearSearch() {
      this.searchQuery = '';
      if (this.searchMode) {
        this.fetchUnloaders();
      }
    },
    handleApiError(error, action) {
      console.error(`Error ${action}:`, error);
      
      if (error.response && error.response.status === 401) {
        this.$store.dispatch('logout');
        this.$router.push('/login');
        this.$store.dispatch('showError', 'Session expired. Please log in again.');
      } else if (error.response && error.response.status === 403) {
        this.error = 'You do not have permission to perform this action';
        this.$store.dispatch('showError', this.error);
      } else {
        this.error = `Failed while ${action}. Please try again.`;
        this.$store.dispatch('showError', this.error);
      }
    },
    handleAddClick() {
      if (this.isAdmin) {
        this.currentUnloader = {
          employeeId: null,
          name: '',
          employeeNumber: '',
          shift: '2nd Shift'
        };
        this.showForm = true;
        this.$nextTick(() => {
          this.scrollToForm();
        });
      } else {
        this.$store.dispatch('showError', 'Only administrators can add new employees');
      }
    },
    handleEdit(unloader) {
      this.currentUnloader = { ...unloader };
      this.showForm = true;
      this.$nextTick(() => {
        this.scrollToForm();
      });
    },
    handleSave(unloader) {
      this.formSubmitting = true;
      const savePromise = unloader.employeeId 
        ? UnloaderService.update(unloader.employeeId, unloader)
        : UnloaderService.create(unloader);

      savePromise
        .then(() => {
          this.$store.dispatch('showSuccess', 
            unloader.employeeId 
              ? 'Employee updated successfully' 
              : 'Employee created successfully'
          );
          this.showForm = false;
          this.currentUnloader = null;
          this.searchMode ? this.handleSearch() : this.fetchUnloaders();
        })
        .catch(error => {
          this.handleApiError(error, 'saving employee');
        })
        .finally(() => {
          this.formSubmitting = false;
        });
    },
    handleDelete(employeeId) {
  if (this.isAdmin) {
    this.employeeToDelete = employeeId;
    this.showDeleteModal = true;
  } else {
    this.$store.dispatch('showError', 'Only administrators can delete employees');
  }
},
confirmDelete() {
  this.loading = true;
  UnloaderService.delete(this.employeeToDelete)
    .then(() => {
      this.$store.dispatch('showSuccess', 'Employee deleted successfully');
      this.searchMode ? this.handleSearch() : this.fetchUnloaders();
    })
    .catch(error => {
      this.handleApiError(error, 'deleting employee');
    })
    .finally(() => {
      this.loading = false;
      this.showDeleteModal = false;
      this.employeeToDelete = null;
    });
},
cancelDelete() {
  this.showDeleteModal = false;
  this.employeeToDelete = null;
},

    handleCancel() {
      this.showForm = false;
      this.currentUnloader = null;
      this.scrollToList();
    },
    scrollToForm() {
      this.$refs.formContainer?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'nearest'
      });
    },
    scrollToList() {
      this.$refs.unloaderList?.$el?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.btn {
  margin-right: 8px;
}

.alert {
  margin-top: 20px;
  margin-bottom: 20px;
}

.spinner-border {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  vertical-align: text-bottom;
  border: 0.25em solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spinner-border 0.75s linear infinite;
}

.input-group {
  margin-bottom: 0;
}

@keyframes spinner-border {
  to { transform: rotate(360deg); }
}

@media (max-width: 425px) {
  .col-md-4.text-right, .col-md-8 {
    margin-top: 10px;
  }
}
</style>