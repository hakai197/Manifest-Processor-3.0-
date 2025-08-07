<template>
  <div class="card">
    <div class="card-header">
      <h2>Trailer List</h2>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>Trailer Number</th>
            <th>Type</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trailer in trailers" :key="trailer.trailerId">
            <td>{{ trailer.trailerId }}</td>
            <td>{{ trailer.trailerNumber }}</td>
            <td>{{ trailer.trailerType }}</td>
            <td>
              <button class="btn btn-primary btn-sm mr-2" @click="handleEditClick(trailer)">Edit</button>
              <button class="btn btn-danger btn-sm mr-2" @click="handleDeleteClick(trailer.trailerNumber)" :disabled="!isAdmin">
                Delete
              </button>
              <button class="btn btn-info btn-sm" @click="openOrdersModal(trailer.trailerNumber)">
                View Orders
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Orders for Trailer {{ selectedTrailer }}</h3>
          <button class="close-button" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>Order #</th>
                <th>Customer</th>
                <th>Shipper</th>
                <th>Door</th>
                <th>HU</th>
                <th>Weight</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in trailerOrders" :key="order.orderId">
                <td>{{ order.orderNumber }}</td>
                <td>{{ order.customerName }}</td>
                <td>{{ order.shipperName }}</td>
                <td>{{ order.doorNumber }}</td>
                <td>{{ order.handlingUnit }}</td>
                <td>{{ order.weight }}</td>
                <td>
                  <span :class="statusClass(order.status)">{{ order.status }}</span>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="trailerOrders.length === 0">No orders for this trailer.</div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import OrderService from '@/services/OrderService';

export default {
  name: 'TrailerList',
  props: {
    trailers: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      showModal: false,
      trailerOrders: [],
      selectedTrailer: null
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  methods: {
    handleEditClick(trailer) {
      this.$emit('edit', trailer);
    },
    handleDeleteClick(trailerNumber) {
      if (this.isAdmin) {
        this.$emit('delete', trailerNumber);
      } else {
        alert('Only administrators can delete trailers');
      }
    },
    openOrdersModal(trailerNumber) {
      OrderService.getByTrailer(trailerNumber)
        .then(response => {
          this.trailerOrders = response.data;
          this.selectedTrailer = trailerNumber;
          this.showModal = true;
        })
        .catch(error => {
          alert('Failed to load orders for trailer.');
          console.error(error);
        });
    },
    closeModal() {
      this.showModal = false;
      this.trailerOrders = [];
      this.selectedTrailer = null;
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
  border-bottom: 1px solid #ccc;
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

</style>