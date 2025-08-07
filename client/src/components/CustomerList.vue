<template>
  <div class="card">
    <div class="card-header">
      <h2>Customer List</h2>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Address</th>
            <th>City</th>
            <th>State</th>
            <th>Zip Code</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customer in customers" :key="customer.customerId">
            <td>{{ customer.customerId }}</td>
            <td>{{ customer.name }}</td>
            <td>{{ customer.address }}</td>
            <td>{{ customer.city }}</td>
            <td>{{ customer.state }}</td>
            <td>{{ customer.zipCode }}</td>
            <td>
              <button class="btn btn-primary btn-sm mr-2" @click="handleEditClick(customer)">Edit</button>
              <button class="btn btn-danger btn-sm mr-2" @click="handleDeleteClick(customer.customerId)" :disabled="!isAdmin">
                Delete
              </button>
              <button class="btn btn-info btn-sm" @click="openOrdersModal(customer.name)">View Orders</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Orders for Customer: {{ selectedName }}</h3>
          <button class="close-button" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>Order #</th>
                <th>Shipper</th>
                <th>Door</th>
                <th>HU</th>
                <th>Weight</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.orderId">
                <td>{{ order.orderNumber }}</td>
                <td>{{ order.shipperName }}</td>
                <td>{{ order.doorNumber }}</td>
                <td>{{ order.handlingUnit }}</td>
                <td>{{ order.weight }}</td>
                <td><span :class="statusClass(order.status)">{{ order.status }}</span></td>
              </tr>
            </tbody>
          </table>
          <div v-if="orders.length === 0">No orders found.</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import OrderService from '@/services/OrderService';

export default {
  name: 'CustomerList',
  props: {
    customers: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      showModal: false,
      orders: [],
      selectedName: ''
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  methods: {
    handleEditClick(customer) {
      this.$emit('edit', customer);
    },
    handleDeleteClick(customerId) {
      if (this.isAdmin) {
        this.$emit('delete', customerId);
      } else {
        alert('Only administrators can delete customers');
      }
    },
    openOrdersModal(customerName) {
      OrderService.getByCustomer(customerName)
        .then(response => {
          this.orders = response.data;
          this.selectedName = customerName;
          this.showModal = true;
        })
        .catch(() => {
          alert('Failed to load orders for customer');
        });
    },
    closeModal() {
      this.showModal = false;
      this.orders = [];
      this.selectedName = '';
    },
    statusClass(status) {
      return {
        'badge badge-success': status === 'Completed',
        'badge badge-primary': status === 'In Progress',
        'badge badge-warning': status === 'Pending',
        'badge badge-danger': status === 'Cancelled'
      };
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
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: var(--primary-color);
  color: white;
}
.modal-body {
  padding: 1rem;
}
.close-button {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  color: white;
  cursor: pointer;
}
.badge {
  display: inline-block;
  padding: 0.25em 0.4em;
  font-size: 75%;
  font-weight: 700;
  border-radius: 0.25rem;
}
.badge-success {
  background-color: #28a745;
  color: #fff;
}
.badge-primary {
  background-color: #007bff;
  color: #fff;
}
.badge-warning {
  background-color: #ffc107;
  color: #212529;
}
.badge-danger {
  background-color: #dc3545;
  color: #fff;
}
</style>
