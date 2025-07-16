<template>
  <section class="dashboard-grid">
    <DashboardCard title="Available Doors">
      <div class="filter-container">
        <input type="text" v-model="doorFilter" placeholder="Filter doors...">
      </div>
      <ul class="card-list">
        <li v-for="door in filteredDoors" :key="door.id">
          <div class="door-name">{{ door.name }}</div>
          <div :class="'status-' + door.status.toLowerCase()">{{ door.status }}</div>
        </li>
      </ul>
    </DashboardCard>
    
    <DashboardCard title="Assigned Doors">
      <ul class="card-list">
        <li v-for="assignment in assignments" :key="assignment.id">
          <div class="assignment-info">
            <span class="door-name">{{ assignment.door }}</span>
            <span class="trailer-info">{{ assignment.trailer }} - {{ assignment.unloader }}</span>
          </div>
          <div class="status-assigned">Assigned</div>
        </li>
      </ul>
    </DashboardCard>
    
    <DashboardCard title="Available Trailers">
      <div class="filter-container">
        <input type="text" v-model="trailerFilter" placeholder="Filter trailers...">
      </div>
      <ul class="card-list">
        <li v-for="trailer in filteredTrailers" :key="trailer.id">
          <div>{{ trailer.number }}</div>
          <div>{{ trailer.type }}</div>
        </li>
      </ul>
    </DashboardCard>
    
    <DashboardCard title="Available Unloaders">
      <div class="filter-container">
        <input type="text" v-model="unloaderFilter" placeholder="Filter unloaders...">
      </div>
      <ul class="card-list">
        <li v-for="unloader in filteredUnloaders" :key="unloader.id">
          <div>{{ unloader.name }}</div>
          <div>{{ unloader.skillLevel }}</div>
        </li>
      </ul>
    </DashboardCard>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import DashboardCard from '../components/DashboardCard.vue'

// Sample data
const doors = ref([
  { id: 1, name: 'Door 1', status: 'Available' },
  { id: 2, name: 'Door 2', status: 'Available' },
  { id: 3, name: 'Door 3', status: 'Maintenance' }
])

const assignments = ref([
  { id: 1, door: 'Door 5', trailer: 'TR-001', unloader: 'John Doe' },
  { id: 2, door: 'Door 6', trailer: 'TR-002', unloader: 'Jane Smith' }
])

const trailers = ref([
  { id: 1, number: 'TR-001', type: 'Dry Van' },
  { id: 2, number: 'TR-002', type: 'Reefer' }
])

const unloaders = ref([
  { id: 1, name: 'John Doe', skillLevel: 'Expert' },
  { id: 2, name: 'Jane Smith', skillLevel: 'Intermediate' }
])

// Filters
const doorFilter = ref('')
const trailerFilter = ref('')
const unloaderFilter = ref('')

// Computed properties
const filteredDoors = computed(() => {
  return doors.value.filter(door => 
    door.name.toLowerCase().includes(doorFilter.value.toLowerCase())
  )
})

const filteredTrailers = computed(() => {
  return trailers.value.filter(trailer => 
    trailer.number.toLowerCase().includes(trailerFilter.value.toLowerCase())
  )
})

const filteredUnloaders = computed(() => {
  return unloaders.value.filter(unloader => 
    unloader.name.toLowerCase().includes(unloaderFilter.value.toLowerCase())
  )
})
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.filter-container {
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #eee;
}

.filter-container input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.card-list {
  list-style-type: none;
  max-height: 300px;
  overflow-y: auto;
  padding: 0;
  margin: 0;
}

.card-list li {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #eee;
  display: grid;
  grid-template-columns: 2fr 1fr;
  align-items: center;
  gap: 1rem;
}

.card-list li:last-child {
  border-bottom: none;
}

.door-name {
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.assignment-info {
  display: flex;
  flex-direction: column;
  font-size: 0.9rem;
  color: #555;
}

.trailer-info {
  font-size: 0.85rem;
  color: #666;
}

.status-available {
  color: var(--success-color);
  font-size: 0.9rem;
  font-weight: bold;
  text-align: right;
}

.status-assigned {
  color: var(--accent-color);
  font-size: 0.9rem;
  font-weight: bold;
  text-align: right;
}

@media (max-width: 1200px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  
  .card-list li {
    grid-template-columns: 1fr;
  }
  
  .status-available,
  .status-assigned {
    text-align: left;
    margin-top: 0.5rem;
  }
}
</style>