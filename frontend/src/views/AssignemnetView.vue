<template>
  <section class="dashboard-grid">
    <DashboardCard wide title="Current Assignments">
      <template #actions>
        <button class="refresh-btn" @click="refreshAssignments">
          <i class="fas fa-sync-alt"></i>
        </button>
      </template>
      
      <ManagementTable 
        :headers="['Door', 'Trailer', 'Unloader', 'Start Time', 'Status', 'Actions']" 
        :items="assignments"
      >
        <template #actions="{ item }">
          <button class="action-btn edit-btn" @click="editAssignment(item.id)">
            <i class="fas fa-edit"></i>
          </button>
          <button class="action-btn delete-btn" @click="deleteAssignment(item.id)">
            <i class="fas fa-trash"></i>
          </button>
        </template>
      </ManagementTable>
    </DashboardCard>
    
    <DashboardCard title="Create New Assignment">
      <form @submit.prevent="createAssignment" class="assignment-form">
        <div class="form-group">
          <label for="door-select">Door:</label>
          <select id="door-select" v-model="newAssignment.doorId" required>
            <option value="">Select Door</option>
            <option v-for="door in availableDoors" :key="door.id" :value="door.id">
              {{ door.name }}
            </option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="trailer-select">Trailer:</label>
          <select id="trailer-select" v-model="newAssignment.trailerId" required>
            <option value="">Select Trailer</option>
            <option v-for="trailer in availableTrailers" :key="trailer.id" :value="trailer.id">
              {{ trailer.number }} ({{ trailer.type }})
            </option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="unloader-select">Unloader:</label>
          <select id="unloader-select" v-model="newAssignment.unloaderId" required>
            <option value="">Select Unloader</option>
            <option v-for="unloader in availableUnloaders" :key="unloader.id" :value="unloader.id">
              {{ unloader.name }} ({{ unloader.skillLevel }})
            </option>
          </select>
        </div>
        
        <button type="submit" class="submit-btn">Create Assignment</button>
        <button type="button" class="cancel-btn" @click="resetForm">Clear Form</button>
      </form>
    </DashboardCard>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import DashboardCard from '../components/DashboardCard.vue'
import ManagementTable from '../components/ManagementTable.vue'

// Sample data
const assignments = ref([
  {
    id: 1,
    Door: 'Dock 1',
    Trailer: 'TR-001',
    Unloader: 'John Doe',
    'Start Time': '2023-05-15 08:30',
    Status: 'Active'
  },
  {
    id: 2,
    Door: 'Dock 2',
    Trailer: 'TR-002',
    Unloader: 'Jane Smith',
    'Start Time': '2023-05-15 09:15',
    Status: 'Pending'
  }
])

const availableDoors = ref([
  { id: 1, name: 'Dock 1' },
  { id: 2, name: 'Dock 2' },
  { id: 3, name: 'Dock 3' }
])

const availableTrailers = ref([
  { id: 1, number: 'TR-001', type: 'Dry Van' },
  { id: 2, number: 'TR-002', type: 'Reefer' }
])

const availableUnloaders = ref([
  { id: 1, name: 'John Doe', skillLevel: 'Expert' },
  { id: 2, name: 'Jane Smith', skillLevel: 'Intermediate' }
])

const newAssignment = ref({
  doorId: '',
  trailerId: '',
  unloaderId: ''
})

const refreshAssignments = () => {
  console.log('Refreshing assignments...')
  // In a real app, this would fetch data from an API
}

const editAssignment = (id) => {
  console.log('Editing assignment:', id)
  // In a real app, this would open an edit modal/form
}

const deleteAssignment = (id) => {
  if (confirm('Are you sure you want to delete this assignment?')) {
    assignments.value = assignments.value.filter(a => a.id !== id)
  }
}

const createAssignment = () => {
  const door = availableDoors.value.find(d => d.id === newAssignment.value.doorId)
  const trailer = availableTrailers.value.find(t => t.id === newAssignment.value.trailerId)
  const unloader = availableUnloaders.value.find(u => u.id === newAssignment.value.unloaderId)
  
  if (door && trailer && unloader) {
    const newId = assignments.value.length > 0 
      ? Math.max(...assignments.value.map(a => a.id)) + 1 
      : 1
    
    assignments.value.push({
      id: newId,
      Door: door.name,
      Trailer: trailer.number,
      Unloader: unloader.name,
      'Start Time': new Date().toLocaleString(),
      Status: 'Pending'
    })
    
    resetForm()
  }
}

const resetForm = () => {
  newAssignment.value = {
    doorId: '',
    trailerId: '',
    unloaderId: ''
  }
}
</script>

<style scoped>
.assignment-form {
  padding: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.form-group select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.submit-btn {
  background-color: var(--success-color);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  margin-top: 1rem;
  width: 100%;
}

.cancel-btn {
  background-color: var(--warning-color);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  margin-top: 0.5rem;
  width: 100%;
}

.refresh-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  padding: 0 0.5rem;
}
</style>