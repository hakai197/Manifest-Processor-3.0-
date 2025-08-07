<template>
  <div class="card mt-4" ref="formCard">
    <div class="card-header">
      {{ formData.customerId ? 'Edit Customer' : 'Add Customer' }}
    </div>
    <div class="card-body">
      <form @submit.prevent="handleSubmit">
        <div class="form-row">
          <div class="form-group col-md-6">
            <label for="name">Full Name *</label>
            <input
              type="text"
              id="name"
              class="form-control"
              v-model="formData.name"
              required
            >
          </div>
          
          <div class="form-group col-md-6">
            <label for="address">Address *</label>
            <input
              type="text"
              id="address"
              class="form-control"
              v-model="formData.address"
              required
            >
          </div>
        </div>

        <div class="form-row">
          <div class="form-group col-md-4">
            <label for="city">City *</label>
            <input
              type="text"
              id="city"
              class="form-control"
              v-model="formData.city"
              required
            >
          </div>
          
          <div class="form-group col-md-4">
            <label for="state">State *</label>
            <select
              id="state"
              class="form-control"
              v-model="formData.state"
              required
            >
              <option value="">Select State</option>
              <option v-for="state in states" :key="state" :value="state">
                {{ state }}
              </option>
            </select>
          </div>
          
          <div class="form-group col-md-4">
            <label for="zipCode">Zip Code *</label>
            <input
              type="text"
              id="zipCode"
              class="form-control"
              v-model="formData.zipCode"
              required
              pattern="^\d{5}(?:[-\s]\d{4})?$"
              title="Enter a valid US zip code (5 or 9 digits)"
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
              {{ formData.customerId ? 'Update' : 'Create' }}
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
  name: 'CustomerForm',
  props: {
    customer: {
      type: Object,
      default: () => ({
        customerId: null,
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
      formData: { ...this.customer },
      error: null,
      isSubmitting: false,
      states: [
        'AL', 'AK', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA',
        'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD',
        'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ',
        'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC',
        'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA', 'WV', 'WI', 'WY'
      ]
    };
  },
  mounted() {
    this.scrollToForm();
  },
  watch: {
    customer: {
      handler(newVal) {
        this.formData = { ...newVal };
      },
      deep: true
    }
  },
  methods: {
    handleSubmit() {
      this.error = null;
      
    
      if (!this.formData.name || !this.formData.address || 
          !this.formData.city || !this.formData.state || 
          !this.formData.zipCode) {
        this.error = 'Please fill all required fields';
        return;
      }

      
      const zipRegex = /^\d{5}(?:[-\s]\d{4})?$/;
      if (!zipRegex.test(this.formData.zipCode)) {
        this.error = 'Please enter a valid US zip code (5 or 9 digits)';
        return;
      }

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
.card {
  border: 1px solid rgba(0, 0, 0, 0.125);
  border-radius: 0.5rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid rgba(0, 0, 0, 0.125);
  padding: 1rem 1.25rem;
  font-weight: 600;
  font-size: 1.25rem;
}

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
}

.alert {
  margin-top: 1rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
}

.btn {
  padding: 0.375rem 0.75rem;
}

.spinner-border-sm {
  width: 1rem;
  height: 1rem;
  border-width: 0.2em;
}


@media (max-width: 425px) {
  .form-row {
    flex-direction: column;
  }
  
  .form-group {
    margin-bottom: 0.75rem;
  }
}
</style>