<template>
  <div class="assignment-form">
    <div class="card">
      <div class="card-header">
        <h3>{{ form.assignmentId ? 'Edit Assignment' : 'Add New Assignment' }}</h3>
      </div>
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <div class="form-group col-md-6">
              <label>Employee *</label>
              <select
                class="form-control"
                v-model="form.employeeId"
                required
                :disabled="loading"
              >
                <option value="" disabled>Select Employee</option>
                <option 
                  v-for="employee in employees" 
                  :key="employee.employeeId" 
                  :value="employee.employeeId"
                >
                  {{ employee.name }} ({{ employee.employeeNumber }})
                </option>
              </select>
            </div>

            <div class="form-group col-md-6">
              <label>Trailer *</label>
              <select
                class="form-control"
                v-model="form.trailerId"
                required
                :disabled="loading"
                @change="loadTrailerOrders"
              >
                <option value="" disabled>Select Trailer</option>
                <option 
                  v-for="trailer in trailers" 
                  :key="trailer.trailerId" 
                  :value="trailer.trailerId"
                >
                  {{ trailer.trailerNumber }} ({{ trailer.trailerType }})
                </option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group col-md-6">
              <label>Door Number *</label>
              <input
                type="text"
                class="form-control"
                v-model="form.doorNumber"
                required
                :disabled="loading"
                placeholder="e.g., Door 1"
              >
            </div>

            <div class="form-group col-md-6">
              <label>Status *</label>
              <select
                class="form-control"
                v-model="form.status"
                required
                :disabled="loading"
              >
                <option value="Pending">Pending</option>
                <option value="Assigned">Assigned</option>
                <option value="In Progress">In Progress</option>
                <option value="Completed">Completed</option>
              </select>
            </div>
          </div>

          <div v-if="trailerMessages.length > 0" class="alert alert-info mt-3">
            <h5>Trailer Recommendations:</h5>
            <ul class="mb-0">
              <li v-for="(message, index) in trailerMessages" :key="index">{{ message }}</li>
            </ul>
            <div v-if="suggestedDoors.length > 0" class="mt-2">
              <strong>Suggested Available Doors:</strong>
              <div class="btn-group btn-group-sm mt-1">
                <button 
                  v-for="door in suggestedDoors" 
                  :key="door"
                  type="button" 
                  class="btn btn-outline-primary"
                  @click="form.doorNumber = door"
                >
                  {{ door }}
                </button>
              </div>
            </div>
          </div>

          <div v-if="timeEstimate > 0" class="alert alert-warning mt-3">
            <h5>Time Estimate:</h5>
            <p class="mb-0">
              Estimated total time for this assignment: <strong>{{ formatTime(timeEstimate) }}</strong>
              <br>
              <small>(Based on {{ totalHUs }} HUs and {{ totalDoors }} door movements)</small>
            </p>
          </div>

          <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>

          <div class="form-actions mt-4">
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm"></span>
              {{ form.assignmentId ? 'Update' : 'Create' }}
            </button>
            <button 
              type="button" 
              class="btn btn-secondary ml-2"
              @click="$emit('cancel')"
              :disabled="loading"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import OrderService from '../services/OrderService'
import AssignmentService from '../services/AssignmentService'

