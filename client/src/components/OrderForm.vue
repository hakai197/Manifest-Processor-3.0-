<template>
  <div class="card mt-4" ref="formCard">
    <div class="card-header">
      {{ formData.orderId ? 'Edit Order' : 'Add Order' }}
    </div>
    <div class="card-body">
      <form @submit.prevent="handleSubmit">
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="orderNumber">Order Number *</label>
            <input
              type="text"
              id="orderNumber"
              class="form-control"
              v-model="formData.orderNumber"
              required
              :disabled="!!formData.orderId"
            >
          </div>
          
          <div class="form-group col-md-6">
            <label for="status">Status *</label>
            <select
              id="status"
              class="form-control"
              v-model="formData.status"
              required
            >
              <option value="Pending">Pending</option>
              <option value="In Progress">In Progress</option>
              <option value="Completed">Completed</option>
              <option value="Cancelled">Cancelled</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="customerId">Customer *</label>
            <select
              id="customerId"
              class="form-control"
              v-model.number="formData.customerId"
              required
            >
              <option value="">Select Customer</option>
              <option 
                v-for="customer in customers" 
                :key="customer.customerId"
                :value="customer.customerId"
              >
                {{ customer.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group col-md-6">
            <label for="shipperId">Shipper *</label>
            <select
              id="shipperId"
              class="form-control"
              v-model.number="formData.shipperId"
              required
            >
              <option value="">Select Shipper</option>
              <option 
                v-for="shipper in shippers" 
                :key="shipper.shipperId"
                :value="shipper.shipperId"
              >
                {{ shipper.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="trailerId">Trailer *</label>
            <select
              id="trailerId"
              class="form-control"
              v-model.number="formData.trailerId"
              required
            >
              <option value="">Select Trailer</option>
              <option 
                v-for="trailer in trailers" 
                :key="trailer.trailerId"
                :value="trailer.trailerId"
              >
                {{ trailer.trailerNumber }} ({{ trailer.trailerType }})
              </option>
            </select>
          </div>
          
          <div class="form-group col-md-6">
            <label for="doorNumber">Door Number *</label>
            <input
              type="text"
              id="doorNumber"
              class="form-control"
              v-model="formData.doorNumber"
              required
            >
          </div>
        </div>

        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="handlingUnit">Handling Units *</label>
            <input
              type="number"
              id="handlingUnit"
              class="form-control"
              v-model.number="formData.handlingUnit"
              required
              min="0"
            >
          </div>
          
          <div class="form-group col-md-6">
            <label for="weight">Weight (lbs) *</label>
            <input
              type="number"
              id="weight"
              class="form-control"
              v-model.number="formData.weight"
              required
              min="0"
            >
          </div>
        </div>
        
        <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
        
        <div class="form-actions mt-4">
          <button 
            type="submit" 
            class="btn btn-primary mr-2"
            :disabled="isSubmitting"
          >
            <span v-if="isSubmitting" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            {{ formData.orderId ? 'Update' : 'Create' }}
          </button>
          <button 
            type="button" 
            class="btn btn-secondary" 
            @click="handleCancel"
            :disabled="isSubmitting"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderForm',
  props: {
    order: {
      type: Object,
      default: () => ({
        orderId: null,
        orderNumber: '',
        customerId: null,
        shipperId: null,
        trailerId: null,
        doorNumber: '',
        handlingUnit: 0,
        weight: 0,
        status: 'Pending'
      })
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
  data() {
    return {
      formData: { ...this.order },
      error: null,
      isSubmitting: false
    };
  },
  watch: {
    order: {
      handler(newVal) {
        this.formData = { ...newVal };
      },
      deep: true
    }
  },
  methods: {
    handleSubmit() {
      this.error = null;
      
      if (!this.formData.orderNumber || !this.formData.customerId || 
          !this.formData.shipperId || !this.formData.trailerId || 
          !this.formData.doorNumber || this.formData.handlingUnit === null || 
          this.formData.weight === null || !this.formData.status) {
        this.error = 'Please fill all required fields';
        return;
      }

      if (this.formData.handlingUnit < 0 || this.formData.weight < 0) {
        this.error = 'Handling units and weight must be positive numbers';
        return;
      }

      const payload = {
        ...this.formData,
        customerId: Number(this.formData.customerId),
        shipperId: Number(this.formData.shipperId),
        trailerId: Number(this.formData.trailerId)
      };

      this.isSubmitting = true;
      this.$emit('save', payload);
    },
    handleCancel() {
      this.$emit('cancel');
    }
  }
};
</script>

<style scoped>
.card {
  border: 1px solid rgba(0, 0, 0, 0.125);
  border-radius: 0.5rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
  animation: fadeIn 0.3s ease-out forwards;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid rgba(0, 0, 0, 0.125);
  padding: 1rem 1.25rem;
  font-weight: 600;
  font-size: 1.25rem;
}

.card-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #495057;
}

.form-control {
  display: block;
  width: 100%;
  padding: 0.5rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  color: #495057;
  background-color: #fff;
  background-clip: padding-box;
  border: 1px solid #ced4da;
  border-radius: 0.375rem;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.form-control:focus {
  color: #495057;
  background-color: #fff;
  border-color: #80bdff;
  outline: 0;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.form-control:disabled {
  background-color: #e9ecef;
  opacity: 1;
}

select.form-control {
  height: calc(2.25rem + 2px);
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' width='4' height='5' viewBox='0 0 4 5'%3e%3cpath fill='%23343a40' d='M2 0L0 2h4zm0 5L0 3h4z'/%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 8px 10px;
}

.alert {
  padding: 0.75rem 1.25rem;
  margin-top: 1rem;
  border: 1px solid transparent;
  border-radius: 0.375rem;
}

.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
  margin-top: 1.5rem;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  user-select: none;
  border: 1px solid transparent;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.375rem;
  transition: all 0.15s ease-in-out;
  cursor: pointer;
}

.btn-primary {
  color: #fff;
  background-color: #007bff;
  border-color: #007bff;
}

.btn-primary:hover {
  background-color: #0069d9;
  border-color: #0062cc;
}

.btn-primary:disabled {
  background-color: #b3d7ff;
  border-color: #b3d7ff;
  cursor: not-allowed;
}

.btn-secondary {
  color: #fff;
  background-color: #6c757d;
  border-color: #6c757d;
}

.btn-secondary:hover {
  background-color: #5a6268;
  border-color: #545b62;
}

.btn-secondary:disabled {
  background-color: #d6d8db;
  border-color: #d6d8db;
  cursor: not-allowed;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
  border-width: 0.2em;
  margin-right: 0.5rem;
}

@media (max-width: 768px) {
  .card-body {
    padding: 1rem;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .btn {
    width: 100%;
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>