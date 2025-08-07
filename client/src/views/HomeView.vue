<template>
  <main class="dashboard-container">
    <h1>Dock Management Dashboard</h1>
    
    <section class="dashboard-grid">
      
      <div class="dashboard-card">
        <div class="card-header">Available Doors</div>
        <div class="filter-container">
          <input type="text" placeholder="Filter doors..." v-model="doorFilter">
        </div>
        <div class="card-content">
          <ul class="card-list" v-if="filteredAvailableDoors.length">
            <li v-for="door in filteredAvailableDoors" :key="door.doorNumber">
              <div class="door-info">
                <span class="door-name">{{ door.doorNumber }}</span>
                <span class="status-available">Available</span>
              </div>
            </li>
          </ul>
          <div v-else class="no-data">No available doors found</div>
        </div>
      </div>
      
    
      <div class="dashboard-card">
        <div class="card-header">Assigned Doors</div>
        <div class="card-content">
          <ul class="card-list" v-if="assignedDoors.length">
            <li v-for="assignment in assignedDoors" :key="assignment.assignmentId">
              <div class="assignment-info">
                <span class="door-name">{{ assignment.doorNumber }}</span>
                <span class="trailer-info">Trailer: {{ assignment.trailerNumber }} ({{ assignment.trailerType }})</span>
                <span class="employee-info">Unloader: {{ assignment.employeeName }}</span>
                <span class="status-assigned">Status: {{ assignment.status }}</span>
              </div>
            </li>
          </ul>
          <div v-else class="no-data">No assigned doors</div>
        </div>
      </div>
      
      <!-- Available Trailers Card -->
      <div class="dashboard-card">
        <div class="card-header">Available Trailers</div>
        <div class="filter-container">
          <input type="text" placeholder="Filter trailers..." v-model="trailerFilter">
        </div>
        <div class="card-content">
          <ul class="card-list" v-if="filteredAvailableTrailers.length">
            <li v-for="trailer in filteredAvailableTrailers" :key="trailer.trailerId">
              <div class="trailer-info">
                <span class="trailer-number">{{ trailer.trailerNumber }}</span>
                <span class="trailer-type">{{ trailer.trailerType }}</span>
                <span class="shipper-info">Shipper: {{ trailer.shipperName }}</span>
              </div>
              <span class="status-available">Available</span>
            </li>
          </ul>
          <div v-else class="no-data">No available trailers found</div>
        </div>
      </div>
      
      
      <div class="dashboard-card">
        <div class="card-header">Available Unloaders</div>
        <div class="filter-container">
          <input type="text" placeholder="Filter unloaders..." v-model="unloaderFilter">
        </div>
        <div class="card-content">
          <ul class="card-list" v-if="filteredAvailableUnloaders.length">
            <li v-for="unloader in filteredAvailableUnloaders" :key="unloader.employeeId">
              <div class="unloader-info">
                <span class="unloader-name">{{ unloader.name }}</span>
                <span class="employee-number">ID: {{ unloader.employeeNumber }}</span>
                <span class="unloader-shift">Shift: {{ unloader.shift }}</span>
              </div>
              <span class="status-available">Available</span>
            </li>
          </ul>
          <div v-else class="no-data">No available unloaders found</div>
        </div>
      </div>
    </section>

    <loading-spinner v-if="isLoading" />
  </main>
</template>

<script>
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import TrailerService from '@/services/TrailerService';
import UnloaderService from '@/services/UnloaderService';
import AssignmentService from '@/services/AssignmentService';

export default {
  name: 'HomeView',
  components: {
    LoadingSpinner
  },
  data() {
    return {
      isLoading: false,
      allDoors: [],
      allTrailers: [],
      allUnloaders: [],
      allAssignments: [],
      doorFilter: '',
      trailerFilter: '',
      unloaderFilter: ''
    };
  },
  computed: {
    filteredAvailableDoors() {
      const assignedDoorNumbers = this.assignedDoors.map(a => a.doorNumber);
      return this.allDoors
        .filter(door => 
          !assignedDoorNumbers.includes(door.doorNumber) &&
          door.doorNumber.toLowerCase().includes(this.doorFilter.toLowerCase())
        );
    },
    assignedDoors() {
      return this.allAssignments.map(assignment => {
        const trailer = this.allTrailers.find(t => t.trailerId === assignment.trailerId);
        const unloader = this.allUnloaders.find(u => u.employeeId === assignment.employeeId);
        
        return {
          ...assignment,
          trailerNumber: trailer ? trailer.trailerNumber : 'Unknown',
          trailerType: trailer ? trailer.trailerType : '',
          shipperName: trailer && trailer.shipperName ? trailer.shipperName : '',
          employeeName: unloader ? unloader.name : 'Unassigned',
          employeeNumber: unloader ? unloader.employeeNumber : ''
        };
      });
    },
    filteredAvailableTrailers() {
      const assignedTrailerIds = this.assignedDoors.map(a => a.trailerId);
      return this.allTrailers
        .filter(trailer => 
          !assignedTrailerIds.includes(trailer.trailerId) &&
          (trailer.trailerNumber.toLowerCase().includes(this.trailerFilter.toLowerCase()) ||
          trailer.trailerType.toLowerCase().includes(this.trailerFilter.toLowerCase()))
        );
    },
    filteredAvailableUnloaders() {
      const assignedUnloaderIds = this.assignedDoors.map(a => a.employeeId);
      return this.allUnloaders
        .filter(unloader => 
          !assignedUnloaderIds.includes(unloader.employeeId) &&
          (unloader.name.toLowerCase().includes(this.unloaderFilter.toLowerCase()) ||
          unloader.shift.toLowerCase().includes(this.unloaderFilter.toLowerCase()))
        );
    }
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.isLoading = true;
      
      
      Promise.all([
        TrailerService.getAll(),
        UnloaderService.getAll(),
        AssignmentService.getAll()
      ])
      .then(responses => {
        this.allTrailers = responses[0].data;
        this.allUnloaders = responses[1].data;
        this.allAssignments = responses[2].data;
        this.allDoors = this.generateDoors(50);
      })
      .catch(error => {
        console.error('Error fetching data:', error);
      })
      .finally(() => {
        this.isLoading = false;
      });
    },
    generateDoors(count) {
      return Array.from({ length: count }, (_, i) => ({
        doorNumber: `Door ${i + 1}`,
        status: 'Available'
      }));
    }
  }
};
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1800px;
  margin: 0 auto;
}

.dashboard-container h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.dashboard-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  background-color: #1E90FF;
  color: white;
  padding: 15px;
  font-size: 1.2rem;
  font-weight: bold;
  text-align: center;
}

.filter-container {
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
}

.filter-container input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.card-content {
  padding: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.card-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.card-list li {
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s;
}

.card-list li:hover {
  background-color: #f8f9fa;
}

.card-list li:last-child {
  border-bottom: none;
}

.door-info, .assignment-info, .trailer-info, .unloader-info {
  display: flex;
  flex-direction: column;
}

.door-name, .trailer-number, .unloader-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.trailer-info, .employee-info, .shipper-info, .unloader-shift {
  font-size: 0.85rem;
  color: #666;
}

.status-available {
  color: #28a745;
  font-weight: bold;
  margin-top: 5px;
}

.status-assigned {
  color: #dc3545;
  font-weight: bold;
  margin-top: 5px;
}

.no-data {
  padding: 20px;
  text-align: center;
  color: #6c757d;
  font-style: italic;
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>