export default {
  props: {
    assignment: Object,
    employees: Array,
    trailers: Array,
    currentAssignments: Array
  },
  data() {
    return {
      form: { ...this.assignment },
      loading: false,
      error: null,
      trailerOrders: [],
      trailerMessages: [],
      suggestedDoors: [],
      allDoorAssignments: [],
      timeEstimate: 0,
      totalHUs: 0,
      totalDoors: 0
    };
  },
  watch: {
    assignment: {
      handler(newVal) {
        this.form = { ...newVal };
      },
      deep: true
    },
    'form.trailerId': {
      handler(newVal) {
        if (newVal) {
          this.loadTrailerOrders();
          this.loadCurrentAssignments();
        }
      }
    }
  },
  methods: {
    loadCurrentAssignments() {
      AssignmentService.getAll()
        .then(response => {
          this.allDoorAssignments = response.data.map(a => a.doorNumber);
        })
        .catch(error => {
          console.error('Error loading current assignments:', error);
        });
    },
    loadTrailerOrders() {
      if (!this.form.trailerId) return;
      
      this.loading = true;
      const trailer = this.trailers.find(t => t.trailerId === this.form.trailerId);
      
      if (trailer) {
        OrderService.getByTrailer(trailer.trailerNumber)
          .then(response => {
            this.trailerOrders = response.data;
            this.analyzeTrailerOrders();
          })
          .catch(error => {
            console.error('Error loading trailer orders:', error);
          })
          .finally(() => {
            this.loading = false;
          });
      }
    },
    analyzeTrailerOrders() {
      this.trailerMessages = [];
      this.suggestedDoors = [];
      
      if (this.trailerOrders.length === 0) return;
      
      
      const qualifyingOrders = this.trailerOrders.filter(order => 
        (order.handlingUnit && order.handlingUnit >= 8) || 
        (order.weight && order.weight >= 10000)
      );
      
      if (qualifyingOrders.length > 0) {
        const orderDetails = qualifyingOrders.map(order => 
          `Order ${order.orderNumber} (${order.handlingUnit || 0} HUs, ${order.weight || 0} lbs)`
        ).join(', ');
        
        this.trailerMessages.push(
          `Head load detected: ${orderDetails}. `
        );
      }
      
      
      const doorGroups = {
        '1-10': { count: 0, doors: [] },
        '11-20': { count: 0, doors: [] },
        '21-30': { count: 0, doors: [] },
        '31-40': { count: 0, doors: [] },
        '41-50': { count: 0, doors: [] }
      };
      
      this.trailerOrders.forEach(order => {
        if (order.doorNumber) {
          const doorNum = parseInt(order.doorNumber.replace(/\D/g, ''));
          if (!isNaN(doorNum)) {
            let range;
            if (doorNum >= 1 && doorNum <= 10) range = '1-10';
            else if (doorNum <= 20) range = '11-20';
            else if (doorNum <= 30) range = '21-30';
            else if (doorNum <= 40) range = '31-40';
            else if (doorNum <= 50) range = '41-50';
            
            if (range) {
              doorGroups[range].count++;
              if (!doorGroups[range].doors.includes(order.doorNumber)) {
                doorGroups[range].doors.push(order.doorNumber);
              }
            }
          }
        }
      });
      
      
      const totalOrders = this.trailerOrders.length;
      let suggestedRange = null;
      let maxPercentage = 0;
      let popularDoors = [];
      
      for (const [range, data] of Object.entries(doorGroups)) {
        const percentage = (data.count / totalOrders) * 100;
        if (percentage >= 25 && percentage > maxPercentage) {
          maxPercentage = percentage;
          suggestedRange = range;
          popularDoors = data.doors;
        }
      }
      
      
      if (suggestedRange) {
        const currentDoorNum = this.form.doorNumber ? parseInt(this.form.doorNumber.replace(/\D/g, '')) : null;
        let inRange = false;
        
        if (!isNaN(currentDoorNum)) {
          const [min, max] = suggestedRange.split('-').map(Number);
          inRange = currentDoorNum >= min && currentDoorNum <= max;
        }
        
        if (!inRange) {
          this.trailerMessages.push(`Most orders (${Math.round(maxPercentage)}%) for this trailer go to doors ${suggestedRange}.`);
          const [min, max] = suggestedRange.split('-').map(Number);
          const availableDoors = this.findAvailableDoorsInRange(min, max);
          
          if (availableDoors.length > 0) {
            this.suggestedDoors = availableDoors;
          } else {
            this.trailerMessages.push(`No available doors found in ${suggestedRange} range.`);
          }
        }
      }
      
      
      if (popularDoors.length > 0) {
        const availablePopularDoors = popularDoors.filter(door => 
          !this.allDoorAssignments.includes(door)
        );
        
        if (availablePopularDoors.length > 0) {
          this.trailerMessages.push('Some frequently used doors for this trailer are available:');
          this.suggestedDoors = [...new Set([...this.suggestedDoors, ...availablePopularDoors])].slice(0, 5);
        }
      }
      
      
      this.calculateTimeEstimate();
    },
    calculateTimeEstimate() {
      if (!this.trailerOrders.length) {
        this.timeEstimate = 0;
        this.totalHUs = 0;
        this.totalDoors = 0;
        return;
      }

      
      this.totalHUs = this.trailerOrders.reduce((sum, order) => sum + (order.handlingUnit || 0), 0);
      
      
      const uniqueDoors = new Set(this.trailerOrders.map(order => order.doorNumber).filter(Boolean));
      this.totalDoors = uniqueDoors.size;
      
      
      const pickupTime = this.totalHUs * 10;
      const dropoffTime = this.totalHUs * 5;
      const doorMovementTime = this.totalDoors * 1 * 2; 
      
      this.timeEstimate = pickupTime + dropoffTime + doorMovementTime;
    },
    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      
      let parts = [];
      if (hours > 0) parts.push(`${hours} hour${hours > 1 ? 's' : ''}`);
      if (minutes > 0) parts.push(`${minutes} minute${minutes > 1 ? 's' : ''}`);
      if (secs > 0 || parts.length === 0) parts.push(`${secs} second${secs !== 1 ? 's' : ''}`);
      
      return parts.join(' ');
    },
    findAvailableDoorsInRange(min, max) {
      const availableDoors = [];
      const assignedDoors = this.allDoorAssignments;
      
      for (let i = min; i <= max; i++) {
        const doorName = `Door ${i}`;
        if (!assignedDoors.includes(doorName)) {
          availableDoors.push(doorName);
          if (availableDoors.length >= 5) break;
        }
      }
      
      return availableDoors;
    },
    handleSubmit() {
      this.error = null;
      
      if (!this.form.employeeId || !this.form.trailerId || !this.form.doorNumber || !this.form.status) {
        this.error = 'Please fill all required fields';
        return;
      }

      this.loading = true;
      this.$emit('save', this.form)
        .finally(() => {
          this.loading = false;
        });
    }
  },
  created() {
    this.loadCurrentAssignments();
  }
};
</script>

