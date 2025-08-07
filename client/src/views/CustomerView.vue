<template>
  <div class="container">
    <router-link to="/" class="btn btn-secondary mb-4">Home</router-link>
    <h1 class="mb-4">Customer Management</h1>
    
  
    <div class="row mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <input
            type="text"
            class="form-control"
            placeholder="Search customers by name..."
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
          :title="isAdmin ? '' : 'Only administrators can add customers'"
        >
          <i class="bi bi-plus-lg"></i> Add Customer
        </button>
      </div>
    </div>
    
    <!-- Status Messages -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
      </div>
    </div>
    
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    
    <div v-if="searchMode && customers.length === 0" class="alert alert-info">
      No customers found matching "{{ searchQuery }}"
    </div>
    
    <!-- Customer List -->
    <CustomerList 
      ref="customerList"
      :customers="customers" 
      @edit="handleEdit" 
      @delete="handleDelete" 
      class="mb-4"
    />
    
    <!-- Customer Form -->
    <div ref="formContainer">
      <CustomerForm 
        v-if="showForm" 
        :customer="currentCustomer" 
        @save="handleSave" 
        @cancel="handleCancel" 
      />
    </div>
     <ConfirmModal
    :show="showDeleteModal"
    title="Confirm Delete"
    message="Are you sure you want to delete this customer?"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />
  </div>
  
  
</template>

<script>
import CustomerService from '../services/CustomerService';
import CustomerList from '../components/CustomerList.vue';
import CustomerForm from '../components/CustomerForm.vue';
import ConfirmModal from '@/components/ConfirmModal.vue';


export default {
  name: 'CustomersView',
  components: {
    CustomerList,
    CustomerForm,
    ConfirmModal
  },
  data() {
    return {
      customers: [],
      showForm: false,
      currentCustomer: null,
      error: null,
      loading: false,
      searchQuery: '',
      searchMode: false,
      showDeleteModal: false,
      customerToDelete: null,
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  created() {
    this.fetchCustomers();
  },
  methods: {
    fetchCustomers() {
      this.loading = true;
      this.error = null;
      this.searchMode = false;
      
      CustomerService.getAll()
        .then(response => {
          this.customers = response.data;
        })
        .catch(error => {
          this.handleApiError(error, 'fetching customers');
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
      
      CustomerService.search(this.searchQuery.trim())
        .then(response => {
          this.customers = response.data;
        })
        .catch(error => {
          if (error.response && error.response.status === 404) {
            this.customers = [];
          } else {
            this.handleApiError(error, 'searching customers');
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    clearSearch() {
      this.searchQuery = '';
      if (this.searchMode) {
        this.fetchCustomers();
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
        this.currentCustomer = {
          customerId: null,
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
        this.$store.dispatch('showError', 'Only administrators can add new customers');
      }
    },
    handleEdit(customer) {
      this.currentCustomer = { ...customer };
      this.showForm = true;
      this.$nextTick(() => {
        this.scrollToForm();
      });
    },
    handleSave(customer) {
      const savePromise = customer.customerId 
        ? CustomerService.update(customer.customerId, customer)
        : CustomerService.create(customer);

      savePromise
        .then(() => {
          this.$store.dispatch('showSuccess', 
            customer.customerId 
              ? 'Customer updated successfully' 
              : 'Customer created successfully'
          );
          this.showForm = false;
          this.currentCustomer = null;
          this.searchMode ? this.handleSearch() : this.fetchCustomers();
        })
        .catch(error => {
          this.handleApiError(error, 'saving customer');
        });
    },
 handleDelete(customerId) {
      if (this.isAdmin) {
        this.customerToDelete = customerId;
        this.showDeleteModal = true;
      } else {
        this.$store.dispatch('showError', 'Only administrators can delete customers');
      }
    },
    
  
    confirmDelete() {
      this.loading = true;
      CustomerService.delete(this.customerToDelete)
        .then(() => {
          this.$store.dispatch('showSuccess', 'Customer deleted successfully');
          this.searchMode ? this.handleSearch() : this.fetchCustomers();
        })
        .catch(error => {
          this.handleApiError(error, 'deleting customer');
        })
        .finally(() => {
          this.loading = false;
          this.showDeleteModal = false;
          this.customerToDelete = null;
        });
    },
    
    cancelDelete() {
      this.showDeleteModal = false;
      this.customerToDelete = null;
    },
    handleCancel() {
      this.showForm = false;
      this.currentCustomer = null;
      this.scrollToList();
    },
    scrollToForm() {
      this.$refs.formContainer?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'nearest'
      });
    },
    scrollToList() {
      this.$refs.customerList?.$el?.scrollIntoView({ 
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