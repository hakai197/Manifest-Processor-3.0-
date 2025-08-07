<template>
  <div class="card">
    <div class="card-header">
      <h2>Order List</h2>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped">
        <thead>
          <tr>
            <th>Order #</th>
            <th>Customer</th>
            <th>Shipper</th>
            <th>Trailer</th>
            <th>Door</th>
            <th>HU</th>
            <th>Weight</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderId">
            <td>{{ order.orderNumber }}</td>
            <td>{{ order.customerName }}</td>
            <td>{{ order.shipperName }}</td>
            <td>{{ order.trailerNumber }}</td>
            <td>{{ order.doorNumber }}</td>
            <td>{{ order.handlingUnit }}</td>
            <td>{{ order.weight }}</td>
            <td>
              <span :class="statusClass(order.status)">{{ order.status }}</span>
            </td>
            <td>
              <button 
                class="btn btn-primary btn-sm mr-2" 
                @click="handleEditClick(order)"
              >
                Edit
              </button>
              <button 
                class="btn btn-danger btn-sm" 
                @click.stop="handleDeleteClick(order.orderNumber)"
                :disabled="!isAdmin"
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
  name: 'OrderList',
  props: {
    orders: {
      type: Array,
      required: true
    },
    customers: {
      type: Array,
      default: () => []
    },
    shippers: {
      type: Array,
      default: () => []
    },
    trailers: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  methods: {
    statusClass(status) {
      return {
        'badge badge-success': status === 'Completed',
        'badge badge-primary': status === 'In Progress',
        'badge badge-warning': status === 'Pending',
        'badge badge-danger': status === 'Cancelled'
      };
    },
    handleEditClick(order) {
      this.$emit('edit', order);
    },
    handleDeleteClick(orderNumber) {
      this.$emit('delete', orderNumber);
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
.badge {
  display: inline-block;
  padding: 0.25em 0.4em;
  font-size: 75%;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: 0.25rem;
}
.badge-success {
  color: #fff;
  background-color: #28a745;
}
.badge-primary {
  color: #fff;
  background-color: #007bff;
}
.badge-warning {
  color: #212529;
  background-color: #ffc107;
}
.badge-danger {
  color: #fff;
  background-color: #dc3545;
}
.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>