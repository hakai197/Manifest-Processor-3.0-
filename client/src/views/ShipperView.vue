<template>
  <div class="container">
    <router-link to="/" class="btn btn-secondary mb-4">Home</router-link>
    <h1 class="mb-4">Shipper Management</h1>
    
   
    <div class="row mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <input
            type="text"
            class="form-control"
            placeholder="Search shippers by name..."
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
          :title="isAdmin ? '' : 'Only administrators can add shippers'"
        >
          <i class="bi bi-plus-lg"></i> Add Shipper
        </button>
      </div>
    </div>
    
 
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
      </div>
    </div>
    
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    
    <div v-if="searchMode && shippers.length === 0" class="alert alert-info">
      No shippers found matching "{{ searchQuery }}"
    </div>
    
    
    <ShipperList 
      ref="shipperList"
      :shippers="shippers" 
      @edit="handleEdit" 
      @delete="handleDelete" 
      class="mb-4"
    />
    
    
    <div ref="formContainer">
      <ShipperForm 
        v-if="showForm" 
        :shipper="currentShipper" 
        @save="handleSave" 
        @cancel="handleCancel" 
        :is-submitting="formSubmitting"
      />
    </div>
      <ConfirmModal
    :show="showDeleteModal"
    title="Confirm Delete"
    message="Are you sure you want to delete this shipper?"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />
  </div>
</template>

<script>
import ShipperService from '../services/ShipperService';
import ShipperList from '../components/ShipperList.vue';
import ShipperForm from '../components/ShipperForm.vue';
import ConfirmModal from '../components/ConfirmModal.vue';

export default {
  name: 'ShippersView',
  components: {
    ShipperList,
    ShipperForm,
    ConfirmModal
  },
  data() {
    return {
      shippers: [],
      showForm: false,
      currentShipper: null,
      error: null,
      loading: false,
      formSubmitting: false,
      searchQuery: '',
      searchMode: false,
      showDeleteModal: false,
      shipperToDelete: null
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  created() {
    this.fetchShippers();
  },
  methods: {
    fetchShippers() {
      this.loading = true;
      this.error = null;
      this.searchMode = false;
      
      ShipperService.getAll()
        .then(response => {
          this.shippers = response.data;
        })
        .catch(error => {
          this.handleApiError(error, 'fetching shippers');
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
      
      ShipperService.search(this.searchQuery.trim())
        .then(response => {
          this.shippers = response.data;
        })
        .catch(error => {
          if (error.response && error.response.status === 404) {
            this.shippers = [];
          } else {
            this.handleApiError(error, 'searching shippers');
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    clearSearch() {
      this.searchQuery = '';
      if (this.searchMode) {
        this.fetchShippers();
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
        this.currentShipper = {
          shipperId: null,
          name: '',
          address: '',
          city: '',
          state: '',
          zipCode: ''
        };
        this.showForm = true;
        this.$nextTick(() => {
          this.scrollToForm();
        });
      } else {
        this.$store.dispatch('showError', 'Only administrators can add new shippers');
      }
    },
    handleEdit(shipper) {
      this.currentShipper = { ...shipper };
      this.showForm = true;
      this.$nextTick(() => {
        this.scrollToForm();
      });
    },
    handleSave(shipper) {
      this.formSubmitting = true;
      const savePromise = shipper.shipperId 
        ? ShipperService.update(shipper.shipperId, shipper)
        : ShipperService.create(shipper);

      savePromise
        .then(() => {
          this.$store.dispatch('showSuccess', 
            shipper.shipperId 
              ? 'Shipper updated successfully' 
              : 'Shipper created successfully'
          );
          this.showForm = false;
          this.currentShipper = null;
          this.searchMode ? this.handleSearch() : this.fetchShippers();
        })
        .catch(error => {
          this.handleApiError(error, 'saving shipper');
        })
        .finally(() => {
          this.formSubmitting = false;
        });
    },
   handleDelete(shipperId) {
      if (this.isAdmin) {
        this.shipperToDelete = shipperId;
        this.showDeleteModal = true;
      } else {
        this.$store.dispatch('showError', 'Only administrators can delete shippers');
      }
    },
    confirmDelete() {
      this.loading = true;
      ShipperService.delete(this.shipperToDelete)
        .then(() => {
          this.$store.dispatch('showSuccess', 'Shipper deleted successfully');
          this.searchMode ? this.handleSearch() : this.fetchShippers();
        })
        .catch(error => {
          this.handleApiError(error, 'deleting shipper');
        })
        .finally(() => {
          this.loading = false;
          this.showDeleteModal = false;
          this.shipperToDelete = null;
        });
    },
    cancelDelete() {
      this.showDeleteModal = false;
      this.shipperToDelete = null;
    },
    handleCancel() {
      this.showForm = false;
      this.currentShipper = null;
      this.scrollToList();
    },
    scrollToForm() {
      this.$refs.formContainer?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'nearest'
      });
    },
    scrollToList() {
      this.$refs.shipperList?.$el?.scrollIntoView({ 
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