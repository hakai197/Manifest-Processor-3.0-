<template>
  <div class="container">
    <router-link to="/" class="btn btn-secondary mb-4">Home</router-link>
    <h1 class="mb-4">Order Management</h1>
    
    <button 
      class="btn btn-primary mb-4" 
      @click="handleAddClick"
      :disabled="!isAdmin"
      :title="isAdmin ? '' : 'Only administrators can add orders'"
    >
      Add New Order
    </button>
    
   <div class="search-container mb-4">
  <div class="input-group">
    <input 
      type="text" 
      class="form-control" 
      placeholder="Search by order number..."
      v-model="searchTerm"
      @keyup.enter="handleSearch"
    >
    <button class="btn btn-success" @click="handleSearch">
      <i class="bi bi-search"></i> Search
    </button>
    <button class="btn btn-outline-secondary" @click="clearSearch">
      Clear
    </button>
  </div>
</div>
    
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
      </div>
    </div>
    
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    
    <OrderList 
      ref="orderList"
      :orders="ordersWithDetails" 
      :customers="customers"
      :shippers="shippers"
      :trailers="trailers"
      @edit="handleEdit" 
      @delete="handleDelete"
      class="mb-4"
    />
    
    <div ref="formContainer">
      <OrderForm 
        v-if="showForm" 
        :order="currentOrder" 
        :customers="customers"
        :shippers="shippers"
        :trailers="trailers"
        @save="handleSave" 
        @cancel="handleCancel" 
      />
    </div>
        <ConfirmModal
    :show="showDeleteModal"
    title="Confirm Delete"
    message="Are you sure you want to delete this order?"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />
  </div>
</template>

<script>
import OrderService from '@/services/OrderService';
import CustomerService from '@/services/CustomerService';
import ShipperService from '@/services/ShipperService';
import TrailerService from '@/services/TrailerService';
import OrderList from '@/components/OrderList.vue';
import OrderForm from '@/components/OrderForm.vue';
import ConfirmModal from '../components/ConfirmModal.vue';

export default {
  name: 'OrdersView',
  components: {
    OrderList,
    OrderForm,
    ConfirmModal,
    
  },
  data() {
    return {
      orders: [],
      customers: [],
      shippers: [],
      trailers: [],
      showForm: false,
      currentOrder: null,
      error: null,
      loading: false,
      searchTerm: '',
      searchMode: false,
      showDeleteModal: false,
      orderToDelete: null
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    },
    ordersWithDetails() {
      return this.orders.map(order => {
        const customer = this.customers.find(c => c.customerId === order.customerId);
        const shipper = this.shippers.find(s => s.shipperId === order.shipperId);
        const trailer = this.trailers.find(t => t.trailerId === order.trailerId);
        
        return {
          ...order,
          customerName: customer ? customer.name : 'Unknown',
          shipperName: shipper ? shipper.name : 'Unknown',
          trailerNumber: trailer ? trailer.trailerNumber : 'Unknown'
        };
      });
    }
  },
  created() {
    this.fetchAllData();
  },
  methods: {
    fetchAllData() {
      this.loading = true;
      this.error = null;
      
      Promise.all([
        OrderService.getAll(),
        CustomerService.getAll(),
        ShipperService.getAll(),
        TrailerService.getAll()
      ])
      .then(responses => {
        this.orders = responses[0].data;
        this.customers = responses[1].data;
        this.shippers = responses[2].data;
        this.trailers = responses[3].data;
      })
      .catch(error => {
        console.error('Error fetching data:', error);
        this.handleApiError(error, 'load data');
      })
      .finally(() => {
        this.loading = false;
      });
    },
    
    handleSearch() {
      if (!this.searchTerm.trim()) {
        this.clearSearch();
        return;
      }
      
      this.loading = true;
      this.error = null;
      this.searchMode = true;
      
      OrderService.getByNumber(this.searchTerm)
        .then(response => {
          this.orders = [response.data];
        })
        .catch(error => {
          console.error('Search error:', error);
          this.error = 'Order not found';
          this.orders = [];
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    clearSearch() {
      this.searchTerm = '';
      this.searchMode = false;
      this.fetchAllData();
    },
    
    handleAddClick() {
      if (this.isAdmin) {
        this.currentOrder = {
          orderId: null,
          orderNumber: '',
          customerId: null,
          shipperId: null,
          trailerId: null,
          doorNumber: '',
          handlingUnit: 0,
          weight: 0,
          status: 'Pending'
        };
        this.showForm = true;
        this.$nextTick(() => {
          this.scrollToForm();
        });
      } else {
        alert('Only administrators can add new orders');
      }
    },
    
    handleEdit(order) {
      this.currentOrder = { ...order };
      this.showForm = true;
      this.$nextTick(() => {
        this.scrollToForm();
      });
    },
    
     handleDelete(orderNumber) {
      if (!this.isAdmin) {
        this.$store.dispatch('showError', 'Only administrators can delete orders');
        return;
      }
      this.orderToDelete = orderNumber;
      this.showDeleteModal = true;
    },
    confirmDelete() {
      this.loading = true;
      OrderService.delete(this.orderToDelete)
        .then(() => {
          this.$store.dispatch('showSuccess', 'Order deleted successfully');
          this.fetchAllData();
        })
        .catch(error => {
          this.handleApiError(error, 'delete order');
        })
        .finally(() => {
          this.loading = false;
          this.showDeleteModal = false;
          this.orderToDelete = null;
        });
    },
    cancelDelete() {
      this.showDeleteModal = false;
      this.orderToDelete = null;
    },
    
    handleSave(order) {
      if (!this.validateOrder(order)) {
        return;
      }

      this.loading = true;
      this.error = null;

      const isUpdate = !!order.orderId;
      const saveOperation = isUpdate 
        ? OrderService.update(order.orderNumber, order)
        : OrderService.create(order);

      saveOperation
        .then(() => {
          this.$store.dispatch('showSuccess', 
            isUpdate ? 'Order updated successfully' : 'Order created successfully');
          this.showForm = false;
          this.currentOrder = null;
          this.fetchAllData();
        })
        .catch(error => {
          this.handleApiError(error, isUpdate ? 'update' : 'create');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    handleCancel() {
      this.showForm = false;
      this.currentOrder = null;
    },
    
    validateOrder(order) {
      if (!order.orderNumber || !order.customerId || !order.shipperId || 
          !order.trailerId || !order.doorNumber) {
        this.error = 'Please fill all required fields';
        return false;
      }
      if (order.handlingUnit < 0 || order.weight < 0) {
        this.error = 'Handling units and weight must be positive numbers';
        return false;
      }
      return true;
    },
    
    handleApiError(error, action) {
      console.error(`Error trying to ${action}:`, error);
      if (error.response) {
        if (error.response.status === 401) {
          this.$router.push('/login');
          return;
        }
        this.error = error.response.data?.message || 
          `Server error (${error.response.status}) when trying to ${action}`;
      } else {
        this.error = `Network error when trying to ${action}`;
      }
    },
    
    scrollToForm() {
      this.$refs.formContainer?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'nearest'
      });
    },
    
    scrollToList() {
      this.$refs.orderList?.$el?.scrollIntoView({ 
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
  margin-right: 10px;
}

.alert {
  margin-top: 20px;
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

.search-container {
  max-width: 600px;
}

.btn-success {
  background-color: #28a745;
  border-color: #28a745;
  color: white;
}

.btn-success:hover {
  background-color: #218838;
  border-color: #1e7e34;
}

.btn-success:disabled {
  background-color: #5cb85c;
  border-color: #5cb85c;
}


.input-group .btn {
  z-index: 0;
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