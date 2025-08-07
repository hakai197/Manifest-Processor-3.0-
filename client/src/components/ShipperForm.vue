<template>
  <div class="card mt-4" ref="formCard">
    <div class="card-header">
      {{ formData.shipperId ? 'Edit Shipper' : 'Add Shipper' }}
    </div>
    <div class="card-body">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="name">Name</label>
          <input
            type="text"
            id="name"
            class="form-control"
            v-model="formData.name"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="address">Address</label>
          <input
            type="text"
            id="address"
            class="form-control"
            v-model="formData.address"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="city">City</label>
          <input
            type="text"
            id="city"
            class="form-control"
            v-model="formData.city"
            required
          >
        </div>
        
        <div class="form-row">
          <div class="form-group col-md-2">
            <label for="state">State</label>
            <input
              type="text"
              id="state"
              class="form-control"
              v-model="formData.state"
              maxlength="2"
              required
            >
          </div>
          <div class="form-group col-md-4">
            <label for="zipCode">Zip Code</label>
            <input
              type="text"
              id="zipCode"
              class="form-control"
              v-model="formData.zipCode"
              required
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
            <span v-if="isSubmitting">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              Saving...
            </span>
            <span v-else>
              {{ formData.shipperId ? 'Update' : 'Create' }}
            </span>
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
  name: 'ShipperForm',
  props: {
    shipper: {
      type: Object,
      default: () => ({
        shipperId: null,
        name: '',
        address: '',
        city: '',
        state: '',
        zipCode: ''
      })
    }
  },
  data() {
    return {
      formData: { ...this.shipper },
      error: null,
      isSubmitting: false
    };
  },
  mounted() {
    this.scrollToForm();
  },
  watch: {
    shipper: {
      handler(newVal) {
        this.formData = { ...newVal };
      },
      deep: true
    }
  },
  methods: {
    handleSubmit() {
      this.error = null;
      this.isSubmitting = true;
      
      this.$emit('save', this.formData);
    },
    handleCancel() {
      this.$emit('cancel');
    },
    scrollToForm() {
      if (this.$refs.formCard) {
        this.$refs.formCard.scrollIntoView({ 
          behavior: 'smooth',
          block: 'nearest'
        });
      }
    }
  }
};
</script>

<style scoped>
.form-group {
  margin-bottom: 1rem;
}

.form-control {
  display: block;
  width: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.btn {
  display: inline-block;
  font-weight: 400;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  user-select: none;
  border: 1px solid transparent;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.25rem;
  transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out, 
              border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.btn-primary {
  color: #fff;
  background-color: #007bff;
  border-color: #007bff;
}

.btn-secondary {
  color: #fff;
  background-color: #6c757d;
  border-color: #6c757d;
}

.mr-2 {
  margin-right: 0.5rem;
}

.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>