<style scoped>
.assignment-form {
  margin-top: 20px;
}

.card {
  border: 1px solid rgba(0,0,0,.125);
  border-radius: 0.25rem;
}

.card-header {
  background-color: #f8f9fa;
  padding: 0.75rem 1.25rem;
  border-bottom: 1px solid rgba(0,0,0,.125);
}

.card-header h3 {
  margin: 0;
  font-size: 1.25rem;
}

.card-body {
  padding: 1.25rem;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  margin-right: -15px;
  margin-left: -15px;
}

.form-group {
  padding-right: 15px;
  padding-left: 15px;
  margin-bottom: 1rem;
  flex: 0 0 50%;
  max-width: 50%;
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

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.ml-2 {
  margin-left: 0.5rem;
}

.mt-3 {
  margin-top: 1rem;
}

.mt-4 {
  margin-top: 1.5rem;
}

.alert {
  padding: 0.75rem 1.25rem;
  border: 1px solid transparent;
  border-radius: 0.25rem;
}

.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}

.alert-info {
  color: #0c5460;
  background-color: #d1ecf1;
  border-color: #bee5eb;
}

.alert-warning {
  color: #856404;
  background-color: #fff3cd;
  border-color: #ffeeba;
}

.alert ul {
  padding-left: 20px;
}

.btn {
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.25rem;
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

.btn-outline-primary {
  color: #007bff;
  background-color: transparent;
  border-color: #007bff;
}

.btn-group-sm .btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.5;
  border-radius: 0.2rem;
  margin-right: 0.25rem;
  margin-bottom: 0.25rem;
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

@keyframes spinner-border {
  to { transform: rotate(360deg); }
}

</style>