<template>
  <div class="card mt-4" ref="formCard">
    <div class="card-header">
      {{ formData.employeeId ? 'Edit Employee' : 'Add Employee' }}
    </div>
    <div class="card-body">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="name">Full Name</label>
          <input
            type="text"
            id="name"
            class="form-control"
            v-model="formData.name"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="employeeNumber">Employee Number</label>
          <input
            type="text"
            id="employeeNumber"
            class="form-control"
            v-model="formData.employeeNumber"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="shift">Shift</label>
          <select
            id="shift"
            class="form-control"
            v-model="formData.shift"
            required
          >
            <option value="1st Shift">1st Shift (6AM-2PM)</option>
            <option value="2nd Shift">2nd Shift (2PM-10PM)</option>
            <option value="3rd Shift">3rd Shift (10PM-6AM)</option>
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
              {{ formData.employeeId ? 'Update' : 'Create' }}
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
  name: 'UnloaderForm',
  props: {
    unloader: {
      type: Object,
      default: () => ({
        employeeId: null,
        name: '',
        employeeNumber: '',
        shift: '2nd Shift'
      })
    },
    isSubmitting: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      formData: { ...this.unloader },
      error: null
    };
  },
  watch: {
    unloader: {
      handler(newVal) {
        this.formData = { ...newVal };
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    handleSubmit() {
      this.error = null;
      this.$emit('save', this.formData);
    },
    handleCancel() {
      this.$emit('cancel');
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
@media (max-width: 425px) {
  .col-md-4.text-right, .col-md-8 {
    margin-top: 10px;
  }
}
</style>