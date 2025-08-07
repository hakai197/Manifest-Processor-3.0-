<template>
  <div class="card mt-4" ref="formCard">
    <div class="card-header">
      {{ formData.trailerId ? 'Edit Trailer' : 'Add Trailer' }}
    </div>
    <div class="card-body">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="trailerNumber">Trailer Number</label>
          <input
            type="text"
            id="trailerNumber"
            class="form-control"
            v-model="formData.trailerNumber"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="trailerType">Trailer Type</label>
          <input
            type="text"
            id="trailerType"
            class="form-control"
            v-model="formData.trailerType"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="shipperId">Shipper ID</label>
          <input
            type="number"
            id="shipperId"
            class="form-control"
            v-model.number="formData.shipperId"
            required
            min="1"
          >
        </div>
        
        <div class="form-group">
          <label for="status">Status</label>
          <select
            id="status"
            class="form-control"
            v-model="formData.status"
            required
          >
            <option value="Unassigned">Unassigned</option>
            <option value="Assigned">Assigned</option>
            <option value="In Maintenance">In Maintenance</option>
          </select>
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
              {{ formData.trailerId ? 'Update' : 'Create' }}
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
  name: 'TrailerForm',
  props: {
    trailer: {
      type: Object,
      default: () => ({
        trailerId: null,
        trailerNumber: '',
        trailerType: '',
        shipperId: 0,
        status: 'Unassigned'
      })
    }
  },
  data() {
    return {
      formData: { ...this.trailer },
      error: null,
      isSubmitting: false
    };
  },
  mounted() {
    this.scrollToForm();
  },
  watch: {
    trailer: {
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
      this.isSubmitting = false;
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